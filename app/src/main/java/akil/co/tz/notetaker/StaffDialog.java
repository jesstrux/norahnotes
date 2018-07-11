package akil.co.tz.notetaker;

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
import android.widget.TextView;
import android.widget.Toast;

import akil.co.tz.notetaker.models.User;

public class StaffDialog  extends DialogFragment {
    TextView cancelBtn, submitBtn;
    View btnSeparator;
    private int currentSection = 0;
    private User mUser;
    private static final String ARG_USER = "user";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private WrapContentViewPager mViewPager;

    private OnPageChangedClickListener pageListener;

    public void setPageListener(OnPageChangedClickListener listener){
        this.pageListener = listener;
    };

    public interface OnPageChangedClickListener {
        void onPageChanged(int position);
    }

    public StaffDialog() {
    }

    public static StaffDialog newInstance(int sectionNumber, User user) {
        StaffDialog fragment = new StaffDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
            currentSection = getArguments().getInt(ARG_SECTION_NUMBER);
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
        return inflater.inflate(R.layout.staff_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Create Resource");

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ((TextView) view.findViewById(R.id.name)).setText(mUser.getName());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = view.findViewById(R.id.sections_adapter);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = view.findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new WrapContentViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentSection = i;
                tabLayout.getTabAt(i).select();

                if(pageListener != null)
                    pageListener.onPageChanged(i);

                if(currentSection == 1){
                    submitBtn.setVisibility(View.VISIBLE);
                    btnSeparator.setVisibility(View.VISIBLE);
                }else{
                    submitBtn.setVisibility(View.GONE);
                    btnSeparator.setVisibility(View.GONE);
                }

                Log.d("WOURA", "Admin fragment position: " + currentSection);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mViewPager.setCurrentItem(currentSection);
    }

    public void closeDialog(){
        this.dismiss();
    }

    public void submitData(int newActivation, int newRole, int newStatus){
        Toast.makeText(getContext(), "Action: " + newActivation + " newRole: " + newRole + " Status: " + newStatus, Toast.LENGTH_SHORT).show();
        closeDialog();
    }

    public static class PlaceholderFragment extends android.support.v4.app.Fragment
        implements StaffDialog.OnPageChangedClickListener{
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_USER = "section_number";
        private User mUser;
        private int section = 0;
        private ChipGroup roleOptions;
        private ChipGroup activationOptions;
        private ChipGroup statusOptions;

        private int newStatus = -1;
        private int newActivation = -1;
        private int newRole = -1;

        private View rootView;

        TextView cancelBtn, submitBtn;

        public PlaceholderFragment() {}

        public static PlaceholderFragment newInstance(int sectionNumber, User user) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_USER, user);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            ((StaffDialog) getParentFragment()).setPageListener(this);

            if(getArguments() != null){
                section = getArguments().getInt(ARG_SECTION_NUMBER);
                mUser = (User) getArguments().getSerializable(ARG_USER);
            }

            Log.d("WOURA", "Placeholder fragment position: " + section);

            rootView = inflater.inflate(R.layout.staff_content, container, false);

            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            cancelBtn = view.findViewById(R.id.cancel_btn);
            submitBtn = view.findViewById(R.id.submit_btn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((StaffDialog) getParentFragment()).dismiss();
                }
            });

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((StaffDialog) getParentFragment()).submitData(newActivation, newRole, newStatus);
                }
            });

            if(section == 0){
                setupDetail(view);
            }else{
                setupManage(view);
            }
        }

        private void setupDetail(View view) {
            (view.findViewById(R.id.details_wrapper)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.manage_wrapper)).setVisibility(View.GONE);

            if(mUser.getRole() != null)
                ((TextView) view.findViewById(R.id.role)).setText(mUser.getRole());

            ((TextView) view.findViewById(R.id.email)).setText(mUser.getEmail());
            ((TextView) view.findViewById(R.id.phone)).setText(mUser.getPhone());
            ((TextView) view.findViewById(R.id.department)).setText(mUser.getDepartment());
            ((TextView) view.findViewById(R.id.position)).setText(mUser.getJob());
        }

        private void setupManage(View view) {
            (view.findViewById(R.id.manage_wrapper)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.details_wrapper)).setVisibility(View.GONE);

            roleOptions = view.findViewById(R.id.role_options);
            activationOptions = view.findViewById(R.id.activation_options);
            statusOptions = view.findViewById(R.id.status_options);

            roleOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                    switch (checkedId){
                        case R.id.activation_pending:
                            newRole = 0;
                            break;
                        case R.id.activation_approved:
                            newRole = 1;
                            break;
                        case R.id.activation_declined:
                            newRole = 2;
                            break;
                    }

                    Toast.makeText(getContext(), "Role changed to:" + newRole, Toast.LENGTH_SHORT).show();
                }
            });

            activationOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                    switch (checkedId){
                        case R.id.activation_pending:
                            newActivation = 0;
                            break;
                        case R.id.activation_approved:
                            newActivation = 1;
                            break;
                        case R.id.activation_declined:
                            newActivation = 2;
                            break;
                    }

                    Toast.makeText(getContext(), "Activation changed to:" + newActivation, Toast.LENGTH_SHORT).show();
                }
            });

            statusOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                    switch (checkedId){
                        case R.id.status_inactive:
                            newStatus = 0;
                            break;
                        case R.id.status_active:
                            newStatus = 1;
                            break;
                        case R.id.status_blocked:
                            newStatus = 2;
                            break;
                    }

                    Toast.makeText(getContext(), "Status changed to:" + newStatus, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPageChanged(int position) {
            section = position;
            Log.d("WOURA", "Placeholder page changed: " + section);

            if(section == 0){
                setupDetail(rootView);
            }else{
                setupManage(rootView);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            currentSection = position;
            return PlaceholderFragment.newInstance(position, mUser);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}