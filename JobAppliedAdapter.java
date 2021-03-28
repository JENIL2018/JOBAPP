package com.jobs.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tycoders.jobs.R;
import com.jobs.item.ItemJob;
import com.jobs.util.PopUpAds;
import com.jobs.util.RvOnClickListener;

import java.util.ArrayList;

public class JobAppliedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemJob> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private RvOnClickListener clickListener;

    public JobAppliedAdapter(Context context, ArrayList<ItemJob> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_applied_job_item, parent, false);
            return new ItemRowHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ItemRowHolder holder = (ItemRowHolder) viewHolder;

            final ItemJob singleItem = dataList.get(position);
            holder.jobTitle.setText(singleItem.getJobName());
            holder.companyTitle.setText(singleItem.getJobCompanyName());
            String date = mContext.getString(R.string.applied_job_date, singleItem.getJobAppliedDate());
            holder.jobDate.setText(date);

            if (singleItem.isJobSeen()) {
                holder.btnApplySeen.setText(mContext.getString(R.string.applied_job_seen));
                holder.btnApplySeen.setBackgroundResource(R.drawable.applied_job_seen_btn);
            } else {
                holder.btnApplySeen.setText(mContext.getString(R.string.applied_job));
                holder.btnApplySeen.setBackgroundResource(R.drawable.applied_job_btn);
            }

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() + 1 : 0);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, companyTitle, jobDate;
        LinearLayout lyt_parent;
        Button btnApplySeen;

        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            companyTitle = itemView.findViewById(R.id.text_job_company);
            jobDate = itemView.findViewById(R.id.text_job_date);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            btnApplySeen = itemView.findViewById(R.id.btn_applied);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
