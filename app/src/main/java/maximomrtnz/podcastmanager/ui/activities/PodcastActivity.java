package maximomrtnz.podcastmanager.ui.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.asynktasks.DeletePodcast;
import maximomrtnz.podcastmanager.asynktasks.InsertPodcast;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.network.ItunesAppleAPI;
import maximomrtnz.podcastmanager.ui.adapters.EpisodesRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by maximo on 26/07/16.
 */

public class PodcastActivity extends AppCompatActivity implements FeedLoader.FeedLoaderListener, RecyclerViewClickListener, LoaderManager.LoaderCallbacks<Cursor>{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_podcast);

        mEpisodes = new ArrayList<>();

        mFeedLoader = new FeedLoader(getApplicationContext(), this);

        mImageLoader = new ImageLoader(getApplicationContext());

        loadPodcast();

        loadUI();

    }


    private void loadUI(){

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

        mAdapter = new EpisodesRecyclerViewAdapter(mEpisodes, mPodcast, getApplicationContext(), this);

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
        mPodcast = new Podcast();
        mPodcast.loadFrom(intent);

        // If we have and Id, we have to get the episodes from database
        if(mPodcast.getId()!=null){

            Log.d(LOG_TAG, "Load Button");

            Log.d(LOG_TAG,"Subscribed Podcast");

            // Init loader to check if we have a mPodcast in the DB
            getLoaderManager().initLoader(EPISODES_LOADER,null,this);

        }else{ // We have to check if the Podcast are in the DB from the feed url

            // We have to check if the selected podcast has an feed url from itunes
            if(mPodcast.getFeedUrl().indexOf("itunes")!=-1){ // From TopPodcast

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
                        getLoaderManager().initLoader(PODCAST_LOADER_BY_FEED_URL,null, PodcastActivity.this);

                    }
                }).getUrlFeed(mPodcast.getFeedUrl());

            }else{ // From Search

                // Init loader to check if we have a mPodcast in the DB
                getLoaderManager().initLoader(PODCAST_LOADER_BY_FEED_URL,null,this);

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
        mProgressBar.setVisibility(View.GONE);


    }


    @Override
    public void onRecyclerViewListClicked(View v, int position) {
        Episode episode = (Episode)v.getTag();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void onFloatingActionButtonPressed(){

        mFloatingActionButton.showProgress(true);

        // Check if we have to subscribe or if we have to unsuscribe
        if(mPodcast.getId() == null){ // Subscribe

            new InsertPodcast(this, new InsertPodcast.InsertPodcastListener() {
                @Override
                public void onPodcastInserted(Uri mNewPodcastUri) {

                    Log.d(LOG_TAG,"New Podcast Subscription -->"+mNewPodcastUri.toString());

                    // Get Podcast Database Id
                    long podcastId = Long.valueOf(mNewPodcastUri.getLastPathSegment());

                    mPodcast.setId(podcastId);

                    mFloatingActionButton.onProgressCompleted();

                }
            }).execute(mPodcast);

        }else{ // Unsuscribe

            new DeletePodcast(this, new DeletePodcast.DeletePodcastListener() {
                @Override
                public void onPodcastDeleted(int deletedRows) {

                    Log.d(LOG_TAG,"Delete Rows -->"+deletedRows);

                    // Remove Id
                    mPodcast.setId(null);

                    // Set FAB Button to finish
                    mFloatingActionButton.showProgress(false);
                    mFloatingActionButton.onProgressCompleted();


                }
            }).execute(mPodcast);

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

        switch (loaderID) {

            case PODCAST_LOADER_BY_FEED_URL:

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

                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        PodcastManagerContentProvider.EPISODE_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Episode.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID+"=?", // No selection clause
                        new String[]{String.valueOf(mPodcast.getId())},             // No selection arguments
                        null                                                        // Default sort order
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
                    mPodcast.loadFrom(cursor);

                    // Load Podcast Episodes from Database
                    getLoaderManager().initLoader(EPISODES_LOADER,null,this);

                }else{ // We don't have a record so we are no subscribed to the current podcast

                    Log.d(LOG_TAG, "Load from Feed");

                    // Let's download from XML Feed
                    mFeedLoader.loadFeed(mPodcast.getFeedUrl());

                }

                break;

            case EPISODES_LOADER:

                Log.d(LOG_TAG, "Load from DB");

                // Load Episodes from Database
                ((EpisodesRecyclerViewAdapter)mAdapter).setCursor(cursor);

                Log.d(LOG_TAG, "Load Button");

                // Load button subscribe/unsubscribe
                loadSubscribedButton();

                // hide progressbar reciclerview
                mProgressBar.setVisibility(View.GONE);

                break;

            default:

                break;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()) {

            case PODCAST_LOADER_BY_FEED_URL:

                break;

            case EPISODES_LOADER:

                Log.d(LOG_TAG, "Reset Episodes Loader");

                // Load Episodes from Database
                ((EpisodesRecyclerViewAdapter)mAdapter).setCursor(null);

                break;

            default:

                break;

        }

    }
}
