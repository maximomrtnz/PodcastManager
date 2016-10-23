package maximomrtnz.podcastmanager.utils;

/**
 * Created by maximo on 15/08/16.
 */

public class Constants {

    public interface NOTIFICATION_ID {
        static int FOREGROUND_SERVICE = 101;
        static int DOWNLOAD_SERVICE = 102;
        static int SYNCHRONIZE_SERVICE = 103;
        static int MEDIA_NOTIFICATION_MANAGER = 104;
    }

    public interface  SYNCHRONIZE_SERVICE{
        static final long REPEAT_TIME = 1000 * 3600;
        static final String NOTIFICATION = "maximomrtnz.podcastmanager.services.receiver";
        static final String RESULT = "result";
    }

    public interface PLAYER_SERVICE{
        static final String FILTER = "maximomrtnz.podcastmanager.communication.REQUEST_PROCESSED";
        static final String COMMAND = "COMMAND";
        static final String DATA = "DATA";
        static final int EPISODE_CHANGE = 1;
        static final int STATE_PLAYING = 2;
        static final int STATE_PAUSED = 3;
        static final int STATE_PREPARING = 4;
        static final int STATE_STOPPED = 5;
        static final int STATE_RETRIVING = 6;
        static final String PREFERENCE_LAST_EPISODE_PLAYED_URL = "LAST_EPISODE_PLAYED_URL";
        static final String PREFERENCE_LAST_EPISODE_PLAYED_DURATION = "LAST_EPISODE_PLAYED_DURATION";
    }

    public interface DIRECTORIES{
        static final String ROOT = "PodcastManager";
        static final String IMAGES = "Images";
        static final String DOWNLOADS = "Downloads";
        static final String FEEDS = "Feeds";
    }


}
