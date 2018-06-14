package akil.co.tz.notetaker.Data;

/**
 * Created by DevDept on 6/14/18.
 */

public class Book {
    private String title;
    private int chapter;
    private int startVerse;
    private int endVerse;

    public Book(String title, int chapter, int startVerse, int endVerse) {
        this.title = title;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getStartVerse() {
        return startVerse;
    }

    public void setStartVerse(int startVerse) {
        this.startVerse = startVerse;
    }

    public int getEndVerse() {
        return endVerse;
    }

    public void setEndVerse(int endVerse) {
        this.endVerse = endVerse;
    }
}
