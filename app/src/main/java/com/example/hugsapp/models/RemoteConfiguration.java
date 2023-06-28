package com.example.hugsapp.models;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class RemoteConfiguration {

    Context context;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
//    int fetchInterval = 3600 * 12; //3600 * 12 = 12 hours
    int fetchInterval = 60 * 15; //every 15 minutes

    public RemoteConfiguration(Context context) {
        this.context = context;


        //Get configurations from firebase
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(fetchInterval)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        HashMap<String, Object> defaults = getDefaults();

        mFirebaseRemoteConfig.setDefaultsAsync(defaults);

        final Task<Void> fetch = mFirebaseRemoteConfig.fetch(fetchInterval);
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mFirebaseRemoteConfig.fetchAndActivate(); //activate new values

            }
        });
    }


    public String getWebsite() {
        return mFirebaseRemoteConfig.getString("WEBSITE_URL");
    }
    public String getInstructions() {
        return mFirebaseRemoteConfig.getString("INSTRUCTIONS");
    }

    public int getSessionDuration() { return Integer.parseInt(mFirebaseRemoteConfig.getString("SESSION_DURATION")); }

    public HashMap<String, Object> getDefaults() {

        HashMap<String, Object> defaults = new HashMap<>();

        //TODO: SET ALL DEFAULTS HERE
        defaults.put("WEBSITE_URL", "https://sites.google.com/hugs-lab.org/hugs-lab/homehttps://sites.google.com/hugs-lab.org/hugs-lab/home");
        defaults.put("SESSION_DURATION", "30");
        defaults.put("INSTRUCTIONS", "1. Power on baby toy with grasp sensors|2. Connect to toy via bluethooth|3. Begin monitoring and collecting force data");

        return defaults;

    }

}
