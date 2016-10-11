package maximomrtnz.podcastmanager.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

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
    private ImageButton mImageButtonPause;
    private ImageButton mImageButtonSkipNext;
    private ImageButton mImageButtonSkipPreviuos;
    private ImageButton mImageButtonMiniPlayPause;
    private ImageButton mImageButtonMiniSkipNext;
    private ImageButton mImageButtonStop;
    private SeekBar mSeekbar;


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    private void handleMessage(Intent msg){
        Bundle data = msg.getExtras();
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
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                break;
            case Constants.PLAYER_SERVICE.STATE_PAUSED:
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_red_60dp));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                break;
            case Constants.PLAYER_SERVICE.STATE_STOPPED:
                mImageButtonPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_red_60dp));
                mImageButtonMiniPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                break;
            case Constants.PLAYER_SERVICE.STATE_PREPARING:

                break;

            case Constants.PLAYER_SERVICE.STATE_RETRIVING:

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
        if (target == mImageButtonPause || target == mImageButtonMiniPlayPause) {
            doAction(PlayerService.ACTION_TOGGLE_PLAYBACK);
        }else if (target == mImageButtonSkipNext || target == mImageButtonMiniSkipNext) {
            doAction(PlayerService.ACTION_SKIP_NEXT);
        }else if (target == mImageButtonSkipPreviuos) {
            doAction(PlayerService.ACTION_SKIP_PREVIOUS);
        }else if (target == mImageButtonStop) {
            doAction(PlayerService.ACTION_STOP);
        }

    }

    @Override
    public void loadUIComponents(View view) {

        mImageButtonPause = (ImageButton) view.findViewById(R.id.image_button_pause);
        mImageButtonMiniPlayPause = (ImageButton)view.findViewById(R.id.image_button_mini_play_pause);
        mImageButtonSkipPreviuos = (ImageButton) view.findViewById(R.id.image_button_skip_previous);
        mImageButtonSkipNext = (ImageButton) view.findViewById(R.id.image_button_skip_next);
        mImageButtonMiniSkipNext = (ImageButton) view.findViewById(R.id.image_button_mini_skip_next);

        mTitle = (TextView)view.findViewById(R.id.text_view_mini_episode_title);
        mImageViewMiniEpisode = (ImageView)view.findViewById(R.id.image_view_mini_episode);

        mImageButtonPause.setOnClickListener(this);
        mImageButtonSkipNext.setOnClickListener(this);
        mImageButtonSkipPreviuos.setOnClickListener(this);
        mImageButtonMiniPlayPause.setOnClickListener(this);
        mImageButtonMiniSkipNext.setOnClickListener(this);

        mImageLoader = new ImageLoader(getActivity());

    }

    private void updateInformation(Episode episode){
        mTitle.setText(episode.getTitle());
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


}
