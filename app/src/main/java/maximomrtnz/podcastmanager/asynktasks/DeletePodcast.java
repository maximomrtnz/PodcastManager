package maximomrtnz.podcastmanager.asynktasks;

import android.content.Context;
import android.os.AsyncTask;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

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
    protected Integer doInBackground(Podcast... podcasts) {

        // Get podcast from arguments
        Podcast podcast = podcasts[0];

        // Delete Podcast
        int deletedRows = mContext.getContentResolver().delete(PodcastManagerContentProvider.PODCAST_CONTENT_URI, PodcastManagerContract.Podcast.WHERE_ID_EQUALS, new String[]{String.valueOf(podcast.getId())});

        // TODO: Delete Podcast Files such as Episode's Images saved on cache and Episode's Audios

        return deletedRows;
    }

    @Override
    protected void onPostExecute(Integer deletedRows) {
        super.onPostExecute(deletedRows);
        if(mListener!=null){
            this.mListener.onPodcastDeleted(deletedRows);
        }
    }
}
