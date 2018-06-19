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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

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

    private EditText mIpView, mEmailView, mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView login_image;

    OkHttpClient client = new OkHttpClient();

    private String login_url= "http://192.168.8.109:9000/api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mIpView = findViewById(R.id.ip);
        mEmailView = findViewById(R.id.email);
        login_image = findViewById(R.id.login_image);

        login_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIpView.setText("http://192.168.8.109:7000");
                mEmailView.setText("wakyj07@gmail.com");
                mPasswordView.setText("@ttss;86%");
                attemptLogin();
                return true;
            }
        });

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

        String url = mIpView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the mUser entered one.
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
            mAuthTask = new UserLoginTask(url, email, password, FirebaseInstanceId.getInstance().getToken());
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

                SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
                if(mUrl.length() < 1)
                    prefs.edit().putString("ip", "http://192.168.8.109:9000").commit();
                else
                    prefs.edit().putString("ip", mUrl).commit();

                RequestBody body = RequestBody.create(JSON, obj.toString());

                Request request = new Request.Builder()
                        .url(mUrl.length() < 1 ? login_url : mUrl + "/api/login.php")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                ResponseBody response_body = response.body();;

                if (response_body != null){
                    String server_response = response_body.string();
                    Log.d("WOURA", "Response from server: " + server_response);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("saved_user", server_response);
                    editor.apply();

                    Gson gson = new Gson();
                    try {
                        return gson.fromJson(server_response, User.class);
                    } catch (Exception e){
                        Log.d("WOURA", "Error parsing mUser: " + e.getMessage());
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

                if(result.isActivated() == null || !result.isActivated()){
                    Intent intent = new Intent(getBaseContext(), SplashScreen.class);
                    intent.putExtra("unconfirmed", true);
                    startActivity(intent);

                    return;
                }

                if(result.getRole() != null)
                    FirebaseMessaging.getInstance().subscribeToTopic(result.getRole().replaceAll("\\s+",""));
                if(result.getDepartment() != null)
                    FirebaseMessaging.getInstance().subscribeToTopic(result.getDepartment().replaceAll("\\s+",""));

                goIn(result);
            } else {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        private void goIn(User user){
            Intent intent = new Intent(getBaseContext(), NoteListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("mUser", user);
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