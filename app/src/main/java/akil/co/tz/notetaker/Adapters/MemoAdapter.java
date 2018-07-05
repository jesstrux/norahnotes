package akil.co.tz.notetaker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.notetaker.NoteDetailActivity;
import akil.co.tz.notetaker.NoteEditActivity;
import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.Utils.StringUtil;
import akil.co.tz.notetaker.models.Memo;

/**
 * Created by DevDept on 6/14/18.
 */

public class MemoAdapter
        extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private final List<Memo> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Memo item = (Memo) view.getTag();
            Context context = view.getContext();
            Intent intent;
            if(item.getBody() != null)
                intent = new Intent(context, NoteDetailActivity.class);
            else
                intent = new Intent(context, NoteEditActivity.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("memo", item);
            intent.putExtras(bundle);

            context.startActivity(intent);
        }
    };

    public MemoAdapter(List<Memo> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Memo memo = mValues.get(position);
        String title = memo.getTitle();
        Boolean has_title = title != null;

        if(memo.getType().equals("Inbox"))
            holder.mIdView.setText(memo.getSenderName() + "  >  " + memo.getTitle());
        else
            holder.mIdView.setText(StringUtil.ellipsize(title, 18) + "  >  " + memo.getRecepientName());

        if(memo.getBody() != null)
            holder.mContentView.setText(memo.getBody());

        if(memo.getAttachments() != null && memo.getAttachments().size() > 0){
            holder.mAttachmentsWrapperView.setVisibility(View.VISIBLE);
            holder.mAttachmentiew.setText("" + memo.getAttachments().size() + " attachments.");
        }

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final HorizontalScrollView mAttachmentsWrapperView;
        final TextView mAttachmentiew;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
            mAttachmentsWrapperView = view.findViewById(R.id.attachment_holder);
            mAttachmentiew = view.findViewById(R.id.attachment_count);
        }
    }
}