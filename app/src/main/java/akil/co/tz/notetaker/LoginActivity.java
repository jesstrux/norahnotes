package akil.co.tz.notetaker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.Manifest.permission.READ_CONTACTS;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask = null;

    private EditText mEmailView, mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    OkHttpClient client = new OkHttpClient();

    private String login_url= "http://192.168.8.109:9000/login_mob.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(login_url, email, password, FirebaseInstanceId.getInstance().getToken());
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Void, User> {

        private final String mUrl;
        private final String mEmail;
        private final String mPassword;
        private final String mDeviceToken;
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        UserLoginTask(String url, String email, String password, String token) {
            mUrl = url;
            mEmail = email;
            mPassword = password;
            mDeviceToken = token;
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("email", mEmail);
                obj.put("password", mPassword);
                obj.put("token", mDeviceToken);

                RequestBody body = RequestBody.create(JSON, obj.toString());

                Request request = new Request.Builder()
                        .url(mUrl)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                ResponseBody response_body = response.body();;

                if (response_body != null){
                    String server_response = response_body.string();
                    Log.d("WOURA", "Response from server: " + server_response);

                    SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_user", server_response);
                    editor.apply();

                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        User user = objectMapper.readValue(server_response, User.class);
                        Log.d("WOURA", user.getName());

                        return user;
                    } catch (Exception e){
                        Log.d("WOURA", "Error parsing user: " + e.getMessage());
                    }
                }

                return null;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final User result) {
            mAuthTask = null;
            if (result != null) {
                final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                Boolean subscribed_to_department = prefs.getString("subscribed_to_department", null) != null;
                final Boolean subscribed_to_role = prefs.getString("subscribed_to_role", null) != null;

                if(result.getDepartment() == null || subscribed_to_department){
                    Toast.makeText(getApplicationContext(), "Already Subscribed to department", Toast.LENGTH_SHORT).show();

                    if(result.getRole() != null && !subscribed_to_role){
                        subscribeToRole(result, prefs);
                    }else{
                        Toast.makeText(getApplicationContext(), "Already Subscribed to role", Toast.LENGTH_SHORT).show();

                        showProgress(false);
                        goIn(result);
                    }
                }else{
                    FirebaseMessaging.getInstance().subscribeToTopic(result.getDepartment().replaceAll("\\s+",""))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("subscribed_to_department", "true");
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), "Subscribed to department", Toast.LENGTH_SHORT).show();
                                }

                                if(result.getRole() != null && !subscribed_to_role){
                                    subscribeToRole(result, prefs);
                                }else{
                                    showProgress(false);
                                    goIn(result);
                                }
                            }
                        });
                }
            } else {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        private void subscribeToRole(final User user, final SharedPreferences prefs){
            FirebaseMessaging.getInstance().subscribeToTopic(user.getRole().replaceAll("\\s+",""))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("subscribed_to_role", "true");
                            editor.apply();
                        }

                        Toast.makeText(getApplicationContext(), "Subscribed to role", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        goIn(user);
                    }
                });
        }

        private void goIn(User user){
            Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            startActivity(intent);

            finish();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}