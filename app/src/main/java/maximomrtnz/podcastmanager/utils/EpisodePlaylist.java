package maximomrtnz.podcastmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import maximomrtnz.podcastmanager.cache.FileCache;
import maximomrtnz.podcastmanager.models.pojos.Episode;
import maximomrtnz.podcastmanager.models.pojos.Podcast;

/**
 * Created by maximo on 04/09/16.
 */

public class EpisodePlaylist {

    private static String LOG_TAG = "EpisodePlaylist";
    private static EpisodePlaylist mInstance = null;

    public static EpisodePlaylist getInstance() {
        if (mInstance == null) {
            mInstance = new EpisodePlaylist();
        }
        return mInstance;
    }

    private TreeMap<String, MediaMetadataCompat> episodes = new TreeMap<>();
    private HashMap<String, String> episodesImages = new HashMap<>();
    private HashMap<String, String> episodesSources = new HashMap<>();
    private HashMap<String, Podcast> podcastsByMediaId = new HashMap<>();
    private HashMap<String, Episode> episodesByMediaId = new HashMap<>();

    public String getRoot() {
        return "root";
    }

    public Episode getEpisodeByMediaId(String mediaId){
        return episodesByMediaId.get(mediaId);
    }

    public Podcast getPodcastByMediaId(String mediaId){
        return podcastsByMediaId.get(mediaId);
    }

    public String getEpisodeUri(String mediaId){
        return episodesSources.get(mediaId);
    }

    public List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata: episodes.values()) {
            result.add(new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public MediaBrowserCompat.MediaItem getMediaItemById(String id){
        MediaBrowserCompat.MediaItem item = null;
        for (MediaMetadataCompat metadata: episodes.values()) {
            if(metadata.getDescription().getMediaId().equals(id)){
                item = new MediaBrowserCompat.MediaItem(metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
                break;
            }
        }
        return item;
    }

    public String getPreviousEpisode(String currentMediaId) {
        String prevMediaId = episodes.lowerKey(currentMediaId);
        if (prevMediaId == null) {
            prevMediaId = episodes.firstKey();
        }
        return prevMediaId;
    }

    public String getNextEpisode(String currentMediaId) {
        String nextMediaId = episodes.higherKey(currentMediaId);
        if (nextMediaId == null) {
            nextMediaId = episodes.firstKey();
        }
        return nextMediaId;
    }

    public MediaMetadataCompat getMetadata(String mediaId) {

        MediaMetadataCompat metadataWithoutBitmap = episodes.get(mediaId);

        // Since MediaMetadata is immutable, we need to create a copy to set the album art
        // We don't set it initially on all items so that they don't take unnecessary memory
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key: new String[]{  MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                                        MediaMetadataCompat.METADATA_KEY_GENRE,
                                         MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                        MediaMetadataCompat.METADATA_KEY_TITLE}) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

        return builder.build();
    }

    public String createMediaMetadata(Podcast podcast, Episode episode) {

        String mediaId = Utils.md5Encode(episode.getEpisodeUrl());

        episodes.put(Utils.md5Encode(episode.getEpisodeUrl()),

                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, podcast.getTitle())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, podcast.getItunesAuthor())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, DateUtils.timeToSeconds(episode.getItunesDuration())*1000)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, podcast.getItunesSumary())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, podcast.getImageUrl())
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, episode.getImageUrl())
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.getTitle())
                        .build());

        episodesImages.put(mediaId,Constants.DIRECTORIES.ROOT+"/"+Constants.DIRECTORIES.IMAGES+"/"+FileCache.getCacheFileName(episode.getImageUrl()));

        episodesSources.put(mediaId,episode.getEpisodeUrl());

        podcastsByMediaId.put(mediaId, podcast);

        episodesByMediaId.put(mediaId, episode);

        return mediaId;

    }


}
