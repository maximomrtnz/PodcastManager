package maximomrtnz.podcastmanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;

public class PlayQueue {


    public interface PlayQueueListener{
        void onPlayQueueChange();
    }

    private final String LOG_TAG = "PlayQueue";
    private Context mContext;
    private PlayQueueListener mPlayQueueListener;
    private Cursor mCursor;


    public PlayQueue(Context context, PlayQueueListener playQueueListener) {
        mContext = context;
        mPlayQueueListener = playQueueListener;
        new LoaderTask().execute();
    }


    public boolean hasNext(){
        return mCursor.moveToNext();
    }

    public boolean hasPreviuos(){
        return mCursor.moveToPrevious();
    }

    public Episode getNext(){
        if(hasNext()){
            return new EpisodeConverter().loadFrom(mCursor);
        }
        return null;
    }

    public Episode getPrevious(){
        if(hasPreviuos()){
            return new EpisodeConverter().loadFrom(mCursor);
        }
        return null;
    }

    public class LoaderTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            // Returns a new CursorLoader
            return mContext.getContentResolver().query(
                    PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                    PodcastManagerContract.Episode.PROJECTION_ALL,
                    PodcastManagerContract.Episode.COLUMN_NAME_FLAG_ON_PLAY_QUEUE+"=?",
                    new String[]{"1"},
                    PodcastManagerContract.Episode.SORT_ORDER_ON_PLAY_QUEUE_TIMESTAMP_DESC);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mCursor = cursor;
            if(mPlayQueueListener!=null){
                mPlayQueueListener.onPlayQueueChange();
            }
        }
    }

}