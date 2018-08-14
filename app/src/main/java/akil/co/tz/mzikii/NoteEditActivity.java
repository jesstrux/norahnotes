package akil.co.tz.mzikii;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import akil.co.tz.mzikii.Data.AppDatabase;
import akil.co.tz.mzikii.models.Post;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NoteEditActivity extends AppCompatActivity {
    String mTitle, mId, mDescription;
    private LinearLayout title_bar;
    private TextView title;
    private EditText content;
    int note_id = -1;
    Button saveBtn;
    AppDatabase db;

    CollectionReference mSongsRef;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        String user_id = prefs.getString("user_id", null);
        mSongsRef = FirebaseFirestore.getInstance().collection("songs/"+user_id+"/list");
    }

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

        if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            mTitle = bundle.getString("title");
            mId = bundle.getString("id");
            mDescription = bundle.getString("description");

            if(mTitle != null && mTitle.length() > 0){
                title_bar.setVisibility(View.VISIBLE);
                title.setText(mTitle);
            }

            if(mDescription != null)
                content.setText(mDescription);
        }
    }

    private void saveNote() {
        String title_value = title.getText().toString();
        String content_value = content.getText().toString();

        if(title_value.length() > 0 && content_value.length() > 0){
            final Map<String, Object> song = new HashMap<>();
            song.put("title", title_value);
            song.put("description", content_value);

            if(mId == null)
                mSongsRef.add(song).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mId = documentReference.getId();
                        song.put("id", mId);
                        documentReference.set(song);
                    }
                });
            else{
                song.put("id", mId);
                mSongsRef.document(mId).set(song);
            }

            Toast.makeText(NoteEditActivity.this, "Song.", Toast.LENGTH_SHORT).show();
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
