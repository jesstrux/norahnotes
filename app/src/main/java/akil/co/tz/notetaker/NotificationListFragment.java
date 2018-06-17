package akil.co.tz.notetaker;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.NotificationAdapter;
import akil.co.tz.notetaker.Adapters.PostAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;
import akil.co.tz.notetaker.models.Post;

public class NotificationListFragment extends Fragment implements NoteListActivity.OnFragmentInteractionListener {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;
    ArrayList<Notification> notifications;
    NotificationAdapter adapter;

    private RecyclerView mRecyclerView;
    private TextView no_posts;

    public NotificationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

//        List<Post> posts = new ArrayList<>();
//        db.postDao().getPosts();

        NotificationUtil notificationUtil = new NotificationUtil();
        notifications = notificationUtil.getNotificaion(getActivity().getApplicationContext());
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        if(notifications == null || notifications.size() < 1){
            no_posts.setVisibility(View.VISIBLE);
            no_posts.setText("No notifications found!");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_list, container, false);

        Activity activity = this.getActivity();
        no_posts = rootView.findViewById(R.id.no_posts);

        mRecyclerView = rootView.findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

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
        notifications.clear();
        adapter.notifyDataSetChanged();

        no_posts.setVisibility(View.VISIBLE);
        no_posts.setText("No notifications found!");
    }
}
