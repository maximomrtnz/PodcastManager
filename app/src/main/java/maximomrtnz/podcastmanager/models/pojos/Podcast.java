package maximomrtnz.podcastmanager.models.pojos;

import android.content.ContentValues;

import java.util.Calendar;
import java.util.List;

import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 20/06/16.
 */

public class Podcast {

        private Long id;
        private String title;
        private String description;
        private String link;
        private Calendar pubDate;
        private Calendar lastBuildDate;
        private String language;
        private String itunesSumary;
        private String itunesAuthor;
        private String copyright;
        private List<Episode> episodes;
        private String feedUrl;
        private String imageUrl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Calendar getPubDate() {
            return pubDate;
        }

        public void setPubDate(Calendar pubDate) {
            this.pubDate = pubDate;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getItunesSumary() {
            return itunesSumary;
        }

        public void setItunesSumary(String itunesSumary) {
            this.itunesSumary = itunesSumary;
        }

        public String getItunesAuthor() {
            return itunesAuthor;
        }

        public void setItunesAuthor(String itunesAuthor) {
            this.itunesAuthor = itunesAuthor;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public Calendar getLastBuildDate() {
            return lastBuildDate;
        }

        public void setLastBuildDate(Calendar lastBuildDate) {
            this.lastBuildDate = lastBuildDate;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<Episode> getEpisodes() {
            return episodes;
        }

        public void setEpisodes(List<Episode> episodes) {
            this.episodes = episodes;
        }

        public String getFeedUrl() {
            return feedUrl;
        }

        public void setFeedUrl(String feedUrl) {
            this.feedUrl = feedUrl;
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
                mNewValues.put(PodcastManagerContract.Podcast._ID, getId());
            }

            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_TITLE, getTitle());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_DESCRIPTION, getDescription());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_COPYRIGHT, getCopyright());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_AUTHOR, getItunesAuthor());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_PUB_DATE, Utils.formatDateAsLong(getPubDate()));
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LINK, getLink());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LANGUAGE, getLanguage());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_LAST_BUILD_DATE, Utils.formatDateAsLong(getLastBuildDate()));
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_ITUNES_SUMMARY, getItunesSumary());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_FEED_URL, getFeedUrl());
            mNewValues.put(PodcastManagerContract.Podcast.COLUMN_NAME_IMAGE_URL, getImageUrl());

            return mNewValues;

        }

}
