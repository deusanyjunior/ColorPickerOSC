package br.usp.ime.compmus;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;

import java.net.NetworkInterface;
import java.util.ArrayList;

import com.scurab.android.colorpicker.R;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        loadNetworkInterfaces();
        PreferenceManager.setDefaultValues(PreferencesActivity.this, R.xml.preferences,
                false);
        initSummary(getPreferenceScreen());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {

        try {
            if (p instanceof ListPreference) {

                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());
            }
            if (p instanceof EditTextPreference) {

                EditTextPreference editTextPref = (EditTextPreference) p;
                if (p.getTitle().toString().contains("assword")) {
                    p.setSummary("******");
                } else {
                    p.setSummary(editTextPref.getText());
                }
            }
            if (MobileDevice.getSystemBuild() >= 11 && p instanceof MultiSelectListPreference) {

                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
        } catch (ClassCastException e) {

            e.printStackTrace();
        }
    }


    private void loadNetworkInterfaces() {

        ListPreference listPreferenceCategory = (ListPreference) findPreference("pref_multicastInterface");
        if (listPreferenceCategory != null) {
            ArrayList<NetworkInterface> categoryList = MobileDevice.getNetworkInterfaces();
            CharSequence entries[] = new String[categoryList.size()];
            CharSequence entryValues[] = new String[categoryList.size()];
            int i = 0;
            for (NetworkInterface category : categoryList) {
                entries[i] = category.getDisplayName();
                entryValues[i] = Integer.toString(i);
                i++;
            }
            listPreferenceCategory.setEntries(entries);
            listPreferenceCategory.setEntryValues(entryValues);
        }
    }
}
