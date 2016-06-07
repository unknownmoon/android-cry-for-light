package unknownmoon.cryforlight;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsLightFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final String TAG = "SettingsLight";

    public SettingsLightFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_light);
    }

    @Override
    public void onStart() {
        super.onStart();

        // initialise the saved max value.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int max = Integer.parseInt(sharedPreferences.getString("pref_light_max", "-1"));
        broadcastConfigMaxChanged(max, "pref_light");
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
            Log.d(TAG, String.format("%s: %d", key, sharedPreferences.getInt(key, getResources().getInteger(R.integer.pref_light_def_val))));
            broadcastConfigChanged("pref_light");
        } else if (key.equals("pref_light_max")) {
            int max = Integer.parseInt(sharedPreferences.getString(key, "-1"));

            Log.d(TAG, String.format("%s: %d", key, max));
            broadcastConfigMaxChanged(max, "pref_light");
            broadcastConfigChanged("pref_light_max");
        }
    }

    /**
     * Send to SliderPreference only
     *
     * @param max New max value.
     * @param key Preference key of target preference.
     */
    private void broadcastConfigMaxChanged(int max, String key) {
        Intent notifyStartedIntent = new Intent("on-slider-max-changed");
        notifyStartedIntent.putExtra("changedMax", max);
        notifyStartedIntent.putExtra("key", key);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(notifyStartedIntent);
    }

    /**
     * General notification, for example for sending to LightService
     *
     * @param key Changed key.
     */
    private void broadcastConfigChanged(String key) {

        Intent notifyStartedIntent = new Intent("on-configuration-changed");
        notifyStartedIntent.putExtra("changedKey", key);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(notifyStartedIntent);
    }
}
