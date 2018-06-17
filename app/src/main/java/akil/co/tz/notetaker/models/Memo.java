package akil.co.tz.notetaker.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by DevDept on 6/6/18.
 */

@Entity
public class Memo implements Serializable {
    @Ignore
    public Memo(int id, String title, String body, String recepientName, int recepientId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.recepientName = recepientName;
        this.recepientId = recepientId;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String body;
    private String recepientName;
    private int recepientId;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecepientName() {
        return recepientName;
    }

    public void setRecepientName(String recepientName) {
        this.recepientName = recepientName;
    }

    public int getRecepientId() {
        return recepientId;
    }

    public void setRecepientId(int recepientId) {
        this.recepientId = recepientId;
    }

    @Override
    public String toString() {
        return body;
    }
}
