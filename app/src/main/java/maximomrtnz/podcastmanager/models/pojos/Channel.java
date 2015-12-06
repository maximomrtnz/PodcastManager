package maximomrtnz.podcastmanager.models.pojos;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Maxi on 11/17/2015.
 */
public class Channel {

    private Long id;
    private String title;
    private String description;
    private String link;
    private Calendar pubDate;
    private Calendar lastBuildDate;
    private String language;
    private ItunesImage itunesImage;
    private String itunesSumary;
    private String itunesAuthor;
    private String copyright;
    private ItunesOwner itunesOwner;
    private List<Item> items;

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

    public ItunesImage getItunesImage() {
        return itunesImage;
    }

    public void setItunesImage(ItunesImage itunesImage) {
        this.itunesImage = itunesImage;
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

    public ItunesOwner getItunesOwner() {
        return itunesOwner;
    }

    public void setItunesOwner(ItunesOwner itunesOwner) {
        this.itunesOwner = itunesOwner;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
