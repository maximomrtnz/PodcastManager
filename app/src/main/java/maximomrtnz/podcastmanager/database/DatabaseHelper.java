package maximomrtnz.podcastmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maxi on 11/21/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "PodcastManager.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String FOREIGN_KEY = " FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES";
    private static final String UNIQUE = "UNIQUE";
    private static final String NOT_NULL = "NOT NULL";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String ON_DELETE_CASCADE = "ON DELETE CASCADE";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_ENTRY_ENCLOSURE =
            "CREATE TABLE " + PodcastManagerContract.Enclosure.TABLE_NAME + " (" +
                    PodcastManagerContract.Enclosure._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_LENGTH + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_EPISODE_ID + TEXT_TYPE + COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Enclosure.COLUMN_EPISODE_ID+") "+REFERENCES+" "+PodcastManagerContract.Episode.TABLE_NAME+"("+PodcastManagerContract.Episode._ID+") " + ON_DELETE_CASCADE +
            " )";

    private static final String SQL_CREATE_ENTRY_PODCAST =
            "CREATE TABLE " + PodcastManagerContract.Podcast.TABLE_NAME + " (" +
                    PodcastManagerContract.Podcast._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_COPYRIGHT + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_SUMMARY + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LAST_BUILD_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Podcast.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL + TEXT_TYPE +
            " )";


    private static final String SQL_CREATE_ENTRY_EPISODE =
            "CREATE TABLE " + PodcastManagerContract.Episode.TABLE_NAME + " (" +
                    PodcastManagerContract.Episode._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_GUID + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID+") "+REFERENCES+" "+PodcastManagerContract.Podcast.TABLE_NAME+"("+PodcastManagerContract.Podcast._ID+") "+ ON_DELETE_CASCADE +
            " )";


    private static final String SQL_DELETE_ENTRY_CHANNEL = "DROP TABLE IF EXISTS " + PodcastManagerContract.Podcast.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ITEM = "DROP TABLE IF EXISTS " + PodcastManagerContract.Episode.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ENCLOSURE = "DROP TABLE IF EXISTS " + PodcastManagerContract.Enclosure.TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRY_ENCLOSURE);
        db.execSQL(SQL_CREATE_ENTRY_PODCAST);
        db.execSQL(SQL_CREATE_ENTRY_EPISODE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRY_ITEM);
        db.execSQL(SQL_DELETE_ENTRY_CHANNEL);
        db.execSQL(SQL_DELETE_ENTRY_ENCLOSURE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

}
