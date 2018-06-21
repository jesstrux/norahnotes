package akil.co.tz.mzikii;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import akil.co.tz.mzikii.models.Post;

public class NoteDetailActivity extends AppCompatActivity {
    Post mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mItem = (Post) bundle.getSerializable("memo");

        setContentView(R.layout.activity_note_detail);
        final AppBarLayout appBar = findViewById(R.id.app_bar);
        final Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle("");

        final LinearLayout title_bar = findViewById(R.id.title_bar);
        final TextView title = findViewById(R.id.title);

        final String post_title = mItem.getTitle();

        if(post_title != null && post_title.length() > 0){
            title.setTextColor(Color.parseColor("#333333"));
            title.setText(post_title);
        }

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        TextView content = findViewById(R.id.note_detail);
        content.setText(mItem.getDetails());

        NestedScrollView note_detail_container = findViewById(R.id.note_detail_container);
        note_detail_container.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public static final String TAG = "WOURA";

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 100) {
                    appBar.setElevation(0);
                    toolbar.setTitle("");
                    title_bar.setVisibility(View.VISIBLE);
                }else{
                    appBar.setElevation(5);
                    toolbar.setTitle(post_title);
                    title_bar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.detail_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
//                navigateUpTo(new Intent(this, NoteListActivity.class));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
