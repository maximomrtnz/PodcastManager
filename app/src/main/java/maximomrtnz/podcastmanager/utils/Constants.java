package maximomrtnz.podcastmanager.utils;

/**
 * Created by maximo on 15/08/16.
 */

public class Constants {

    public interface ACTION {
        static String MAIN_ACTION = "maximomrtnz.action.main";
        static String INIT_ACTION = "maximomrtnz.action.action.init";
        static String PREV_ACTION = "maximomrtnz.action.prev";
        static String PLAY_ACTION = "maximomrtnz.action.play";
        static String NEXT_ACTION = "maximomrtnz.action.next";
        static String STARTFOREGROUND_ACTION = "maximomrtnz.action.startforeground";
        static String STOPFOREGROUND_ACTION = "maximomrtnz.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        static int FOREGROUND_SERVICE = 101;
        static int DOWNLOAD_SERVICE = 102;
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
