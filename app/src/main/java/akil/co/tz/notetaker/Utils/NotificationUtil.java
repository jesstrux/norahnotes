package akil.co.tz.notetaker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akil.co.tz.notetaker.models.Notification;

public class NotificationUtil {
    public static final String PREFS_NAME = "Notification_APP";
    public static final String FAVORITES = "Notification_List";

    public NotificationUtil() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveNotification(Context context, List<Notification> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addNotification(Context context, Notification notification) {
        List<Notification> favorites = getNotificaion(context);
        if (favorites == null)
            favorites = new ArrayList<Notification>();
        favorites.add(notification);
        saveNotification(context, favorites);
    }

    public void removeNotification(Context context, Notification Notification) {
        ArrayList<Notification> favorites = getNotificaion(context);
        if (favorites != null) {
            favorites.remove(Notification);
            saveNotification(context, favorites);
        }
    }

    public void emptyNotifications(Context context) {
        ArrayList<Notification> favorites = getNotificaion(context);
        if (favorites != null) {
            favorites.clear();
            saveNotification(context, favorites);
        }
    }

    public ArrayList<Notification> getNotificaion(Context context) {
        SharedPreferences settings;
        List<Notification> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Notification[] favoriteItems = gson.fromJson(jsonFavorites,
                    Notification[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Notification>(favorites);
        } else
            return null;

        return (ArrayList<Notification>) favorites;
    }
}
