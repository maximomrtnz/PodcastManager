package maximomrtnz.podcastmanager.database;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import maximomrtnz.podcastmanager.models.pojos.Podcast;
import maximomrtnz.podcastmanager.utils.DateUtils;

/**
 * Created by maximo on 01/09/16.
 */

public class PodcastConverter implements Converter<Podcast> {

    private String LOG_TAG = "PodcastConverter";

    @Override
    public ContentProviderOperation toDeleteOperation(Podcast podcast) {
        return ContentProviderOperation.newDelete(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withSelection(PodcastManagerContract.Podcast.WHERE_ID_EQUALS,new String[]{String.valueOf(podcast.getId())})
                .build();
    }

    @Override
    public ContentProviderOperation toInsertOperation(Podcast podcast) {
        ContentValues podcastContentValue = loadToContentValue(podcast);

        return  ContentProviderOperation.newInsert(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withValues(podcastContentValue)
                .build();
    }

    @Override
    public ContentProviderOperation toUpdateOperation(Podcast podcast) {
        return null;
    }

    @Override
    public ContentValues loadToContentValue(Podcast podcast) {

        ContentValues contentValues = new ContentValues();

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */

        if(podcast.getId()!=null){
            contentValues.put(PodcastManagerContract.Podcast._ID, podcast.getId());
        }

        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_TITLE, podcast.getTitle());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_DESCRIPTION, podcast.getDescription());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_COPYRIGHT, podcast.getCopyright());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_AUTHOR, podcast.getItunesAuthor());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_PUB_DATE, DateUtils.formatDateAsLong(podcast.getPubDate()));
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LINK, podcast.getLink());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LANGUAGE, podcast.getLanguage());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LAST_BUILD_DATE, DateUtils.formatDateAsLong(podcast.getLastBuildDate()));
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_SUMMARY, podcast.getItunesSumary());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL, podcast.getFeedUrl());
        contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_IMAGE_URL, podcast.getImageUrl());

        if(podcast.getSubscribed()!=null){
            contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_FLAG_SUBSCRIBED, podcast.getSubscribed());
        }

        if(podcast.getLastModifiedDate()!=null){
            contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LAST_MODIFIED_DATE, DateUtils.formatDateAsLong(podcast.getLastModifiedDate()));
        }

        if(podcast.getEpisodesCount()!=null){
            contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_EPISODES_COUNT, podcast.getEpisodesCount());
        }

        if(podcast.getNewEpisodesAdded()!=null){
            contentValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_NEW_EPISODES_ADDED, podcast.getNewEpisodesAdded());
        }

        return contentValues;

    }

    @Override
    public Podcast loadFrom(Cursor cursor) {

        Podcast podcast = new Podcast();

        podcast.setId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Podcast._ID)));
        podcast.setTitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_TITLE)));
        podcast.setFeedUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL)));
        podcast.setImageUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_IMAGE_URL)));
        podcast.setCopyright(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_COPYRIGHT)));
        podcast.setDescription(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_DESCRIPTION)));
        podcast.setItunesAuthor(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_AUTHOR)));
        podcast.setItunesSumary(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_SUMMARY)));
        podcast.setLanguage(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_LANGUAGE)));
        podcast.setLastBuildDate(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_LAST_BUILD_DATE))));
        podcast.setPubDate(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_PUB_DATE))));
        podcast.setLink(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_LINK)));
        podcast.setLastModifiedDate(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_LAST_MODIFIED_DATE))));
        podcast.setEpisodesCount(cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_EPISODES_COUNT)));
        podcast.setNewEpisodesAdded(cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_NEW_EPISODES_ADDED)));

        int isSubscribed = cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Podcast.COLUMN_NAME_FLAG_SUBSCRIBED));

        podcast.setSubscribed((isSubscribed==1)?true:false);

        return podcast;

    }
}
