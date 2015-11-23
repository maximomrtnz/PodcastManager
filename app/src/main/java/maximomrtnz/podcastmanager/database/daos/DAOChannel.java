package maximomrtnz.podcastmanager.database.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.Utils;
import maximomrtnz.podcastmanager.database.DatabaseHelper;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Channel;

/**
 * Created by Maxi on 11/22/2015.
 */
public class DAOChannel {

    public static final String[] projection = {
            PodcastManagerContract.Channel.COLUMN_NAME_ID,
            PodcastManagerContract.Channel.COLUMN_NAME_DESCRIPTION,
            PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_OWNER_ID,
            PodcastManagerContract.Channel.COLUMN_NAME_COPYRIGHT,
            PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_AUTHOR,
            PodcastManagerContract.Channel.COLUMN_NAME_LINK,
            PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_IMAGE_ID,
            PodcastManagerContract.Channel.COLUMN_NAME_PUB_DATE,
            PodcastManagerContract.Channel.COLUMN_NAME_TITLE,
            PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_SUMMARY,
            PodcastManagerContract.Channel.COLUMN_NAME_LAST_BUILD_DATE
    };

    public static final String sortOrder = PodcastManagerContract.Channel.COLUMN_NAME_TITLE + " DESC";

    public static List<Channel> getAll(Context context){

        List<Channel> channels = new ArrayList<>();

        DatabaseHelper mDbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PodcastManagerContract.Channel.TABLE_NAME,                       // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor.moveToFirst()) {

            while(cursor.isAfterLast() == false) {

                Channel channel = new Channel();

                channel.setId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_ID)));
                channel.setCopyright(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_COPYRIGHT)));
                channel.setDescription(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_DESCRIPTION)));
                channel.setItunesAuthor(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_AUTHOR)));
                channel.setItunesSumary(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_ITUNES_SUMMARY)));
                channel.setLanguage(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_LANGUAGE)));
                channel.setLink(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_LINK)));
                channel.setTitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_TITLE)));
                channel.setLastBuildDate(Utils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_LAST_BUILD_DATE))));
                channel.setPubDate(Utils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Channel.COLUMN_NAME_LAST_BUILD_DATE))));


                channels.add(channel);

                cursor.moveToNext();

            }

        }

        return channels;

    }

}
