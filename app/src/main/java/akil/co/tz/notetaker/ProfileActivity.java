package akil.co.tz.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Switch is_offline_switch;
    LinearLayout is_offline_wrapper;
    Boolean offline;
    SharedPreferences prefs;
    TextView full_name, role, is_offline, email, phone, department, position;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getDefaultSharedPreferences(getApplicationContext());
        offline = prefs.getBoolean("is_offline", false);

        is_offline_wrapper = findViewById(R.id.is_offline_wrapper);
        is_offline_switch = findViewById(R.id.is_offline_switch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reflectOfflineState();

        Bundle intent_extras = getIntent().getExtras();
        if(intent_extras != null){
            mUser = (User) intent_extras.getSerializable("mUser");

            if(mUser == null || mUser.getName() == null)
                return;

            full_name = findViewById(R.id.full_name);
            full_name.setText(mUser.getName());

            role = findViewById(R.id.role);
            if (mUser.getRole() != null)
                role.setText(mUser.getRole());

            email = findViewById(R.id.email);
            email.setText(mUser.getEmail());

            phone = findViewById(R.id.phone);
            phone.setText(mUser.getPhone());

            department = findViewById(R.id.department);
            department.setText(mUser.getDepartment());

            position = findViewById(R.id.position);
            position.setText(mUser.getJob());
        }

        is_offline_wrapper.setOnClickListener(this);
        is_offline_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                offline = is_offline_switch.isChecked();
                prefs.edit().putBoolean("is_offline", offline).commit();
                reflectOfflineState();
            }
        });
    }

    public void logout(View view){
        if(mUser != null){
            if(mUser.getDepartment() != null)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mUser.getDepartment().replaceAll("\\s+",""));

            if(mUser.getRole() != null)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mUser.getRole().replaceAll("\\s+",""));
        }

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

    private void reflectOfflineState() {
        is_offline_switch.setChecked(offline);

//        is_offline = findViewById(R.id.is_offline);
//        is_offline.setText("Go Online");
//
//        if(!offline){
//            is_offline.setText("Go Offline");
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
//                navigateUpTo(new Intent(this, NoteListActivity.class));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.is_offline_wrapper || v.getId() == R.id.is_offline_switch){
            offline = !offline;
            prefs.edit().putBoolean("is_offline", offline).commit();
            reflectOfflineState();
            Log.d("WOURA", "Saved offline state!");
        }
    }
}
