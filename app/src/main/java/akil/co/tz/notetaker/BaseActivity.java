package akil.co.tz.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BaseActivity extends AppCompatActivity {

    private User mUser;
    private Fragment mHostFragment;
    private BottomNavigationView navigation;

    private SharedPreferences prefs;
    private Boolean offline = false;

    Badge notifications_badge;
    private int mNotificationsIdx = 2;

    NavController navController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if(navController == null)
                return false;

            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    navController.navigate(R.id.dashboardFragment);
                    return true;
                case R.id.navigation_memos:
                    navController.navigate(R.id.memosFragment);
                    return true;
                case R.id.navigation_admin:
                    navController.navigate(R.id.adminFragment);
                    return true;
                case R.id.navigation_notifications:
                    if(notifications_badge != null)
                        notifications_badge.hide(true);

                    navController.navigate(R.id.notificationListFragment);
                    return true;
                case R.id.navigation_profile:
                    navController.navigate(R.id.profileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        navController = Navigation.findNavController(findViewById(R.id.main_nav_host_fragment));

        prefs = getDefaultSharedPreferences(getApplicationContext());
        offline = prefs.getBoolean("is_offline", false);

//        mHostFragment = findViewById(R.id.main_nav_host_fragment);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            mUser = (User) bundle.getSerializable("mUser");

            Log.d("WOURA", mUser.getRole().equals("Admin") + "");
            if(mUser != null && mUser.getRole() != null){
                if(mUser.getRole().equals("Admin")){
                    navigation.getMenu().getItem(2).setVisible(true);
                    mNotificationsIdx = 3;
                }
            }
        }

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) navigation.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(mNotificationsIdx);

        Log.d("WOURA", navigation.getChildCount() + " nav childs");
        Log.d("WOURA", bottomNavigationMenuView.getChildCount() + " nav_view childs");

        if(v != null){
            notifications_badge = new QBadgeView(this).bindTarget(v).setBadgeNumber(5);
            notifications_badge.isExactMode();
        }
        else
            Log.d("WOURA", "View out of bounds");
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

}
