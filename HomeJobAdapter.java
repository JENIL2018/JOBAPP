package com.jobs.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tycoders.jobs.MyApplication;
import com.tycoders.jobs.R;
import com.tycoders.jobs.SignInActivity;
import com.jobs.item.ItemJob;
import com.jobs.util.ApplyJob;
import com.jobs.util.Constant;
import com.jobs.util.NetworkUtils;
import com.jobs.util.PopUpAds;
import com.jobs.util.RvOnClickListener;
import com.jobs.util.SaveClickListener;
import com.jobs.util.SaveJob;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeJobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemJob> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeJobAdapter(Context context, ArrayList<ItemJob> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_job_item_new, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;

        final ItemJob singleItem = dataList.get(position);
        holder.jobTitle.setText(singleItem.getJobName());
        holder.jobType.setText(singleItem.getJobType());
        holder.jobAddress.setText(singleItem.getJobAddress());
        Picasso.get().load(singleItem.getJobImage()).placeholder(R.drawable.placeholder).into(holder.jobImage);


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });

        holder.btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getIsLogin()) {
                    if (NetworkUtils.isConnected(mContext)) {
                        new ApplyJob(mContext).userApply(singleItem.getId());
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.need_login), Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(mContext, SignInActivity.class);
                    intentLogin.putExtra("isOtherScreen", true);
                    mContext.startActivity(intentLogin);
                }
            }
        });

        if (singleItem.isJobFavourite()) {
            holder.imageFav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            holder.imageFav.setImageResource(R.drawable.ic_fav);
        }

        holder.imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().getIsLogin()) {
                    if (NetworkUtils.isConnected(mContext)) {
                        SaveClickListener saveClickListener = new SaveClickListener() {
                            @Override
                            public void onItemClick(boolean isSave, String message) {
                                if (isSave) {
                                    holder.imageFav.setImageResource(R.drawable.ic_fav_hover);
                                } else {
                                    holder.imageFav.setImageResource(R.drawable.ic_fav);
                                }
                            }
                        };
                        new SaveJob(mContext).userSave(singleItem.getId(),saveClickListener);
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.need_login), Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(mContext, SignInActivity.class);
                    intentLogin.putExtra("isOtherScreen", true);
                    mContext.startActivity(intentLogin);
                }
            }
        });

        switch (singleItem.getJobType()) {
            case Constant.JOB_TYPE_HOURLY:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.hourly_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.hourly_time_bg));
                break;
            case Constant.JOB_TYPE_HALF:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.half_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.half_time_bg));
                break;
            case Constant.JOB_TYPE_FULL:
                holder.jobType.setTextColor(mContext.getResources().getColor(R.color.full_time_text));
                holder.cardViewType.setCardBackgroundColor(mContext.getResources().getColor(R.color.full_time_bg));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobAddress, jobType;
        LinearLayout lyt_parent;
        Button btnApplyJob;
        CardView cardViewType;
        CircleImageView jobImage;
        ImageView imageFav;

        ItemRowHolder(View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_job_title);
            jobType = itemView.findViewById(R.id.text_job_type);
            jobAddress = itemView.findViewById(R.id.text_job_address);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            cardViewType = itemView.findViewById(R.id.cardJobType);
            jobImage = itemView.findViewById(R.id.image_job);
            imageFav = itemView.findViewById(R.id.imageFav);
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job);
        }
    }
}
