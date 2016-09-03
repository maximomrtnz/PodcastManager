package maximomrtnz.podcastmanager.database;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by maximo on 31/08/16.
 */

public interface Converter<T> {

    ContentProviderOperation toDeleteOperation(T object);

    ContentProviderOperation toInsertOperation(T object);

    ContentProviderOperation toUpdateOperation(T object);

    ContentValues loadToContentValue(T object);

    T loadFrom(Cursor cursor);

}
