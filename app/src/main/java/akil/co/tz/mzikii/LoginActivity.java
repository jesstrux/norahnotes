package akil.co.tz.mzikii;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import akil.co.tz.mzikii.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2;
    private ImageView login_image;
    private String SERVER_API = "675307021913-9hhbppgebt274o6ir9v9ev4a0ep5hksg.apps.googleusercontent.com";
    FirebaseAuth mAuth;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_API)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private User convertUser(FirebaseUser account){
        return new User(account.getUid(), account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());
    }

    private void goIn(User user) {
        Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mUser", user);
        intent.putExtras(bundle);
        startActivity(intent);

        finish();
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("WOURA", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("WOURA", "signInWithCredential:success");

                            linkAccount(mAuth.getCurrentUser());
                        } else {
                            Log.w("WOURA", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication Failed!", Toast.LENGTH_SHORT).show();
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void linkAccount(final FirebaseUser user) {
        if(user == null || user.getEmail() == null){
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), "123456");

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("WOURA", "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            goIn(convertUser(user));
                        } else {
                            Log.w("WOURA", "linkWithCredential:failure", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            goIn(convertUser(user));
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (Exception e) {
                Log.d("WOURA", "signInResult:failed code=" + e.getMessage());
                Toast.makeText(getBaseContext(), "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}