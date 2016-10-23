package maximomrtnz.podcastmanager.services;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.IOException;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.broadcastreceivers.MusicIntentReceiver;
import maximomrtnz.podcastmanager.broadcastreceivers.NotificationBroadcast;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.utils.AudioFocusHelper;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.MusicFocusable;
import maximomrtnz.podcastmanager.utils.NotificationHelper;
import maximomrtnz.podcastmanager.utils.PlayQueue;
import maximomrtnz.podcastmanager.ui.fragments.PlayerFragment;
import maximomrtnz.podcastmanager.utils.SharedPrefsUtils;

/**
 * Service that handles media playback. This is the Service through which we perform all the media
 * handling in our application. Upon initialization, it starts a {@link PlayQueue} to scan
 * the user's media. Then, it waits for Intents (which come from our main activity,
 * {@link PlayerFragment}, which signal the service to perform specific operations: Play, Pause,
 * Rewind, Skip, etc.
 */
public class PlayerService extends Service implements OnCompletionListener, OnPreparedListener, OnErrorListener, MusicFocusable, PlayQueue.PlayQueueListener{

    // The tag we put on debug messages
    final static String TAG = "PlayerService";

    // These are the Intent actions that we are prepared to handle. Notice that the fact these
    // constants exist in our class is a mere convenience: what really defines the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for our service in
    // AndroidManifest.xml.
    public static final String ACTION_TOGGLE_PLAYBACK = "maximomrtnz.podcastmanager.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "maximomrtnz.podcastmanager.action.PLAY";
    public static final String ACTION_PAUSE = "maximomrtnz.podcastmanager.action.PAUSE";
    public static final String ACTION_STOP = "maximomrtnz.podcastmanager.action.STOP";
    public static final String ACTION_SKIP_NEXT = "maximomrtnz.podcastmanager.action.SKIP_NEXT";
    public static final String ACTION_SKIP_PREVIOUS = "maximomrtnz.podcastmanager.action.SKIP_PREVIUOS";
    public static final String ACTION_ADD_TO_PLAY_QUEUE = "maximomrtnz.podcastmanager.action.ADD_TO_PLAY_QUEUE";

    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;

    // our media player
    MediaPlayer mPlayer = null;

    // our AudioFocusHelper object, if it's available (it's available on SDK level >= 8)
    // If not available, this will be null. Always check for null before using!
    AudioFocusHelper mAudioFocusHelper = null;

    Episode mCurrentEpisode;

    Boolean mPlayFromPrefferences = false;

    ImageLoader mImageLoader = new ImageLoader(this);

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    // indicates the state our service:
    public enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
                    // paused in this state if we don't have audio focus. But we stay in this state
                    // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };

    State mState = State.Retrieving;

    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not.
    boolean mStartPlayingAfterRetrieve = false;

    enum PauseReason {
        UserRequest,  // paused by user request
        FocusLoss,    // paused because of audio focus loss
    };

    // why did we pause? (only relevant if mState == State.Paused)
    PauseReason mPauseReason = PauseReason.UserRequest;

