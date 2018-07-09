package akil.co.tz.notetaker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import akil.co.tz.notetaker.models.Memo;
import akil.co.tz.notetaker.models.User;
import androidx.navigation.Navigation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MemoReplyFragment extends Fragment {
    private MemoReplyTask memoReplyTask = null;
    Memo mItem;
    private View rootView;
    EditText inputBox;
    OkHttpClient client = new OkHttpClient();

    public MemoReplyFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memo_reply, container, false);

        mItem = (Memo) getArguments().getSerializable("memo");

        final AppBarLayout appBar = rootView.findViewById(R.id.app_bar);
        final Toolbar toolbar = rootView.findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        final LinearLayout title_bar = rootView.findViewById(R.id.title_bar);
        final TextView title = rootView.findViewById(R.id.title);

        final String memo_title = mItem.getTitle();

        if(memo_title != null && memo_title.length() > 0){
            title.setTextColor(Color.parseColor("#333333"));
            title.setText(memo_title);
        }

        inputBox = rootView.findViewById(R.id.note_reply);
        inputBox.requestFocus();

        Button memoReplyBtn = rootView.findViewById(R.id.sendBtn);
        memoReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReply();
            }
        });
//        InputMethodManager imm = (InputMethodManager)
//                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(inputBox, InputMethodManager.SHOW_IMPLICIT);

        TextView memoRecepient = rootView.findViewById(R.id.memo_recepient);
        memoRecepient.setText(mItem.getSenderName());

        NestedScrollView note_detail_container = rootView.findViewById(R.id.note_detail_container);
        note_detail_container.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public static final String TAG = "WOURA";

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= 100) {
                    appBar.setElevation(0);
                    toolbar.setTitle("");
                }else{
                    appBar.setElevation(2);
                    toolbar.setTitle(memo_title);
                }
            }
        });
        return rootView;
    }

    public void sendReply(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputBox.getWindowToken(), 0);

        ((BaseActivity) getActivity()).hideProgress();
        new MemoReplyTask().execute(String.valueOf(mItem.getId()), inputBox.getText().toString());
    }

    private void goBack(){
        Navigation.findNavController(rootView).navigateUp();
    }

    public class MemoReplyTask extends AsyncTask<String, Void, String> {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        MemoReplyTask() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("memo_id", params[0]);
                obj.put("action", 2);
                obj.put("content", params[1]);

                SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
                String mUrl = prefs.getString("ip", null);

                String userJson = prefs.getString("saved_user",null);
                User user = new Gson().fromJson(userJson, User.class);
                String user_id = user.getId();

                obj.put("user_id", user_id);

                RequestBody body = RequestBody.create(JSON, obj.toString());

                Request request = new Request.Builder()
                        .url(mUrl + "/api/send_reply.php")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                ResponseBody response_body = response.body();;

                if (response_body != null){
                    return response_body.string();
                }

                return null;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            memoReplyTask = null;
            ((BaseActivity) getActivity()).hideProgress();
            Log.d("WOURA", "Response from api: " + result);
            Toast.makeText(getContext(), "Reply sent", Toast.LENGTH_SHORT).show();
            goBack();
        }

        @Override
        protected void onCancelled() {
            memoReplyTask = null;
            ((BaseActivity) getActivity()).hideProgress();
        }
    }
}