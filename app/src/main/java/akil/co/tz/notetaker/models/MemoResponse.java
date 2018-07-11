package akil.co.tz.notetaker.models;

import java.io.Serializable;


public class MemoResponse implements Serializable {
    private String name;
    private String status;
    private String comment;

    public final static int STATUS_UNKNOWN = 0;
    public final static int STATUS_ACCEPTED = 1;
    public final static int STATUS_REJECTED = 2;

    public MemoResponse(String name, String status, String comment) {
        this.name = name;
        this.comment = comment;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String level) {
        this.comment = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " " + comment + " " + status;
    }
}
