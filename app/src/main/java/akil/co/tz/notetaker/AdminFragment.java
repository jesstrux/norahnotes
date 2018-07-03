package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AdminFragment extends Fragment {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;

    private String memos_url= "http://192.168.8.109:9000/api/my_memos.php?user_id=";

    private RecyclerView mRecyclerView;
    private TextView no_posts;
    private static final String ARG_USER_ID = "user_id";

    ArrayList<Memo> memoList = new ArrayList<>();
    ArrayList<Memo> originalList = new ArrayList<>();
    MemoAdapter memoAdapter;

    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        new MemoFetchTask().execute(getArguments().getString(ARG_USER_ID));

        memoAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(memoAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        mRecyclerView = rootView.findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assert mRecyclerView != null;

        no_posts = rootView.findViewById(R.id.no_posts);

        setupRecyclerView(mRecyclerView);

        return rootView;
    }

    public class MemoFetchTask extends AsyncTask<String, Void, ArrayList<Memo>> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected ArrayList<Memo> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
            String url = prefs.getString("ip", null);
            String user_id = null;

            String userJson = prefs.getString("saved_user",null);
            User user = new Gson().fromJson(userJson, User.class);
            user_id = user.getId();

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
                    Log.d("WOURA", "Server response: " + response_str);

                    return new Gson().fromJson(response_str.toString(), new TypeToken<List<Memo>>(){}.getType());
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
                originalList.addAll(result);
                memoList.addAll(result);
                memoAdapter.notifyDataSetChanged();

                if(result.size() < 1){
                    no_posts.setVisibility(View.VISIBLE);
                    no_posts.setText("No memos found!");
                }
            }
            else{
                Log.d("WOURA", "Found no memos");
                no_posts.setVisibility(View.VISIBLE);
                no_posts.setText("No memos found!");
            }
        }

        @Override
        protected void onCancelled() {
//            showProgress(false);
        }
    }
}
