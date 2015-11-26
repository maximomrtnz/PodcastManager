package maximomrtnz.podcastmanager.database.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import maximomrtnz.podcastmanager.database.DatabaseHelper;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Channel;
import maximomrtnz.podcastmanager.models.pojos.ItunesOwner;

/**
 * Created by Maxi on 11/25/2015.
 */
public class DAOItunesOwner extends DAO{

    public static final String[] projection = {
        PodcastManagerContract.ItunesOwner.COLUMN_NAME_ID,
        PodcastManagerContract.ItunesOwner.COLUMN_NAME_NAME,
        PodcastManagerContract.ItunesOwner.COLUMN_NAME_EMAIL
    };

    public static final String sortOrder = PodcastManagerContract.Channel.COLUMN_NAME_TITLE + " DESC";

    public DAOItunesOwner(Context context){
        super(context);
    }

    @Override
    public List<Object> getAll() {

        List itunesOwners = new ArrayList<>();

        DatabaseHelper mDbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PodcastManagerContract.ItunesOwner.TABLE_NAME,                       // The table to query
                projection,                                                          // The columns to return
                null,                                                                // The columns for the WHERE clause
                null,                                                                // The values for the WHERE clause
                null,                                                                // don't group the rows
                null,                                                                // don't filter by row groups
                sortOrder                                                            // The sort order
        );

        if (cursor.moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                ItunesOwner itunesOwner = new ItunesOwner();

                itunesOwner.setId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.ItunesOwner.COLUMN_NAME_ID)));
                itunesOwner.setName(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.ItunesOwner.COLUMN_NAME_NAME)));
                itunesOwner.setEmail(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.ItunesOwner.COLUMN_NAME_EMAIL)));

                itunesOwners.add(itunesOwner);

            }

        }

        return itunesOwners;

    }

    @Override
    public void delete(Object object) {

    }

    @Override
    public void update(Object object) {

    }

    @Override
    public void insert(Object object) {

        ItunesOwner itunesOwner = (ItunesOwner)object;

        DatabaseHelper mDbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PodcastManagerContract.ItunesOwner.COLUMN_NAME_NAME, itunesOwner.getName());
        values.put(PodcastManagerContract.ItunesOwner.COLUMN_NAME_EMAIL, itunesOwner.getEmail());

        long newRowId;

        newRowId = db.insert(
                PodcastManagerContract.Channel.TABLE_NAME,
                null,
                values);

        itunesOwner.setId(newRowId);

    }
}
