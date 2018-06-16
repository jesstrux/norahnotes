package akil.co.tz.notetaker.models;

import java.io.Serializable;

/**
 * Created by DevDept on 6/14/18.
 */

public class Book implements Serializable {
    private String title;
    private String[] chapters;
    private int[] verses;

    public Book(String title, String[] chapters, int[] verses) {
        this.title = title;
        this.chapters = chapters;
        this.verses = verses;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getChapters() {
        return chapters;
    }

    public void setChapters(String[] chapters) {
        this.chapters = chapters;
    }

    public String[] getChapterVerses(int index) {
        String[] verse_array = new String[verses[index]];

        for (int i = 1; i <= verses[index]; i++){
            verse_array[i] = "" + i;
        }

        return verse_array;
    }
}
