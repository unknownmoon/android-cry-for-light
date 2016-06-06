package unknownmoon.cryforlight;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Toast mToast;
    private SensorHandler mSensorHandler;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // check sensor available in devise. if available then get reading
        if (lightSensor == null) {
            showMsg("No light sensor :(", Toast.LENGTH_SHORT);
        } else {

            mSensorHandler = new SensorHandler();
            sensorManager.registerListener(mSensorHandler,
                    lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        return view;
    }

    protected void showMsg(String msg, int duration) {
        if (mToast != null) {

            // dismiss the previous message if exists.
            mToast.cancel();
        }

        mToast = Toast.makeText(getContext(), msg, duration);
        mToast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (mSensorHandler != null) {
            SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mSensorHandler);
        }
    }

    private final class SensorHandler implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float brightness = event.values[0];

                TextView valueView = (TextView) getView().findViewById(R.id.current_brightness_value);
                valueView.setText(getString(R.string.current_brightness_value_format, brightness));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
