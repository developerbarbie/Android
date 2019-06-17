package com.isaacson.josie.jisaacsonfinalproject;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs =
                getPreferenceScreen().getSharedPreferences() ;
        onSharedPreferenceChanged(prefs, "pref_bricks") ;
        onSharedPreferenceChanged(prefs, "pref_hits") ;
        onSharedPreferenceChanged(prefs, "pref_balls") ;

    }

    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().
                registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(sharedPreferences != null){
            Preference pref = findPreference(key) ;
            Log.v("Prefs", key);
            int interval;
            switch(key) {
                // EditTextPrefs only support strings, even if restricted to numbers
                case "pref_bricks":
                    interval = Integer.parseInt(sharedPreferences.getString(key,"5")) ;
                    if (interval < 1 || interval > 100){
                        interval = 5 ;
                        displayInputWarning("bricks");
                    }
                    sharedPreferences.edit().putString(key, Integer.toString(interval)).commit() ;
                    pref.setTitle("Initial Brick Count (1-100) = " + interval);
                    break ;
                case "pref_hits":
                    interval = Integer.parseInt(sharedPreferences.getString(key,"2")) ;
                    if (interval < 1 || interval > 4){
                        interval = 2 ;
                        displayInputWarning("hits");
                    }
                    sharedPreferences.edit().putString(key, Integer.toString(interval)).commit() ;
                    pref.setTitle("Hits to Remove Brick (1-4) = " + interval);
                    break ;
                case "pref_balls":
                    interval = Integer.parseInt(sharedPreferences.getString(key,"3")) ;
                    if (interval < 1 || interval > 4){
                        interval = 3 ;
                        displayInputWarning("balls");
                    }
                    sharedPreferences.edit().putString(key,Integer.toString(interval)).commit() ;
                    pref.setTitle("Balls Per Level (1-4) = " + interval);
                    break ;
            }
        }

    }

    public void displayInputWarning(String dataType){
        switch(dataType){
            case "bricks":
                Toast.makeText(getActivity(),
                        "Value must be between 1 - 100. Default was set.",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            case "hits":
                Toast.makeText(getActivity(),
                        "Value must be between 1 - 4. Default was set.",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
            case "balls":
                Toast.makeText(getActivity(),
                        "Value must be between 1 - 4. Default was set.",
                        Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

}
