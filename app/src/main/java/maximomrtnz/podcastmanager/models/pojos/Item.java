package maximomrtnz.podcastmanager.models.pojos;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Maxi on 11/17/2015.
 */
public class Item {

    private Long id;
    private String title;
    private String link;
    private ItunesImage itunesImage;
    private String itunesAuthor;
    private Integer itunesDuration;
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

    public ItunesImage getItunesImage() {
        return itunesImage;
    }

    public void setItunesImage(ItunesImage itunesImage) {
        this.itunesImage = itunesImage;
    }

    public String getItunesAuthor() {
        return itunesAuthor;
    }

    public void setItunesAuthor(String itunesAuthor) {
        this.itunesAuthor = itunesAuthor;
    }

    public Integer getItunesDuration() {
        return itunesDuration;
    }

    public void setItunesDuration(Integer itunesDuration) {
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
}
