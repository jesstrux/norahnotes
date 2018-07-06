package akil.co.tz.notetaker;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import akil.co.tz.notetaker.Adapters.NotificationAdapter;
import akil.co.tz.notetaker.Utils.NotificationUtil;
import akil.co.tz.notetaker.models.Notification;

public class NotificationListFragment extends Fragment {
    private static final int SCROLL_DIRECTION_UP = -1;
    public static final String ARG_ITEM_ID = "item_id";

    NotificationUtil notificationUtil;

    private boolean isOffline;

    ArrayList<Notification> notifications;
    NotificationAdapter adapter;

    private Button clearNotifcationsBtn;
    private ImageButton toggleDozeModeBtn;

    private RecyclerView mRecyclerView;
    private TextView no_notifications;

    Context appContext;

    public NotificationListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getActivity().getApplicationContext();
        notificationUtil = new NotificationUtil();

        notifications = notificationUtil.getNotifications(appContext);
        isOffline = notificationUtil.isDozed(appContext);

        notificationUtil.setUnreadCount(appContext,0);
        ((BaseActivity)getActivity()).removeNotificationsBadge();

        LocalBroadcastManager.getInstance(appContext).registerReceiver(
                mMessageReceiver, new IntentFilter("notificationCountAdded"));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        no_notifications = rootView.findViewById(R.id.no_notifications);
        clearNotifcationsBtn = rootView.findViewById(R.id.clearNotificationsBtn);
        toggleDozeModeBtn = rootView.findViewById(R.id.toggleDozeModeBtn);

        toggleDozeModeBtn.setImageResource(isOffline ? R.drawable.ic_notify_off : R.drawable.ic_notify_on);
        toggleDozeModeBtn.setAlpha(isOffline ? 0.4f : 1f);


        toggleDozeModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationUtil.toggleDozed(appContext);
                isOffline = !isOffline;
                toggleDozeModeBtn.setImageResource(isOffline ? R.drawable.ic_notify_off : R.drawable.ic_notify_on);
                toggleDozeModeBtn.setAlpha(isOffline ? 0.4f : 1f);
            }
        });

        if(notifications == null || notifications.size() < 1){
            no_notifications.setVisibility(View.VISIBLE);
            clearNotifcationsBtn.setClickable(false);
            clearNotifcationsBtn.setAlpha(0.4f);
        }

        mRecyclerView = rootView.findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        final AppBarLayout appBar = rootView.findViewById(R.id.app_bar);


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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager n = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            n.cancel("SMEMO", 0);

            notifications.clear();
            notifications.addAll(notificationUtil.getNotifications(appContext));
            adapter.notifyDataSetChanged();

            setNotificationsUi();
        }
    };

    private void setNotificationsUi(){
        boolean notifications_available = notifications != null && notifications.size() > 0;

        no_notifications.setVisibility(notifications_available ? View.VISIBLE : View.GONE);

        clearNotifcationsBtn.setClickable(notifications_available);
        clearNotifcationsBtn.setAlpha(notifications_available ? 0.4f : 1f);
    }


    public void clearNotifcations(View view) {
        notificationUtil.emptyNotifications(appContext);

        notifications.clear();
        adapter.notifyDataSetChanged();

        setNotificationsUi();
    }
}
