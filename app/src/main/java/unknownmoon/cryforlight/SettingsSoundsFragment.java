package unknownmoon.cryforlight;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSoundsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public SettingsSoundsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_sounds);
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
        if (key.equals("pref_sound_level")) {
            // TODO
            Log.d("Sound L", String.format("%s: %d", key, sharedPreferences.getInt(key, getResources().getInteger(R.integer.pref_sound_level_def_val))));
            broadcastConfigChanged("pref_sound_level");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Log.d("Sound P", String.format("REQ: %d\nRES: %d\nDATA: %s", requestCode, resultCode, data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI).toString()));
            broadcastConfigChanged("pref_sound_file");
        } catch (Exception e) {
            // simply ignore for the moment.
            Log.d("Sound P", String.format("REQ: %d\nRES: %d", requestCode, resultCode));
        }
    }

    private void broadcastConfigChanged(String key) {
        // Answer the started
        Intent notifyStartedIntent = new Intent("on-configuration-changed");
        notifyStartedIntent.putExtra("changedKey", key);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(notifyStartedIntent);
    }
}
