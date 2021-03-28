package com.jobs.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.tycoders.jobs.R;
import com.tycoders.jobs.UserListActivity;
import com.jobs.item.ItemJob;
import com.jobs.util.API;
import com.jobs.util.Constant;
import com.jobs.util.PopUpAds;
import com.jobs.util.RvOnClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class JobProviderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemJob> dataList;
    private Context mContext;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private RvOnClickListener clickListener;

    public JobProviderAdapter(Context context, ArrayList<ItemJob> dataList) {
        this.dataList = dataList;
        this.mContext = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_job_provider, parent, false);
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
            holder.categoryTitle.setText(singleItem.getJobCategoryName());
            String date = mContext.getString(R.string.date_posted) + singleItem.getJobDate();

            holder.jobDate.setText(date);
            holder.jobDescription.setText(Html.fromHtml(singleItem.getJobDesc()));
            holder.jobAddress.setText(singleItem.getJobAddress());
            String total = mContext.getString(R.string.total_job);
            holder.btnApplyJob.setText(String.format(total, singleItem.getJobApplyTotal()));

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
                }
            });

            holder.btnApplyJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserListActivity.class);
                    intent.putExtra("Id", singleItem.getId());
                    mContext.startActivity(intent);
                }
            });

            holder.btnDeleteJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteJob(holder.getAdapterPosition(), singleItem.getId());
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
        TextView jobTitle, categoryTitle, jobDate, jobDescription, jobAddress;
        LinearLayout lyt_parent;
        Button btnApplyJob, btnDeleteJob;

        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            categoryTitle = itemView.findViewById(R.id.text_job_category);
            jobDate = itemView.findViewById(R.id.text_job_date);
            jobDescription = itemView.findViewById(R.id.text_job_description);
            jobAddress = itemView.findViewById(R.id.text_job_address);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
            btnDeleteJob = itemView.findViewById(R.id.btn_delete_job);
        }
    }

    private void deleteJob(final int position, final String id) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_this_job))
                .setMessage(mContext.getString(R.string.delete_job_confirm))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                        jsObj.addProperty("method_name", "delete_job");
                        jsObj.addProperty("delete_job_id", id);
                        params.put("data", API.toBase64(jsObj.toString()));
                        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                dataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, dataList.size());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            }

                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
