package akil.co.tz.notetaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.Adapters.BookAdapter;
import akil.co.tz.notetaker.Adapters.PostAdapter;
import akil.co.tz.notetaker.Data.Book;
import akil.co.tz.notetaker.models.Post;

public class PickVerseActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Book> mBookList = new ArrayList<>();
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_verse);

        mAdapter = new BookAdapter(this, mBookList);
        mRecyclerView = findViewById(R.id.book_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        getBooks();
    }

    private void getBooks(){
        String json;
        try{
            InputStream is = getAssets().open("bible_books.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);

                if(obj != null){
                    Book book = new Book(obj.getString("title"), -1, -1, -1);
                    mBookList.add(book);
                }

//                Log.d("WOURA", "title: " + obj.getString("title"));
            }

            mAdapter.notifyDataSetChanged();

        }catch (Exception e){
            Log.d("WOURA", "Error: " + e.getMessage());
        }
    }
}
