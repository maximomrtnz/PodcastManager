package maximomrtnz.podcastmanager.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
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

    public static ContentProviderOperation toDeleteOperation(Podcast podcast){

        return ContentProviderOperation.newDelete(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withSelection(PodcastManagerContract.Podcast.WHERE_ID_EQUALS,new String[]{String.valueOf(podcast.getId())})
                .build();

    }

    public static ContentProviderOperation toInsertOperation(Podcast podcast){

        ContentValues podcastContentValue = new ContentValues();

        podcast.loadTo(podcastContentValue);

        return  ContentProviderOperation.newInsert(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withValues(podcastContentValue)
                .build();

    }

    public static ContentProviderOperation toInsertOperation(Episode episode){
        ContentValues episodeContentValue = new ContentValues();

        episode.loadTo(episodeContentValue);

        // Add episode to batch list
        return ContentProviderOperation.newInsert(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                .withValues(episodeContentValue)
                .build();
    }

    public static ContentProviderOperation toUpdateOperation(Episode episode){
        ContentValues episodeContentValue = new ContentValues();

        episode.loadTo(episodeContentValue);

        // Add episode to batch list
        return ContentProviderOperation.newUpdate(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                .withSelection(PodcastManagerContract.Episode.WHERE_ID_EQUALS,new String[]{String.valueOf(episode.getId())})
                .withValues(episodeContentValue)
                .build();
    }

    public static boolean isEpisodeInsert(Uri uri){
        String regex = "\\d+";

        if(uri.toString().contains(PodcastManagerContentProvider.EPISODE_CONTENT_URI.toString())){
            return uri.getLastPathSegment().matches(regex);
        }
        return false;
    }

}
