package maximomrtnz.podcastmanager.database;

/**
 * A helper interface which defines constants for work with the DB.
 *
 * Created by maximo on 05/08/16.
 */
interface DatabaseSchema {

    static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "PodcastManager.db";

    static final String TEXT_TYPE = " TEXT";
    static final String INTEGER_TYPE = " INTEGER";
    static final String PRIMARY_KEY = " PRIMARY KEY";
    static final String FOREIGN_KEY = " FOREIGN KEY";
    static final String REFERENCES = " REFERENCES";
    static final String UNIQUE = "UNIQUE";
    static final String NOT_NULL = "NOT NULL";
    static final String AUTOINCREMENT = " AUTOINCREMENT";
    static final String ON_DELETE_CASCADE = "ON DELETE CASCADE";
    static final String COMMA_SEP = ",";


    static final String SQL_CREATE_ENTRY_PODCAST =
            "CREATE TABLE " + PodcastManagerContract.Podcast.TABLE_NAME + " (" +
                    PodcastManagerContract.Podcast._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL + TEXT_TYPE +NOT_NULL + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_COPYRIGHT + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_SUMMARY + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LAST_BUILD_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                    UNIQUE +"("+PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL+")" +
                    " )";


    static final String SQL_CREATE_ENTRY_EPISODE =
            "CREATE TABLE " + PodcastManagerContract.Episode.TABLE_NAME + " (" +
                    PodcastManagerContract.Episode._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_GUID + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUMMARY + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL + TEXT_TYPE + COMMA_SEP +
                    UNIQUE +"("+PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL+")" +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID+") "+REFERENCES+" "+PodcastManagerContract.Podcast.TABLE_NAME+"("+PodcastManagerContract.Podcast._ID+") "+ ON_DELETE_CASCADE +
                    " )";


    static final String SQL_DELETE_ENTRY_CHANNEL = "DROP TABLE IF EXISTS " + PodcastManagerContract.Podcast.TABLE_NAME;
    static final String SQL_DELETE_ENTRY_ITEM = "DROP TABLE IF EXISTS " + PodcastManagerContract.Episode.TABLE_NAME;


}

