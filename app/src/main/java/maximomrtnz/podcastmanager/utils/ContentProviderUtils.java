package maximomrtnz.podcastmanager.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by maximo on 19/08/16.
 */

public class ContentProviderUtils {

    public static ArrayList<ContentProviderOperation> toContentProviderOperation(List<Episode> list, long podcastId){

        ArrayList<ContentProviderOperation> episodes = new ArrayList<>();

        for(Episode episode : list){

            // If we dont have episode's URL then we don't add it
            if(episode.getEpisodeUrl()==null){
                continue;
            }

            ContentValues episodeContentValue = new ContentValues();

            // Set podcast id
            episode.setPodcastId(podcastId);

            episode.loadTo(episodeContentValue);

            // Add apisode to batch list
            episodes.add(ContentProviderOperation.newInsert(PodcastManagerContentProvider.EPISODE_CONTENT_URI)
                    .withValues(episodeContentValue)
                    .build());

        }

        return episodes;

    }

    public static ArrayList<ContentProviderOperation> toContentProviderOperation(List<Podcast> list){

        ArrayList<ContentProviderOperation> podcasts = new ArrayList<>();

        for(Podcast podcast : list) {

            podcasts.add(toContentProviderOperation(podcast));

        }

        return podcasts;
    }

    public static ContentProviderOperation toContentProviderOperation(Podcast obj){

        ContentValues podcastContentValue = new ContentValues();

        obj.loadTo(podcastContentValue);

        return  ContentProviderOperation.newInsert(PodcastManagerContentProvider.PODCAST_CONTENT_URI)
                .withValues(podcastContentValue)
                .build();

    }

}
