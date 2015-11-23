package maximomrtnz.podcastmanager.models.pojos;

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
}
