package maximomrtnz.podcastmanager.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.services.AudioService;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 14/08/16.
 */

public class AudioPlayerActivity extends BaseActivity implements MediaController.MediaPlayerControl {

    private static String LOG_TAG = "AudioPlayerActivity";

    private MusicController mController;
    private AudioService mAudioService;
    private ImageLoader mImageLoader;
    private ImageView mImageViewEpisode;
    private TextView mEpisodeTitle;
    private TextView mEpisodeDuration;
    private ImageButton mImageButtonPlay;
    private ImageButton mImageButtonPause;
    private ImageButton mImageButtonForward;
    private ImageButton mImageButtonRewind;
    private SeekBar mSeekBarEpisodePosition;
    private long mTimeElapsed = 0;
    private long mFinalTime = 0;
    private int mForwardTime = 2000;
    private int mBackwardTime = 2000;
    private Handler durationHandler = new Handler();



    //binding
    private boolean mAudioBound = false;

    //activity and playback pause flags
    private boolean mPaused = false;
    private boolean mPlaybackPaused = false;

    private Intent playIntent;

    private List<Episode> mEpisodeList;


    //connect to the service
    private ServiceConnection mAudioConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            AudioService.AudioBinder binder = (AudioService.AudioBinder)service;
            //get service
            mAudioService = binder.getService();

            //pass list
            mAudioService.setList(mEpisodeList);

            mAudioBound = true;

            episodePicked(0);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAudioBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio_player);

        // GetPodcast Information from caller activity
        Intent intent = getIntent();

        Episode episode = new Episode();

        episode.loadFrom(intent);

        mEpisodeList = new ArrayList<>();

        mEpisodeList.add(episode);

        setController();

        loadUI();

    }

    @Override
    public void loadUI() {

        mImageLoader = new ImageLoader(getApplicationContext());

        mImageViewEpisode = (ImageView) findViewById(R.id.image_view_episode);

        mImageLoader.displayImage(mEpisodeList.get(0).getImageUrl(),mImageViewEpisode);

        mEpisodeTitle = (TextView) findViewById(R.id.text_view_episode_title);

        mEpisodeTitle.setText(mEpisodeList.get(0).getTitle());

        mEpisodeDuration = (TextView) findViewById(R.id.text_view_episode_duration);

        mImageButtonPlay = (ImageButton)findViewById(R.id.image_button_play);

        mImageButtonPause = (ImageButton)findViewById(R.id.image_button_pause);

        mImageButtonRewind = (ImageButton)findViewById(R.id.image_button_rewind);

        mImageButtonForward = (ImageButton)findViewById(R.id.image_button_forward);

        mSeekBarEpisodePosition = (SeekBar)findViewById(R.id.seek_bar_episode_position);

        mImageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        mImageButtonPause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                pause();
            }

        });

        mImageButtonRewind.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                rewind();
            }

        });

        mImageButtonForward.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                forward();
            }

        });

    }

    @Override
    public void start() {
        mAudioService.go();
    }

    @Override
    public void pause() {
        mPlaybackPaused = true;
        mAudioService.pausePlayer();
    }

    public void rewind(){
        if(mAudioService!=null && mAudioBound && mAudioService.isPng()) {
            //check if we can go forward at forwardTime seconds before song endes
            if ((mTimeElapsed - mBackwardTime) > 0) {
                mTimeElapsed = mTimeElapsed - mBackwardTime;
                //seek to the exact second of the track
                seekTo((int) mTimeElapsed);
            }
        }
    }

    public void forward(){

        if(mAudioService!=null && mAudioBound && mAudioService.isPng()) {

            //check if we can go forward at forwardTime seconds before song endes
            if ((mTimeElapsed + mForwardTime) <= mFinalTime) {
                mTimeElapsed = mTimeElapsed + mForwardTime;
                //seek to the exact second of the track
                seekTo((int) mTimeElapsed);
            }

        }

    }

    @Override
    public int getDuration() {
        if(mAudioService!=null && mAudioBound && mAudioService.isPng()) {
            return mAudioService.getDur();
        }
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(mAudioService!=null && mAudioBound && mAudioService.isPng()) {
            return mAudioService.getPosn();
        }
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        mAudioService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mAudioService!=null && mAudioBound){
            return mAudioService.isPng();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void setController(){
        mController = new MusicController(this);
        mController.setMediaPlayer(this);
        //controller.setAnchorView(findViewById(R.id.song_list));
        mController.setEnabled(true);
    }

    public class MusicController extends MediaController {

        public MusicController(Context c){
            super(c);
        }

        public void hide(){}

    }

    private void playNext(){
        mAudioService.playNext();
        if(mPlaybackPaused){
            setController();
            mPlaybackPaused=false;
        }
        mController.show(0);
    }

    private void playPrev(){
        mAudioService.playPrev();
        if(mPlaybackPaused){
            setController();
            mPlaybackPaused=false;
        }
        mController.show(0);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mPaused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mPaused){
            setController();
            mPaused=false;
        }
    }

    @Override
    protected void onStop() {
        mController.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mAudioService = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, AudioService.class);
            playIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            bindService(playIntent, mAudioConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void episodePicked(int episodePosition){
        mAudioService.setEpisode(episodePosition);
        mAudioService.playEpisode();
        if(mPlaybackPaused){
            setController();
            mPlaybackPaused=false;
        }
        mController.show(0);

        mFinalTime = getDuration();

        mSeekBarEpisodePosition.setMax((int) mFinalTime);

        mTimeElapsed = getCurrentPosition();

        mSeekBarEpisodePosition.setProgress((int) mTimeElapsed);

        durationHandler.postDelayed(updateSeekBarTime, 100);

        Log.d(LOG_TAG,mFinalTime+"");
        Log.d(LOG_TAG,mTimeElapsed+"");

    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {

            if(mAudioService!=null && mAudioBound && mAudioService.isPng()) {

                //get current position
                mTimeElapsed = getCurrentPosition();

                // get final time
                mFinalTime = getDuration();


                //set seekbar progress
                mSeekBarEpisodePosition.setProgress((int) mTimeElapsed);

                // set max
                mSeekBarEpisodePosition.setMax((int) mFinalTime);

                //set time remaing

                mEpisodeDuration.setText(String.format("%s / %s", Utils.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(mTimeElapsed)), Utils.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(mFinalTime))));

            }

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };


}
