package com.flynorc.a08_newsapp;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //bind the current values to be displayed
            Preference nrResults = findPreference(getString(R.string.settings_nr_results_key));
            bindPreferenceSummaryToValue(nrResults);

            Preference query = findPreference(getString(R.string.settings_query_key));
            bindPreferenceSummaryToValue(query);
        }

        /*
         * helper to display the current value for a certain preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            //validate nr results
            if(preference.getKey() == getString(R.string.settings_nr_results_key)) {
                int nrResults = Integer.parseInt( (String) newValue);
                if( nrResults < 1 || nrResults > 50) {
                    Toast.makeText(getActivity(), R.string.nr_reuslts_validation_fail_toast,  Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            //validate query
            if(preference.getKey() == getString(R.string.settings_query_key)) {
                String query = (String) newValue;
                if(query.isEmpty() || query.length() > 50) {
                    Toast.makeText(getActivity(), R.string.query_validation_fail_toast,  Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            //validation passed, save the value and update the preview
            preference.setSummary(newValue.toString());
            return true;
        }
    }
}
