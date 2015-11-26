package maximomrtnz.podcastmanager.database.daos;

import android.content.Context;

import java.util.List;

/**
 * Created by Maxi on 11/25/2015.
 */
public abstract class DAO {

    protected Context context;

    public DAO(Context context){
        this.context = context;
    }

    public abstract List<Object> getAll();

    public abstract void delete(Object object);

    public abstract void update(Object object);

    public abstract void insert(Object object);

}
