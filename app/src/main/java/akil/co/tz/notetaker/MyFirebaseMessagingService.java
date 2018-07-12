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
import akil.co.tz.notetaker.models.Memo;
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
                processVerifiedUser(remoteMessage.getData());
                return;
            }
            else if (type != null && type.equals(NotificationUtil.TYPE_USER_REGISTERED)){
                openVerifyUser(remoteMessage.getData());
                return;
            }

            sendNotification(remoteMessage.getData(), notificationUtil.isDozed(getApplicationContext()));
        }
    }

    private void openVerifyUser(Map<String, String> receivedMessage) {
        String title = receivedMessage.get("title");
        String message = receivedMessage.get("message");
        String type = receivedMessage.get("action_type");
        String user_data = receivedMessage.get("embeded_data");

        NavDeepLinkBuilder deepLinkBuilder = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.navigation_graph);

        deepLinkBuilder.setDestination(R.id.VerifyUserFragment);

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", new Gson().fromJson(user_data, User.class));

        deepLinkBuilder.setArguments(bundle);

        PendingIntent pendingIntent = deepLinkBuilder.createPendingIntent();

        String channelId = "admin";
        Uri defaultSoundUri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.unconvinced);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.small_logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setNumber(notificationUtil.getUnreadCount(getApplicationContext()))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify("SMEMO", 0, notificationBuilder.build());
    }


    private void processVerifiedUser(Map<String, String> receivedMessage) {
        String user_data = receivedMessage.get("embeded_data");

        Log.d("WOURA", "Received ACTIVATE_USER notification");

        User user;
        user = new Gson().fromJson(user_data, User.class);
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("saved_user", user_data);
            editor.apply();

            if(user.getRole() != null)
                FirebaseMessaging.getInstance().subscribeToTopic(user.getRole().replaceAll("\\s+",""));
            if(user.getDepartment() != null)
                FirebaseMessaging.getInstance().subscribeToTopic(user.getDepartment().replaceAll("\\s+",""));

            if(user.isAdmin()){
                FirebaseMessaging.getInstance().subscribeToTopic("Admin");
            }

            sendNotification("You have been activated!", "Hey " + user.getFirstName() + ", we will progress you in a moment.", NotificationUtil.TYPE_ACTIVATE_USER, null);

            Intent intent = new Intent("userActivated");
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }catch (Exception e){
            Log.d("WOURA", e.getMessage());
        }
    }

    private void sendNotification(Map<String, String> receivedMessage, boolean isDozed) {
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

        if(!isDozed)
            sendNotification(title, message, type, data);
    }

    private void sendNotification(String title, String messageBody, String type, String data) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NavDeepLinkBuilder deepLinkBuilder = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.navigation_graph);

        if(type != null && type.equals(NotificationUtil.TYPE_MEMO_RECEIVED)){
            if(data != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("memo", new Gson().fromJson(data, Memo.class));
                deepLinkBuilder.setArguments(bundle);
                deepLinkBuilder.setDestination(R.id.memoReadFragment);
            }else{
                deepLinkBuilder.setDestination(R.id.navigation_memos);
            }
        }
        else if(type != null && type.equals(NotificationUtil.TYPE_MEMO_REPLIED)){
            if(data != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("memo", new Gson().fromJson(data, Memo.class));
                deepLinkBuilder.setArguments(bundle);
                deepLinkBuilder.setDestination(R.id.memoProgressFragment);
            }else{
                deepLinkBuilder.setDestination(R.id.navigation_memos);
            }
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
