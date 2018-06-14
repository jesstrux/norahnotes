package akil.co.tz.notetaker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import akil.co.tz.notetaker.Adapters.PostAdapter;
import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.dummy.DummyContent;
import akil.co.tz.notetaker.models.Post;
import akil.co.tz.notetaker.ui.dialogs.PostTitleDialog;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private boolean mTwoPane;
    private RecyclerView mRecyclerView;
    private static final int SCROLL_DIRECTION_UP = -1;
    private Dialog postTitleDialog;
    private EditText title_name;
    private Button submitTitleBtn;
    private ImageButton cancleTitleBtn;
    private TextView no_posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        startActivity(new Intent(this, PickVerseActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        final AppBarLayout appBar = findViewById(R.id.app_bar);
        no_posts = findViewById(R.id.no_posts);

        if (findViewById(R.id.note_detail_container) != null) {
            mTwoPane = true;
        }

        postTitleDialog = new Dialog(this);

        mRecyclerView = findViewById(R.id.note_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                Log.d("WOURA", "Add Clicked!!!");
                login();

                return true;
            case R.id.action_add:
                Log.d("WOURA", "Add Clicked!!!");
                createPost();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void login(){
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void createPost() {
        postTitleDialog.setContentView(R.layout.post_title_dialog);
        title_name = postTitleDialog.findViewById(R.id.post_title);
        submitTitleBtn = postTitleDialog.findViewById(R.id.submit_btn);
        cancleTitleBtn = postTitleDialog.findViewById(R.id.cancel_btn);

        submitTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_name.getText().toString();

                if(title.length() < 3){
                    Toast.makeText(NoteListActivity.this, "Title empty or too short.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NoteListActivity.this, "Title is:  " + title, Toast.LENGTH_SHORT).show();
                    postTitleDialog.dismiss();

                    savePostTitle(title);
                }
            }
        });

        cancleTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTitleDialog.dismiss();
            }
        });

        postTitleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        postTitleDialog.show();
    }

    private void savePostTitle(String title){
        Post new_post = new Post(title, null, null, null);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        long inserted_id = db.postDao().insert(new_post);
        new_post.setId((int) inserted_id);

        openPost(new_post);
    }

    private void openPost(Post new_post){
        Intent intent = new Intent(getBaseContext(), NoteEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", new_post);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        List<Post> posts = db.postDao().getPosts();
        if(posts.size() < 1)
            no_posts.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new PostAdapter(this, posts, mTwoPane));
    }
}
