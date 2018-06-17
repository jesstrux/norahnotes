package akil.co.tz.notetaker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import akil.co.tz.notetaker.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());

        String strJson = prefs.getString("saved_user",null);
        Log.d("WOURA", "LOGGED USER: " + strJson);

        if (strJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                User user = objectMapper.readValue(strJson, User.class);

                Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);

                finish();
            } catch (IOException e){
                e.printStackTrace();
            }
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
