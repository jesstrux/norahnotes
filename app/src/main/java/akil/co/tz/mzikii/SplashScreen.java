package akil.co.tz.mzikii;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import akil.co.tz.mzikii.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            goIn(new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getPhotoUrl().toString()));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private User convertUser(GoogleSignInAccount account){
        return new User(account.getId(), account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
    }

    private void goIn(User user) {
        Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mUser", user);
        intent.putExtras(bundle);
        startActivity(intent);

        finish();
    }
}
