package akil.co.tz.notetaker.models;

public class Notification {
    String title;
    String message;
//    String type = null;
//    String data = null;
//    String time = null;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
