package akil.co.tz.notetaker;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import akil.co.tz.notetaker.dummy.DummyContent;
import akil.co.tz.notetaker.models.Post;

public class NoteDetailActivity extends AppCompatActivity {
    Post mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mItem = DummyContent.ITEM_MAP.get(getIntent().getStringExtra(NoteDetailFragment.ARG_ITEM_ID));
        Bundle bundle = getIntent().getExtras();
        mItem = (Post) bundle.getSerializable("post");

        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle("");

        LinearLayout title_bar = findViewById(R.id.title_bar);
        TextView title = findViewById(R.id.title);

        String post_title = mItem.getTitle();

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

//        if (savedInstanceState == null) {
//            Bundle arguments = new Bundle();
//            arguments.putString(NoteDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(NoteDetailFragment.ARG_ITEM_ID));
//            NoteDetailFragment fragment = new NoteDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.note_detail_container, fragment)
//                    .commit();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                navigateUpTo(new Intent(this, NoteListActivity.class));
                return true;
            case R.id.action_edit:
                Context context = getBaseContext();
                Intent intent = new Intent(context, NoteEditActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", mItem);
                intent.putExtras(bundle);
                context.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
