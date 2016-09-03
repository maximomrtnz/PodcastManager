package maximomrtnz.podcastmanager.ui.activities;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.asynktasks.DeletePodcast;
import maximomrtnz.podcastmanager.asynktasks.InsertPodcast;
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
import maximomrtnz.podcastmanager.ui.adapters.EpisodesRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.dialogs.ConfirmDialog;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.JsonUtil;
import maximomrtnz.podcastmanager.utils.Utils;
import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by maximo on 26/07/16.
 */

public class PodcastActivity extends BaseActivity implements FeedLoader.FeedLoaderListener, RecyclerViewClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static String LOG_TAG = "PodcastActivity";

    // Identifies a particular Loader being used in this component
    private static final int PODCAST_LOADER_BY_FEED_URL = 0;
    private static final int EPISODES_LOADER = 1;

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
    private FabButton mFloatingActionButton;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastReceiver mSynchronizeServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();

            if(bundle.getInt(Constants.SYNCHRONIZE_SERVICE.RESULT)==RESULT_OK){
                mSwipeRefreshLayout.setRefreshing(false);
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_podcast);

        mEpisodes = new ArrayList<>();

        mFeedLoader = new FeedLoader(getApplicationContext(), this);

        mImageLoader = new ImageLoader(getApplicationContext(), new ImageLoader.ImageLoadeListener() {

            @Override
            public void onImageLoader(Bitmap bitmap) {
                extractPaletteColors(bitmap);
            }

        });

        loadPodcast();

        loadUI();

    }

    @Override
    public void loadUI(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        // Enabling Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbarLayout);
        mCollapsingToolbarLayout.setTitle(mPodcast.getTitle());

        // Get Image View from Layout
        mImageViewPodcast = (ImageView)findViewById(R.id.image_view_podcast);

        // Set Image Url
        mImageLoader.displayImage(mPodcast.getImageUrl(),mImageViewPodcast);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EpisodesRecyclerViewAdapter(mEpisodes, getApplicationContext(), this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        // Get Floating Button from Layout
        mFloatingActionButton = (FabButton) findViewById(R.id.floating_action_button);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingActionButtonPressed();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

    }

    private void loadSubscribedButton(){

        // Set visibility
        mFloatingActionButton.setVisibility(View.VISIBLE);

        if(mPodcast.getId()==null) {

            // Set icons
            mFloatingActionButton.setIcon(R.drawable.ic_add, R.drawable.ic_fab_complete);

        }else{

            // Set icons
            mFloatingActionButton.setIcon(R.drawable.ic_fab_complete,R.drawable.ic_add);

        }

    }


    private void loadPodcast(){

        // GetPodcast Information from caller activity
        Intent intent = getIntent();

        // Set podcast's data into Podcast Object
        mPodcast = JsonUtil.getInstance().fromJson(intent.getStringExtra("podcast"),Podcast.class);

        // If we have and Id, we have to get the episodes from database
        if(mPodcast.getId()!=null){

            Log.d(LOG_TAG, "Load Button");

            Log.d(LOG_TAG,"Subscribed Podcast");

            // Init loader to check if we have a mPodcast in the DB
            getLoaderManager().initLoader(EPISODES_LOADER,null,this);

        }else { // We have to check if the Podcast are in the DB from the feed url

            // We have to check if the selected podcast has an feed url from itunes
            if (mPodcast.getFeedUrl().indexOf("itunes") != -1) { // From TopPodcast

                // Call ItunesAPI to get feed URL for downloading Podcast Episodies
                new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onSuccess(Object arg) {

                        Log.d(LOG_TAG, "URL Feed -->" + String.valueOf(arg));

                        // Set feed url
                        mPodcast.setFeedUrl(String.valueOf(arg));

                        // Init loader to check if we have a mPodcast in the DB
                        getLoaderManager().initLoader(PODCAST_LOADER_BY_FEED_URL, null, PodcastActivity.this);

                    }
                }).getUrlFeed(mPodcast.getFeedUrl());

            } else { // From Search or Subscriptions

                // Init loader to check if we have a mPodcast in the DB
                getLoaderManager().initLoader(PODCAST_LOADER_BY_FEED_URL, null, this);

            }

        }

    }


    @Override
    public void onFeedLoader(Podcast podcast) {

        Log.d(LOG_TAG,"Podcast Loaded -->"+podcast.getEpisodes().size());

        // Update Podcast Information
        mPodcast = podcast;

        // Load Episodes List
        mEpisodes.clear();
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

        Log.d(LOG_TAG, episode.getTitle());

        Intent i = new Intent(getApplicationContext(), AudioPlayerActivity.class);

        i.putExtra("episode",JsonUtil.getInstance().toJson(episode));

        startActivity(i);

        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return true;

            case R.id.action_refresh:

                refreshItems();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void onFloatingActionButtonPressed(){

        // Check if we have to subscribe or if we have to unsuscribe
        if(mPodcast.getId() == null){ // Subscribe

            mFloatingActionButton.showProgress(true);

            new InsertPodcast(this, new InsertPodcast.InsertPodcastListener() {

                @Override
                public void onPodcastInserted(List<Uri> podcastUris) {

                    if(!podcastUris.isEmpty()) {

                        Uri podcastUri = podcastUris.get(0);

                        Log.d(LOG_TAG, "New Podcast Subscription -->" + podcastUri.toString());

                        // Set Podcast Id
                        mPodcast.setId(Long.valueOf(podcastUri.getLastPathSegment()));

                        mFloatingActionButton.showProgress(false);
                        mFloatingActionButton.onProgressCompleted();

                        // switch icons
                        loadSubscribedButton();

                        Utils.scheduleTask(PodcastActivity.this, Constants.SYNCHRONIZE_SERVICE.REPEAT_TIME);

                    }

                }
            }).execute(mPodcast);

        }else{ // Unsuscribe

            ConfirmDialog dialog = new ConfirmDialog ();

            dialog.setArgs(new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onConfirm() {

                    // Show progress dialog bar
                    mFloatingActionButton.showProgress(true);

                    new DeletePodcast(PodcastActivity.this, new DeletePodcast.DeletePodcastListener() {
                        @Override
                        public void onPodcastDeleted(int deletedRows) {

                            Log.d(LOG_TAG,"Delete Rows -->"+deletedRows);

                            // Delete id
                            mPodcast.setId(null);

                            // Set FAB Button to finish
                            mFloatingActionButton.showProgress(false);
                            mFloatingActionButton.onProgressCompleted();

                            // switch icons
                            loadSubscribedButton();

                        }
                    }).execute(mPodcast);
                }

                @Override
                public void onCancel() {

                }
            }, getString(R.string.dialog_title_confirm_deletion), MessageFormat.format(getResources().getString(R.string.dialog_message_confirm_deletion),new Object[]{mPodcast.getTitle()}).toString(), getString(R.string.dialog_ok), getString(R.string.dialog_cancel));

            // Show confirm dialog
            showDialogFragment(dialog);

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

        switch (loaderID) {

            case PODCAST_LOADER_BY_FEED_URL:

                Log.d(LOG_TAG, "Created Podcast Loader");

                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        PodcastManagerContentProvider.PODCAST_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Podcast.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL+"=?",   // Selection clause
                        new String[]{mPodcast.getFeedUrl()},                        // Selection arguments
                        null                                                        // Default sort order
                );

            case EPISODES_LOADER:

                Log.d(LOG_TAG, "Created Episodes Loader");

                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
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

            case PODCAST_LOADER_BY_FEED_URL:

                Log.d(LOG_TAG, "Load Podcast by Feed Url");

                // if we have a row
                if(cursor.moveToFirst()){

                    // Get cursor and load podcast
                    mPodcast = new PodcastConverter().loadFrom(cursor);

                        // Load Podcast Episodes from Database
                    getLoaderManager().initLoader(EPISODES_LOADER, null, this);

                }else{ // We don't have a record so we are no subscribed to the current podcast

                    Log.d(LOG_TAG, "Load from Feed");

                    // Let's download from XML Feed
                    mFeedLoader.loadFeed(mPodcast.getFeedUrl(),false);

                }

                // Unsuscribe
                getLoaderManager().destroyLoader(PODCAST_LOADER_BY_FEED_URL);

                break;

            case EPISODES_LOADER:

               if(cursor!=null && cursor.getCount()>0) {

                    Log.d(LOG_TAG, "Load from DB");

                    // Load episodes
                    mEpisodes.clear();

                    while (cursor.moveToNext()) {
                        Episode episode = new EpisodeConverter().loadFrom(cursor);
                        mEpisodes.add(episode);
                    }

                    mPodcast.setEpisodes(mEpisodes);

                    mAdapter.notifyDataSetChanged();

                    // Load button subscribe/unsubscribe
                    loadSubscribedButton();

                    // hide progressbar reciclerview
                    mProgressBar.setVisibility(View.GONE);

                }

                break;

            default:

                break;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_podcast_activity, menu);
        return true;
    }

    private void refreshItems(){

        Log.d(LOG_TAG, "REFRESHING ITEMS-->"+mPodcast.getId());

        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(true);
        }

        if(mPodcast.getId() != null){

            //Call synchronizeservice
            Intent i = new Intent(this, SynchronizeService.class);

            // Pass only the data that we need to avoid hit the 1MB limit
            Podcast podcast = new Podcast();

            podcast.setId(mPodcast.getId());
            podcast.setFeedUrl(mPodcast.getFeedUrl());

            i.putExtra("podcast",JsonUtil.getInstance().toJson(podcast));

            startService(i);

        }else{

            // Load again from XML Feed
            mFeedLoader.loadFeed(mPodcast.getFeedUrl(),true);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSynchronizeServiceReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mSynchronizeServiceReceiver, new IntentFilter(Constants.SYNCHRONIZE_SERVICE.NOTIFICATION));
    }

    @Override
    public void setPrimaryColor(int color) {

        super.setPrimaryColor(color);

        if(mCollapsingToolbarLayout!=null) {
            mCollapsingToolbarLayout.setStatusBarScrimColor(color);
            mCollapsingToolbarLayout.setContentScrimColor(color);
        }

    }
}
