package akil.co.tz.notetaker.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import akil.co.tz.notetaker.R;
import akil.co.tz.notetaker.models.AdminItem;
import akil.co.tz.notetaker.models.Ufs;

public class RepliesAdapter
        extends RecyclerView.Adapter<RepliesAdapter.ViewHolder> {

    private final List<Ufs> mValues;

    public RepliesAdapter(List<Ufs> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ufs_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Ufs item = mValues.get(position);

        viewHolder.name.setText(item.getName());
        int status  = Integer.valueOf(item.getStatus());

        if(status == Ufs.STATUS_ACCEPTED) {
            viewHolder.icon.setBackgroundResource(R.color.tintXls);
            viewHolder.response.setText("Accepted");
        } else if(status == Ufs.STATUS_REJECTED) {
            viewHolder.icon.setBackgroundResource(R.color.tintPdf);
            viewHolder.response.setText("Rejected");
        }
        else{
            viewHolder.response.setText("Unknown");
        }


        viewHolder.itemView.setTag(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View icon;
        final TextView name;
        final TextView response;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.ufs_name);
            response = view.findViewById(R.id.ufs_response);
            icon = view.findViewById(R.id.icon_view);
        }
    }
}