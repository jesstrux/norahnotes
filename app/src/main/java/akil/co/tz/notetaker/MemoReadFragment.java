package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akil.co.tz.notetaker.models.Attachment;
import akil.co.tz.notetaker.models.Memo;
import androidx.navigation.Navigation;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MemoReadFragment extends Fragment {
    Memo mItem;
    FloatingActionButton replyBtn;
    Button showRepliesBtn;
    boolean is_sent = false;

    private View rootView;

    public MemoReadFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memo_read, container, false);

        mItem = (Memo) getArguments().getSerializable("memo");

        //((BaseActivity)getActivity()).login();

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

        TextView content = rootView.findViewById(R.id.note_detail);
        content.setText(mItem.getBody());
        TextView memoDate = rootView.findViewById(R.id.memo_date);
        memoDate.setText(mItem.getDate());
        replyBtn = rootView.findViewById(R.id.reply_btn);
        showRepliesBtn = rootView.findViewById(R.id.show_replies_btn);

        setAttachments(mItem);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mItem.getType().equals("Inbox")){
            setupReceived(mItem);
        }else{
            setupSent(mItem);
            is_sent = true;
        }
    }

    private void setAttachments(Memo memo) {
        SharedPreferences prefs = getDefaultSharedPreferences(getActivity().getApplicationContext());
        final String ip = prefs.getString("ip", null);

        ArrayList<Attachment> attachments = memo.getAttachments();
        if(attachments == null || attachments.size() < 1){
            return;
        }

        String[] attachments_types = {"image", "pdf", "docx", "xls"};
        List<String> attachments_type_list = Arrays.asList(attachments_types);
        LinearLayout attachmentsView = rootView.findViewById(R.id.attachments_view);
        LinearLayout attachmentsWrapper = rootView.findViewById(R.id.attachments_wrapper);

        int[] attachment_icons = {R.drawable.ic_image, R.drawable.ic_pdf, R.drawable.ic_document, R.drawable.ic_xls};
        int[] attachment_tints = {R.color.tintImage, R.color.tintPdf, R.color.tintDoc, R.color.tintXls};

        attachmentsView.setVisibility(View.VISIBLE);
        for (int i = 0; i < attachments.size(); i++){
            final Attachment attachment = attachments.get(i);
            MemoAttachmentView memoAttachmentView;
            String attachmentTitle = attachment.getTitle();
            String type = attachment.getType();

            if(type != null){
                int type_idx = attachments_type_list.indexOf(type);

                int icon = attachment_icons[type_idx];
                int tint = attachment_tints[type_idx];

                memoAttachmentView = new MemoAttachmentView(getContext(), attachmentTitle, icon, tint);

                memoAttachmentView.setIcon(icon);
                memoAttachmentView.setTint(tint);
            }else{
                memoAttachmentView = new MemoAttachmentView(getContext());
                memoAttachmentView.setTitle(attachment.getTitle());
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 12, 0);
            memoAttachmentView.setLayoutParams(params);

            attachmentsWrapper.addView(memoAttachmentView);

            memoAttachmentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ip != null){
                        String attachment_url = ip + "/uploads/" + attachment.getSrc();
                        Toast.makeText(getContext(), attachment_url, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupReceived(Memo memo){
        LinearLayout receivedWrapper = rootView.findViewById(R.id.received_wrapper);
        TextView memoSender = rootView.findViewById(R.id.memo_sender);

        receivedWrapper.setVisibility(View.VISIBLE);
        memoSender.setText(memo.getSenderName());

        showRepliesBtn.setVisibility(View.GONE);

        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Replying to memo...", Toast.LENGTH_LONG).show();
            }
        });

        showFab(true);
    }

    private void showFab(final boolean realFab){
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Log.i("tag", "This'll run 300 milliseconds later");

                    if(realFab){
                        if (ViewCompat.isLaidOut(replyBtn) ||
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            replyBtn.show();
                        } else {
                            replyBtn.animate().cancel();//cancel all animations
                            replyBtn.setScaleX(0f);
                            replyBtn.setScaleY(0f);
                            replyBtn.setAlpha(0f);
                            replyBtn.setVisibility(View.VISIBLE);

                            replyBtn.animate().setDuration(200).scaleX(1).scaleY(1).alpha(1)
                                    .setInterpolator(new LinearOutSlowInInterpolator());
                        }
                    }else{
                        showRepliesBtn.setScaleX(0.5f);
                        showRepliesBtn.setTranslationX(showRepliesBtn.getWidth() / 3);
                        showRepliesBtn.setAlpha(0f);
                        showRepliesBtn.setVisibility(View.VISIBLE);
                        showRepliesBtn.animate().setDuration(200).scaleX(1).translationX(0f).alpha(1)
                                .setInterpolator(new LinearOutSlowInInterpolator());
                    }
                }
            },
            200);
    }

    private void hideFab(boolean realFab){
        if(realFab){
            replyBtn.setVisibility(View.INVISIBLE);
            replyBtn.animate().setDuration(200).scaleX(0f).scaleY(0f).alpha(0f)
                    .setInterpolator(new LinearOutSlowInInterpolator());
        }else{
            showRepliesBtn.setVisibility(View.INVISIBLE);
            showRepliesBtn.animate().setDuration(200).scaleX(0f).translationX(showRepliesBtn.getWidth() / 2).alpha(0f)
                    .setInterpolator(new LinearOutSlowInInterpolator());
        }
    }

    private void setupSent(Memo memo){
        LinearLayout sentWrapper = rootView.findViewById(R.id.sent_wrapper);
        TextView recepient = rootView.findViewById(R.id.memo_recepient);

        sentWrapper.setVisibility(View.VISIBLE);
        recepient.setText(memo.getRecepientName());
        replyBtn.setVisibility(View.GONE);

        String[] ufs_name_list = memo.getUfsNames();

        if(ufs_name_list != null){
            LinearLayout ufsWrapper = rootView.findViewById(R.id.ufs_wrapper);
            TextView ufs_names = rootView.findViewById(R.id.ufs_names);
            ufsWrapper.setVisibility(View.VISIBLE);
            ufs_names.setText(TextUtils.join(", ", ufs_name_list));
        }

        showRepliesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "See to memo replies...", Toast.LENGTH_LONG).show();
            }
        });

        showFab(false);
    }

    private void goBack(){
        hideFab(is_sent);
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Navigation.findNavController(rootView).navigateUp();
                    ((BaseActivity) getActivity()).showNav();
                }
            },
        50);
    }
}