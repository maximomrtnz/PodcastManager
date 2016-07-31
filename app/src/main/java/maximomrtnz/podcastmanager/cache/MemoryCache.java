package maximomrtnz.podcastmanager.cache;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by maximo on 23/07/16.
 */

public class MemoryCache {

    private static final String LOG_TAG = "MemoryCache";
    private Map<String, Object> cache = Collections.synchronizedMap(new LinkedHashMap<String, Object>(10,1.5f,true));//Last argument true for LRU ordering
    private long size=0;//current allocated size
    private long limit=1000000;//max memory in bytes

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }

    public void setLimit(long new_limit){
        limit = new_limit;
        Log.i(LOG_TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
    }

    public Object get(String id){
        try{
            if(!cache.containsKey(id)) {
                return null;
            }
            return cache.get(id);
        }catch(NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    public void put(String id, Object o){
        try{
            if(cache.containsKey(id)) {
                size -= getSizeInBytes(cache.get(id));
            }
            cache.put(id, o);
            size += getSizeInBytes(o);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }

    private void checkSize() {
        Log.i(LOG_TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, Object>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while(iter.hasNext()){
                Entry<String, Object> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            Log.i(LOG_TAG, "Clean cache. New size "+cache.size());
        }
    }

    public void clear() {
        try{
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    long getSizeInBytes(Object o) {
        if(o instanceof Bitmap) {
            if (o == null) {
                return 0;
            }
            return ((Bitmap)o).getRowBytes() * ((Bitmap)o).getHeight();
        }
        return 0;
    }
}