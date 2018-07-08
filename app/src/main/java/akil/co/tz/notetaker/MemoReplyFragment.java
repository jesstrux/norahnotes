package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akil.co.tz.notetaker.models.Attachment;
import akil.co.tz.notetaker.models.Memo;
import androidx.navigation.Navigation;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MemoReplyFragment extends Fragment {
    Memo mItem;
    private View rootView;
    EditText content;

    public MemoReplyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memo_reply, container, false);

        mItem = (Memo) getArguments().getSerializable("memo");

        final AppBarLayout appBar = rootView.findViewById(R.id.app_bar);
        final Toolbar toolbar = rootView.findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        final LinearLayout title_bar = rootView.findViewById(R.id.title_bar);
        final TextView title = rootView.findViewById(R.id.title);

        final String memo_title = mItem.getTitle();

        if(memo_title != null && memo_title.length() > 0){
            title.setTextColor(Color.parseColor("#333333"));
            title.setText(memo_title);
        }

        content = rootView.findViewById(R.id.note_detail);

        TextView memoDate = rootView.findViewById(R.id.memo_date);
        memoDate.setText(mItem.getDate());

        TextView memoRecepient = rootView.findViewById(R.id.memo_recepient);
        memoRecepient.setText(mItem.getSenderName());

        NestedScrollView note_detail_container = rootView.findViewById(R.id.note_detail_container);
        note_detail_container.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public static final String TAG = "WOURA";

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 100) {
                    appBar.setElevation(0);
                    toolbar.setTitle("");
                }else{
                    appBar.setElevation(2);
                    toolbar.setTitle(memo_title);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout receivedWrapper = rootView.findViewById(R.id.received_wrapper);
        TextView memoSender = rootView.findViewById(R.id.memo_sender);

        receivedWrapper.setVisibility(View.VISIBLE);
        memoSender.setText(mItem.getSenderName());
    }

    private void goBack(){
        Navigation.findNavController(rootView).navigateUp();
    }
}