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

    public interface DIRECTORIES{
        static final String ROOT = "PodcastManager";
        static final String IMAGES = "Images";
        static final String DOWNLOADS = "Downloads";
        static final String FEEDS = "Feeds";
    }


}
