package maximomrtnz.podcastmanager.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.database.DatabaseHelper;

/**
 * Created by maximo on 29/07/16.
 */

public class PodcastManagerContentProvider extends ContentProvider {

    private DatabaseHelper mDatabase;

    public static final String AUTHORITY = "maximomrtnz.podcastmanager.database.podcastmanagercontentprovider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    public static final Uri PODCAST_CONTENT_URI = Uri.withAppendedPath(PodcastManagerContentProvider.AUTHORITY_URI, PodcastManagerContract.Podcast.CONTENT_PATH);

    public static final Uri EPISODE_CONTENT_URI = Uri.withAppendedPath(PodcastManagerContentProvider.AUTHORITY_URI, PodcastManagerContract.Episode.CONTENT_PATH);

    public static final Uri ENCLOSURE_CONTENT_URI = Uri.withAppendedPath(PodcastManagerContentProvider.AUTHORITY_URI, PodcastManagerContract.Enclosure.CONTENT_PATH);

    private static final UriMatcher URI_MATCHER;


    // helper constants for use with the UriMatcher
    private static final int PODCAST_DIR = 0;
    private static final int PODCAST_ID = 1;
    private static final int EPISODE_DIR = 2;
    private static final int EPISODE_ID = 3;
    private static final int ENCLOSURE_DIR = 4;
    private static final int ENCLOSURE_ID = 5;


    static {

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Podcast.CONTENT_PATH, PODCAST_DIR);
        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Podcast.CONTENT_PATH + "/#", PODCAST_ID);

        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Episode.CONTENT_PATH, EPISODE_DIR);
        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Episode.CONTENT_PATH + "/#", EPISODE_ID);

        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Enclosure.CONTENT_PATH, ENCLOSURE_DIR);
        URI_MATCHER.addURI(AUTHORITY, PodcastManagerContract.Enclosure.CONTENT_PATH + "/#", ENCLOSURE_ID);
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

            case ENCLOSURE_ID:
                queryBuilder.appendWhere(PodcastManagerContract.Enclosure._ID + "=" + uri.getLastPathSegment());
            case ENCLOSURE_DIR:
                queryBuilder.setTables(PodcastManagerContract.Enclosure.TABLE_NAME);
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

            case ENCLOSURE_DIR:
                return PodcastManagerContract.Enclosure.CONTENT_TYPE;
            case ENCLOSURE_ID:
                return PodcastManagerContract.Enclosure.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        try {

            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {

                case PODCAST_DIR:

                case PODCAST_ID:

                    final long podcastId = dbConnection.insertOrThrow(PodcastManagerContract.Podcast.TABLE_NAME, null, values);

                    final Uri newPodcastUri = ContentUris.withAppendedId(PODCAST_CONTENT_URI, podcastId);

                    getContext().getContentResolver().notifyChange(newPodcastUri, null);

                    dbConnection.setTransactionSuccessful();

                    return newPodcastUri;

                case EPISODE_DIR:

                case EPISODE_ID:

                    final long episodeId = dbConnection.insertOrThrow(PodcastManagerContract.Episode.TABLE_NAME, null, values);

                    final Uri newEpisodeUri = ContentUris.withAppendedId(EPISODE_CONTENT_URI, episodeId);

                    getContext().getContentResolver().notifyChange(newEpisodeUri, null);

                    dbConnection.setTransactionSuccessful();

                    return newEpisodeUri;

                case ENCLOSURE_DIR:

                case ENCLOSURE_ID:

                    final long enclosureId = dbConnection.insertOrThrow(PodcastManagerContract.Enclosure.TABLE_NAME, null, values);

                    final Uri newEnclosureUri = ContentUris.withAppendedId(ENCLOSURE_CONTENT_URI, enclosureId);

                    getContext().getContentResolver().notifyChange(newEnclosureUri, null);

                    dbConnection.setTransactionSuccessful();

                    return newEnclosureUri;

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

                case ENCLOSURE_DIR:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Enclosure.TABLE_NAME, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case ENCLOSURE_ID:

                    deleteCount = dbConnection.delete(PodcastManagerContract.Enclosure.TABLE_NAME, PodcastManagerContract.Enclosure.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });

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

        List<Uri> joinUris = new ArrayList<>();

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

                case ENCLOSURE_DIR:

                    updateCount = dbConnection.update(PodcastManagerContract.Enclosure.TABLE_NAME, values, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();

                    break;

                case ENCLOSURE_ID:

                    final long enclosureId = ContentUris.parseId(uri);

                    updateCount = dbConnection.update(PodcastManagerContract.Enclosure.TABLE_NAME, values, PodcastManagerContract.Enclosure._ID + "=" + enclosureId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);

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

            for (Uri joinUri : joinUris) {
                getContext().getContentResolver().notifyChange(joinUri, null);
            }

        }

        return updateCount;
    }
}
