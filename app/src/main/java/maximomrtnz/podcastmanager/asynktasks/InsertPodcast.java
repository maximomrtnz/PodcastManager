package maximomrtnz.podcastmanager.asynktasks;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Enclosure;
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
    protected Uri doInBackground(Podcast... podcasts) {

        Podcast podcast = podcasts[0];

        // Defines a new Uri object that receives the result of the insertion
        Uri mNewPodcastUri = mContext.getContentResolver().insert(
                PodcastManagerContentProvider.PODCAST_CONTENT_URI, // the podcast content URI
                podcast.toContentValue()                          // the values to insert
        );

        // Get Podcast Database Id
        long podcastId = Long.valueOf(mNewPodcastUri.getLastPathSegment());

        // Insert Podcast Episodes
        ArrayList<ContentProviderOperation> episodes = new ArrayList<ContentProviderOperation>();

        for(Episode episode : podcast.getEpisodes()){
            episodes.add(ContentProviderOperation.newInsert(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                    .withValue(PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID, podcastId)
                    .withValues(episode.toContentValue())
                    .build());
        }


        ContentProviderResult[] results = null;

        try{
            results = mContext.getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY,episodes);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        if(results!=null){

            ArrayList<ContentProviderOperation> enclosures = new ArrayList<ContentProviderOperation>();

            // Insert Episodes Enclosures
            for(Integer i = 0; i < results.length ; i++){

                Episode episode = podcast.getEpisodes().get(i);

                ContentProviderResult result = results[i];

                Enclosure enclosure = episode.getEnclosure();

                // Get Podcast Database Id
                long episodetId = Long.valueOf(result.uri.getLastPathSegment());

                Log.d(LOG_TAG,"New Podcast Episode -->"+result.uri.toString());

                enclosures.add(ContentProviderOperation.newInsert(PodcastManagerContentProvider.ENCLOSURE_CONTENT_URI)
                        .withValue(PodcastManagerContract.Enclosure.COLUMN_EPISODE_ID, episodetId)
                        .withValues(enclosure.toContentValue())
                        .build());

            }

            results = null;

            try{
                results = mContext.getContentResolver().applyBatch(PodcastManagerContentProvider.AUTHORITY,enclosures);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            } catch (OperationApplicationException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }

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
