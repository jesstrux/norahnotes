package akil.co.tz.notetaker;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.MemoAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MemosFragment extends Fragment implements MemoAdapter.ItemClickCallback {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;
    private static final String ARG_USER_ID = "user_id";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private  int mPostition;

    AppBarLayout appBar;
    View rootView;

    public MemosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memos, container, false);

        appBar = rootView.findViewById(R.id.app_bar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = rootView.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = rootView.findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setCurrentItem(1);

        return rootView;
    }

    public void setAppBarElevation(int elevation){
        if(appBar != null)
            appBar.setElevation(elevation);
    }

    @Override
    public void onClick(Bundle b) {
        ((BaseActivity)getActivity()).hideNav();
        NavController navController = Navigation.findNavController(rootView);
        navController.navigate(R.id.memoReadFragment, b);
    }

    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private String memos_url= "http://192.168.8.109:9000/api/my_memos.php?user_id=";

        private RecyclerView mRecyclerView;
        private TextView no_posts;

        ArrayList<Memo> memoList = new ArrayList<>();
        MemoAdapter memoAdapter;

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
            View rootView = inflater.inflate(R.layout.memo_list, container, false);

            mRecyclerView = rootView.findViewById(R.id.memo_list);
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
                        ((MemosFragment)getParentFragment()).setAppBarElevation(5);
                    } else {
                        ((MemosFragment)getParentFragment()).setAppBarElevation(0);
                    }
                }
            });

            return rootView;
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
            String[] types = {"Drafts", "Inbox", "Sent"};
            String type = types[getArguments().getInt(ARG_SECTION_NUMBER)];
            new MemoFetchTask().execute(type);

            String[] messages = {"Memo Drafts", "Inbox Memos", "Sent Memos"};
            no_posts.setText("No " + messages[getArguments().getInt(ARG_SECTION_NUMBER)] + " found!");

            memoAdapter = new MemoAdapter(memoList);
            memoAdapter.setItemClickCallback((MemosFragment) getParentFragment());
            recyclerView.setAdapter(memoAdapter);
        }

        public class MemoFetchTask extends AsyncTask<String, Void, ArrayList<Memo>> {
            private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            @Override
            protected ArrayList<Memo> doInBackground(String... params) {
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

                url += "/api/my_memos.php?user_id=" + user_id;

                Request.Builder builder = new Request.Builder();
                builder.url(url);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();

                    if(response.body() != null) {
                        String response_str = response.body().string();
//                        Log.d("WOURA", "Server response: " + response_str);

                        ArrayList<Memo> memos = new Gson().fromJson(response_str.toString(), new TypeToken<List<Memo>>(){}.getType());
                        ArrayList<Memo> result_memos = new ArrayList<>();

                        for( Memo a : memos) {
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
            protected void onPostExecute(final ArrayList<Memo> result) {
                if(result != null){
                    Log.d("WOURA", "Found " + result.size() + " memos");
                    memoList.clear();
                    memoList.addAll(result);
                    memoAdapter.notifyDataSetChanged();

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

            if(position == 0){
                Log.d("WOURA", "Switched to pos 0");
            }
            else if(position == 1){
                Log.d("WOURA", "Switched to pos 1");
            }

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
