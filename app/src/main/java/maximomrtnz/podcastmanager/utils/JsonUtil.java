package maximomrtnz.podcastmanager.utils;

import com.google.gson.Gson;

/**
 * Created by maximo on 28/08/16.
 */

public class JsonUtil {

    private static JsonUtil mInstance = null;
    private Gson mGson;

    protected JsonUtil() {
        mGson = new Gson();
    }

    public static JsonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new JsonUtil();
        }
        return mInstance;
    }

    public <T> T fromJson(String json, Class<T> classOfT) {

        return mGson.fromJson(json, classOfT);
    }

    public String toJson(Object src) {

        return mGson.toJson(src);
    }


}
