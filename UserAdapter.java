package com.jobs.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.tycoders.jobs.R;
import com.jobs.item.ItemUser;
import com.jobs.util.API;
import com.jobs.util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ItemRowHolder> {

    private ArrayList<ItemUser> dataList;
    private Context mContext;
    private String jobId;

    public UserAdapter(Context context, ArrayList<ItemUser> dataList, String jobId) {
        this.dataList = dataList;
        this.mContext = context;
        this.jobId = jobId;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_user, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final ItemUser singleItem = dataList.get(position);
        holder.textName.setText(singleItem.getUserName());
        holder.textEmail.setText(singleItem.getUserEmail());
        holder.textPhone.setText(singleItem.getUserPhone());
        if (!singleItem.getUserCity().isEmpty()) {
            holder.textCity.setText(singleItem.getUserCity());
        } else {
            holder.textCity.setVisibility(View.GONE);
        }
        if (!singleItem.getUserImage().isEmpty()) {
            Picasso.get().load(singleItem.getUserImage()).placeholder(R.mipmap.ic_launcher_app).into(holder.image);
        }

        if (singleItem.getUserResume().isEmpty()) {
            holder.btnResume.setVisibility(View.GONE);
        }

        holder.textEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", singleItem.getUserEmail(), null));
                emailIntent
                        .putExtra(Intent.EXTRA_SUBJECT, "Reply for the post ");
                mContext.startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
            }
        });

        holder.textPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", singleItem.getUserPhone(), null));
                mContext.startActivity(intent);
            }
        });

        holder.btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(singleItem.getUserResume()));
                mContext.startActivity(intent);
            }
        });

        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(singleItem.getUserId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView textName, textEmail, textPhone, textCity;
        LinearLayout lyt_parent;
        Button btnResume, btnStatus;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textName = itemView.findViewById(R.id.text_job_title);
            textEmail = itemView.findViewById(R.id.text_email);
            textPhone = itemView.findViewById(R.id.text_phone);
            textCity = itemView.findViewById(R.id.text_city);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            btnResume = itemView.findViewById(R.id.btn_show_resume);
            btnStatus = itemView.findViewById(R.id.btn_show_status);
        }
    }

    private void changeStatus(final String userId) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.status))
                .setMessage(mContext.getString(R.string.did_you_seen))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                        jsObj.addProperty("method_name", "user_apply_job_seen");
                        jsObj.addProperty("apply_user_id", userId);
                        jsObj.addProperty("job_id", jobId);
                        params.put("data", API.toBase64(jsObj.toString()));
                        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(mContext, mContext.getString(R.string.status_seen), Toast.LENGTH_SHORT).show();
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
}
