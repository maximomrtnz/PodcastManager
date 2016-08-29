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
    private Boolean isPlayed = false;
    private Boolean isDirty;

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

    public void loadTo(ContentValues mNewValues){

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */

        if(getId()!=null){
            mNewValues.put(PodcastManagerContract.Episode._ID, getId());
        }

        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_TITLE, getTitle());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION, getDescription());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR, getItunesAuthor());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE, DateUtils.formatDateAsLong(getPubDate()));
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_LINK, getLink());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL, getImageUrl());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID, getPodcastId());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_GUID, getGuid());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION, getItunesDuration());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE, getItunesSubtitle());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUMMARY, getItunesSummary());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL, getEpisodeUrl());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_PLAYED, (getPlayed())?1:0);

    }


    public void loadFrom(Cursor cursor){

        this.setId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode._ID)));
        this.setTitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_TITLE)));
        this.setPodcastId(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_PODCAST_ID)));
        this.setImageUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL)));
        this.setItunesDuration(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_DURATION)));
        this.setDescription(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_DESCRIPTION)));
        this.setItunesAuthor(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_AUTHOR)));
        this.setItunesSummary(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUMMARY)));
        this.setPubDate(DateUtils.getCalendarFromFormattedLong(cursor.getLong(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE))));
        this.setLink(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_LINK)));
        this.setItunesSubtitle(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_ITUNES_SUBTITLE)));
        this.setGuid(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_GUID)));
        this.setEpisodeUrl(cursor.getString(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_EPISODE_URL)));
        this.setPlayed(cursor.getInt(cursor.getColumnIndex(PodcastManagerContract.Episode.COLUMN_NAME_FLAG_PLAYED)) == 1);

    }

}
