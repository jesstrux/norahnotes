package akil.co.tz.notetaker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import akil.co.tz.notetaker.models.Attachment;
import akil.co.tz.notetaker.models.Memo;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NoteDetailActivity extends AppCompatActivity {
    Memo mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Bundle bundle = getIntent().getExtras();
        mItem = (Memo) bundle.getSerializable("memo");

        final AppBarLayout appBar = findViewById(R.id.app_bar);
        final Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.setTitle("");

        final LinearLayout title_bar = findViewById(R.id.title_bar);
        final TextView title = findViewById(R.id.title);

        final String memo_title = mItem.getTitle();

        if(memo_title != null && memo_title.length() > 0){
            title.setTextColor(Color.parseColor("#333333"));
            title.setText(memo_title);
        }

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView content = findViewById(R.id.note_detail);
        content.setText(mItem.getBody());
        TextView memoDate = findViewById(R.id.memo_date);
        memoDate.setText(mItem.getDate());

        if(mItem.getType().equals("Inbox")){
            setupReceived(mItem);
        }else{
            setupSent(mItem);
        }

        setAttachments(mItem);

        NestedScrollView note_detail_container = findViewById(R.id.note_detail_container);
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
    }

    private void setAttachments(Memo memo) {
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        final String ip = prefs.getString("ip", null);

        ArrayList<Attachment> attachments = memo.getAttachments();
        if(attachments == null || attachments.size() < 1){
            return;
        }

        String[] attachments_types = {"image", "pdf", "docx", "xls"};
        List<String> attachments_type_list = Arrays.asList(attachments_types);
        LinearLayout attachmentsView = findViewById(R.id.attachments_view);
        LinearLayout attachmentsWrapper = findViewById(R.id.attachments_wrapper);

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

                memoAttachmentView = new MemoAttachmentView(getBaseContext(), attachmentTitle, icon, tint);

                memoAttachmentView.setIcon(icon);
                memoAttachmentView.setTint(tint);
            }else{
                memoAttachmentView = new MemoAttachmentView(getBaseContext());
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
                        Toast.makeText(getBaseContext(), attachment_url, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupReceived(Memo memo){
        LinearLayout receivedWrapper = findViewById(R.id.received_wrapper);
        TextView memoSender = findViewById(R.id.memo_sender);

        receivedWrapper.setVisibility(View.VISIBLE);
        memoSender.setText(memo.getSenderName());

        FloatingActionButton replyBtn = findViewById(R.id.reply_btn);
        replyBtn.show();
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Replying to memo...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSent(Memo memo){
        LinearLayout sentWrapper = findViewById(R.id.sent_wrapper);
        TextView recepient = findViewById(R.id.memo_recepient);

        sentWrapper.setVisibility(View.VISIBLE);
        recepient.setText(memo.getRecepientName());

        String[] ufs_name_list = memo.getUfsNames();

        if(ufs_name_list != null){
            LinearLayout ufsWrapper = findViewById(R.id.ufs_wrapper);
            TextView ufs_names = findViewById(R.id.ufs_names);
            ufsWrapper.setVisibility(View.VISIBLE);
            ufs_names.setText(TextUtils.join(", ", ufs_name_list));
        }

        Button showRepliesBtn = findViewById(R.id.show_replies_btn);
        showRepliesBtn.setVisibility(View.VISIBLE);
        showRepliesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "See to memo replies...", Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.detail_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
//                navigateUpTo(new Intent(this, NoteListActivity.class));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
