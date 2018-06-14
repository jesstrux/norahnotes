package akil.co.tz.notetaker.Adapters;

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

import akil.co.tz.notetaker.NoteDetailActivity;
import akil.co.tz.notetaker.NoteDetailFragment;
import akil.co.tz.notetaker.NoteEditActivity;
import akil.co.tz.notetaker.NoteListActivity;
import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.Post;

/**
 * Created by DevDept on 6/14/18.
 */

public class PostAdapter
        extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final NoteListActivity mParentActivity;
    private final List<Post> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Post item = (Post) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(NoteDetailFragment.ARG_ITEM_ID, item.getId());
                NoteDetailFragment fragment = new NoteDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.note_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent;
                if(item.getDetails() != null)
                    intent = new Intent(context, NoteDetailActivity.class);
                else
                    intent = new Intent(context, NoteEditActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("post", item);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        }
    };

    public PostAdapter(NoteListActivity parent, List<Post> items, boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String theme = mValues.get(position).getTheme();
        String title = mValues.get(position).getTitle();
        Boolean has_title = title != null;

        if(has_title)
            holder.mIdView.setText(mValues.get(position).getTitle());
        else
            holder.mIdView.setVisibility(View.GONE);

//            holder.mIdView.setTextColor(theme.equals("pink") ? Color.WHITE : Color.BLACK);
        if(mValues.get(position).getDetails() != null)
            holder.mContentView.setText(mValues.get(position).getDetails());
//            holder.mContentView.setTextColor(theme.equals("pink") ? Color.WHITE : Color.BLACK);

        if(!has_title){
            holder.mContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            holder.mContentView.setMaxLines(3);
        }

//            holder.itemView.setBackgroundResource(theme.equals("pink") ? R.color.colorPink : R.color.colorGreen);

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

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
        }
    }
}