package akil.co.tz.notetaker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WOURA";
    SharedPreferences prefs;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        prefs = getDefaultSharedPreferences(getApplicationContext());

        Log.d(TAG, "FirebaseMessage From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Message: " + message);

            Boolean offline = prefs.getBoolean("is_offline", false);

            if(!offline){
                sendNotification(title, message);
                Log.d(TAG, "Not offline, sending notification.");
            }else{
                Log.d(TAG, "Offline, saving only.");
            }

            NotificationUtil notificationUtil = new NotificationUtil();
            notificationUtil.addNotification(getApplicationContext(), new Notification(title, message));
        }
    }


    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
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

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
