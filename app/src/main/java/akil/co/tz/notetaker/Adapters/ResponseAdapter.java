package akil.co.tz.notetaker.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.MemoResponse;
import akil.co.tz.notetaker.models.Ufs;

public class ResponseAdapter
        extends RecyclerView.Adapter<ResponseAdapter.ViewHolder> {

    private final List<MemoResponse> mValues;

    public ResponseAdapter(List<MemoResponse> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.response_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        MemoResponse item = mValues.get(position);

        viewHolder.name.setText(item.getName());
        viewHolder.response.setText(item.getComment());

        if(position == getItemCount() - 1)
            viewHolder.separator.setVisibility(View.GONE);

        viewHolder.itemView.setTag(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView response;
        final View separator;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            response = view.findViewById(R.id.response);
            separator = view.findViewById(R.id.separator_view);
        }
    }
}