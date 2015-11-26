package maximomrtnz.podcastmanager.database;

import android.provider.BaseColumns;

/**
 * Created by Maxi on 11/21/2015.
 */
public class PodcastManagerContract {

    /**
    * Channel Columns
    */
    public static abstract class Channel implements BaseColumns {
        public static final String TABLE_NAME = "channels";
        public static final String TYPE = "channel";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_LAST_BUILD_DATE = "last_build_date";
        public static final String COLUMN_NAME_LANGUAGE = "language";
        public static final String COLUMN_NAME_ITUNES_IMAGE_ID = "itunes_image_id";
        public static final String COLUMN_NAME_ITUNES_SUMMARY = "itunes_summary";
        public static final String COLUMN_NAME_ITUNES_AUTHOR = "itunes_author";
        public static final String COLUMN_NAME_COPYRIGHT = "copyright";
        public static final String COLUMN_NAME_ITUNES_OWNER_ID = "itunes_owner_id";
    }

    /**
     * Item Columns
     */
    public  static abstract class Item implements BaseColumns{
        public static final String TABLE_NAME = "items";
        public static final String TYPE = "item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CHANNEL_ID = "channel_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_ITUNES_AUTHOR = "itunes_author";
        public static final String COLUMN_NAME_ITUNES_DURATION = "itunes_duration";
        public static final String COLUMN_NAME_ITUNES_SUBTITLE = "itunes_subtitle";
        public static final String COLUMN_NAME_ITUNES_SUMMARY = "itunes_summary";
        public static final String COLUMN_NAME_PUB_DATE = "pub_date";
        public static final String COLUMN_NAME_GUID = "guid";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

    }

    /**
     * ItunesImage Columns
     */
    public static abstract class ItunesImage implements BaseColumns{
        public static final String TABLE_NAME = "itune_images";
        public static final String TYPE = "itune_image";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_SRC = "href";
        public static final String COLUMN_NAME_PARENT_ID = "parent_id";
        public static final String COLUMN_NAME_PARENT_TYPE = "parent_type";
    }

    /**
     * Enclosure Columns
     */
    public static abstract class Enclosure implements BaseColumns{
        public static final String TABLE_NAME = "enclosures";
        public static final String TYPE = "enclosure";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LENGTH = "length";
        public static final String COLUMN_NAME_ITEM_ID = "item_id";
    }

    /**
     * ItunesOwner Columns
     */
    public static abstract class ItunesOwner implements BaseColumns{
        public static final String TABLE_NAME = "itunes_owners";
        public static final String TYPE = "itune_owner";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

}
