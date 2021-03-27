package com.jobs.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tycoders.jobs.R;
import com.jobs.adapter.SkillsAdapter;
import com.jobs.item.ItemJob;

import java.util.ArrayList;
import java.util.Arrays;

public class JobDetailsFragment extends Fragment {

    WebView webView;
    TextView textSalary, textJobQualification, textWorkDay, textWorkTime, textExp, textType;
    ItemJob itemJob;
    RecyclerView recyclerView;
    ArrayList<String> mSkills;

    public static JobDetailsFragment newInstance(ItemJob itemJob) {
        JobDetailsFragment f = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("itemJob", itemJob);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_details, container, false);
        if (getArguments() != null) {
            itemJob = (ItemJob) getArguments().getSerializable("itemJob");
        }
        webView = rootView.findViewById(R.id.text_job_description);
        textSalary = rootView.findViewById(R.id.text_job_salary);
        textJobQualification = rootView.findViewById(R.id.text_job_qualification);
        textSalary = rootView.findViewById(R.id.text_job_salary);
        textWorkDay = rootView.findViewById(R.id.text_job_work_day);
        textWorkTime = rootView.findViewById(R.id.text_job_work_time);
        textExp = rootView.findViewById(R.id.text_job_exp);
        textType = rootView.findViewById(R.id.text_job_type);
        recyclerView = rootView.findViewById(R.id.rv_skills);
        mSkills = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemJob.getJobDesc();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.otf\")}body{font-family: MyFont;color: #9E9E9E;text-align:left;font-size:14px;margin-left:0px}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";
        

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        textSalary.setText(itemJob.getJobSalary());
        textJobQualification.setText(itemJob.getJobQualification());
        textType.setText(itemJob.getJobType());
        textWorkDay.setText(itemJob.getJobWorkDay());
        textWorkTime.setText(itemJob.getJobWorkTime());
        textExp.setText(itemJob.getJobExperience());

        if (!itemJob.getJobSkill().isEmpty()) {
            mSkills = new ArrayList<>(Arrays.asList(itemJob.getJobSkill().split(",")));
            SkillsAdapter skillsAdapter = new SkillsAdapter(getActivity(), mSkills);
            recyclerView.setAdapter(skillsAdapter);
        }

        return rootView;
    }
}
