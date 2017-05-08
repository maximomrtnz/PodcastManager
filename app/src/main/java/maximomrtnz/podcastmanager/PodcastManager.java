package maximomrtnz.podcastmanager;

import android.app.Application;

import maximomrtnz.podcastmanager.utils.PlayQueue;

/**
 * Created by maximo on 10/10/16.
 */

public class PodcastManager extends Application {

    public void onCreate(){
        super.onCreate();
        // Init singletons
        PlayQueue.getInstance().setContext(this);

    }

}
