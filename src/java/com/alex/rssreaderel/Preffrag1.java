package com.alex.rssreaderel;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Alex on 16.02.2017.
 */
public class Preffrag1 extends PreferenceFragment {


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int preferenceFile_toLoad = -1;
        String settings = getArguments().getString("settings");
        if ("pref1".equalsIgnoreCase(settings)) {
            preferenceFile_toLoad = R.xml.pref1;
        } else if ("pref2".equalsIgnoreCase(settings)) {
            // Load the preferences from an XML resource
            preferenceFile_toLoad = R.xml.pref2;
        }else if ("font".equalsIgnoreCase(settings)) {
            // Load the preferences from an XML resource
            preferenceFile_toLoad = R.xml.font_pref;
        }else if ("notice".equalsIgnoreCase(settings)) {
            // Load the preferences from an XML resource
            preferenceFile_toLoad = R.xml.notice_pref;
        }
            addPreferencesFromResource(preferenceFile_toLoad);

        }


    }
