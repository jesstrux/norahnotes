package akil.co.tz.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import akil.co.tz.notetaker.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Switch is_offline_switch;
    LinearLayout is_offline_wrapper;
    Boolean offline;
    SharedPreferences prefs;
    TextView full_name, role, is_offline, email, phone, department, position;

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
            User user = (User) intent_extras.getSerializable("user");

            if(user == null || user.getName() == null)
                return;

            full_name = findViewById(R.id.full_name);
            full_name.setText(user.getName());

            role = findViewById(R.id.role);
            if (user.getRole() != null)
                role.setText(user.getRole());

            email = findViewById(R.id.email);
            email.setText(user.getEmail());

            phone = findViewById(R.id.phone);
            phone.setText(user.getPhone());

            department = findViewById(R.id.department);
            department.setText(user.getDepartment());

            position = findViewById(R.id.position);
            position.setText(user.getJob());
        }

        is_offline_wrapper.setOnClickListener(this);
    }

    public void logout(View view){
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("saved_user", null);
        editor.putString("subscribed_to_department", null);
        editor.putString("subscribed_to_role", null);
        editor.commit();

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
