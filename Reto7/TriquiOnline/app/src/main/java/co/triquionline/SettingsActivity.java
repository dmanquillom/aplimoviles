package co.triquionline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

                    if(s.compareTo(TriquiActivity.victoryMessageKey) == 0){
                        setVictoryMessage();
                    } else if (s.compareTo(TriquiActivity.difficultyLevelKey) == 0) {
                        setDifficultyLevelSummary();
                    }
                }
            };
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
            setVictoryMessage();
            setDifficultyLevelSummary();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }

        private void setVictoryMessage (){
            EditTextPreference victoryMessagePref = (EditTextPreference) findPreference(TriquiActivity.victoryMessageKey);
            String victoryMessage = getPreferenceScreen().getSharedPreferences().getString(TriquiActivity.victoryMessageKey, getString(R.string.difficulty_expert));
            victoryMessagePref.setSummary((CharSequence) victoryMessage);
        }

        private void setDifficultyLevelSummary(){
            ListPreference difficultyLevelPref = (ListPreference) findPreference(TriquiActivity.difficultyLevelKey);
            String difficulty = getPreferenceScreen().getSharedPreferences().getString(TriquiActivity.difficultyLevelKey, getString(R.string.difficulty_expert));
            difficultyLevelPref.setSummary((CharSequence) difficulty);
        }
    }
}