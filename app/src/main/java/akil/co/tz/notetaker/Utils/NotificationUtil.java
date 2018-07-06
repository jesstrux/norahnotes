package akil.co.tz.notetaker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.Notification;

public class NotificationUtil {
    public static final String PREFS_NAME = "Notification_APP";
    public static final String NOTIFICATIONS = "Notification_List";
    public static final String DOZE_MODE_ON = "DOZE_MODE_ON";
    public static final String UNREAD_COUNT = "UNREAD_COUNT";

    public static final String TYPE_MEMO_RECEIVED = "MEMO_RECEIVED";
    public static final String TYPE_MEMO_REPLIED = "MEMO_REPLIED";
    public static final String TYPE_USER_REGISTERED = "USER_REGISTERED";
    public static final String TYPE_ACTIVATE_USER = "ACTIVATE_USER";

    public NotificationUtil() {
        super();
    }

    public static int getResIcon(String memo_type){
        int icon = -1;
        switch (memo_type){
            case TYPE_MEMO_RECEIVED:
                icon = R.drawable.ic_email;
                break;

            case TYPE_MEMO_REPLIED:
                icon = R.drawable.ic_reply;
                break;

            case TYPE_USER_REGISTERED:
                icon = R.drawable.ic_person_add;
                break;
        }

        return icon;
    }

    public int getUnreadCount(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(UNREAD_COUNT, 0);
    }

    public void setUnreadCount(Context context, int count){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(UNREAD_COUNT, count).commit();
    }

    public void addUnreadCount(Context context){
        setUnreadCount(context, getUnreadCount(context) + 1);
    }

    public boolean isDozed(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(DOZE_MODE_ON, false);
    }

    public void toggleDozed(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        boolean is_dozed = settings.getBoolean(DOZE_MODE_ON, false);
        editor.putBoolean(DOZE_MODE_ON, !is_dozed).commit();
    }

    public void saveNotification(Context context, List<Notification> notifications) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonNotifications = gson.toJson(notifications);

        editor.putString(NOTIFICATIONS, jsonNotifications);

        editor.commit();
    }

    public void addNotification(Context context, Notification notification) {
        List<Notification> notifications = getNotifications(context);
        if (notifications == null)
            notifications = new ArrayList<>();
        notifications.add(notification);
        saveNotification(context, notifications);

        addUnreadCount(context);
    }

    public void removeNotification(Context context, Notification Notification) {
        ArrayList<Notification> notifications = getNotifications(context);
        if (notifications != null) {
            notifications.remove(Notification);
            saveNotification(context, notifications);
        }
    }

    public int getNotificationCount(Context context) {
        ArrayList<Notification> notifications = getNotifications(context);
        if(notifications == null)
            return 0;
        else
            return notifications.size();
    }

    public void emptyNotifications(Context context) {
        ArrayList<Notification> notifications = getNotifications(context);
        if (notifications != null) {
            notifications.clear();
            saveNotification(context, notifications);
        }
    }

    public ArrayList<Notification> getNotifications(Context context) {
        SharedPreferences settings;
        List<Notification> notifications;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(NOTIFICATIONS)) {
            String jsonNotifications = settings.getString(NOTIFICATIONS, null);
            Gson gson = new Gson();
            Notification[] notificationItems = gson.fromJson(jsonNotifications,
                    Notification[].class);

            notifications = Arrays.asList(notificationItems);
            notifications = new ArrayList<>(notifications);

            Collections.reverse(notifications);
        } else
            return null;

        return (ArrayList<Notification>) notifications;
    }
}
