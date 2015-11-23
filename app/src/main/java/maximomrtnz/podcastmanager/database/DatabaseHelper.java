package maximomrtnz.podcastmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maxi on 11/21/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String FOREIGN_KEY = " FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String ON_DELETE_CASCADE = "ON DELETE CASCADE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRY_ITUNES_IMAGE =
            "CREATE TABLE " + PodcastManagerContract.ItunesImage.TABLE_NAME + " (" +
                    PodcastManagerContract.ItunesImage.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.ItunesImage.COLUMN_NAME_SRC + TEXT_TYPE +
            " )";

    private static final String SQL_CREATE_ENTRY_ITUNES_OWNER =
            "CREATE TABLE " + PodcastManagerContract.ItunesOwner.TABLE_NAME + " (" +
                    PodcastManagerContract.ItunesOwner.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.ItunesOwner.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.ItunesOwner.COLUMN_NAME_NAME + TEXT_TYPE +
            " )";

    private static final String SQL_CREATE_ENTRY_ENCLOSURE =
            "CREATE TABLE " + PodcastManagerContract.Enclosure.TABLE_NAME + " (" +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_LENGTH + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Enclosure.COLUMN_NAME_URL + TEXT_TYPE +
            " )";

    private static final String SQL_CREATE_ENTRY_CHANNEL =
            "CREATE TABLE " + PodcastManagerContract.Channel.TABLE_NAME + " (" +
                    PodcastManagerContract.Channel.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_COPYRIGHT + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_IMAGE_ID + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_OWNER_ID + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_SUMMARY + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_LAST_BUILD_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Channel.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_IMAGE_ID+") "+REFERENCES+" "+PodcastManagerContract.ItunesImage.TABLE_NAME+"("+PodcastManagerContract.ItunesImage.COLUMN_NAME_ID+")"+
                    FOREIGN_KEY + " ("+PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_OWNER_ID+") "+REFERENCES+" "+PodcastManagerContract.ItunesOwner.TABLE_NAME+"("+PodcastManagerContract.ItunesOwner.COLUMN_NAME_ID+")"+
            " )";


    private static final String SQL_CREATE_ENTRY_ITEM =
            "CREATE TABLE " + PodcastManagerContract.Item.TABLE_NAME + " (" +
                    PodcastManagerContract.Item.COLUMN_NAME_ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_CHANNEL_ID + INTEGER_TYPE +  COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_ENCLOSURE_ID + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_GUID + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_ITUNES_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_ITUNES_DURATION + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_ITUNES_IMAGE_ID + INTEGER_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_ITUNES_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    PodcastManagerContract.Item.COLUMN_NAME_PUB_DATE + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Item.COLUMN_NAME_ITUNES_IMAGE_ID+") "+REFERENCES+" "+PodcastManagerContract.ItunesImage.TABLE_NAME+"("+PodcastManagerContract.ItunesImage.COLUMN_NAME_ID+") "+ COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Item.COLUMN_NAME_ENCLOSURE_ID+") "+REFERENCES+" "+PodcastManagerContract.Enclosure.TABLE_NAME+"("+PodcastManagerContract.Enclosure.COLUMN_NAME_ID+") "+ COMMA_SEP +
                    FOREIGN_KEY + " ("+PodcastManagerContract.Item.COLUMN_NAME_CHANNEL_ID+") "+REFERENCES+" "+PodcastManagerContract.Channel.TABLE_NAME+"("+PodcastManagerContract.Channel.COLUMN_NAME_ID+") "+ ON_DELETE_CASCADE +
            " )";


    private static final String SQL_DELETE_ENTRY_CHANNEL = "DROP TABLE IF EXISTS " + PodcastManagerContract.Channel.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ITEM = "DROP TABLE IF EXISTS " + PodcastManagerContract.Item.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ENCLOSURE = "DROP TABLE IF EXISTS " + PodcastManagerContract.Enclosure.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ITUNES_IMAGE = "DROP TABLE IF EXISTS " + PodcastManagerContract.ItunesImage.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY_ITUNES_OWNER = "DROP TABLE IF EXISTS " + PodcastManagerContract.ItunesOwner.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PodcastManager.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRY_ITUNES_IMAGE);
        db.execSQL(SQL_CREATE_ENTRY_ITUNES_OWNER);
        db.execSQL(SQL_CREATE_ENTRY_ENCLOSURE);
        db.execSQL(SQL_CREATE_ENTRY_CHANNEL);
        db.execSQL(SQL_CREATE_ENTRY_ITEM);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRY_ITEM);
        db.execSQL(SQL_DELETE_ENTRY_CHANNEL);
        db.execSQL(SQL_DELETE_ENTRY_ITUNES_IMAGE);
        db.execSQL(SQL_DELETE_ENTRY_ITUNES_OWNER);
        db.execSQL(SQL_DELETE_ENTRY_ENCLOSURE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
