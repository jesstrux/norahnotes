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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.User;

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
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    User user = objectMapper.readValue(strJson, User.class);
                    user.setActivated(true);

                    final User mUser = user;

                    Gson gson = new Gson();
                    String jsonUser = gson.toJson(user);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_user", jsonUser);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "You've beeb confirmed!", Toast.LENGTH_SHORT).show();

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            subscribeUSer(prefs, mUser);
                        }
                    });
                }catch (Exception e){
                    Log.d("WOURA", e.getMessage());
                }
                return;
            }

            Boolean offline = prefs.getBoolean("is_offline", false);

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Message: " + message);

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

    private void subscribeUSer(final SharedPreferences prefs, final User user) {
        Boolean subscribed_to_department = prefs.getString("subscribed_to_department", null) != null;
        final Boolean subscribed_to_role = prefs.getString("subscribed_to_role", null) != null;

        if(user.getDepartment() == null || subscribed_to_department){
            Toast.makeText(getApplicationContext(), "Already Subscribed to department", Toast.LENGTH_SHORT).show();

            if(user.getRole() != null && !subscribed_to_role){
                subscribeToRole(user, prefs);
            }else{
                Toast.makeText(getApplicationContext(), "Already Subscribed to role", Toast.LENGTH_SHORT).show();
                goIn(user);
            }
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic(user.getDepartment().replaceAll("\\s+",""))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("subscribed_to_department", "true");
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Subscribed to department", Toast.LENGTH_SHORT).show();
                            }

                            if(user.getRole() != null && !subscribed_to_role){
                                subscribeToRole(user, prefs);
                            }else{
                                goIn(user);
                            }
                        }
                    });
        }
    }

    private void subscribeToRole(final User user, final SharedPreferences prefs){
        FirebaseMessaging.getInstance().subscribeToTopic(user.getRole().replaceAll("\\s+",""))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("subscribed_to_role", "true");
                            editor.apply();
                        }

                        Toast.makeText(getApplicationContext(), "Subscribed to role", Toast.LENGTH_SHORT).show();
                        goIn(user);
                    }
                });
    }

    private void goIn(User user){
        Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mUser", user);
        intent.putExtras(bundle);
        startActivity(intent);
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
