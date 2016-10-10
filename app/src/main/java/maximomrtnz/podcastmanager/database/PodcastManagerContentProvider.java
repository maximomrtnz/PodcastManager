package maximomrtnz.podcastmanager.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.database.DatabaseHelper;

/**
 * Created by maximo on 29/07/16.
 */

public class PodcastManagerContentProvider extends ContentProvider {

    private String LOG_TAG = "PMContentProvider";

    private DatabaseHelper mDatabase;

    public static final String AUTHORITY = "maximomrtnz.podcastmanager.database.podcastmanagercontentprovider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    public static final Uri PODCAST_CONTENT_URI = Uri.withAppendedPath(PodcastManagerContentProvider.AUTHORITY_URI, PodcastManagerContract.Podcast.CONTENT_PATH);

    public static final Uri EPISODE_CONTENT_URI = Uri.withAppendedPath(PodcastManagerContentProvider.AUTHORITY_URI, PodcastManagerContract.Episode.CONTENT_PATH);

    private static final UriMatcher URI_MATCHER;


    // helper constants for use with the UriMatcher
    private static final int PODCAST_DIR = 0;
    private static final int PODCAST_ID = 1;
    private static final int EPISODE_DIR = 2;
    private static final int EPISODE_ID = 3;
    private static final int ITEM_DIR = 4;
    private static final int ITEM_ID = 5;


    static {

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Podcast.CONTENT_PATH, PODCAST_DIR);
        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Podcast.CONTENT_PATH + "/#", PODCAST_ID);

        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Episode.CONTENT_PATH, EPISODE_DIR);
        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Episode.CONTENT_PATH + "/#", EPISODE_ID);


    }

    @Override
    public boolean onCreate() {
        mDatabase = new DatabaseHelper(getContext());
        return false;
    }



    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase dbConnection = mDatabase.getReadableDatabase();

        switch (URI_MATCHER.match(uri)) {

            case PODCAST_ID:
                queryBuilder.appendWhere(PodcastManagerContract.Podcast._ID + "=" + uri.getLastPathSegment());
            case PODCAST_DIR:
                queryBuilder.setTables(PodcastManagerContract.Podcast.TABLE_NAME);
                break;

            case EPISODE_ID:
                queryBuilder.appendWhere(PodcastManagerContract.Episode._ID + "=" + uri.getLastPathSegment());
            case EPISODE_DIR:
                queryBuilder.setTables(PodcastManagerContract.Episode.TABLE_NAME);
                break;

            default :
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        Cursor cursor = queryBuilder.query(dbConnection, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {

            case PODCAST_DIR:
                return PodcastManagerContract.Podcast.CONTENT_TYPE;
            case PODCAST_ID:
                return PodcastManagerContract.Podcast.CONTENT_ITEM_TYPE;

            case EPISODE_DIR:
                return PodcastManagerContract.Episode.CONTENT_TYPE;
            case EPISODE_ID:
                return PodcastManagerContract.Episode.CONTENT_ITEM_TYPE;

            case ITEM_DIR:
                return PodcastManagerContract.Episode.CONTENT_TYPE;
            case ITEM_ID:
                return PodcastManagerContract.Episode.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        Object result;

        try {

            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {

                case PODCAST_DIR:

                case PODCAST_ID:

                    result = upsert(dbConnection,uri,PodcastManagerContract.Podcast.TABLE_NAME, values, PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL);

                    if(result instanceof Integer){
                        if(((Integer)result)>0) {
                            getContext().getContentResolver().notifyChange(uri, null);
                        }
                    }else if(result instanceof Uri){
                        uri = (Uri) result;
                        getContext().getContentResolver().notifyChange(uri, null);
                    }

                    dbConnection.setTransactionSuccessful();

                    return uri;

                case EPISODE_DIR:

                case EPISODE_ID:

                    result = upsert(dbConnection,uri,PodcastManagerContract.Episode.TABLE_NAME, values, PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL);

                    if(result instanceof Integer){
                        if(((Integer)result)>0) {
                            getContext().getContentResolver().notifyChange(uri, null);
                        }
                    }else if(result instanceof Uri){
                        uri = (Uri) result;
                        getContext().getContentResolver().notifyChange(uri, null);
                    }

                    dbConnection.setTransactionSuccessful();

                    return uri;

                default :

                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            dbConnection.endTransaction();

        }

        return null;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {

        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        int deleteCount = 0;

        try {

            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {

                case PODCAST_DIR:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Podcast.TABLE_NAME, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case PODCAST_ID:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Podcast.TABLE_NAME, PodcastManagerContract.Podcast.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });

                    dbConnection.setTransactionSuccessful();

                    break;

                case EPISODE_DIR:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Episode.TABLE_NAME, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case EPISODE_ID:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Episode.TABLE_NAME, PodcastManagerContract.Episode.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });

                    dbConnection.setTransactionSuccessful();

                    break;

                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        int updateCount = 0;


        try {

            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {

                case PODCAST_DIR:

                    updateCount = dbConnection.update(PodcastManagerContract.Podcast.TABLE_NAME, values, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case PODCAST_ID:

                    final long podcastId = ContentUris.parseId(uri);

                    updateCount = dbConnection.update(PodcastManagerContract.Podcast.TABLE_NAME, values, PodcastManagerContract.Podcast._ID + "=" + podcastId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case EPISODE_DIR:

                    updateCount = dbConnection.update(PodcastManagerContract.Episode.TABLE_NAME, values, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case EPISODE_ID:

                    final long episodeId = ContentUris.parseId(uri);

                    updateCount = dbConnection.update(PodcastManagerContract.Episode.TABLE_NAME, values, PodcastManagerContract.Episode._ID + "=" + episodeId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }

        } finally {

            dbConnection.endTransaction();

        }

        if (updateCount > 0) {

            getContext().getContentResolver().notifyChange(uri, null);

        }

        return updateCount;
    }

    /**
     * In case of a conflict when inserting the values, another update query is sent.
     *
     * @param db     Database to insert to.
     * @param uri    Content provider uri.
     * @param table  Table to insert to.
     * @param values The values to insert to.
     * @param column Column to identify the object.
     * @throws android.database.SQLException
     */
    private Object upsert(SQLiteDatabase db, Uri uri, String table, ContentValues values, String column) throws SQLException {
        try {
            long id = db.insertOrThrow(table, null, values);
            return ContentUris.withAppendedId(uri, id);
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?", new String[]{values.getAsString(column)});
            if (nrRows == 0) {
                throw e;
            }
            return nrRows;
        }
    }

}
