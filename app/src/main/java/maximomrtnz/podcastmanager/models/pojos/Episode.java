package maximomrtnz.podcastmanager.models.pojos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import maximomrtnz.podcastmanager.database.PodcastManagerContentProvider;
import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.utils.DateUtils;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by Maxi on 11/17/2015.
 */
public class Episode {

    private Long id;
    private Long podcastId;
    private Long downloadId;
    private String title;
    private String link;
    private String imageUrl;
    private String itunesAuthor;
    private String itunesDuration;
    private String itunesSubtitle;
    private String itunesSummary;
    private Calendar pubDate;
    private String guid;
    private String description;
    private String episodeUrl;
    private Boolean isPlayed;
    private Boolean isDirty;
    private Boolean isOnPlayQueue;
    private Calendar onPlayQueueTimeStamp;
    private Integer remainderDuration;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getItunesAuthor() {
        return itunesAuthor;
    }

    public void setItunesAuthor(String itunesAuthor) {
        this.itunesAuthor = itunesAuthor;
    }

    public String getItunesDuration() {
        return itunesDuration;
    }

    public void setItunesDuration(String  itunesDuration) {
        this.itunesDuration = itunesDuration;
    }

    public String getItunesSubtitle() {
        return itunesSubtitle;
    }

    public void setItunesSubtitle(String itunesSubtitle) {
        this.itunesSubtitle = itunesSubtitle;
    }

    public Calendar getPubDate() {
        return pubDate;
    }

    public void setPubDate(Calendar pubDate) {
        this.pubDate = pubDate;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getItunesSummary() {
        return itunesSummary;
    }

    public void setItunesSummary(String itunesSummary) {
        this.itunesSummary = itunesSummary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(Long podcastId) {
        this.podcastId = podcastId;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public Boolean getPlayed() {
        return isPlayed;
    }

    public void setPlayed(Boolean played) {
        isPlayed = played;
    }

    public Boolean getDirty() {
        return isDirty;
    }

    public void setDirty(Boolean dirty) {
        isDirty = dirty;
    }

    public Long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }

    public Boolean getOnPlayQueue() {
        return isOnPlayQueue;
    }

    public void setOnPlayQueue(Boolean onPlayQueue) {
        isOnPlayQueue = onPlayQueue;
    }

    public Calendar getOnPlayQueueTimeStamp() {
        return onPlayQueueTimeStamp;
    }

    public void setOnPlayQueueTimeStamp(Calendar onPlayQueueTimeStamp) {
        this.onPlayQueueTimeStamp = onPlayQueueTimeStamp;
    }

    public Integer getRemainderDuration() {
        return remainderDuration;
    }

    public void setRemainderDuration(Integer remainderDuration) {
        this.remainderDuration = remainderDuration;
    }
}
