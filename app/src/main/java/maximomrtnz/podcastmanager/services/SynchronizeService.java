package maximomrtnz.podcastmanager.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.FeedLoader;
import maximomrtnz.podcastmanager.database.Converter;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.ui.activities.MainActivity;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.DateUtils;
import maximomrtnz.podcastmanager.utils.JsonUtil;
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

        Converter podcastConverter = new PodcastConverter();

        Integer episodesAdded = 0;

        // Podcast List
        List<Podcast> podcasts = new ArrayList<>();

        // Podcast List to Upsert
        ArrayList<ContentProviderOperation> podcastsToUpsert = new ArrayList<>();

        ContentProviderResult[] results = null;

        Cursor cursor = null;

        try {

            // Check if intent has extras
            if(intent.getExtras()==null) {

                // Get Podcast from Database
                cursor = getContentResolver().query(
                        PodcastManagerContentProvider.PODCAST_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Podcast.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Podcast.COLUMN_NAME_FLAG_SUBSCRIBED+"=?",
                        new String[]{"1"},            // No selection arguments
                        null                                                        // Default sort order
                );

            }else{

                Podcast podcast = JsonUtil.getInstance().fromJson(intent.getStringExtra("podcast"),Podcast.class);

                // Get Podcast from Database
                cursor = getContentResolver().query(
                        PodcastManagerContentProvider.PODCAST_CONTENT_URI,          // Table to query
                        PodcastManagerContract.Podcast.PROJECTION_ALL,              // Projection to return
                        PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL+"=?",   // Selection clause
                        new String[]{podcast.getFeedUrl()},                        // Selection arguments
                        null                                                        // Default sort order
                );

            }

            // Check if the cursor is empty or not
            if (cursor != null) {

                while(cursor.moveToNext()) {

                    // Instantiate a Podcast Object
                    Podcast podcast = (Podcast)podcastConverter.loadFrom(cursor);

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
            publishResults(Activity.RESULT_OK);
            return; // Finish services
        }

        // Then we need to get the feed's url saved on each podcast and download and parse its episodes
        for(Podcast podcast : podcasts){

            Podcast oldPodcastFeed = mFeedLoader.getFeed(podcast.getFeedUrl(),false);

            Podcast currentPodcastFeed = mFeedLoader.getFeed(podcast.getFeedUrl(),true);

            Set<String> oldEpisodeUrls = new HashSet<>();

            int newEpisodes = 0;

            for(Episode oldEpisode : oldPodcastFeed.getEpisodes()){
                oldEpisodeUrls.add(oldEpisode.getEpisodeUrl());
            }

            for(Episode currentEpisode : currentPodcastFeed.getEpisodes()){
                if(oldEpisodeUrls.contains(currentEpisode.getEpisodeUrl())){
                    newEpisodes++;
                }
            }

            if(newEpisodes>0) {

                podcast.setEpisodesCount(currentPodcastFeed.getEpisodes().size());
                podcast.setNewEpisodesAdded(newEpisodes);
                podcast.setLastModifiedDate(Calendar.getInstance());

                podcastsToUpsert.add(new PodcastConverter().toInsertOperation(podcast));

                episodesAdded += newEpisodes;

            }
        }

        // If we have podcast to upsert
        if(podcastsToUpsert.isEmpty()){
            Log.d(LOG_TAG,"NO PODCAST TO UPDATE");
            publishResults(Activity.RESULT_OK);
            return;
        }

        try {
            results = getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY, podcastsToUpsert);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        if(results!=null){

            if(episodesAdded>0) {

                // Show user notifications
                NotificationHelper notificationHelper = new NotificationHelper(this)
                        .setIcon(R.drawable.ic_new)
                        .setIntent(new Intent(this, MainActivity.class))
                        .setAutoCancel(true)
                        .setTitle(getString(R.string.app_name))
                        .setContentText(MessageFormat.format(getString(R.string.notification_content_text_new_episodes), new String[]{String.valueOf(episodesAdded)}));

                notificationHelper.build();

                notificationHelper.show(Constants.NOTIFICATION_ID.SYNCHRONIZE_SERVICE);

            }

            publishResults(Activity.RESULT_OK);

        }

    }


    private void publishResults(int result) {
        Intent intent = new Intent(Constants.SYNCHRONIZE_SERVICE.NOTIFICATION);
        intent.putExtra(Constants.SYNCHRONIZE_SERVICE.RESULT, result);
        sendBroadcast(intent);
    }

}
