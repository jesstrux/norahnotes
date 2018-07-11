package akil.co.tz.notetaker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import akil.co.tz.notetaker.NoteDetailActivity;
import akil.co.tz.notetaker.NoteEditActivity;
import akil.co.tz.notetaker.NoteListActivity;
import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.AdminItem;
import akil.co.tz.notetaker.models.Memo;

public class AdminItemAdapter
        extends RecyclerView.Adapter<AdminItemAdapter.ViewHolder>{

    private final List<AdminItem> mValues;

    private OnItemClickListener mListener;

    public void setmListener(OnItemClickListener listener){
        this.mListener = listener;
    };

    public interface OnItemClickListener {
        void onClick(AdminItem item);
    }

    public AdminItemAdapter(List<AdminItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AdminItem adminItem = mValues.get(position);

        holder.mTitleView.setText(adminItem.getTitle());
        int icon = R.drawable.ic_department;

        if(adminItem.getType().equals("Job"))
            icon = R.drawable.ic_work;
        else if (adminItem.getType().equals("Staff")){
            icon = R.drawable.ic_person;
            holder.mIconView.setPadding(17,17,17,17);
        }

        holder.mIconView.setImageResource(icon);
        holder.itemView.setTag(adminItem);

        if(adminItem.getType().equals("Staff")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null)
                        mListener.onClick(mValues.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mIconView;
        final TextView mTitleView;

        ViewHolder(View view) {
            super(view);
            mIconView = view.findViewById(R.id.icon_view);
            mTitleView = view.findViewById(R.id.title_text);
        }
    }
}