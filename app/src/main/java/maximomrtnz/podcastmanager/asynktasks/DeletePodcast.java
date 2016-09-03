package maximomrtnz.podcastmanager.asynktasks;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

import maximomrtnz.podcastmanager.database.Converter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.utils.ContentProviderUtils;

/**
 * Created by maximo on 31/07/16.
 */

public class DeletePodcast extends AsyncTask<Podcast,Integer, Integer> {

    private final String LOG_TAG = "InsertPodcast";

    private final DeletePodcast.DeletePodcastListener mListener;

    private Context mContext;

    public interface DeletePodcastListener{
        void onPodcastDeleted(int deletedRows);
    }

    public DeletePodcast(Context context, DeletePodcast.DeletePodcastListener listener){
        mListener = listener;
        mContext = context;
    }

    @Override
    protected Integer doInBackground(Podcast... args) {

        ArrayList<ContentProviderOperation> podcasts = new ArrayList<ContentProviderOperation>();

        Converter converter = new PodcastConverter();

        for (Podcast podcast : args){
            // Add podcast
            podcasts.add(converter.toDeleteOperation(podcast));
        }

        // Delete Podcast
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

        // TODO: Delete Podcast Files such as Episode's Images saved on cache and Episode's Audios
        return results.length;
    }

    @Override
    protected void onPostExecute(Integer deletedRows) {
        super.onPostExecute(deletedRows);
        if(mListener!=null){
            this.mListener.onPodcastDeleted(deletedRows);
        }
    }
}
