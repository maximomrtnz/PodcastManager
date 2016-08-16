package maximomrtnz.podcastmanager.asynktasks;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by maximo on 30/07/16.
 */

public class InsertPodcast extends AsyncTask<Podcast,Uri, Uri>{

    private final String LOG_TAG = "InsertPodcast";

    private final InsertPodcast.InsertPodcastListener mListener;

    private Context mContext;

    public interface InsertPodcastListener{
        void onPodcastInserted(Uri mNewPodcastUri);
    }


    public InsertPodcast(Context context, InsertPodcast.InsertPodcastListener listener){
        mListener = listener;
        mContext = context;
    }

    @Override
    protected Uri doInBackground(Podcast... args) {

        Podcast podcast = args[0];

        Uri mNewPodcastUri;

        ContentValues podcastContentValue = new ContentValues();

        podcast.loadTo(podcastContentValue);

        // Insert Podcast
        ArrayList<ContentProviderOperation> podcasts = new ArrayList<>();

        // Add podcast
        podcasts.add(ContentProviderOperation.newInsert(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withValues(podcastContentValue)
                .build());


        ContentProviderResult[] results = null;

        try{
            results = mContext.getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY,podcasts);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        if(results==null){
            return null;
        }

        // Get URI
        mNewPodcastUri = results[0].uri;

        Log.d(LOG_TAG, ""+mNewPodcastUri);

        // Get Podcast Database Id
        long podcastId = Long.valueOf(mNewPodcastUri.getLastPathSegment());

        // Insert Podcast Episodes
        ArrayList<ContentProviderOperation> episodes = new ArrayList<>();

        for(Episode episode : podcast.getEpisodes()){

            ContentValues episodeContentValue = new ContentValues();

            // Set podcast id
            episode.setPodcastId(podcastId);

            episode.loadTo(episodeContentValue);

            // Add apisode to batch list
            episodes.add(ContentProviderOperation.newInsert(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                    .withValues(episodeContentValue)
                    .build());

        }


        results = null;

        try{
            results = mContext.getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY,episodes);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        return mNewPodcastUri;
    }

    @Override
    protected void onPostExecute(Uri mNewPodcastUri) {
        super.onPostExecute(mNewPodcastUri);
        if(mListener!=null){
            mListener.onPodcastInserted(mNewPodcastUri);
        }
    }
}
