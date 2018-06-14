package akil.co.tz.notetaker;

import android.app.ListActivity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import akil.co.tz.notetaker.Data.AppDatabase;
import akil.co.tz.notetaker.models.Post;

public class NoteEditActivity extends AppCompatActivity {
    Post mPost;
    private LinearLayout title_bar;
    private EditText title, content;
    int note_id = -1;
    Button saveBtn;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        title_bar = findViewById(R.id.title_bar);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            mPost = (Post) bundle.getSerializable("post");

            if(mPost != null){
                note_id = mPost.getId();

                String post_title = mPost.getTitle();

                if(post_title != null && post_title.length() > 0){
                    title_bar.setVisibility(View.VISIBLE);
                    title.setText(post_title);
                }

                String details = mPost.getDetails();

                if(details != null)
                    content.setText(details);
            }
        }
    }

    private void saveNote() {
        String title_value = title.getText().toString();
        String content_value = content.getText().toString();

        if(note_id != -1 || (title_value.length() < 1 && content_value.length() < 1)){
            mPost.setTitle(title_value);
            mPost.setDetails(content_value);
            db.postDao().updatePost(mPost);
            Toast.makeText(NoteEditActivity.this, "Note saved.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(NoteEditActivity.this, "All fields can't be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View view){
//        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        startActivity(new Intent(this, NoteListActivity.class));
    }
}
