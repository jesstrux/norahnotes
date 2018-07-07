package akil.co.tz.notetaker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MemoAttachmentView extends LinearLayout {
    private TextView mTitle;
    private ImageView mImage;
    private Context mContext;

    public MemoAttachmentView(Context context) {
        super(context);
    }

    public MemoAttachmentView(Context context, String title, int icon, int iconTint) {
        super(context);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_attachment_view, this, true);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.attachment_bg);

        mTitle = (TextView) getChildAt(1);
        mImage = (ImageView) getChildAt(0);

        mTitle.setText(title);

        mImage.setImageResource(icon);
        mImage.setColorFilter(getResources().getColor(iconTint));

//        mImage.setColorFilter(ContextCompat.getColor(context, iconTint), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mImage.setColorFilter(ContextCompat.getColor(context, iconTint), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public MemoAttachmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MemoAttachment, 0, 0);

        String titleText = a.getString(R.styleable.MemoAttachment_attachmentName);

        int icon = a.getResourceId(R.styleable.MemoAttachment_iconSrc,
                R.drawable.ic_attachment);

        @SuppressWarnings("ResourceAsColor")
//        String iconTintString = a.getString(R.styleable.MemoAttachment_tintColor);
//        int iconTint = Color.parseColor(iconTintString);
        int iconTint = a.getColor(R.styleable.MemoAttachment_tintColor,
                R.color.colorAccent);
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.attachment_bg);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_attachment_view, this, true);

        mImage = (ImageView) getChildAt(0);
        mImage.setImageResource(icon);
        ImageViewCompat.setImageTintList(mImage, ColorStateList.valueOf(iconTint));

        mTitle = (TextView) getChildAt(1);
        mTitle.setText(titleText);
    }

    public void setIcon(int icon) {
        mImage.setImageResource(icon);
    }

    public void setTint(int color) {
        mImage.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
