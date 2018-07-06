package akil.co.tz.notetaker.models;

public class Notification {
    private String title;
    private String message;
    private String type;
    private String data = null;
    private long received_at;

    public Notification(String title, String message, String type, long received_at) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.received_at = received_at;
    }

    public Notification(String title, String message, String type, long received_at, String data) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.data = data;
        this.received_at = received_at;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getReceivedAt() {
        return received_at;
    }

    public void setReceivedAt(long received_at) {
        this.received_at = received_at;
    }
}
