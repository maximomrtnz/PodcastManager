package maximomrtnz.podcastmanager.ui.fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.ui.adapters.EpisodesRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.adapters.PodcastRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.Constants;

/**
 * Created by maximo on 10/10/16.
 */

public class PlayQueueFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener {



    private List<Episode> mEpisodes;
    private RecyclerView mRecyclerView;
    private EpisodesRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_playqueue, container, false);

        mEpisodes = new ArrayList<>();

        loadUIComponents(v);

        setToolbar(v);

        loadPlayQueue();

        return v;

    }

    private void loadPlayQueue() {

        mProgressBar.setVisibility(View.VISIBLE);

        // Load Podcast Subscribed
        getActivity().getLoaderManager().initLoader(Constants.LOADER.PLAY_QUEUE_LOADER, null, this);

    }

    /*
    * Callback that's invoked when the system has initialized the Loader and
    * is ready to start the query. This usually happens when initLoader() is
    * called. The loaderID argument contains the ID value passed to the
    * initLoader() call.
    */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        // Check Loader Id

        switch (loaderID) {

            case Constants.LOADER.PLAY_QUEUE_LOADER:

                // Set columns to retrieve
                String[] projection = PodcastManagerContract.Episode.PROJECTION_ALL;

                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        PodcastManagerContentProvider.EPISODE_CONTENT_URI, // Table to query
                        projection,      // Projection to return
                        PodcastManagerContract.Episode.COLUMN_NAME_FLAG_ON_PLAY_QUEUE+"=?",
                        new String[]{"1"},            // No selection arguments
                        PodcastManagerContract.Episode.SORT_ORDER_ON_PLAY_QUEUE_TIMESTAMP_DESC
                );

            default:
                break;

        }

        return null;
    }

    /*
     * Defines the callback that CursorLoader calls
     * when it's finished its query
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
        mProgressBar.setVisibility(View.GONE);
    }

    /*
     * Defines the callback that CursorLoader calls
     * when it's finished its query
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void loadUIComponents(View view) {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EpisodesRecyclerViewAdapter(getContext(), this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

    }

    @Override
    public void onRecyclerViewListClicked(View v, int position) {

        Episode episode = (Episode) v.getTag();

        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).playEpisode(episode);
        }

    }


}
