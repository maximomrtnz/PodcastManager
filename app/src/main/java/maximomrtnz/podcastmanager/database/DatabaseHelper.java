package maximomrtnz.podcastmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maxi on 11/21/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseSchema.SQL_CREATE_ENTRY_ENCLOSURE);
        db.execSQL(DatabaseSchema.SQL_CREATE_ENTRY_PODCAST);
        db.execSQL(DatabaseSchema.SQL_CREATE_ENTRY_EPISODE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseSchema.SQL_DELETE_ENTRY_ITEM);
        db.execSQL(DatabaseSchema.SQL_DELETE_ENTRY_CHANNEL);
        db.execSQL(DatabaseSchema.SQL_DELETE_ENTRY_ENCLOSURE);
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
