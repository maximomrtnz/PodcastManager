package maximomrtnz.podcastmanager.utils;

import android.content.Context;
import android.content.Intent;
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
    private List<Episode> mEpisodes = new ArrayList<>();
    private Integer mIndex = -1;

    public void addListener(PlayQueueListener playQueueListener){
        mPlayQueueListeners.add(playQueueListener);
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void loadQueue(){
        new LoaderTask().execute();
    }

    public Episode getByUrl(String url){

        for(Integer i = 0; i < mEpisodes.size(); i++){

            if(mEpisodes.get(i).getEpisodeUrl().equals(url)){
                mIndex = i;
                return mEpisodes.get(i);
            }

        }

        return null;

    }

    public void remove(Episode episode){

        if(mEpisodes.remove(episode)) {

            // Remove episode from the play queue
            episode.setOnPlayQueue(false);
            episode.setPlayed(true);

            // Insert/Update into Database
            mContext.getContentResolver().insert(
                    PodcastManagerContentProvider.EPISODE_CONTENT_URI,
                    new EpisodeConverter().loadToContentValue(episode)
            );

        }

    }

    public boolean add(Podcast podcast, Episode episode){

        // If Podcast is not saved, save it
        if(podcast.getId()==null){

            // Insert/Update into Database
            Uri newPodcastUri = mContext.getContentResolver().insert(
                    PodcastManagerContentProvider.PODCAST_CONTENT_URI,
                    new PodcastConverter().loadToContentValue(podcast)
            );

            Long id = ContentProviderUtils.getIdFromUri(newPodcastUri);

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

        // Add to list
        int index = contains(episode);

        if(index!=-1){
            mEpisodes.remove(index);
        }

        mEpisodes.add(0,episode);

        mIndex = -1;

        return true;

    }

    public Episode getNext() {

        mIndex++;

        if(mEpisodes.isEmpty()){
            return null;
        }

        if(mEpisodes.size()<=mIndex){
            mIndex = 0;
        }

        return mEpisodes.get(mIndex);

    }

    public Episode getPrevious(){

        mIndex--;

        if(mEpisodes.isEmpty()){
            return null;
        }

        if(mIndex<0){
            mIndex = mEpisodes.size()-1;
        }

        return mEpisodes.get(mIndex);

    }

    private Integer contains(Episode episode){
        for(Integer i= 0; i < mEpisodes.size(); i++){
            if(mEpisodes.get(i).getEpisodeUrl().equals(episode.getEpisodeUrl())){
                return i;
            }
        }
        return -1;
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

            mEpisodes.clear();

            mIndex = -1;

            while (cursor.moveToNext()) {
                mEpisodes.add(new EpisodeConverter().loadFrom(cursor));
            }

            for(PlayQueueListener pl : mPlayQueueListeners){
                pl.onPlayQueueChange();
            }

        }
    }

}