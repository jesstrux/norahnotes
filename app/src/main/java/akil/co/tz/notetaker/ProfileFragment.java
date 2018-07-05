package akil.co.tz.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.MemoAdapter;
import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.Navigation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    Switch is_offline_switch;
    LinearLayout is_offline_wrapper;
    Boolean offline;
    SharedPreferences prefs;
    TextView full_name, role, is_offline, email, phone, department, position;
    User mUser;

    Button logout_btn;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
        offline = prefs.getBoolean("is_offline", false);

        is_offline_wrapper = rootView.findViewById(R.id.is_offline_wrapper);
        is_offline_switch = rootView.findViewById(R.id.is_offline_switch);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        logout_btn = rootView.findViewById(R.id.logout_btn);
//        logout_btn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.adminFragment));

        reflectOfflineState();

        String userJson = prefs.getString("saved_user",null);

        if(userJson != null){
            mUser = new Gson().fromJson(userJson, User.class);

            if(mUser == null || mUser.getName() == null)
                return rootView;

            full_name = rootView.findViewById(R.id.full_name);
            full_name.setText(mUser.getName());

            role = rootView.findViewById(R.id.role);
            if (mUser.getRole() != null)
                role.setText(mUser.getRole());

            email = rootView.findViewById(R.id.email);
            email.setText(mUser.getEmail());

            phone = rootView.findViewById(R.id.phone);
            phone.setText(mUser.getPhone());

            department = rootView.findViewById(R.id.department);
            department.setText(mUser.getDepartment());

            position = rootView.findViewById(R.id.position);
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

        return rootView;
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
    public void onClick(View v) {

        if(v.getId() == R.id.is_offline_wrapper || v.getId() == R.id.is_offline_switch){
            offline = !offline;
            prefs.edit().putBoolean("is_offline", offline).commit();
            reflectOfflineState();
            Log.d("WOURA", "Saved offline state!");
        }
    }
}
