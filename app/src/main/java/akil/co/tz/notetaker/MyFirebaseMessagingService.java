package akil.co.tz.notetaker;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.NavDeepLinkBuilder;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WOURA";
    SharedPreferences prefs;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prefs = getDefaultSharedPreferences(getApplicationContext());

        Log.d(TAG, "FirebaseMessage From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("action_type");

            if (type != null && type.equals("ACTIVATE_USER")){
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

                    sendNotification("You have been activated!", "Hey " + user.getFirstName() + ", we will progress you in a moment.", type);

                    Intent intent = new Intent("userActivated");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }catch (Exception e){
                    Log.d("WOURA", e.getMessage());
                }
                return;
            }

            Boolean offline = prefs.getBoolean("is_offline", false);

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            if(!offline){
                sendNotification(title, message, type);
                Log.d(TAG, "Not offline, sending notification.");
            }else{
                Log.d(TAG, "Offline, saving only.");
            }

            NotificationUtil notificationUtil = new NotificationUtil();
            notificationUtil.addNotification(getApplicationContext(), new Notification(title, message));
        }
    }

    private void sendNotification(String title, String messageBody, String type) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        NavDeepLinkBuilder deepLinkBuilder = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.navigation_graph);

        if(type != null && type.equals("MEMO_RECEIVED"))
            deepLinkBuilder.setDestination(R.id.navigation_memos);
        else if(type != null && type.equals("USER_REGISTERED"))
            deepLinkBuilder.setDestination(R.id.navigation_admin);
        else
            deepLinkBuilder.setDestination(R.id.navigation_notifications);

        PendingIntent pendingIntent = deepLinkBuilder.createPendingIntent();

        String channelId = "admin";
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.unconvinced);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.small_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify("SMEMO", 0, notificationBuilder.build());
    }
}
