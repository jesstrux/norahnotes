package akil.co.tz.notetaker;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());

        String strJson = prefs.getString("saved_user",null);
        Log.d("WOURA", "LOGGED USER: " + strJson);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("userActivated"));

        if(getIntent().getStringExtra("unconfirmed") != null){
            setContentView(R.layout.not_confirmed);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                User user = objectMapper.readValue(strJson, User.class);
                TextView hello_text = findViewById(R.id.hello_text);
                hello_text.setText("Hey " + user.getFirstName() + ",");

            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            if (strJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    User user = objectMapper.readValue(strJson, User.class);

                    if(user.isActivated() != null && user.isActivated()){
                        goIn(user);

                        return;
                    }

                    setContentView(R.layout.not_confirmed);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }else{
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
    }

    private void goIn(User user) {
        Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mUser", user);
        intent.putExtras(bundle);
        startActivity(intent);

        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel("SMEMO", 0);
            goIn((User) intent.getSerializableExtra("user"));
        }
    };

    public void logout(View view){
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("saved_user", null);
        editor.putString("subscribed_to_department", null);
        editor.putString("subscribed_to_role", null);
        editor.commit();

        NotificationUtil notificationUtil = new NotificationUtil();
        notificationUtil.emptyNotifications(getApplicationContext());

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
