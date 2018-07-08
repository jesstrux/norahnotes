package akil.co.tz.notetaker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.notetaker.NoteDetailActivity;
import akil.co.tz.notetaker.NoteEditActivity;
import akil.co.tz.notetaker.NoteListActivity;
import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.Utils.StringUtil;
import akil.co.tz.notetaker.models.Memo;

/**
 * Created by DevDept on 6/14/18.
 */


public class MemoAdapter
        extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private final List<Memo> mValues;
    public static int MEMO_ITEM_TYPE_CARD = 1;
    public static int MEMO_ITEM_TYPE_FLAT = 2;

    private int memo_item_type = MEMO_ITEM_TYPE_CARD;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback{
        void onClick(Bundle b);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

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

            if(itemClickCallback != null){
                itemClickCallback.onClick(bundle);
                Log.d("WOURA", "Going to inner fragment...");
            }
            else{
                intent.putExtras(bundle);
                context.startActivity(intent);
                Log.d("WOURA", "Going to activity...");
            }

        }
    };

    public MemoAdapter(List<Memo> items) {
        mValues = items;
    }

    public MemoAdapter(List<Memo> items, int type) {
        mValues = items;
        memo_item_type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(memo_item_type == MEMO_ITEM_TYPE_CARD)
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_item, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.memo_item_flat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Memo memo = mValues.get(position);
        String title = memo.getTitle();

        if(memo.getType().equals("Inbox"))
            holder.mIdView.setText(memo.getSenderName() + "  >  " + memo.getTitle());
        else
            holder.mIdView.setText(StringUtil.ellipsize(title, 18) + "  >  " + memo.getRecepientName());

        if(memo.getBody() != null)
            holder.mContentView.setText(memo.getBody());

        if(memo.getAttachments() != null && memo.getAttachments().size() > 0){
            holder.mAttachmentTview.setVisibility(View.VISIBLE);
            holder.mAttachmentTview.setText("" + memo.getAttachments().size() + " attachments.");

            holder.mContentView.setSingleLine(true);
        }

        if(memo_item_type == MEMO_ITEM_TYPE_FLAT && position == getItemCount() - 1){
            holder.mSeparatorView.setVisibility(View.INVISIBLE);
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
        final TextView mAttachmentTview;
        final View mSeparatorView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
            mAttachmentTview = view.findViewById(R.id.attachment_count);
            mSeparatorView = view.findViewById(R.id.separator_view);
        }
    }
}