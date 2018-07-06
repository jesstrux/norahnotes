package akil.co.tz.notetaker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoAttachmentView extends LinearLayout {
    private TextView mTitle;
    private ImageView mImage;

    public MemoAttachmentView(Context context) {
        super(context);
    }

    public MemoAttachmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MemoAttachment, 0, 0);

        String titleText = a.getString(R.styleable.MemoAttachment_attachmentName);

        int icon = a.getResourceId(R.styleable.MemoAttachment_iconSrc,
                R.drawable.ic_attachment);

        @SuppressWarnings("ResourceAsColor")
        int iconTint = a.getColor(R.styleable.MemoAttachment_tintColor,
                Color.parseColor("#555555"));
        a.recycle();

        setOrientation(LinearLayout.VERTICAL);
        setPadding(20, 20, 20, 20);
        setMinimumHeight(110);
        setMinimumWidth(170);
        setBackgroundResource(R.drawable.rounded_bg);

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
        ImageViewCompat.setImageTintList(mImage, ColorStateList.valueOf(color));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
