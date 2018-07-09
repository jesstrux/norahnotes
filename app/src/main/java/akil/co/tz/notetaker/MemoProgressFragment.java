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
import android.support.v7.widget.Toolbar;
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

        Context appContext = getActivity().getApplicationContext();
        RelativeLayout progressWrapper = rootView.findViewById(R.id.progressWrapper);
        DifferentColorCircularBorder border = new DifferentColorCircularBorder(progressWrapper);
        int pass = 360 / mItem.getUfs().size();
        boolean completed = true;
        for (int i = 0; i < mItem.getUfs().size(); i++){
            Ufs ufs = mItem.getUfs().get(i);
            if(Integer.valueOf(ufs.getStatus()) == Ufs.STATUS_UNKNOWN)
                completed = false;
            border.addBorderPortion(appContext, Color.parseColor(Integer.valueOf(ufs.getStatus()) == Ufs.STATUS_ACCEPTED ? "#ffa500" : "#DDDDDD"), pass * i, (pass * (i + 1) - 10));
        }

        TextView progressText = rootView.findViewById(R.id.progress_text);
        progressText.setText(completed ? "PROCESSED" : "IN PROGRESS");

        ListView ufs_list = rootView.findViewById(R.id.ufs_list);
        ufs_list.setAdapter(new UfsArrayAdapter(getContext(), mItem.getUfs()));

        return rootView;
    }

    private class UfsArrayAdapter extends ArrayAdapter<Ufs> {
        private final Context ctx;
        private final ArrayList<Ufs> items;

        public UfsArrayAdapter(Context context,
                                  ArrayList<Ufs> items) {
            super(context, -1, items);
            ctx = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.ufs_item, parent, false);

            TextView name = rowView.findViewById(R.id.ufs_name);
            TextView response = rowView.findViewById(R.id.ufs_response);
            View icon = rowView.findViewById(R.id.icon_view);

            Ufs item = items.get(position);
            if (Integer.valueOf(item.getStatus()) == Ufs.STATUS_ACCEPTED) {
                icon.setBackgroundResource(R.color.colorAccent);
            } else {
                icon.setBackgroundResource(R.color.tintPdf);
            }

            return rowView;
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void goBack(){
        Navigation.findNavController(rootView).navigateUp();
    }

    private class ProgressView extends View{
        public ProgressView(Context context){
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint p = new Paint();
            p.setColor(Color.parseColor("#2196F3"));
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