package maximomrtnz.podcastmanager.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.ui.activities.AudioPlayerActivity;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.NotificationHelper;

import android.app.Notification;
import android.app.PendingIntent;
import android.view.View;

/**
 * Created by maximo on 14/08/16.
 */

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private String LOG_TAG = "AudioService";

    //media player
    private MediaPlayer mPlayer;
    //song list
    private List<Episode> mEpisodes;
    //current position
    private int mEpisodePosition;
    private final IBinder mAudioBind = new AudioBinder();
    private ImageLoader mImageLoader;
    private NotificationHelper mNotificationHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAudioBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        mEpisodePosition = 0;
        //create player
        mPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public void setList(List<Episode> episodes){
        mEpisodes = episodes;
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    public void playEpisode(){

        mPlayer.reset();

        Episode playEpisode = mEpisodes.get(mEpisodePosition);

        Log.e(LOG_TAG, "Playing podcast -->"+playEpisode.getTitle());

        try{
            mPlayer.setDataSource(playEpisode.getEpisodeUrl());
        }catch(Exception e){
            Log.e(LOG_TAG, "Error setting data source", e);
        }

        mPlayer.prepareAsync();


    }

    public void setEpisode(int episodeIndex){
        mEpisodePosition = episodeIndex;
    }

    public int getPosn(){
        return mPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mPlayer.getDuration();
    }

    public boolean isPng(){
        return mPlayer.isPlaying();
    }

    public void pausePlayer(){
        mNotificationHelper.setIcon(R.drawable.ic_play_notification_icon).show(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        mNotificationHelper.getContentView().setImageViewResource(R.id.status_bar_play, R.drawable.ic_pause);
        mPlayer.pause();
    }

    public void seek(int posn){
        mPlayer.seekTo(posn);
    }

    public void go(){
        mNotificationHelper.setIcon(R.drawable.ic_pause_notification_icon).show(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        mNotificationHelper.getContentView().setImageViewResource(R.id.status_bar_play, R.drawable.ic_play);
        mPlayer.start();
    }

    public void playPrev(){
        mEpisodePosition--;
        if(mEpisodePosition>0) {
            mEpisodePosition = mEpisodes.size() - 1;
        }
        playEpisode();
    }

    //skip to next
    public void playNext(){
        mEpisodePosition++;
        if(mEpisodePosition==mEpisodes.size()){
            mEpisodePosition = 0;
        }
        playEpisode();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent==null){
            return START_STICKY;
        }

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification(intent);
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
            if(isPng()) {
                pausePlayer();
            }else{
                go();
            }
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotification(Intent intent) {

        Episode episode = new Episode();

        episode.loadFrom(intent);

        mImageLoader = new ImageLoader(this);

        Intent notificationIntent = new Intent(this, AudioPlayerActivity.class);
        episode.loadTo(notificationIntent);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent previousIntent = new Intent(this, AudioService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, AudioService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent closeIntent = new Intent(this, AudioService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);

        mNotificationHelper = new NotificationHelper(this)
                .setIcon(R.drawable.ic_pause_notification_icon)
                .setContentView(views)
                .setIntent(notificationIntent);


        mNotificationHelper.getContentView().setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        mNotificationHelper.getContentView().setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        mNotificationHelper.getContentView().setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        mNotificationHelper.getContentView().setImageViewResource(R.id.status_bar_play, R.drawable.ic_pause);
        mNotificationHelper.getContentView().setTextViewText(R.id.status_bar_track_name, episode.getTitle());
        mNotificationHelper.getContentView().setImageViewBitmap(R.id.status_bar_icon, mImageLoader.getBitmap(episode.getImageUrl()));

        // Show Notification
        mNotificationHelper.show(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mNotificationHelper.getNotification());

    }

}
