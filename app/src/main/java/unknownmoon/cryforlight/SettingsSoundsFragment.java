package unknownmoon.cryforlight;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsSoundsFragment extends PreferenceFragment {


    public SettingsSoundsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_sounds);
    }

}
