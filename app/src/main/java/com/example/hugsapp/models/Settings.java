package com.example.hugsapp.models;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    Context mContext;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Settings(Context context) {
        mContext = context;
        preferences = mContext.getSharedPreferences("com.example.hugsapp.settings", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public boolean seenWelcome() {
        return preferences.getBoolean("seen_welcome", false);
    }

    public void setSeenWelcome(boolean haveSeen) {
        editor.putBoolean("seen_welcome", haveSeen);
        editor.apply();
    }



}
