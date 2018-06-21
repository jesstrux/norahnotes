package akil.co.tz.mzikii;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import akil.co.tz.mzikii.Adapters.PostAdapter;
import akil.co.tz.mzikii.Data.AppDatabase;
import akil.co.tz.mzikii.models.Post;
import akil.co.tz.mzikii.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteListActivity extends AppCompatActivity {
    private boolean mTwoPane;
    private Dialog postTitleDialog;
    private EditText title_name;
    private Button submitTitleBtn;
    private ImageButton cancleTitleBtn;
    private User mUser;

    ArrayList<Post> posts;
    PostAdapter adapter;

    private RecyclerView mRecyclerView;
    private TextView no_posts;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mSongsRef = mRootRef.child("songs");

    CollectionReference mLinksRef = FirebaseFirestore.getInstance().collection("link_urls");

    FirebaseUser mCurrentUser;

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mSongsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                Log.d("WOURA", "The new value is: " + val);

                Toast.makeText(getBaseContext(), "Value for songs changed to: " + val, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        postTitleDialog = new Dialog(this);

        no_posts = findViewById(R.id.no_posts);
        RecyclerView rview = findViewById(R.id.note_list);
        setupRecyclerView(rview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                createPost();
                return true;
             case R.id.action_sync:
                syncApp();
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void syncApp() {
        Permissions.check(this,
            new String[]{Manifest.permission.CAMERA},
            "Camera permission required because...", new Permissions.Options()
                    .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
            new PermissionHandler() {
                @Override
                public void onGranted() {
                    startActivityForResult(new Intent(getBaseContext(), ScanActivity.class), 72);
                }
            });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "muziki")
                .allowMainThreadQueries()
                .build();

        List<Post> posts = db.postDao().getPosts();

        adapter = new PostAdapter(posts, false);
        recyclerView.setAdapter(adapter);

        if(posts == null || posts.size() < 1){
            no_posts.setVisibility(View.VISIBLE);
            no_posts.setText("No posts found!");
        }
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
        mSongsRef.setValue(title);
//        Post new_post = new Post(title, null, null, null);
//        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "muziki")
//                .allowMainThreadQueries()
//                .build();
//
//        long inserted_id = db.postDao().insert(new_post);
//        new_post.setId((int) inserted_id);
//
//        openPost(new_post);
    }

    private void openPost(Post new_post){
        Intent intent = new Intent(getBaseContext(), NoteEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", new_post);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 72) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                Log.d("WOURA", "Result from scan");

                Map<String, Object> user = new HashMap<>();
                user.put("email", mCurrentUser.getEmail());
                user.put("password", "123456");
                mLinksRef.document(result).set(user);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
