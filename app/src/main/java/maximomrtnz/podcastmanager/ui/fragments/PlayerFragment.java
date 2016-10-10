package maximomrtnz.podcastmanager.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.services.PlayerService;
import maximomrtnz.podcastmanager.utils.JsonUtil;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_player,container,false);

        loadUIComponents(v);

        return v;
    }

    @Override
    public void onClick(View target) {

        // Send the correct intent to the MusicService, according to the button that was clicked
        if (target == mImageButtonPause) {
            doAction(PlayerService.ACTION_PLAY);
        }else if (target == mImageButtonPause) {
            doAction(PlayerService.ACTION_PAUSE);
        }else if (target == mImageButtonSkipNext) {
            doAction(PlayerService.ACTION_SKIP);
        }else if (target == mImageButtonSkipPreviuos) {
            doAction(PlayerService.ACTION_REWIND);
        }else if (target == mImageButtonStop) {
            doAction(PlayerService.ACTION_STOP);
        }

    }

    @Override
    public void loadUIComponents(View view) {

        mImageButtonPause = (ImageButton) view.findViewById(R.id.image_button_pause);
        mImageButtonSkipPreviuos = (ImageButton) view.findViewById(R.id.image_button_skip_previous);
        mImageButtonSkipNext = (ImageButton) view.findViewById(R.id.image_button_skip_next);

        mImageButtonPause.setOnClickListener(this);
        mImageButtonSkipNext.setOnClickListener(this);
        mImageButtonSkipPreviuos.setOnClickListener(this);

    }

    public void play(Episode episode){
        // Send an intent with the episode to play. This is expected by
        // PlayerService.
        Intent i = new Intent(getContext(),PlayerService.class);
        i.setAction(PlayerService.ACTION_URL);
        i.putExtra("Episode", JsonUtil.getInstance().toJson(episode));

        Log.d(LOG_TAG,JsonUtil.getInstance().toJson(episode));

        getActivity().startService(i);
    }

    public void doAction(String action){
        Intent i = new Intent(getContext(),PlayerService.class);
        i.setAction(action);
        getActivity().startService(i);
    }

}
