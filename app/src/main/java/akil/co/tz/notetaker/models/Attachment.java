package akil.co.tz.notetaker.models;

import java.io.Serializable;


public class Attachment implements Serializable {
    private String title;
    private String type;
    private String src;

    public Attachment(String title, String type, String src) {
        this.title = title;
        this.type = type;
        this.src = src;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return title;
    }
}
