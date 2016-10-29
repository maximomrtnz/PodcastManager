package maximomrtnz.podcastmanager.ui.fragments;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.network.ItunesAppleAPI;
import maximomrtnz.podcastmanager.services.SynchronizeService;
import maximomrtnz.podcastmanager.ui.activities.BaseActivity;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.ui.adapters.EpisodesRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.dialogs.ConfirmDialog;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.JsonUtil;

/**
 * Created by maximo on 26/07/16.
 */

public class PodcastFragment extends BaseFragment implements FeedLoader.FeedLoaderListener, RecyclerViewClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static String LOG_TAG = "PodcastFragment";

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Podcast mPodcast;
    private ImageView mImageViewPodcast;
    private ImageLoader mImageLoader;
    private FeedLoader mFeedLoader;
    private List<Episode> mEpisodes;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mFloatingActionButton;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Map<String,Episode> mEpisodesByUrl = new HashMap<>();


    private BroadcastReceiver mSynchronizeServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();

            if(bundle.getInt(Constants.SYNCHRONIZE_SERVICE.RESULT)== Activity.RESULT_OK){

                mSwipeRefreshLayout.setRefreshing(false);

                // Let's download from XML Feed
                mFeedLoader.loadFeed(mPodcast.getFeedUrl(), false);

            }

        }

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_podcast, container, false);

        mEpisodes = new ArrayList<>();

        mFeedLoader = new FeedLoader(getContext(), this);

        mImageLoader = new ImageLoader(getContext());


        loadUIComponents(v);

        setToolbar(v);

        loadPodcast();

        return v;

    }


    @Override
    public void loadUIComponents(View view){

        // Get Image View from Layout
        mImageViewPodcast = (ImageView)view.findViewById(R.id.image_view_podcast);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EpisodesRecyclerViewAdapter(mEpisodes, getContext(), this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)view.findViewById(R.id.progress_bar);

        // Get Floating Button from Layout
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingActionButtonPressed();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

    }

    private void loadSubscribedButton() {

        // Set visibility
        mFloatingActionButton.setVisibility(View.VISIBLE);

        // Set icons
        mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_add));

        if(mPodcast.getSubscribed()!=null) {

            if(mPodcast.getSubscribed()) {
                mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_check));
            }

        }

    }


    private void loadPodcast(){

        // Init Proggress Bar
        if(mProgressBar.getVisibility()==View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        // Set podcast's data into Podcast Object
        mPodcast = JsonUtil.getInstance().fromJson(getArguments().getString("podcast"),Podcast.class);

        Log.d(LOG_TAG, getArguments().getString("podcast"));

        if(isAdded()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mPodcast.getTitle());
        }

        // Set Image Url
        mImageLoader.displayImage(mPodcast.getImageUrl(),mImageViewPodcast);

        mImageLoader.loadAsync(mPodcast.getImageUrl(), new ImageLoader.ImageLoadeListener() {
            @Override
            public void onImageLoader(Bitmap bitmap) {
                extractPaletteColors(bitmap);
            }
        });

        // If we have and Id, we have to get the episodes from database
        if(mPodcast.getId()!=null){

            // Init loader to check if we have a mPodcast in the DB
            getLoaderManager().initLoader(Constants.LOADER.EPISODES_LOADER,null,this);

        }

        // We have to check if the selected podcast has an feed url from itunes
        if (mPodcast.getFeedUrl().indexOf("itunes") != -1) { // From TopPodcast

            // Call ItunesAPI to get feed URL for downloading Podcast Episodies
            new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onSuccess(Object arg) {

                    // Set feed url
                    mPodcast.setFeedUrl(String.valueOf(arg));

                    // Init loader to check if we have a mPodcast in the DB
                    getLoaderManager().initLoader(Constants.LOADER.PODCAST_LOADER_BY_FEED_URL, null, PodcastFragment.this);

                }
            }).getUrlFeed(mPodcast.getFeedUrl());

        } else { // From Search or Subscriptions

            // Init loader to check if we have a mPodcast in the DB
            getLoaderManager().initLoader(Constants.LOADER.PODCAST_LOADER_BY_FEED_URL, null, this);

        }

    }


    @Override
    public void onFeedLoader(Podcast podcast) {

        if(!isAdded()){
            return;
        }

        // Load Episodes List
        mEpisodes.clear();

        for(Episode episode : podcast.getEpisodes()){

            if(mEpisodesByUrl.containsKey(episode.getEpisodeUrl())){

                // Get Episode From DB
                Episode episodeFromDB = mEpisodesByUrl.get(episode.getEpisodeUrl());

                // Set stored Data From stored Episode
                episode.setPlayed(episodeFromDB.getPlayed());

            }

        }

        Log.d(LOG_TAG, podcast.getEpisodes().size()+"");

        mEpisodes.addAll(podcast.getEpisodes());
        mAdapter.notifyDataSetChanged();

        // Load button subscribe/unsubscribe
        loadSubscribedButton();

        // hide progressbar reciclerview
        if(mProgressBar.getVisibility()==View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }

        if(mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }


    @Override
    public void onRecyclerViewListClicked(View v, int position) {

        Episode episode = (Episode)v.getTag();

        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).playEpisode(mPodcast,episode);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                //finish(); // close this activity and return to preview activity (if there is any)
                //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return true;

            case R.id.action_refresh:

                refreshItems();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void onFloatingActionButtonPressed(){

        if(mPodcast.getSubscribed()==null){
            mPodcast.setSubscribed(false);
        }

        // Check if we have to subscribe or if we have to unsuscribe
        if(mPodcast.getSubscribed()==true){ // Unsubscribe

            ConfirmDialog dialog = new ConfirmDialog ();

            dialog.setArgs(new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onConfirm() {

                    mPodcast.setSubscribed(false);

                    // Insert/Update into Database
                    getContext().getContentResolver().insert(
                            PodcastManagerContentProvider.PODCAST_CONTENT_URI,
                            new PodcastConverter().loadToContentValue(mPodcast)
                    );

                    mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_add));

                }

                @Override
                public void onCancel() {

                }
            }, getString(R.string.dialog_title_confirm_deletion), MessageFormat.format(getResources().getString(R.string.dialog_message_confirm_deletion),new Object[]{mPodcast.getTitle()}).toString(), getString(R.string.dialog_ok), getString(R.string.dialog_cancel));

            // Show confirm dialog
            ((BaseActivity)getActivity()).showDialogFragment(dialog);

        }else{ // Subscribe

            mPodcast.setSubscribed(true);

            // Insert/Update into Database
            Uri newPodcastUri = getContext().getContentResolver().insert(
                    PodcastManagerContentProvider.PODCAST_CONTENT_URI,
                    new PodcastConverter().loadToContentValue(mPodcast)
            );

            Long id = ContentProviderUtils.getIdFromUri(newPodcastUri);

            if(id!=0){
                mPodcast.setId(id);
            }

            mPodcast.setEpisodesCount(mEpisodes.size());
            mPodcast.setLastModifiedDate(Calendar.getInstance());

            mFloatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fab_check));

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

        switch (loaderID) {

            case Constants.LOADER.PODCAST_LOADER_BY_FEED_URL:

                Log.d(LOG_TAG, "Created Podcast Loader");

                // Returns a new CursorLoader
                return new CursorLoader(
                        getContext(),   // Parent activity context
                        PodcastManagerContentProvider.PODCAST_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Podcast.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL+"=?",   // Selection clause
                        new String[]{mPodcast.getFeedUrl()},                        // Selection arguments
                        null                                                        // Default sort order
                );

            case Constants.LOADER.EPISODES_LOADER:

                Log.d(LOG_TAG, "Created Episodes Loader");

                // Returns a new CursorLoader
                return new CursorLoader(
                        getContext(),   // Parent activity context
                        PodcastManagerContentProvider.EPISODE_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Episode.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID+"=?", // No selection clause
                        new String[]{String.valueOf(mPodcast.getId())},             // No selection arguments
                        PodcastManagerContract.Episode.SORT_ORDER                                                        // Default sort order
                );

            default:
                break;

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {

            case Constants.LOADER.PODCAST_LOADER_BY_FEED_URL:

                // if we have a row
                if(cursor.moveToFirst()){

                    // Get cursor and load podcast
                    mPodcast = new PodcastConverter().loadFrom(cursor);

                    // Load Podcast Episodes from Database
                    getLoaderManager().initLoader(Constants.LOADER.EPISODES_LOADER, null, this);

                }else {

                    // Let's download from XML Feed
                    mFeedLoader.loadFeed(mPodcast.getFeedUrl(), false);

                }

                // Unsuscribe
                //getLoaderManager().destroyLoader(PODCAST_LOADER_BY_FEED_URL);

                break;

            case Constants.LOADER.EPISODES_LOADER:

               if(cursor!=null && cursor.getCount()>0) {

                   mEpisodesByUrl.clear();

                   while (cursor.moveToNext()) {
                       Episode episode = new EpisodeConverter().loadFrom(cursor);
                       mEpisodesByUrl.put(episode.getEpisodeUrl(),episode);
                   }

                }

                // Let's download from XML Feed
                mFeedLoader.loadFeed(mPodcast.getFeedUrl(), false);

                break;

            default:

                break;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void refreshItems(){

        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }

        if(mPodcast.getId() != null){

            //Call synchronizeservice
            Intent i = new Intent(getContext(), SynchronizeService.class);

            // Pass only the data that we need to avoid hit the 1MB limit
            Podcast podcast = new Podcast();

            podcast.setId(mPodcast.getId());
            podcast.setFeedUrl(mPodcast.getFeedUrl());

            i.putExtra("podcast",JsonUtil.getInstance().toJson(podcast));

            getActivity().startService(i);

        }else{

            // Load again from XML Feed
            mFeedLoader.loadFeed(mPodcast.getFeedUrl(),true);

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(mSynchronizeServiceReceiver);

        // Set New Episodes in 0 because we saw new episodes every time we opened this fragment

        if(mPodcast.getNewEpisodesAdded() == null){
            return;
        }

        if(mPodcast.getNewEpisodesAdded()==0){
            return;
        }

        mPodcast.setNewEpisodesAdded(0);

        // Save into DB

        // Insert/Update into Database
        getContext().getContentResolver().insert(
                PodcastManagerContentProvider.PODCAST_CONTENT_URI,
                new PodcastConverter().loadToContentValue(mPodcast)
        );

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mSynchronizeServiceReceiver, new IntentFilter(Constants.SYNCHRONIZE_SERVICE.NOTIFICATION));
    }

    @Override
    public void setPrimaryColor(int color) {

        super.setPrimaryColor(color);

        if(mCollapsingToolbarLayout!=null) {
            mCollapsingToolbarLayout.setStatusBarScrimColor(color);
            mCollapsingToolbarLayout.setContentScrimColor(color);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.menu_podcast_fragment, menu);

    }



}
