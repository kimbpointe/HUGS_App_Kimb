package com.example.hugsapp.onboarding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hugsapp.Onboarding;
import com.example.hugsapp.R;
import com.example.hugsapp.models.Settings;

public class OnboardFive extends Fragment {
    Activity mActivity;
    Context mContext;

    TextView title, description, skip, web;
    Button next;
    ImageView back;
    Settings settings;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();

        settings = new Settings(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboard_five_fragment, null);

        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        skip = view.findViewById(R.id.skip);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.start);
        web = view.findViewById(R.id.web);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Onboarding.nextTab();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Onboarding) mActivity).exitReport();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Onboarding.lastTab();
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://sites.google.com/hugs-lab.org/hugs-lab/home";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });


        return view;
    }
}
