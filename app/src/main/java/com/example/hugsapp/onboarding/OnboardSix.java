package com.example.hugsapp.onboarding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.hugsapp.Onboarding;
import com.example.hugsapp.R;
import com.example.hugsapp.models.Settings;

public class OnboardSix extends Fragment {
    Activity mActivity;
    Context mContext;
    LottieAnimationView check;
    TextView title, description, skip;
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

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboard_six_fragment, null);

        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        skip = view.findViewById(R.id.skip);
        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.start);
        check = view.findViewById(R.id.animateCheck);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Onboarding) mActivity).exitReport();
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


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        check.playAnimation();

    }
}
