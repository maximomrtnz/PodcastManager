package maximomrtnz.podcastmanager.ui.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.ui.adapters.PodcastRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.JsonUtil;

/**
 * Created by maximo on 17/06/16.
 */

public class SubscriptionsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener{

    // Identifies a particular Loader being used in this component
    private static final int PODCAST_SUBSCRIBED_LOADER = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_subscriptions,container,false);

        loadUIComponents(v);

        setToolbar(v);

        loadPodcastsList();

        return v;
    }

    private void loadPodcastsList() {

        mProgressBar.setVisibility(View.VISIBLE);

        // Load Podcast Subscribed
        getActivity().getLoaderManager().initLoader(PODCAST_SUBSCRIBED_LOADER, null, this);

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

            case PODCAST_SUBSCRIBED_LOADER:

                // Set columns to retrieve
                String[] projection = PodcastManagerContract.Podcast.PROJECTION_ALL;

                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        PodcastManagerContentProvider.PODCAST_CONTENT_URI, // Table to query
                        projection,      // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
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
        ((PodcastRecyclerViewAdapter)mAdapter).setCursor(data);
        mProgressBar.setVisibility(View.GONE);
    }

    /*
     * Defines the callback that CursorLoader calls
     * when it's finished its query
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((PodcastRecyclerViewAdapter)mAdapter).setCursor(null);
    }

    @Override
    public void loadUIComponents(View view) {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PodcastRecyclerViewAdapter(getContext(), this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

    }

    @Override
    public void onRecyclerViewListClicked(View v, int position) {

        Podcast podcast = (Podcast)v.getTag();

        if(mActivity!=null) {
            mActivity.showPodcast(podcast);
        }

    }

}
