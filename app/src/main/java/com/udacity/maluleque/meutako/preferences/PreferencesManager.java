package com.udacity.maluleque.meutako.preferences;

import android.content.SharedPreferences;

public class PreferencesManager {

    private static PreferencesManager INSTANCE = null;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;


    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PreferencesManager(SharedPreferences prefs) {
        pref = prefs;
        editor = pref.edit();
    }

    public static synchronized PreferencesManager getInstance(SharedPreferences prefs){
        if (INSTANCE == null){
            INSTANCE = new PreferencesManager(prefs);
        }
        return INSTANCE;
    }

    public void setFirstLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


}
