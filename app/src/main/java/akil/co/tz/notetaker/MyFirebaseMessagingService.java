package akil.co.tz.notetaker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Map;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.NavDeepLinkBuilder;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WOURA";
    SharedPreferences prefs;
    NotificationUtil notificationUtil;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prefs = getDefaultSharedPreferences(getApplicationContext());
        notificationUtil = new NotificationUtil();

        Log.d(TAG, "FirebaseMessage From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("action_type");

            if (type != null && type.equals(NotificationUtil.TYPE_ACTIVATE_USER)){
                processVerifiedUser();
                return;
            }

            if(!notificationUtil.isDozed(getApplicationContext())){
                sendNotification(remoteMessage.getData());
                Log.d(TAG, "Not offline, sending notification.");
            }else{
                Log.d(TAG, "Offline, saving only.");
            }
        }
    }


    private void processVerifiedUser() {
        Log.d("WOURA", "Received ACTIVATE_USER notification");
        final SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());

        String strJson = prefs.getString("saved_user",null);
        Gson gson = new Gson();
        User user;
        try {
            user = gson.fromJson(strJson, User.class);
            user.setActivated(true);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("saved_user", gson.toJson(user));
            editor.apply();

            if(user.getRole() != null)
                FirebaseMessaging.getInstance().subscribeToTopic(user.getRole().replaceAll("\\s+",""));
            if(user.getDepartment() != null)
                FirebaseMessaging.getInstance().subscribeToTopic(user.getDepartment().replaceAll("\\s+",""));

            sendNotification("You have been activated!", "Hey " + user.getFirstName() + ", we will progress you in a moment.", NotificationUtil.TYPE_ACTIVATE_USER);

            Intent intent = new Intent("userActivated");
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }catch (Exception e){
            Log.d("WOURA", e.getMessage());
        }
    }

    private void sendNotification(Map<String, String> receivedMessage) {
        String title = receivedMessage.get("title");
        String message = receivedMessage.get("message");
        String type = receivedMessage.get("action_type");
        String data = receivedMessage.get("embeded_data");

        Notification notification = new Notification(title, message, type, Calendar.getInstance().getTimeInMillis());

        if(data != null)
            notification = new Notification(title, message, type, Calendar.getInstance().getTimeInMillis(), data);

        notificationUtil.addNotification(getApplicationContext(), notification);

        Intent intent = new Intent("notificationCountAdded");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        sendNotification(title, message, type);
    }

    private void sendNotification(String title, String messageBody, String type) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NavDeepLinkBuilder deepLinkBuilder = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.navigation_graph);

        if(type != null && type.equals(NotificationUtil.TYPE_MEMO_RECEIVED))
            deepLinkBuilder.setDestination(R.id.navigation_memos);
        else if(type != null && type.equals(NotificationUtil.TYPE_MEMO_REPLIED)){
            deepLinkBuilder.setDestination(R.id.navigation_memos); // TODO Replace this with deep link into memo read page
        }
        else if(type != null && type.equals(NotificationUtil.TYPE_USER_REGISTERED))
            deepLinkBuilder.setDestination(R.id.navigation_admin);
        else
            deepLinkBuilder.setDestination(R.id.navigation_notifications);

        PendingIntent pendingIntent = deepLinkBuilder.createPendingIntent();

        String channelId = "admin";
        // Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.unconvinced);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.small_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setNumber(notificationUtil.getUnreadCount(getApplicationContext()))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        ShortcutBadger.applyCount(getApplicationContext(), notificationUtil.getUnreadCount(getApplicationContext())); //for 1.1.4+
//        ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3

        if(type != null){
            int large_icon = NotificationUtil.getResIcon(type);
            if(large_icon != -1){
                notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), large_icon));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setPriority(android.app.Notification.PRIORITY_MAX);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify("SMEMO", 0, notificationBuilder.build());
    }
}
