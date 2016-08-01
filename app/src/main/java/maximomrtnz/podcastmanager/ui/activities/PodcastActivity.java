package maximomrtnz.podcastmanager.ui.activities;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.asynktasks.DeletePodcast;
import maximomrtnz.podcastmanager.asynktasks.InsertPodcast;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Enclosure;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.network.ItunesAppleAPI;
import maximomrtnz.podcastmanager.ui.adapters.EpisodesRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.adapters.PodcastRecyclerViewAdapter;
import maximomrtnz.podcastmanager.ui.listeners.RecyclerViewClickListener;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 26/07/16.
 */

public class PodcastActivity extends AppCompatActivity implements FeedLoader.FeedLoaderListener, RecyclerViewClickListener{

    private static String LOG_TAG = "PodcastActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_podcast);

        mEpisodes = new ArrayList<>();

        mFeedLoader = new FeedLoader(getApplicationContext(), this);

        loadPodcast();

        mImageLoader = new ImageLoader(getApplicationContext());

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

        mAdapter = new EpisodesRecyclerViewAdapter(mEpisodes, mPodcast, getApplicationContext(), (RecyclerViewClickListener) this);

        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        // Get Floating Button from Layout
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.floating_action_button);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFloatingActionButtonPressed();
            }
        });


        // Get XML From URL
        loadPodcastItems();

    }

    private void loadPodcast(){
        // GetPodcast Information from caller activity
        Intent intent = getIntent();

        // Set podcast's data into Podcast Object
        mPodcast = new Podcast();
        mPodcast.setTitle(intent.getStringExtra("title"));
        mPodcast.setFeedUrl(intent.getStringExtra("feedUrl"));
        mPodcast.setImageUrl(intent.getStringExtra("imageUrl"));
    }

    private void loadPodcastItems(){

        mProgressBar.setVisibility(View.VISIBLE);

        // Get URL to download Podcast Episodies
        // Get Podcast from Itunes API
        new ItunesAppleAPI(new ItunesAppleAPI.ItunesAppleAPIListener() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onSuccess(Object arg) {
                Log.d(LOG_TAG, "URL Feed -->"+String.valueOf(arg));
                mFeedLoader.loadFeed(String.valueOf(arg));
            }
        }).getUrlFeed(mPodcast.getFeedUrl());

    }


    @Override
    public void onFeedLoader(Podcast podcast) {

        Log.d(LOG_TAG,"Podcast Loaded -->"+podcast.getEpisodes().size());

        // Set Podcast Information
        mPodcast = podcast;

        // Load Episodes List
        mEpisodes.clear();
        mEpisodes.addAll(podcast.getEpisodes());
        mAdapter.notifyDataSetChanged();

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

        // Check if we have to subscribe or if we have to unsuscribe
        if(mPodcast.getId() == null){ // Subscribe

            new InsertPodcast(getApplicationContext(), new InsertPodcast.InsertPodcastListener() {
                @Override
                public void onPodcastInserted(Uri mNewPodcastUri) {

                    Log.d(LOG_TAG,"New Podcast Subscription -->"+mNewPodcastUri.toString());

                    // Get Podcast Database Id
                    long podcastId = Long.valueOf(mNewPodcastUri.getLastPathSegment());

                    mPodcast.setId(podcastId);

                    mFloatingActionButton.setImageResource(R.drawable.ic_check);
                    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSubscribed)));

                }
            }).execute(mPodcast);

        }else{ // Unsuscribe

            new DeletePodcast(getApplicationContext(), new DeletePodcast.DeletePodcastListener() {
                @Override
                public void onPodcastDeleted(int deletedRows) {

                    Log.d(LOG_TAG,"Delete Rows -->"+deletedRows);

                    // Change Button Look and feel
                    mFloatingActionButton.setImageResource(R.drawable.ic_favorite_border);
                    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                    // Remove Id
                    mPodcast.setId(null);

                }
            }).execute(mPodcast);

        }

    }

}
