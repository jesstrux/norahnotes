package akil.co.tz.notetaker.models;

import java.io.Serializable;


public class Ufs implements Serializable {
    private String name;
    private String level;
    private String status;

    public Ufs(String name, String level, String status) {
        this.name = name;
        this.level = level;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " " + level + " " + status;
    }
}
