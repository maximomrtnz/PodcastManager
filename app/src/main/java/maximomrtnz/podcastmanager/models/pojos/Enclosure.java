package maximomrtnz.podcastmanager.models.pojos;

import android.content.ContentValues;

import maximomrtnz.podcastmanager.database.PodcastManagerContract;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by Maxi on 11/17/2015.
 */
public class Enclosure {

    private Long id;
    private String url;
    private String type;
    private Long length;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentValues toContentValue(){

        // Defines an object to contain the new values to insert
        ContentValues mNewValues = new ContentValues();

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */

        if(getId()!=null){
            mNewValues.put(PodcastManagerContract.Enclosure._ID, getId());
        }

        mNewValues.put(PodcastManagerContract.Enclosure.COLUMN_NAME_LENGTH, getLength());
        mNewValues.put(PodcastManagerContract.Enclosure.COLUMN_NAME_TYPE, getType());
        mNewValues.put(PodcastManagerContract.Enclosure.COLUMN_NAME_URL, getUrl());

        return mNewValues;

    }
}
