package com.example.hugsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.hugsapp.models.OnboardPagerAdapter;
import com.example.hugsapp.models.Settings;
import com.example.hugsapp.onboarding.OnboardFive;
import com.example.hugsapp.onboarding.OnboardFour;
import com.example.hugsapp.onboarding.OnboardOne;
import com.example.hugsapp.onboarding.OnboardSix;
import com.example.hugsapp.onboarding.OnboardThree;
import com.example.hugsapp.onboarding.OnboardTwo;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class Onboarding extends AppCompatActivity {

    Settings settings;
    static ViewPager viewPager;
    TabLayout tabLayout;
    static final int MAX_TABS = 6;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        settings = new Settings(this);
        tabLayout = findViewById(R.id.onboardTabs);
        viewPager = findViewById(R.id.onboard_view_pager);

        Bundle extras = getIntent().getExtras();


        init();

    }

    private void init() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        //Add fragments of report here
        fragments.add(new OnboardOne());
        fragments.add(new OnboardTwo());
        fragments.add(new OnboardThree());
        fragments.add(new OnboardFour());
        fragments.add(new OnboardFive());
        fragments.add(new OnboardSix());




        OnboardPagerAdapter pagerAdapter = new OnboardPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    public void exitReport() {
        settings.setSeenWelcome(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public static void nextTab() {
        if(viewPager.getCurrentItem()+1 < MAX_TABS)
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
    }

    public static void lastTab() {
        if(viewPager.getCurrentItem()-1 >= 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }


}