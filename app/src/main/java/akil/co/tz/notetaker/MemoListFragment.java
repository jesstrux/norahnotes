package akil.co.tz.notetaker;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akil.co.tz.notetaker.Adapters.MemoAdapter;
import akil.co.tz.notetaker.Adapters.PostAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.dummy.DummyContent;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MemoListFragment extends Fragment implements NoteListActivity.OnFragmentInteractionListener {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;

    private String memos_url= "http://192.168.8.109:9000/api/my_memos.php?user_id=";

    private RecyclerView mRecyclerView;
    private TextView no_posts;

    ArrayList<Memo> memoList = new ArrayList<>();
    ArrayList<Memo> originalList = new ArrayList<>();
    MemoAdapter memoAdapter;

    public MemoListFragment() {
    }

    private static final String ARG_USER_ID = "user_id";


    public static MemoListFragment newInstance(String user_id) {
        MemoListFragment fragment = new MemoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        new MemoFetchTask().execute(memos_url + getArguments().getString(ARG_USER_ID));

        memoAdapter = new MemoAdapter(memoList);
        recyclerView.setAdapter(memoAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assert mRecyclerView != null;

        no_posts = rootView.findViewById(R.id.no_posts);

        setupRecyclerView(mRecyclerView);

        Activity activity = this.getActivity();
        final AppBarLayout appBar = activity.findViewById(R.id.app_bar);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(appBar == null)
                    return;

                if (mRecyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
                    appBar.setElevation(5);
                } else {
                    appBar.setElevation(0);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClearList() {

    }

    @Override
    public void onFilterList(CharSequence item) {
        Log.d("WOURA", "New Filter: " + item.toString());
        ArrayList<Memo> memos = new ArrayList<>();

        memoList.clear();
        if(item.toString().equals("All")){
            memoList.addAll(originalList);
            memoAdapter.notifyDataSetChanged();
        }else{
            for( Memo a : originalList) {
                if (a.getType().equals(item.toString())) {
                    memoList.add(a);
                }
            }

            memoAdapter.notifyDataSetChanged();
        }

        Toast.makeText(getContext(), "Memos filtered!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        NoteListActivity activity = (NoteListActivity) getActivity();
        activity.setListener(this);
    }

    public class MemoFetchTask extends AsyncTask<String, Void, ArrayList<Memo>> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected ArrayList<Memo> doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
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
            else
                Log.d("WOURA", "Found no memos");
        }

        @Override
        protected void onCancelled() {
//            showProgress(false);
        }
    }
}
