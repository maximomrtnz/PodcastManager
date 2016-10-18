package maximomrtnz.podcastmanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import maximomrtnz.podcastmanager.database.EpisodeConverter;
import maximomrtnz.podcastmanager.database.PodcastConverter;
import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

public class PlayQueue{

    private static PlayQueue mInstance = null;

    protected PlayQueue(){}

    public static PlayQueue getInstance() {
        if (mInstance == null) {
            mInstance = new PlayQueue();
        }
        return mInstance;
    }

    public interface PlayQueueListener{
        void onPlayQueueChange();
    }

    private final String LOG_TAG = "PlayQueue";
    private Context mContext;
    private List<PlayQueueListener> mPlayQueueListeners = new ArrayList<>();
    private Cursor mCursor;
    private ListIterator<Episode> mEpisodes;
    private boolean nextWasCalled = false;
    private boolean previousWasCalled = false;

    public void addListener(PlayQueueListener playQueueListener){
        mPlayQueueListeners.add(playQueueListener);
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void loadQueue(){
        new LoaderTask().execute();
    }

    public void remove(Episode episode){

        mEpisodes.remove();

        // Remove episode from the play queue
        episode.setOnPlayQueue(false);
        episode.setPlayed(true);

        // Insert/Update into Database
        mContext.getContentResolver().insert(
                PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                new EpisodeConverter().loadToContentValue(episode)
        );

    }

    public boolean add(Podcast podcast, Episode episode){

        // If Podcast is not saved, save it
        if(podcast.getId()==null){

            // Insert/Update into Database
            Uri newPodcastUri = mContext.getContentResolver().insert(
                    PodcastManagerContentProvider.PODCAST_CONTENT_URI,
                    new PodcastConverter().loadToContentValue(podcast)
            );

            Log.d(LOG_TAG,newPodcastUri+"");

            Long id = ContentProviderUtils.getIdFromUri(newPodcastUri);

            Log.d(LOG_TAG,id+"");

            if(id==0){
                return false;
            }

            podcast.setId(id);

        }

        if(podcast.getId()==null){
            return false;
        }

        if(episode.getPodcastId()==null) {
            episode.setPodcastId(podcast.getId());
        }

        // Save episode into de play queue
        episode.setOnPlayQueue(true);
        episode.setOnPlayQueueTimeStamp(Calendar.getInstance());

        // Insert/Update into Database
        mContext.getContentResolver().insert(
                PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                new EpisodeConverter().loadToContentValue(episode)
        );

        return true;

    }

    public Episode getNext() {

        Episode toReturn = null;

        nextWasCalled = true;

        if (previousWasCalled) {
            previousWasCalled = false;
            if (mEpisodes.hasNext()) {
                mEpisodes.next();
            }
        }

        if (mEpisodes.hasNext()) {
            toReturn = mEpisodes.next();
        }else{
            while (mEpisodes.hasPrevious()){
                toReturn = mEpisodes.previous();
            }
        }

        return toReturn;
    }

    public Episode getPrevious(){

        Episode toReturn = null;

        if (nextWasCalled) {
            if(mEpisodes.hasPrevious()){
                mEpisodes.previous();
            }
            nextWasCalled = false;
        }

        previousWasCalled = true;

        if(mEpisodes.hasPrevious()){
            toReturn = mEpisodes.previous();
        }else{
            while(mEpisodes.hasNext()){
                toReturn = mEpisodes.next();
            }
        }

        return toReturn;

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

            List<Episode> episodes = new ArrayList<>();

            while (mCursor.moveToNext()) {
                episodes.add(new EpisodeConverter().loadFrom(mCursor));
            }

            mEpisodes = episodes.listIterator();

            for(PlayQueueListener pl : mPlayQueueListeners){
                pl.onPlayQueueChange();
            }
        }
    }

}