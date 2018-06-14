package akil.co.tz.notetaker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.notetaker.Data.Book;
import akil.co.tz.notetaker.NoteDetailActivity;
import akil.co.tz.notetaker.NoteDetailFragment;
import akil.co.tz.notetaker.NoteEditActivity;
import akil.co.tz.notetaker.NoteListActivity;
import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.VerseBottomSheet;
import akil.co.tz.notetaker.models.Post;

/**
 * Created by DevDept on 6/14/18.
 */

public class BookAdapter
        extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final AppCompatActivity mParentActivity;
    private final List<Book> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Book item = (Book) view.getTag();
//            final AppCompatActivity context = view.getContext();

            VerseBottomSheet verseBottomSheet = new VerseBottomSheet();
            verseBottomSheet.show(mParentActivity.getSupportFragmentManager(), "versebottomsheet");
        }
    };

    public BookAdapter(AppCompatActivity ctx, List<Book> items) {
        mValues = items;
        mParentActivity = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = mValues.get(position).getTitle();
        holder.mItemView.setText(title);

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mItemView;
        final ImageView mIconView;

        ViewHolder(View view) {
            super(view);
            mItemView = view.findViewById(R.id.title_text);
            mIconView = view.findViewById(R.id.icon_view);
        }
    }
}