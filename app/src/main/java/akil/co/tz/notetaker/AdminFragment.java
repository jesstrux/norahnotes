package akil.co.tz.notetaker;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.AdminItemAdapter;
import akil.co.tz.notetaker.Adapters.MemoAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.models.AdminItem;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AdminFragment extends Fragment {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;
    private static final String ARG_USER_ID = "user_id";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private  int mPostition;
    private Button addBtn;
    private TextView pageTitle;
    private View fakeNavBarSpacer;
    private LinearLayout fakeNavBar;

    AppBarLayout appBar;
    private String m_Title;

    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        appBar = rootView.findViewById(R.id.app_bar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = rootView.findViewById(R.id.tabs);

        addBtn = rootView.findViewById(R.id.addBtn);
        fakeNavBar = rootView.findViewById(R.id.fake_navbar);
        pageTitle = rootView.findViewById(R.id.pageTitle);
        fakeNavBarSpacer = rootView.findViewById(R.id.fake_navbar_spacer);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItem();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();

                final ChangeBounds transition = new ChangeBounds();
                transition.setDuration(200L);
                TransitionManager.beginDelayedTransition(fakeNavBar, transition);
                if(i == 2){
                    fakeNavBarSpacer.setVisibility(View.GONE);
                    addBtn.setVisibility(View.GONE);
                }
                else{
                    String[] btn_titles = {"Create Dept.", "Create Job"};
                    addBtn.setText(btn_titles[i]);
                    addBtn.setVisibility(View.VISIBLE);
                    fakeNavBarSpacer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        return rootView;
    }

    public void setAppBarElevation(int elevation){
        if(appBar != null)
            appBar.setElevation(elevation);
    }

    public void showAddItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] types = {"Department", "Job", "Staff"};
        final String type = types[mPostition - 1];
        builder.setTitle("Add " + type);

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Title = input.getText().toString();

                ((BaseActivity) getActivity()).showProgress("Saving " + type + "...");
                new AdminAddTask().execute(type);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private String memos_url= "http://192.168.8.109:9000/api/my_memos.php?user_id=";

        private RecyclerView mRecyclerView;
        private TextView no_posts;

        ArrayList<AdminItem> adminItems = new ArrayList<>();
        AdminItemAdapter adminItemAdapter;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.admin_item_list, container, false);

            mRecyclerView = rootView.findViewById(R.id.admin_item_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            assert mRecyclerView != null;

            no_posts = rootView.findViewById(R.id.no_posts);

            setupRecyclerView(mRecyclerView);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (mRecyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
                        ((AdminFragment)getParentFragment()).setAppBarElevation(5);
                    } else {
                        ((AdminFragment)getParentFragment()).setAppBarElevation(0);
                    }
                }
            });

            return rootView;
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
            String[] types = {"Department", "Job", "Staff"};
            String type = types[getArguments().getInt(ARG_SECTION_NUMBER)];
            new AdminDataFetchTask().execute(type);

            String[] messages = {"Departments", "Jobs", "Staff Members"};
            no_posts.setText("No " + messages[getArguments().getInt(ARG_SECTION_NUMBER)] + " found!");

            adminItemAdapter = new AdminItemAdapter(adminItems);
            recyclerView.setAdapter(adminItemAdapter);
        }

        public class AdminDataFetchTask extends AsyncTask<String, Void, ArrayList<AdminItem>> {
            private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            @Override
            protected ArrayList<AdminItem> doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();
                SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
                String url = prefs.getString("ip", null);
                String user_id = null;
                String mType = null;

                String userJson = prefs.getString("saved_user",null);
                User user = new Gson().fromJson(userJson, User.class);
                user_id = user.getId();

                mType = params[0];

                Log.d("WOURA", "Type is: " + mType);

                if(url == null || user_id == null || mType == null)
                    return null;

                url += "/api/admin_data.php?user_id=" + user_id;

                Request.Builder builder = new Request.Builder();
                builder.url(url);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();

                    if(response.body() != null) {
                        String response_str = response.body().string();
                        Log.d("WOURA", "Server response: " + response_str);

                        ArrayList<AdminItem> adminItems = new Gson().fromJson(response_str.toString(), new TypeToken<List<AdminItem>>(){}.getType());
                        ArrayList<AdminItem> result_memos = new ArrayList<>();

                        for( AdminItem a : adminItems) {
                            if (a.getType().equals(mType)) {
                                result_memos.add(a);
                            }
                        }

                        return result_memos;
                    }

                    return null;
                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(final ArrayList<AdminItem> result) {
                if(result != null){
                    Log.d("WOURA", "Found " + result.size() + " admin items");
                    adminItems.clear();
                    adminItems.addAll(result);
                    adminItemAdapter.notifyDataSetChanged();

                    if(result.size() < 1){
                        no_posts.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    Log.d("WOURA", "Found no memos");
                    no_posts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onCancelled() {
//            showProgress(false);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            mPostition = position;
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public class AdminAddTask extends AsyncTask<String, Void, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
            String url = prefs.getString("ip", null);
            String user_id = null;
            String mType = null;

            String userJson = prefs.getString("saved_user",null);
            User user = new Gson().fromJson(userJson, User.class);
            user_id = user.getId();

            mType = params[0];

            Log.d("WOURA", "Type is: " + mType);

            if(url == null || user_id == null || mType == null)
                return null;

            url += "/api/add_"+params[0].toLowerCase()+".php";

            try {
                JSONObject obj = new JSONObject();
                obj.put("name", m_Title);

                RequestBody body = RequestBody.create(JSON, obj.toString());

                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(body);
                Request request = builder.build();

                Response response = client.newCall(request).execute();

                if(response.body() != null) {
                    String response_str = response.body().string();
                    Log.d("WOURA", "Server response: " + response_str);

                    return response_str;
                }

                return null;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            ((BaseActivity) getActivity()).hideProgress();
            if(result != null){
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            }
            else{
                Log.d("WOURA", "Found no memos");
            }
        }

        @Override
        protected void onCancelled() {
//            showProgress(false);
        }
    }
}
