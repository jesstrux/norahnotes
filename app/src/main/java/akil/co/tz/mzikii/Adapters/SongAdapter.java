package akil.co.tz.mzikii.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.mzikii.NoteEditActivity;
import akil.co.tz.mzikii.R;
import akil.co.tz.mzikii.models.Song;

/**
 * Created by DevDept on 6/14/18.
 */

public class SongAdapter
        extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Song item = (Song) view.getTag();
            Context context = view.getContext();
            Intent intent;
//            if(item.getDescription() != null)
//                intent = new Intent(context, NoteDetailActivity.class);
//            else
                intent = new Intent(context, NoteEditActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("title", item.getTitle());
            bundle.putString("id", item.getId());
            bundle.putString("description", item.getDescription());
            intent.putExtras(bundle);

            context.startActivity(intent);
        }
    };

    public SongAdapter(List<Song> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Song item = mValues.get(position);
        String title = item.getTitle();
        Boolean has_title = title != null;

        if(has_title)
            holder.mIdView.setText(title);
        else{
            holder.mIdView.setVisibility(View.GONE);

            holder.mContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            holder.mContentView.setMaxLines(3);
        }

        if(item.getDescription() != null)
            holder.mContentView.setText(item.getDescription());

        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}