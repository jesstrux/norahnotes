package akil.co.tz.mzikii;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import akil.co.tz.mzikii.models.User;

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

            Boolean offline = prefs.getBoolean("is_offline", false);

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            if(!offline){
                sendNotification(title, message);
                Log.d(TAG, "Not offline, sending notification.");
            }else{
                Log.d(TAG, "Offline, saving only.");
            }
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "admin";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_add_circle)
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
