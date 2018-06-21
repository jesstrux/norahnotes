package akil.co.tz.mzikii.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by DevDept on 6/6/18.
 */

@Entity
public class Post implements Serializable {
    @Ignore
    public Post(int id, String title, String details, String theme) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.theme = theme;
    }

    public Post(String title, String coverUrl, String details, String theme) {
        this.title = title;
        this.coverUrl = coverUrl;
        this.details = details;
        this.theme = theme;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String coverUrl;
    private String details;
    private String theme;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String cover_url) {
        this.coverUrl = cover_url;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return details;
    }
}
