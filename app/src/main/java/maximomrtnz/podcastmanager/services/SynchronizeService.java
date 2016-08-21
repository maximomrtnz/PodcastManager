package maximomrtnz.podcastmanager.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.NotificationHelper;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 11/08/16.
 */

public class SynchronizeService extends IntentService{

    private static String LOG_TAG = "SynchronizeService";

    private FeedLoader mFeedLoader;

    public SynchronizeService(){
        super(LOG_TAG);
        mFeedLoader = new FeedLoader(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(LOG_TAG, "Running SynchronizeService");

        // Podcast List
        List<Podcast> podcasts = new ArrayList<>();

        // Episodes List to Upsert
        ArrayList<ContentProviderOperation> episodesToUpsert = new ArrayList<>();

        // Podcast List to Upsert
        ArrayList<ContentProviderOperation> podcastsToUpsert = new ArrayList<>();

        Cursor cursor = null;

        try {
            // Get Podcast from Database
            cursor = getContentResolver().query(
                    PodcastManagerContentProvider.PODCAST_CONTENT_URI,          // Table to query
                    PodcastManagerContract.Podcast.PROJECTION_ALL,              // Projection to return
                    null,                                                       // Selection clause
                    null,                                                       // Selection arguments
                    null                                                        // Default sort order
            );

            // Check if the cursor is empty or not
            if (cursor != null) {

                while(cursor.moveToNext()) {

                    // Instantiate a Podcast Object
                    Podcast podcast = new Podcast();

                    // Load Podcast from Cursor
                    podcast.loadFrom(cursor);

                    // Add to Podcast List
                    podcasts.add(podcast);

                }

            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        Log.d(LOG_TAG, "Podcast to Synchnize -->"+podcasts.size());

        if(podcasts.isEmpty()){
            return; // Finish services
        }

        // Clear old XML files
        mFeedLoader.clearCache();

        // Then we need to get the feed's url saved on each podcast and download and parse its episodes
        for(Podcast podcast : podcasts){

            Podcast tempPodcast = mFeedLoader.getFeed(podcast.getFeedUrl());

            // We need to check if pub date or lastbuild date have changed
            // in order to avoid unnecessary db operations
            if(tempPodcast.getPubDate()!=podcast.getPubDate() || tempPodcast.getLastBuildDate()!=podcast.getLastBuildDate()) {

                podcastsToUpsert.add(ContentProviderUtils.toContentProviderOperation(tempPodcast));

                episodesToUpsert.addAll(ContentProviderUtils.toContentProviderOperation(tempPodcast.getEpisodes(), podcast.getId()));

            }

        }


        //If we have episodes to upsert
        if(!episodesToUpsert.isEmpty()) {

            ContentProviderResult[] results = null;

            try {
                results = getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY, episodesToUpsert);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            } catch (OperationApplicationException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }

            if(results!=null){

                Log.d(LOG_TAG, "Showing Notification");

                // Show user notifications
                new NotificationHelper(this)
                        .setIcon(R.drawable.ic_new)
                        .setIntent(new Intent(this, MainActivity.class))
                        .setAutoCancel(true)
                        .setTitle(getString(R.string.app_name))
                        .show(0);

            }

        }



    }


}
