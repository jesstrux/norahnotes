package akil.co.tz.notetaker;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import akil.co.tz.notetaker.dummy.DummyContent;
import akil.co.tz.notetaker.models.Post;

public class NoteDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private Post mItem;

    public NoteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.app_bar);
            if (appBarLayout != null) {
                Boolean theme_is_pink = mItem.getTheme().equals("pink");
                int bgRes = theme_is_pink ? R.color.colorPink : R.color.colorGreen;
                int color = theme_is_pink ? Color.WHITE : Color.BLACK;

                appBarLayout.setTitle(mItem.getTitle());
                appBarLayout.setBackgroundResource(bgRes);
                appBarLayout.setContentScrimResource(bgRes);
                appBarLayout.setCollapsedTitleTextColor(color);
                appBarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(color));

                Toolbar toolbar = appBarLayout.findViewById(R.id.detail_toolbar);
                toolbar.setNavigationIcon(R.drawable.ic_chevron_left);

//                activity.getWindow().setStatusBarColor(getResources().getColor(bgRes));
//                if(!theme_is_pink){
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        int flags = appBarLayout.getSystemUiVisibility();
//                        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//                        appBarLayout.setSystemUiVisibility(flags);
//                    }
//
//                    toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
//                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.note_detail)).setText(mItem.getDetails());
        }

        return rootView;
    }
}
