package akil.co.tz.notetaker.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akil.co.tz.notetaker.models.Post;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Post> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, Post> ITEM_MAP = new HashMap<Integer, Post>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Post item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static Post createDummyItem(int position) {
        String theme = position % 2 == 0 ? "pink" : "blue";
        String title = position % 3 == 0 ? null : "My wonderful post " + position;
        return new Post(position, title, makeDetails(position), theme);
    }

    private static String makeDetails(int position) {
        String lorem = "Lorem ipsum dolor sit, amet consectetur adipisicing elit. Eum at, accusantium sunt suscipit eius alias modi voluptatem neque dicta ipsum reiciendis numquam. Totam obcaecati non repudiandae iure nam dolorem officiis.\n\n";
        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        builder.append("\nMore details information here.\n");

        for (int i = 0; i < position; i++) {
            builder.append(lorem);
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String title;
        public final String details;
        public final String theme;

        public DummyItem(String id, String title, String details, String theme) {
            this.id = id;
            this.title = title;
            this.details = details;
            this.theme = theme;
        }

        @Override
        public String toString() {
            return details;
        }
    }
}
