package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

import akil.co.tz.notetaker.models.AdminItem;
import akil.co.tz.notetaker.models.User;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class VerifyUserFragment extends Fragment {
    SharedPreferences prefs;
    TextView full_name, role, email, phone, department, position;
    User mUser;

    Button activate_btn;

    public VerifyUserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verify_user, container, false);

        activate_btn = rootView.findViewById(R.id.activate_btn);

        if(getArguments() != null)
            mUser = (User) getArguments().getSerializable("user");

        if(mUser != null){
            if(mUser.getName() == null)
                return rootView;

            full_name = rootView.findViewById(R.id.full_name);
            full_name.setText(mUser.getName());

            role = rootView.findViewById(R.id.role);
            if (mUser.getRole() != null)
                role.setText(mUser.getRole());

            email = rootView.findViewById(R.id.email);
            email.setText(mUser.getEmail());

            phone = rootView.findViewById(R.id.phone);
            phone.setText(mUser.getPhone());

            department = rootView.findViewById(R.id.department);
            department.setText(mUser.getDepartment());

            position = rootView.findViewById(R.id.position);
            position.setText(mUser.getJob());


            activate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(mUser);
                }
            });
        }

        return rootView;
    }

    private void showDialog(User user){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        StaffDialog staffDialog = StaffDialog.newInstance(user);
        staffDialog.show(fm, "view_staff");
    }
}
