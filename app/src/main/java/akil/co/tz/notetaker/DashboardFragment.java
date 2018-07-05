package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.MemoAdapter;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class DashboardFragment extends Fragment {
    private static final int SCROLL_DIRECTION_UP = -1;

    public DashboardFragment() {
        // Required empty public constructor
    }

    private String memos_url= "http://192.168.8.109:9000/api/my_memos.php?user_id=";

    private RecyclerView mRecyclerView;
    private TextView no_memos;

    ArrayList<Memo> memoList = new ArrayList<>();
    MemoAdapter memoAdapter;
    User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
        String userJson = prefs.getString("saved_user",null);
        mUser = new Gson().fromJson(userJson, User.class);

        if(mUser != null){
            TextView dept_name = rootView.findViewById(R.id.dept_memos_title);
            dept_name.setText(mUser.getDepartment());
        }

        mRecyclerView = rootView.findViewById(R.id.memo_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assert mRecyclerView != null;

        no_memos = rootView.findViewById(R.id.no_memos);

        setupRecyclerView(mRecyclerView);

        final AppBarLayout appBar = rootView.findViewById(R.id.app_bar);

        NestedScrollView note_detail_container = rootView.findViewById(R.id.nested_scroll_view);
        note_detail_container.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public static final String TAG = "WOURA";

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 100) {
                    appBar.setElevation(0);
                }else{
                    appBar.setElevation(5);
                }
            }
        });

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if(mUser != null)
            new MemoFetchTask().execute(mUser.getId());

        no_memos.setText("No recent memos found!");

        memoAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(memoAdapter);
    }

    public class MemoFetchTask extends AsyncTask<String, Void, ArrayList<Memo>> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected ArrayList<Memo> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
            String url = prefs.getString("ip", null);
            String user_id = params[0];

            if(url == null || user_id == null)
                return null;

            url += "/api/my_memos.php?user_id=" + user_id;

            Request.Builder builder = new Request.Builder();
            builder.url(url);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();

                if(response.body() != null) {
                    String response_str = response.body().string();

                    ArrayList<Memo> memos = new Gson().fromJson(response_str.toString(), new TypeToken<List<Memo>>(){}.getType());

                    return memos;
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
                    no_memos.setVisibility(View.VISIBLE);
                }
            }
            else{
                Log.d("WOURA", "Found no memos");
                no_memos.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
//            showProgress(false);
        }
    }
}