    // do we have audio focus?
    enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }

    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;

    // whether the song we are playing is streaming from the network
    boolean mIsStreaming = false;

    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiLock mWifiLock;

    // The ID we use for the notification (the onscreen alert that appears at the notification
    // area at the top of the screen as an icon -- and as text as well if the user expands the
    // notification area).
    final int NOTIFICATION_ID = 1;

    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    AudioManager mAudioManager;
    NotificationHelper mNotificationHelper;


    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists.
     */
    void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        } else {
            mPlayer.reset();

        }
    }

    @Override
    public void onCreate() {

        mState = State.Retrieving;

        Log.i(TAG, "debug: Creating service");

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        mNotificationHelper = new NotificationHelper(this);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Create the retriever and start an asynchronous task that will prepare it.
        PlayQueue.getInstance().addListener(this);

        PlayQueue.getInstance().loadQueue();

        // create the Audio Focus Helper, if the Audio Focus feature is available (SDK 8 or above)
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
        }else {
            mAudioFocus = AudioFocus.Focused; // no focus feature, so we always "have" audio focus
        }

        mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }
    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equals(ACTION_TOGGLE_PLAYBACK)){
            processTogglePlaybackRequest();
        }else if (action.equals(ACTION_PLAY)){
            processPlayRequest();
        }else if (action.equals(ACTION_PAUSE)){
            processPauseRequest();
        }else if (action.equals(ACTION_SKIP_NEXT)){
            processSkipNextRequest();
        }else if (action.equals(ACTION_STOP)){
            processStopRequest();
        }else if (action.equals(ACTION_SKIP_PREVIOUS)){
            processSkipPreviuosRequest();
        }else if (action.equals(ACTION_ADD_TO_PLAY_QUEUE)){
            processAddRequest();
        }

        return START_NOT_STICKY; // Means we started the service, but don't want it to
        // restart in case it's killed.
    }

    void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }


    void processPlayRequest() {
        if (mState == State.Retrieving) {
            mStartPlayingAfterRetrieve = true;
            return;
        }
        tryToGetAudioFocus();
        // actually play the song
        if (mState == State.Stopped) {
            // If we're stopped, just go ahead to the next song and start playing
            playNextSong();
        }
        else if (mState == State.Paused) {
            // If we're paused, just continue playback and restore the 'foreground service' state.
            mState = State.Playing;
            sendStateChangeMessage(mState);
            setUpAsForeground();
            configAndStartMediaPlayer();
        }

    }

    void processPauseRequest() {
        if (mState == State.Retrieving) {
            // If we are still retrieving media, clear the flag that indicates we should start
            // playing when we're ready
            mStartPlayingAfterRetrieve = false;
            return;
        }
        if (mState == State.Playing) {
            // Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;
            sendStateChangeMessage(mState);
            mPlayer.pause();
            relaxResources(false); // while paused, we always retain the MediaPlayer
            // do not give up audio focus
        }

    }

    void processSkipPreviuosRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            playPreviuosSong();
        }
    }

    void processSkipNextRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            playNextSong();
        }
    }

    void processStopRequest() {
        processStopRequest(false);
    }

    void processStopRequest(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {
            mState = State.Stopped;
            sendStateChangeMessage(mState);
            // let go of all resources...
            relaxResources(true);
            giveUpAudioFocus();
            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }

    /**
     * Releases resources used by the service for playback. This includes the "foreground service"
     * status and notification, the wake locks and possibly the MediaPlayer.
     *
     * @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
     */
    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }

    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.abandonFocus()) {
            mAudioFocus = AudioFocus.NoFocusNoDuck;
        }
    }

    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause, even if mState
            // is State.Playing. But we stay in the Playing state so that we know we have to resume
            // playback once we get the focus back.
            if (mPlayer.isPlaying()) mPlayer.pause();
            return;
        }else if (mAudioFocus == AudioFocus.NoFocusCanDuck) {
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        }else {
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud
        }
        if (!mPlayer.isPlaying()){
            mPlayer.start();
        }
    }

    void processAddRequest() {

        playNextSong();

    }

    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.requestFocus()) {
            mAudioFocus = AudioFocus.Focused;
        }
    }

    void playPreviuosSong(){

        Episode playingItem = PlayQueue.getInstance().getPrevious();

        onEpisodeChange(playingItem);

    }

    /**
     * Starts playing the next song. If manualUrl is null, the next song will be randomly selected
     * from our Media Retriever (that is, it will be a random song in the user's device). If
     * manualUrl is non-null, then it specifies the URL or path to the song that will be played
     * next.
     */
    void playNextSong() {

        Episode playingItem = PlayQueue.getInstance().getNext();

        onEpisodeChange(playingItem);

    }

    void onEpisodeChange(Episode episode){

        mState = State.Stopped;

        mCurrentEpisode = episode;

        sendEpisodeChangeMessage(episode);

        relaxResources(false); // release everything except MediaPlayer

        try {

            if (episode == null) {
                processStopRequest(true); // stop everything!
                return;
            }

            mIsStreaming = episode.getEpisodeUrl().startsWith("http:") || episode.getEpisodeUrl().startsWith("https:");

            // set the source of the media player a a content URI
            createMediaPlayerIfNeeded();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(episode.getEpisodeUrl());

            mState = State.Preparing;

            sendStateChangeMessage(mState);

            setUpAsForeground();

            // starts preparing the media player in the background. When it's done, it will call
            // our OnPreparedListener (that is, the onPrepared() method on this class, since we set
            // the listener to 'this').
            //
            // Until the media player is prepared, we *cannot* call start() on it!
            mPlayer.prepareAsync();

            // If we are streaming from the internet, we want to hold a Wifi lock, which prevents
            // the Wifi radio from going to sleep while the song is playing. If, on the other hand,
            // we are *not* streaming, we want to release the lock if we were holding it before.
            if (mIsStreaming){
                mWifiLock.acquire();
            }else if (mWifiLock.isHeld()){
                mWifiLock.release();
            }

        }catch (IOException ex) {
            Log.e("MusicService", "IOException playing next song: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Called when media player is done playing current song. */
    public void onCompletion(MediaPlayer player) {

        // Remove finished song from the Play Queue
        PlayQueue.getInstance().remove(mCurrentEpisode);

        playNextSong();

    }

    /** Called when media player is done preparing. */
    public void onPrepared(MediaPlayer player) {
        // The media player is done preparing. That means we can start playing!
        mState = State.Playing;
        sendStateChangeMessage(mState);
        updateNotification();
        configAndStartMediaPlayer();
    }

    /** Updates the notification. */
    void updateNotification() {
        mNotificationHelper.show(NOTIFICATION_ID);
    }


    void setUpAsForeground() {

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        final RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.player_service_notification);

        remoteView.setTextViewText(R.id.text_view_episode_title,mCurrentEpisode.getTitle());

        mImageLoader.loadAsync(mCurrentEpisode.getImageUrl(), new ImageLoader.ImageLoadeListener() {
            @Override
            public void onImageLoader(Bitmap bitmap) {
                remoteView.setImageViewBitmap(R.id.image_view_episode,bitmap);
            }
        });

        Intent intentPause = new Intent(NotificationBroadcast.ACTION_PLAY_PAUSE);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this,0, intentPause,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.button_pause,pendingIntentPause);

        Intent intentSkipNext = new Intent(NotificationBroadcast.ACTION_SKIP_NEXT);
        PendingIntent pendingIntentSkipNext = PendingIntent.getBroadcast(this,0, intentSkipNext,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.button_next,pendingIntentSkipNext);

        Intent intentSkipPreviuos = new Intent(NotificationBroadcast.ACTION_SKIP_PREVIOUS);
        PendingIntent pendingIntentSkipPreviuos = PendingIntent.getBroadcast(this,0, intentSkipPreviuos,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.button_previuos,pendingIntentSkipPreviuos);

        mNotificationHelper
                .setContentView(remoteView)
                .setIcon(R.drawable.ic_play)
                .setContentIntent(pi)
                .setOngoing(true);

        Notification notification = mNotificationHelper.build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;

        startForeground(NOTIFICATION_ID, notification);

    }
    /**
     * Called when there's an error playing media. When this happens, the media player goes to
     * the Error state. We warn the user about the error and reset the media player.
     */
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));
        mState = State.Stopped;
        sendStateChangeMessage(mState);
        relaxResources(true);
        giveUpAudioFocus();
        return true; // true indicates we handled the error
    }

    public void onGainedAudioFocus() {
        mAudioFocus = AudioFocus.Focused;
        // restart media player with new focus settings
        if (mState == State.Playing) {
            configAndStartMediaPlayer();
        }
    }

    public void onLostAudioFocus(boolean canDuck) {
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying()) {
            configAndStartMediaPlayer();
        }
    }

    @Override
    public void onDestroy() {

        // Service is being killed, so make sure we release our resources
        mState = State.Stopped;
        sendStateChangeMessage(mState);
        relaxResources(true);
        giveUpAudioFocus();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onPlayQueueChange() {

        // Done retrieving!
        mState = State.Stopped;

        sendStateChangeMessage(mState);

        tryToGetAudioFocus();

        playNextSong();

    }

    private void sendEpisodeChangeMessage(Episode episode) {
        Intent intent = new Intent(Constants.PLAYER_SERVICE.FILTER);
        intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.EPISODE_CHANGE);
        if(episode!=null) {
            intent.putExtra(Constants.PLAYER_SERVICE.DATA, episode.getEpisodeUrl());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStateChangeMessage(State state) {
        Intent intent = new Intent(Constants.PLAYER_SERVICE.FILTER);
        if(state == State.Playing) {
            intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.STATE_PLAYING);
        }else if(state == State.Paused){
            intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.STATE_PAUSED);
        }else if(state == State.Stopped){
            intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.STATE_STOPPED);
        }else if(state == State.Preparing){
            intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.STATE_PREPARING);
        }else if(state == State.Retrieving){
            intent.putExtra(Constants.PLAYER_SERVICE.COMMAND, Constants.PLAYER_SERVICE.STATE_RETRIVING);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int position){
        if(mPlayer!=null) {
            mPlayer.seekTo(position);
        }
    }

}