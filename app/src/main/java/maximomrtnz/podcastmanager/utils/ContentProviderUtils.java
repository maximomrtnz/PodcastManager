package maximomrtnz.podcastmanager.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by maximo on 19/08/16.
 */

public class ContentProviderUtils {

    public static boolean isEpisodeInsert(Uri uri){
        String regex = "\\d+";

        if(uri.toString().contains(PodcastManagerContentProvider.EPISODE_CONTENT_URI.toString())){
            return uri.getLastPathSegment().matches(regex);
        }
        return false;
    }

    public static Long getIdFromUri(Uri uri){
        String regex = "\\d+";

        if(uri.getLastPathSegment().matches(regex)){
            return Long.valueOf(uri.getLastPathSegment());
        }

        return 0L;
    }

}
