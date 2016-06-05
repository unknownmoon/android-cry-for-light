package unknownmoon.cryforlight;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Boolean mIsServiceOn = false;
    private Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setLogo(R.mipmap.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setActivated(mIsServiceOn);
    }

    public void onServiceButtonToggled(View view) {
        mIsServiceOn = !mIsServiceOn;
        String msgStatus = getString(R.string.service_status_off);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setActivated(mIsServiceOn);

        if (mIsServiceOn) {
            msgStatus = getString(R.string.service_status_on);
        }

        showMsg(getString(R.string.service_status_msg, msgStatus), Toast.LENGTH_SHORT);

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
    }

    protected void showMsg(String msg, int duration) {
        if (mToast != null) {

            // dismiss the previous message if exists.
            mToast.cancel();
        }

        mToast = Toast.makeText(getApplicationContext(), msg, duration);
        mToast.show();
    }
}
