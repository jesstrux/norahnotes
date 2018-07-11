package akil.co.tz.notetaker.models;

import java.io.Serializable;


public class AdminItem implements Serializable {
    private int id;
    private String title;
    private String type;
    private User user;

    public AdminItem(int id, String title, String type, User user) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.user = user;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return title;
    }
}
