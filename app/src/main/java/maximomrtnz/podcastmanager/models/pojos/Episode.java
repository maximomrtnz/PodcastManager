package maximomrtnz.podcastmanager.models.pojos;

import android.content.ContentValues;

import java.util.Calendar;
import java.util.Date;

import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by Maxi on 11/17/2015.
 */
public class Episode {

    private Long id;
    private String title;
    private String link;
    private String imageUrl;
    private String itunesAuthor;
    private String itunesDuration;
    private String itunesSubtitle;
    private String itunesSummary;
    private Calendar pubDate;
    private Enclosure enclosure;
    private String guid;
    private String description;

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

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
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
    public ContentValues toContentValue(){

        // Defines an object to contain the new values to insert
        ContentValues mNewValues = new ContentValues();

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
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_PUB_DATE, Utils.formatDateAsLong(getPubDate()));
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_LINK, getLink());
        mNewValues.put(PodcastManagerContract.Episode.COLUMN_NAME_IMAGE_URL, getImageUrl());

        return mNewValues;

    }

}
