package maximomrtnz.podcastmanager.database;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.utils.DateUtils;

/**
 * Created by maximo on 31/08/16.
 */

public class EpisodeConverter implements Converter<Episode> {

    @Override
    public ContentProviderOperation toDeleteOperation(Episode object) {
        return null;
    }

    @Override
    public ContentProviderOperation toInsertOperation(Episode episode) {

        ContentValues episodeContentValue = loadToContentValue(episode);

        // Add episode to batch list
        return ContentProviderOperation.newInsert(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                .withValues(episodeContentValue)
                .build();

    }

    @Override
    public ContentProviderOperation toUpdateOperation(Episode episode) {
        ContentValues episodeContentValue = loadToContentValue(episode);

        // Add episode to batch list
        return ContentProviderOperation.newUpdate(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                .withSelection(PodcastManagerContract.Episode.WHERE_ID_EQUALS,new String[]{String.valueOf(episode.getId())})
                .withValues(episodeContentValue)
                .build();
    }

    @Override
    public ContentValues loadToContentValue(Episode episode) {

        ContentValues contentValues = new ContentValues();

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */

        if(episode.getId()!=null){
            contentValues.put(PodcastManagerContract.Episode._ID, episode.getId());
        }

        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_TITLE, episode.getTitle());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION, episode.getDescription());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR, episode.getItunesAuthor());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE, DateUtils.formatDateAsLong(episode.getPubDate()));
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_LINK, episode.getLink());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL, episode.getImageUrl());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID, episode.getPodcastId());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_GUID, episode.getGuid());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION, episode.getItunesDuration());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE, episode.getItunesSubtitle());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUMMARY, episode.getItunesSummary());
        contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL, episode.getEpisodeUrl());

        if(episode.getPlayed() != null) {
            contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_PLAYED, episode.getPlayed());
        }

        if(episode.getDownloadId()!=null){
            contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_DOWNLOAD_MANAGER_ID, episode.getDownloadId());
        }

        if(episode.getOnPlayQueue()!=null){
            contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_ON_PLAY_QUEUE, episode.getOnPlayQueue());
        }

        if(episode.getOnPlayQueueTimeStamp()!=null){
            contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ON_PLAY_QUEUE_TIMESTAMP, DateUtils.formatDateAsLong(episode.getOnPlayQueueTimeStamp()));
        }

        if(episode.getRemainderDuration()!=null){
            contentValues.put(PodcastManagerContract.Episode.COLUMN_NAME_DURATION_REMAINDER, episode.getRemainderDuration());
        }

        return contentValues;

    }

    @Override
    public Episode loadFrom(Cursor cursor) {

        Episode episode = new Episode();

        // Set Episodes Fields
        episode.setId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode._ID)));
        episode.setTitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_TITLE)));
        episode.setPodcastId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID)));
        episode.setImageUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL)));
        episode.setItunesDuration(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION)));
        episode.setDescription(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION)));
        episode.setItunesAuthor(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR)));
        episode.setItunesSummary(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUMMARY)));
        episode.setPubDate(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE))));
        episode.setLink(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_LINK)));
        episode.setItunesSubtitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE)));
        episode.setGuid(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_GUID)));
        episode.setEpisodeUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL)));
        episode.setOnPlayQueueTimeStamp(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ON_PLAY_QUEUE_TIMESTAMP))));
        episode.setRemainderDuration(cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_DURATION_REMAINDER)));

        // Set Custom Fields
        int isPlayed = cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_PLAYED));

        int isOnPlayQueue = cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_ON_PLAY_QUEUE));

        episode.setPlayed((isPlayed==1)?true:false);

        episode.setDownloadId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_DOWNLOAD_MANAGER_ID)));

        episode.setOnPlayQueue((isOnPlayQueue==1)?true:false);

        return episode;

    }

}
