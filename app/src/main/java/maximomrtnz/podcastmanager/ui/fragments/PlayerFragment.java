package maximomrtnz.podcastmanager.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.services.PlayerService;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.DateUtils;
import maximomrtnz.podcastmanager.utils.JsonUtil;
import maximomrtnz.podcastmanager.utils.PlayQueue;


/**
 * Created by maximo on 14/08/16.
 */

public class PlayerFragment extends BaseFragment implements View.OnClickListener{

    private static String LOG_TAG = "PlayerFragment";
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private ImageLoader mImageLoader;
    private ImageView mImageViewMiniEpisode;
    private TextView mTitle;
    private TextView mTextViewStartDuration;
    private TextView mTextViewEndDuration;
    private FloatingActionButton mFABPause;
    private ImageButton mImageButtonSkipNext;
    private ImageButton mImageButtonSkipPreviuos;
    private ImageButton mImageButtonMiniPlayPause;
    private ImageButton mImageButtonMiniSkipNext;
    private SeekBar mSeekbar;
    private PlayerService mService;
    boolean mBound = false;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    private final Handler mHandler = new Handler();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    private void handleMessage(Intent msg){

        if(!isAdded()) {
            return;
        }

        Bundle data = msg.getExtras();

        AnimationDrawable animation;
        AnimationDrawable animationMini;

        switch (data.getInt(Constants.PLAYER_SERVICE.COMMAND, 0)){

            case Constants.PLAYER_SERVICE.EPISODE_CHANGE:

                Long episodeId = data.getLong(Constants.PLAYER_SERVICE.DATA, 0L);

                if(episodeId!=0){

                    Cursor cursor = getActivity().getContentResolver().query(
                            PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                            PodcastManagerContract.Episode.PROJECTION_ALL,
                            PodcastManagerContract.Episode._ID+"=?",
                            new String[]{String.valueOf(episodeId)},
                            null);

                    if(cursor.moveToFirst()){
                        updateInformation(new EpisodeConverter().loadFrom(cursor));
                    }

                }

                break;
            case Constants.PLAYER_SERVICE.STATE_PLAYING:
                scheduleSeekbarUpdate();
                mFABPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_36dp));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                mTitle.setSelected(true);
                break;
            case Constants.PLAYER_SERVICE.STATE_PAUSED:
                stopSeekbarUpdate();
                mFABPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                break;
            case Constants.PLAYER_SERVICE.STATE_STOPPED:
                mFABPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                stopSeekbarUpdate();
                break;
            case Constants.PLAYER_SERVICE.STATE_PREPARING:
                stopSeekbarUpdate();
                animation = (AnimationDrawable)getResources().getDrawable(R.drawable.ic_spinner_white_24dp);
                animationMini = (AnimationDrawable)getResources().getDrawable(R.drawable.ic_spinner_white_24dp);
                mFABPause.setImageDrawable(animation);
                mImageButtonMiniPlayPause.setImageDrawable(animationMini);
                animation.start();
                animationMini.start();
                break;

            case Constants.PLAYER_SERVICE.STATE_RETRIVING:
                animation = (AnimationDrawable)getResources().getDrawable(R.drawable.ic_spinner_white_24dp);
                animationMini = (AnimationDrawable)getResources().getDrawable(R.drawable.ic_spinner_white_24dp);
                mFABPause.setImageDrawable(animation);
                mImageButtonMiniPlayPause.setImageDrawable(animationMini);
                animation.start();
                animationMini.start();
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_player,container,false);
        loadUIComponents(v);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.PLAYER_SERVICE.FILTER));
        return v;
    }

    @Override
    public void onClick(View target) {

        // Send the correct intent to the MusicService, according to the button that was clicked
        if (target == mFABPause || target == mImageButtonMiniPlayPause) {
            doAction(PlayerService.ACTION_TOGGLE_PLAYBACK);
        }else if (target == mImageButtonSkipNext || target == mImageButtonMiniSkipNext) {
            doAction(PlayerService.ACTION_SKIP_NEXT);
        }else if (target == mImageButtonSkipPreviuos) {
            doAction(PlayerService.ACTION_SKIP_PREVIOUS);
        }

    }

    @Override
    public void loadUIComponents(View view) {

        mFABPause = (FloatingActionButton) view.findViewById(R.id.fab_pause);
        mImageButtonMiniPlayPause = (ImageButton)view.findViewById(R.id.image_button_mini_play_pause);
        mImageButtonSkipPreviuos = (ImageButton) view.findViewById(R.id.image_button_skip_previous);
        mImageButtonSkipNext = (ImageButton) view.findViewById(R.id.image_button_skip_next);
        mImageButtonMiniSkipNext = (ImageButton) view.findViewById(R.id.image_button_mini_skip_next);
        mTitle = (TextView)view.findViewById(R.id.text_view_mini_episode_title);
        mImageViewMiniEpisode = (ImageView)view.findViewById(R.id.image_view_mini_episode);
        mTextViewEndDuration = (TextView)view.findViewById(R.id.text_view_end_duration);
        mTextViewStartDuration = (TextView)view.findViewById(R.id.text_view_start_duration);
        mSeekbar = (SeekBar)view.findViewById(R.id.seek_bar_episode_position);

        mFABPause.setOnClickListener(this);
        mImageButtonSkipNext.setOnClickListener(this);
        mImageButtonSkipPreviuos.setOnClickListener(this);
        mImageButtonMiniPlayPause.setOnClickListener(this);
        mImageButtonMiniSkipNext.setOnClickListener(this);

        mImageLoader = new ImageLoader(getActivity());

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewStartDuration.setText(DateUtils.formatSeconds(progress/1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mBound) {
                    mService.seekTo(seekBar.getProgress());
                }
                scheduleSeekbarUpdate();
            }
        });

    }

    private void updateInformation(Episode episode){

        mTitle.setText(episode.getTitle());

        mTextViewEndDuration.setText(episode.getItunesDuration());

        mSeekbar.setMax(DateUtils.timeToSeconds(episode.getItunesDuration()).intValue()*1000);

        if(episode.getRemainderDuration()!=null){
            mTextViewStartDuration.setText(DateUtils.formatSeconds(episode.getRemainderDuration()));
            mSeekbar.setProgress(episode.getRemainderDuration());
        }else{
            mTextViewStartDuration.setText(DateUtils.formatSeconds(0));
            mSeekbar.setProgress(0);
        }

        mImageLoader.loadAsync(episode.getImageUrl(), new ImageLoader.ImageLoadeListener() {
            @Override
            public void onImageLoader(Bitmap bitmap) {
                mImageViewMiniEpisode.setImageBitmap(bitmap);
            }
        });

    }

    public void play(Podcast podcast, Episode episode){

        PlayQueue.getInstance().add(podcast,episode);

        // Send an intent with the episode to play. This is expected by
        // PlayerService.
        Intent i = new Intent(getContext(),PlayerService.class);
        i.setAction(PlayerService.ACTION_ADD_TO_PLAY_QUEUE);
        getActivity().startService(i);

    }

    public void doAction(String action){
        Intent i = new Intent(getContext(),PlayerService.class);
        i.setAction(action);
        getActivity().startService(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), PlayerService.class);
        getActivity().bindService(intent, mConnection, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            updateProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void updateProgress() {
        if(mBound) {
            int currentPosition = mService.getCurrentPosition();
            mSeekbar.setProgress(currentPosition);
            Log.d(LOG_TAG,"CURRENT POSTION"+currentPosition);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

}
