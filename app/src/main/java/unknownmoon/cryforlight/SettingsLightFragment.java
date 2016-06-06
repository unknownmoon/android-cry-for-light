package unknownmoon.cryforlight;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsLightFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingsLightFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_light);
    }


    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_light")) {
            // TODO
            Log.d("Light", String.format("%s: %d", key, sharedPreferences.getInt(key, getResources().getInteger(R.integer.pref_light_def_val))));
            broadcastConfigChanged("pref_light");
        }
    }

    private void broadcastConfigChanged(String key) {
        // Answer the started
        Intent notifyStartedIntent = new Intent("on-configuration-changed");
        notifyStartedIntent.putExtra("changedKey", key);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(notifyStartedIntent);
    }
}
