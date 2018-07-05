package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BaseActivity extends AppCompatActivity {

    ConstraintLayout container;

    private User mUser;
    private Fragment mHostFragment;
    private BottomNavigationView navigation;

    private SharedPreferences prefs;
    private Boolean offline = false;

    Badge notifications_badge;
    private int mNotificationsIdx = 3;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_base);

        container = findViewById(R.id.container);

        navController = Navigation.findNavController(findViewById(R.id.main_nav_host_fragment));

        prefs = getDefaultSharedPreferences(getApplicationContext());
        offline = prefs.getBoolean("is_offline", false);

        navigation = findViewById(R.id.navigation);

        NavigationUI.setupWithNavController(navigation, navController);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.d("WOURA", menuItem.getTitle().toString() + " selected");

                NavigationUI.onNavDestinationSelected(menuItem, navController);

                if(menuItem.getItemId() == R.id.navigation_notifications){
                    if(notifications_badge != null)
                        notifications_badge.hide(true);
                }

                return true;
            }
        });

        String strJson = prefs.getString("saved_user",null);
        Log.d("WOURA", "LOGGED USER: " + strJson);

        if (strJson != null) {
            mUser = new Gson().fromJson(strJson, User.class);

            if(mUser != null && mUser.getRole() != null){
                if(mUser.getRole().equals("Admin")){
                    navigation.getMenu().getItem(2).setVisible(true);
                    mNotificationsIdx = 3;
                }
            }

            BottomNavigationMenuView bottomNavigationMenuView =
                    (BottomNavigationMenuView) navigation.getChildAt(0);
            View v = bottomNavigationMenuView.getChildAt(mNotificationsIdx);

            if(v != null){
                notifications_badge = new QBadgeView(this).bindTarget(v)
                        .setBadgeNumber(5)
                        .setBadgeBackgroundColor(Color.parseColor("#ffa500"));
                notifications_badge.isExactMode();
            }
            else
                Log.d("WOURA", "View out of bounds");

            showNav();
        }else{
            logout();
        }
    }

    public void logout(View view){
        logout();
    }

    public void login(){
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        showNav();
    }

    public void showNav(){
        TransitionManager.beginDelayedTransition(container, new Slide());
        navigation.setVisibility(View.VISIBLE);
    }

    private void logout(){
        if(mUser != null){
            if(mUser.getDepartment() != null)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mUser.getDepartment().replaceAll("\\s+",""));

            if(mUser.getRole() != null)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mUser.getRole().replaceAll("\\s+",""));
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("saved_user", null);
        editor.putString("subscribed_to_department", null);
        editor.putString("subscribed_to_role", null);
        editor.commit();

        NotificationUtil notificationUtil = new NotificationUtil();
        notificationUtil.emptyNotifications(getApplicationContext());

        navController.navigate(R.id.loginFragment);

        TransitionManager.beginDelayedTransition(container, new Slide());
        navigation.setVisibility(View.GONE);
    }

}
