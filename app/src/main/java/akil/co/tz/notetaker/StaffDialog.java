package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import akil.co.tz.notetaker.models.AdminItem;
import akil.co.tz.notetaker.models.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class StaffDialog  extends DialogFragment {
    TextView cancelBtn, submitBtn;
    View btnSeparator;
    private int currentSection = 0;
    private User mUser;
    private static final String ARG_USER = "user";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ChipGroup roleOptions;
    private ArrayList<AdminItem> roles;

    private int newStatus = -1;
    private int newActivation = -1;
    private int newRole = -1;

    private View rootView;

    private OnPageChangedClickListener pageListener;

    public void setPageListener(OnPageChangedClickListener listener){
        this.pageListener = listener;
    };

    public interface OnPageChangedClickListener {
        void onPageChanged(int position);
    }

    public StaffDialog() {
    }

    public static StaffDialog newInstance(User user) {
        StaffDialog fragment = new StaffDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null){
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
        rootView = inflater.inflate(R.layout.staff_dialog, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Create Resource");

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ((TextView) view.findViewById(R.id.name)).setText(mUser.getName());

        setupManage(rootView);
    }

    public void closeDialog(){
        this.dismiss();
    }

    public void submitData(int newActivation, int newRole, int newStatus){
        if(newRole == -1){
            Toast.makeText(getContext(), "Please pick a role.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AdminSaveTask().execute();
    }

    private void setupManage(View view) {
        LinearLayout manageWrapper = view.findViewById(R.id.manage_wrapper);
        manageWrapper.setVisibility(View.VISIBLE);

        (view.findViewById(R.id.details_wrapper)).setVisibility(View.GONE);

        roleOptions = view.findViewById(R.id.role_options);

        cancelBtn = manageWrapper.findViewById(R.id.cancel_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Save data clicked", Toast.LENGTH_SHORT).show();
                closeDialog();
            }
        });

        submitBtn = manageWrapper.findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Save data clicked", Toast.LENGTH_SHORT).show();
                submitData(newActivation, newRole, newStatus);
            }
        });

        roleOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.role_staff:
                        newRole = 1;
                        break;
                    case R.id.role_Lecturer:
                        newRole = 2;
                        break;
                    case R.id.role_Admin:
                        newRole = 4;
                        break;
                    case R.id.role_HOD:
                        newRole = 5;
                        break;
                }

                Toast.makeText(getContext(), "Role changed to:" + newRole, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class AdminSaveTask extends AsyncTask<Integer, Void, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected String doInBackground(Integer... params) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
            String url = prefs.getString("ip", null);

            if(mUser == null || newRole == -1)
                return null;

            url += "/api/activate_user.php";

            try {
                JSONObject obj = new JSONObject();
                obj.put("user_id", mUser.getId());
                obj.put("role", newRole);

                RequestBody body = RequestBody.create(JSON, obj.toString());

                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.post(body);
                Request request = builder.build();

                Response response = client.newCall(request).execute();

                if(response.body() != null) {
                    String response_str = response.body().string();
                    Log.d("WOURA", "Server response: " + response_str);

                    return response_str;
                }

                return null;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            Toast.makeText(getContext(), "User Verified", Toast.LENGTH_SHORT).show();
            closeDialog();
        }

        @Override
        protected void onCancelled() {
//            showProgress(false);
        }
    }
}