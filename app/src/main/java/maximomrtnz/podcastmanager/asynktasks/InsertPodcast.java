package maximomrtnz.podcastmanager.asynktasks;

import android.content.ContentProvider;
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
import java.util.Arrays;
import java.util.List;

import maximomrtnz.podcastmanager.database.Converter;
import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 30/07/16.
 */

public class InsertPodcast extends AsyncTask<Podcast,List<Uri>, List<Uri>>{

    private final String LOG_TAG = "InsertPodcast";

    private final InsertPodcast.InsertPodcastListener mListener;

    private Context mContext;

    public interface InsertPodcastListener{
        void onPodcastInserted(List<Uri> podcastUris);
    }


    public InsertPodcast(Context context, InsertPodcast.InsertPodcastListener listener){
        mListener = listener;
        mContext = context;
    }

    @Override
    protected List<Uri> doInBackground(Podcast... args) {

        List<Uri> podcastUris = new ArrayList<>();

        // Insert Podcast
        ArrayList<ContentProviderOperation> podcasts = new ArrayList<>();

        Converter podcastConverter = new PodcastConverter();

        Converter episodeConverter = new EpisodeConverter();

        for(Podcast podcast : args){
            podcasts.add(podcastConverter.toInsertOperation(podcast));
        }

        ArrayList<ContentProviderOperation> episodes = new ArrayList<>();

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

        for(int i = 0; i < results.length; i++){

            // Get podcast
            Podcast podcast = args[i];

            // Get URI
            Uri podcastUri = results[i].uri;

            // Add uri to list
            podcastUris.add(podcastUri);

            // Get Podcast Database Id
            long podcastId = Long.valueOf(podcastUri.getLastPathSegment());

            // Insert Podcast Episodes
            for(Episode episode : podcast.getEpisodes()){

                episode.setPodcastId(podcastId);

                episodes.add(episodeConverter.toInsertOperation(episode));

            }

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

        return podcastUris;
    }

    @Override
    protected void onPostExecute(List<Uri> podcastUris) {
        super.onPostExecute(podcastUris);
        if(mListener!=null){
            mListener.onPodcastInserted(podcastUris);
        }
    }
}
