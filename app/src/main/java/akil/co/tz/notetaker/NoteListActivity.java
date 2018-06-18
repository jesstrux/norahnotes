package akil.co.tz.notetaker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import akil.co.tz.notetaker.Adapters.PostAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.dummy.DummyContent;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import akil.co.tz.notetaker.ui.dialogs.PostTitleDialog;

import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NoteListActivity extends AppCompatActivity {
    private boolean mTwoPane;
    private Dialog postTitleDialog;
    private EditText title_name;
    private Button submitTitleBtn;
    private ImageButton cancleTitleBtn;
    Menu optionsMenu;
    private  int mPostition;

    SharedPreferences prefs;
    Boolean offline = false;

    private User mUser;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private OnFragmentInteractionListener mListener;

    interface OnFragmentInteractionListener {
        void onClearList();
        void onFilterList(CharSequence item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        prefs = getDefaultSharedPreferences(getApplicationContext());
        offline = prefs.getBoolean("is_offline", false);

        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            mUser = (User) bundle.getSerializable("user");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();

                if(i == 0 && optionsMenu != null){
                    optionsMenu.findItem(R.id.action_filter)
                            .setVisible(true);
                    optionsMenu.findItem(R.id.action_add)
                            .setVisible(true);
                    optionsMenu.findItem(R.id.action_clear_notifications)
                            .setVisible(false);
                    optionsMenu.findItem(R.id.action_notify)
                            .setVisible(false);
                }
                else if(i == 1 && optionsMenu != null){
                    optionsMenu.findItem(R.id.action_filter)
                            .setVisible(false);
                    optionsMenu.findItem(R.id.action_add)
                            .setVisible(false);
                    optionsMenu.findItem(R.id.action_clear_notifications)
                            .setVisible(true);
                    optionsMenu.findItem(R.id.action_notify)
                            .setVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        postTitleDialog = new Dialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        optionsMenu = menu;

        if(offline)
            optionsMenu.findItem(R.id.action_notify)
                .setIcon(R.drawable.ic_notify_off);


        Log.d("WOURA", optionsMenu == null ? "No options memu on created" : "Ipo options menu on created!");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                filterList();
                return true;
            case R.id.action_add:
                createPost();
                return true;
            case R.id.action_clear_notifications:
                clearNotificatoins();
                return true;
            case R.id.action_profile:
                viewProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterList() {
        final CharSequence[] items = {
                "All", "Inbox", "Sent"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Notifications");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.d("WOURA", mListener == null ? "True" : "False");
                if(mListener != null)
                    mListener.onFilterList(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void clearNotificatoins() {
        NotificationUtil notificationUtil = new NotificationUtil();
        notificationUtil.emptyNotifications(getApplicationContext());

        if(mListener != null)
            mListener.onClearList();

        optionsMenu.findItem(R.id.action_clear_notifications)
                .setVisible(false);
    }

    private void viewProfile() {
        Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", mUser);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void createPost() {
//        postTitleDialog.setContentView(R.layout.post_title_dialog);
//        title_name = postTitleDialog.findViewById(R.id.post_title);
//        submitTitleBtn = postTitleDialog.findViewById(R.id.submit_btn);
//        cancleTitleBtn = postTitleDialog.findViewById(R.id.cancel_btn);
//
//        submitTitleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String title = title_name.getText().toString();
//
//                if(title.length() < 3){
//                    Toast.makeText(NoteListActivity.this, "Title empty or too short.", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(NoteListActivity.this, "Title is:  " + title, Toast.LENGTH_SHORT).show();
//                    postTitleDialog.dismiss();
//
//                    savePostTitle(title);
//                }
//            }
//        });
//
//        cancleTitleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                postTitleDialog.dismiss();
//            }
//        });
//
//        postTitleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        postTitleDialog.show();

        Toast.makeText(this, "Create Memo Feature Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void savePostTitle(String title){
        Post new_post = new Post(title, null, null, null);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        long inserted_id = db.postDao().insert(new_post);
        new_post.setId((int) inserted_id);

        openPost(new_post);
    }

    private void openPost(Post new_post){
        Intent intent = new Intent(getBaseContext(), NoteEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", new_post);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainActivity.PlaceholderFragment newInstance(int sectionNumber) {
            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public void setListener(OnFragmentInteractionListener listener){
        mListener = listener;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Log.d("WOURA", optionsMenu == null ? "No options memu" : "Ipo options menu!");
            mPostition = position;

            if(position == 0){
                MemoListFragment memoListFragment = MemoListFragment.newInstance(mUser.getId());
                mListener = (OnFragmentInteractionListener) memoListFragment;
                return memoListFragment;
            }
            else if(position == 1){
                NotificationListFragment notificationListFragment = new NotificationListFragment();
                mListener = (OnFragmentInteractionListener) notificationListFragment;

                return notificationListFragment;
            }

            else
                return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
