package maximomrtnz.podcastmanager.models.pojos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
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
public class Episode implements Parcelable{

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
    private Integer progress;

    public Episode(){

    }

    protected Episode(Parcel in) {
        title = in.readString();
        link = in.readString();
        imageUrl = in.readString();
        itunesAuthor = in.readString();
        itunesDuration = in.readString();
        itunesSubtitle = in.readString();
        itunesSummary = in.readString();
        guid = in.readString();
        description = in.readString();
        episodeUrl = in.readString();
        id = in.readLong();
        downloadId = in.readLong();
        progress = in.readInt();

        id = (id==-1)?null:id;
        downloadId = (downloadId==-1)?null:downloadId;
        progress = (progress==-1)?null:progress;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(imageUrl);
        dest.writeString(itunesAuthor);
        dest.writeString(itunesDuration);
        dest.writeString(itunesSubtitle);
        dest.writeString(itunesSummary);
        dest.writeString(guid);
        dest.writeString(description);
        dest.writeString(episodeUrl);
        dest.writeLong((id==null)?-1:id);
        dest.writeLong((downloadId==null)?-1:downloadId);
        dest.writeInt((progress==null)?-1:progress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
