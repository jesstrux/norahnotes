package akil.co.tz.notetaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.RepliesAdapter;
import akil.co.tz.notetaker.Adapters.ResponseAdapter;
import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.Ufs;
import androidx.navigation.Navigation;

public class MemoProgressFragment extends Fragment {
    Memo mItem;
    private View rootView;
    EditText content;

    public MemoProgressFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memo_progress, container, false);

        mItem = (Memo) getArguments().getSerializable("memo");

        final AppBarLayout appBar = rootView.findViewById(R.id.app_bar);
        final Toolbar toolbar = rootView.findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle(mItem.getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        NestedScrollView note_detail_container = rootView.findViewById(R.id.note_detail_container);
        note_detail_container.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public static final String TAG = "WOURA";

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 100) {
                    appBar.setElevation(0);
                }else{
                    appBar.setElevation(2);
                }
            }
        });

        if(mItem.getUfs() != null && mItem.getUfs().size() > 0){
            Context appContext = getActivity().getApplicationContext();
            RelativeLayout progressWrapper = rootView.findViewById(R.id.progressWrapper);
            DifferentColorCircularBorder border = new DifferentColorCircularBorder(progressWrapper);
            int pass = 360 / mItem.getUfs().size();
            String progress = "IN PROGRESS";

            for (int i = 0; i < mItem.getUfs().size(); i++){
                Ufs ufs = mItem.getUfs().get(i);
                int status = Integer.valueOf(ufs.getStatus());

                if(!progress.equals("REJECTED")){
                    if(status == Ufs.STATUS_UNKNOWN)
                        progress = "IN PROGRESS";
                    else if(status == Ufs.STATUS_ACCEPTED)
                        progress = "ACCEPTED";
                    else
                        progress = "REJECTED";
                }

                String color = "#DDDDDD";
                switch (status){
                    case Ufs.STATUS_ACCEPTED:
                        color = "#338833";
                        break;
                    case Ufs.STATUS_REJECTED:
                        color = "#FF5555";
                        break;
                }

                border.addBorderPortion(appContext, Color.parseColor(color), pass * i, (pass * (i + 1) - 10));
            }

            TextView progressText = rootView.findViewById(R.id.progress_text);
            progressText.setText(progress);

            View ufs_separator = rootView.findViewById(R.id.ufs_separator);
            ufs_separator.setVisibility(View.VISIBLE);

            RecyclerView ufs_list = rootView.findViewById(R.id.ufs_list);
            ufs_list.setVisibility(View.VISIBLE);
            ufs_list.setLayoutManager(new LinearLayoutManager(getContext()));
            ufs_list.setAdapter(new RepliesAdapter(mItem.getUfs()));
        }

        if(mItem.getResponses() != null && mItem.getResponses().size() > 0){
            RecyclerView ufs_responses = rootView.findViewById(R.id.response_list);
            ufs_responses.setLayoutManager(new LinearLayoutManager(getContext()));
            ufs_responses.setAdapter(new ResponseAdapter(mItem.getResponses()));
        }else{
            TextView no_responses = rootView.findViewById(R.id.no_responses_tview);
            no_responses.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void goBack(){
        Navigation.findNavController(rootView).navigateUp();
    }

    private class ProgressViewOld extends View{
        public ProgressViewOld(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint p = new Paint();
            p.setColor(Color.parseColor("#338833"));
            DashPathEffect dashPath = new DashPathEffect(new float[]{5,5}, (float)1.0);
            p.setPathEffect(dashPath);
            p.setStrokeWidth(4);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawPaint(p);

            canvas.drawCircle(100, 100, 50, p);

            invalidate();
        }
    }
}