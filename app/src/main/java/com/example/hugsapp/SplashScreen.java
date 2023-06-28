package com.example.hugsapp;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hugsapp.R;
import com.example.hugsapp.models.Settings;


public class SplashScreen extends AppCompatActivity {

    TextView title;
    int SPLASH_TIMEOUT = 1300; //2500
    Settings settings;
    LottieAnimationView babyFace;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        title = findViewById(R.id.app_title);
        settings = new Settings(this);
        babyFace = findViewById(R.id.splash_img);

        Intent welcomeIntent = new Intent(SplashScreen.this, Onboarding.class);
        Intent homeIntent = new Intent(SplashScreen.this, MainActivity.class);

        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        babyFace.playAnimation();

        babyFace.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!settings.seenWelcome()) {
                    startActivity(new Intent(welcomeIntent));
                } else {
                    startActivity(new Intent(homeIntent));
                }

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if(!settings.seenWelcome()) {
                    startActivity(new Intent(welcomeIntent));
                } else {
                    startActivity(new Intent(homeIntent));
                }

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });



    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}