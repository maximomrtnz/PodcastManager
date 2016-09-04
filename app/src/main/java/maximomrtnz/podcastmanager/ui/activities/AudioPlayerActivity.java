package maximomrtnz.podcastmanager.ui.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FileCache;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.services.AudioService;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.DownloadManagerHelper;
import maximomrtnz.podcastmanager.utils.JsonUtil;
import maximomrtnz.podcastmanager.utils.Utils;
import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by maximo on 14/08/16.
 */

public class AudioPlayerActivity extends BaseActivity implements MediaController.MediaPlayerControl {

    private static String LOG_TAG = "AudioPlayerActivity";

    private MusicController mController;
    private AudioService mAudioService;
    private Toolbar mToolbar;
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
    private Handler mDurationHandler = new Handler();
    private Handler mProgressHandler = new Handler();
    private DownloadManagerHelper mDownloadManagerHelper;
    private long mDownloadId;
    private Boolean mIsUserMovingSeekBar = false;
    private FileCache mFileCache;
    private FabButton mFloatingActionButton;

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

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {

            if(mAudioService!=null && mAudioBound && mAudioService.isPng()) { // Playing

                //get current position
                mTimeElapsed = getCurrentPosition();

                // get final time
                mFinalTime = getDuration();

                //set seekbar progress
                mSeekBarEpisodePosition.setProgress((int) mTimeElapsed);

                // set max
                if(!mIsUserMovingSeekBar) {
                    mSeekBarEpisodePosition.setMax((int) mFinalTime);
                }

                //set time remaing

                mEpisodeDuration.setText(String.format("%s / %s", Utils.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(mTimeElapsed)), Utils.formatSeconds(TimeUnit.MILLISECONDS.toSeconds(mFinalTime))));

            }

            //repeat yourself that again in 100 miliseconds
            mDurationHandler.postDelayed(this, 100);
        }
    };


    /**
     * Checks download progress and updates status, then re-schedules itself.
     */
    private Runnable progressChecker = new Runnable() {
        @Override
        public void run() {
            float progress = 0;
            try {
                progress = mDownloadManagerHelper.getProgressPercentage(mDownloadId);
                mFloatingActionButton.setProgress(progress);
            } finally {
                if (progress < 100) {
                    mProgressHandler.postDelayed(progressChecker, 1000);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio_player);

        // GetPodcast Information from caller activity
        Intent intent = getIntent();

        Episode episode = JsonUtil.getInstance().fromJson(intent.getStringExtra("episode"),Episode.class);

        mEpisodeList = new ArrayList<>();

        mEpisodeList.add(episode);

        setController();

        loadUI();

        mFileCache = new FileCache(this,Constants.DIRECTORIES.DOWNLOADS);

        mDownloadManagerHelper = new DownloadManagerHelper(this);

        checkIfIsDownloading();

    }

    @Override
    public void loadUI() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.label_now_playing));

        setSupportActionBar(mToolbar);

        // Enabling Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                // Show Play Button
                mImageButtonPause.setVisibility(View.VISIBLE);

                // Hide Play button
                mImageButtonPlay.setVisibility(View.GONE);

                start();

            }

        });

        mImageButtonPause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // Show Play button
                mImageButtonPlay.setVisibility(View.VISIBLE);

                // Hide Play Button
                mImageButtonPause.setVisibility(View.GONE);

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

        mSeekBarEpisodePosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserMovingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserMovingSeekBar = false;
            }
        });

        mFloatingActionButton = (FabButton) findViewById(R.id.floating_action_button);


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
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
    protected void onPause() {
        super.onPause();

        mPaused = true;

        ArrayList<ContentProviderOperation> episodesToUpsert = new ArrayList<>();

        EpisodeConverter episodeConverter = new EpisodeConverter();

        for(Episode episode : mEpisodeList){
            if(episode.getDirty()){
                episodesToUpsert.add(episodeConverter.toUpdateOperation(episode));
            }
        }

        // Save episode changes
        try {
            getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY, episodesToUpsert);
        }catch (RemoteException e){
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }catch (OperationApplicationException e){
            e.printStackTrace();
            Log.d(LOG_TAG,e.getMessage());
        }

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
            playIntent.putExtra("episodes",JsonUtil.getInstance().toJson(mEpisodeList));
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

        mDurationHandler.postDelayed(updateSeekBarTime, 100);

        // Mark episode as played
        mEpisodeList.get(0).setPlayed(true);

        // Set as a dirty to save changes before activity is closed
        mEpisodeList.get(0).setDirty(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return true;

            case R.id.action_download:
                downloadFile();
                return true;

            case R.id.action_deleted:
                deleteFile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audio_player_activity, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Episode currentEpisode = mEpisodeList.get(0);

        if(currentEpisode.getDownloadId() != null){
            menu.findItem(R.id.action_deleted).setVisible(true);
            menu.findItem(R.id.action_download).setVisible(false);
        }else{
            menu.findItem(R.id.action_deleted).setVisible(false);
            menu.findItem(R.id.action_download).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);

    }

    private void downloadFile(){

        Episode currentEpisode = mEpisodeList.get(0);

        if(currentEpisode.getDownloadId()==0){

            mDownloadId = mDownloadManagerHelper.download(currentEpisode.getEpisodeUrl(), Constants.DIRECTORIES.ROOT + "/" + Constants.DIRECTORIES.DOWNLOADS, FileCache.getAudioFileName(currentEpisode.getEpisodeUrl()), currentEpisode.getTitle(), getString(R.string.notification_text_download_in_progress));

            // Save downloadId on episode to track download progress
            currentEpisode.setDownloadId(mDownloadId);

            // Set as a dirty to save changes before activity is closed
            currentEpisode.setDirty(true);

            showDownloadingFabButton();

        }else{

            deleteFile();

        }

    }

    private void deleteFile(){

        mProgressHandler.removeCallbacks(progressChecker);

        mFloatingActionButton.showProgress(false);

        Episode currentEpisode = mEpisodeList.get(0);

        mDownloadManagerHelper.deleteDownload(currentEpisode.getDownloadId());

        // Remove Download Id
        currentEpisode.setDownloadId(0L);

        // Set as a dirty to save changes before activity is closed
        currentEpisode.setDirty(true);

        // Reload Toolbar Menus
        invalidateOptionsMenu();

        mFloatingActionButton.resetIcon();

    }

    private void checkIfIsDownloading(){

        Episode currentEpisode = mEpisodeList.get(0);

        if(currentEpisode.getDownloadId()!=0){

            int status = mDownloadManagerHelper.getDownloadStatus(currentEpisode.getDownloadId());

            mDownloadId = currentEpisode.getDownloadId();

            // If currently running
            switch (status){
                case DownloadManager.STATUS_RUNNING:
                    showDownloadingFabButton();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    showDeleteFabButton();
                    break;
                default:
                    break;
            }

        }

    }

    private void showDownloadingFabButton(){
        mFloatingActionButton.showProgress(true);
        mFloatingActionButton.setProgress(0);
        // Start progress handler
        progressChecker.run();
    }

    private void showDeleteFabButton(){
        mFloatingActionButton.onProgressCompleted();
    }

}
