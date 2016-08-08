package maximomrtnz.podcastmanager.database;

import android.provider.BaseColumns;

/**
 * Created by Maxi on 11/21/2015.
 */
public class PodcastManagerContract {

    /**
    * Podcast Columns
    */
    public static abstract class Podcast implements BaseColumns {

        public static final String TABLE_NAME = "podcasts";
        public static final String TYPE = "podcast";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_FEED_URL = "feed_url";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_LAST_BUILD_DATE = "last_build_date";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final String COLUMN_NAME_ITUNES_SUMMARY = "itunes_summary";
        public static final String COLUMN_NAME_ITUNES_AUTHOR = "itunes_author";
        public static final String COLUMN_NAME_COPYRIGHT = "copyright";

        /**
         * Content Provider
         */
        public static final String CONTENT_PATH = "podcasts";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.podcastmanager_database.podcasts";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.podcastmanager_database.podcasts";

        public static final String WHERE_ID_EQUALS = _ID + " =?";


        public static final String[] PROJECTION_ALL = {
                                                        _ID,
                                                        COLUMN_NAME_FEED_URL,
                                                        COLUMN_NAME_COPYRIGHT,
                                                        COLUMN_NAME_DESCRIPTION,
                                                        COLUMN_NAME_IMAGE_URL,
                                                        COLUMN_NAME_ITUNES_AUTHOR,
                                                        COLUMN_NAME_ITUNES_SUMMARY,
                                                        COLUMN_NAME_LANGUAGE,
                                                        COLUMN_NAME_LAST_BUILD_DATE,
                                                        COLUMN_NAME_PUB_DATE,
                                                        COLUMN_NAME_TITLE,
                                                        COLUMN_NAME_LINK
                                                    };
    }

    /**
     * Episode Columns
     */
    public  static abstract class Episode implements BaseColumns{

        public static final String TABLE_NAME = "episodes";
        public static final String TYPE = "episodes";
        public static final String COLUMN_NAME_PODCAST_ID = "podcast_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_ITUNES_AUTHOR = "itunes_author";
        public static final String COLUMN_NAME_ITUNES_DURATION = "itunes_duration";
        public static final String COLUMN_NAME_ITUNES_SUBTITLE = "itunes_subtitle";
        public static final String COLUMN_NAME_ITUNES_SUMMARY = "itunes_summary";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_GUID = "guid";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final String COLUMN_NAME_EPISODE_URL = "episode_url";

        /**
         * Content Provider
         */
        public static final String CONTENT_PATH = "episodes";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.podcastmanager_database.episodes";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.podcastmanager_database.episodes";

        public static final String WHERE_ID_EQUALS = _ID + " =?";

        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_NAME_PODCAST_ID,
                COLUMN_NAME_ITUNES_DURATION,
                COLUMN_NAME_DESCRIPTION,
                COLUMN_NAME_IMAGE_URL,
                COLUMN_NAME_ITUNES_AUTHOR,
                COLUMN_NAME_ITUNES_SUMMARY,
                COLUMN_NAME_GUID,
                COLUMN_NAME_ITUNES_SUBTITLE,
                COLUMN_NAME_PUB_DATE,
                COLUMN_NAME_TITLE,
                COLUMN_NAME_LINK,
                COLUMN_NAME_EPISODE_URL
        };

    }



}
