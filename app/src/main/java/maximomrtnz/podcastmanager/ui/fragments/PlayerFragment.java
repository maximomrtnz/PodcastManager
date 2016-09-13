package maximomrtnz.podcastmanager.ui.fragments;

import android.content.ComponentName;

import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;

import maximomrtnz.podcastmanager.services.PlayerService;

import maximomrtnz.podcastmanager.utils.DateUtils;
import maximomrtnz.podcastmanager.utils.EpisodePlaylist;

/**
 * Created by maximo on 14/08/16.
 */

public class PlayerFragment extends BaseFragment {

    private static String LOG_TAG = "PlayerFragment";

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private ImageLoader mImageLoader;
    private ImageView mImageViewEpisode;
    private TextView mEpisodeTitle;
    private TextView mTextViewStartDuration;
    private TextView mTextViewEndDuration;
    private ImageButton mImageButtonPause;
    private ImageButton mImageButtonSkipNext;
    private ImageButton mImageButtonSkipPreviuos;
    private SeekBar mSeekbar;


    private PlaybackStateCompat mCurrentState;

    private MediaBrowserCompat mMediaBrowser;

    private MediaMetadataCompat mCurrentMetadata;

    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    private final Handler mHandler = new Handler();

    private PlaybackStateCompat mLastPlaybackState;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };


    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
            @Override
            public void onConnected() {

                mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mSubscriptionCallback);

                MediaControllerCompat mediaController = null;

                try {
                    mediaController = new MediaControllerCompat(getActivity(), mMediaBrowser.getSessionToken());
                }catch (RemoteException e){
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage());
                }

                if(mediaController!=null) {

                    mActivity.setSupportMediaController(mediaController);

                    PlaybackStateCompat state = mediaController.getPlaybackState();
                    MediaMetadataCompat metadata = mediaController.getMetadata();

                    updatePlaybackState(state);
                    updateMetadata(metadata);
                    mediaController.registerCallback(mMediaControllerCallback);
                    updateDuration(metadata);

                    if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING || state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
                        scheduleSeekbarUpdate();
                    }

                }
            }
        };

    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            updateMetadata(metadata);
            updateDuration(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onSessionDestroyed() {
            updatePlaybackState(null);
        }
    };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(String parentId, List<MediaBrowserCompat.MediaItem> children) {

                }
            };

    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        if (item.isPlayable()) {
            mActivity.getSupportMediaController().getTransportControls().playFromMediaId(item.getMediaId(), null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player, container, false);

        mImageLoader = new ImageLoader(getContext());

        loadUIComponents(view);

        return view;

    }


    @Override
    public void loadUIComponents(View view) {

        mImageViewEpisode = (ImageView) view.findViewById(R.id.image_view_episode);

        mEpisodeTitle = (TextView) view.findViewById(R.id.text_view_episode_title);

        mTextViewEndDuration = (TextView) view.findViewById(R.id.text_view_end_duration);

        mTextViewStartDuration = (TextView) view.findViewById(R.id.text_view_start_duration);

        mImageButtonPause = (ImageButton)view.findViewById(R.id.image_button_pause);

        mImageButtonSkipNext = (ImageButton)view.findViewById(R.id.image_button_skip_next);

        mImageButtonSkipPreviuos = (ImageButton)view.findViewById(R.id.image_button_skip_previous);

        mSeekbar = (SeekBar)view.findViewById(R.id.seek_bar_episode_position);

        mImageButtonPause.setOnClickListener(mPlaybackButtonListener);

        mImageButtonSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipNext();
            }
        });

        mImageButtonSkipPreviuos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipPrevious();
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewStartDuration.setText(DateUtils.formatSeconds(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mActivity.getSupportMediaController() != null) {
            mActivity.getSupportMediaController().unregisterCallback(mMediaControllerCallback);
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.unsubscribe(mCurrentMetadata.getDescription().getMediaId());
            mMediaBrowser.disconnect();
        }
    }

    private void updatePlaybackState(PlaybackStateCompat state) {

        if(state==null){
            return;
        }

        mCurrentState = state;

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle));
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_red_60dp));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_red_60dp));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                stopSeekbarUpdate();
                break;
            default:
                Log.d(LOG_TAG, "Unhandled state "+state.getState());
        }


    }

    private void updateMetadata(MediaMetadataCompat metadata) {

        if(metadata == null){
            return;
        }

        mCurrentMetadata = metadata;

        mEpisodeTitle.setText(metadata.getDescription().getTitle());

        Log.d(LOG_TAG,metadata.getDescription().getIconUri().toString());

        // Load Image Async
        mImageLoader.displayImage(metadata.getDescription().getIconUri().toString(), mImageViewEpisode);

    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser = new MediaBrowserCompat(getActivity(), new ComponentName(getActivity(), PlayerService.class), mConnectionCallback, null);
        mMediaBrowser.connect();
    }


    private final View.OnClickListener mPlaybackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int state = mCurrentState == null ? PlaybackState.STATE_NONE : mCurrentState.getState();
            if (state == PlaybackState.STATE_PAUSED || state == PlaybackState.STATE_STOPPED || state == PlaybackState.STATE_NONE) {
                if (mCurrentMetadata == null) {
                    mCurrentMetadata = EpisodePlaylist.getInstance().getMetadata(EpisodePlaylist.getInstance().getMediaItems().get(0).getMediaId());
                    updateMetadata(mCurrentMetadata);
                }
                getActivity().getSupportMediaController().getTransportControls().playFromMediaId(mCurrentMetadata.getDescription().getMediaId(), null);
            } else {
                getActivity().getSupportMediaController().getTransportControls().pause();
            }
        }
    };

    private void seekTo(long longPos){
        Log.d(LOG_TAG, longPos+"");
        mActivity.getSupportMediaController().getTransportControls().seekTo(longPos);
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

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (mCurrentState == null) {
            return;
        }
        long currentPosition = mCurrentState.getPosition();

        if (mCurrentState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            long timeDelta = SystemClock.elapsedRealtime() - mCurrentState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mCurrentState.getPlaybackSpeed();
        }

        mSeekbar.setProgress((int) currentPosition/1000);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekbar.setMax(duration);
        mTextViewEndDuration.setText(DateUtils.formatSeconds(duration));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

    public void skipNext(){
        MediaControllerCompat.TransportControls controls = mActivity.getSupportMediaController().getTransportControls();
        controls.skipToNext();
    }

    public void skipPrevious(){
        MediaControllerCompat.TransportControls controls = mActivity.getSupportMediaController().getTransportControls();
        controls.skipToPrevious();
    }

}
