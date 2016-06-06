package unknownmoon.cryforlight;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LightService extends Service {
    private final int NOTIFICATION_ID = 1;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private SensorHandler mSensorHandler;
    private Notification.Builder mBuilder;
    private Toast mToast = null;

    public LightService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        mBuilder = new Notification.Builder(getApplicationContext());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Cry for Light is on!")
                .setContentText("Let's keep the light on!")
                .setAutoCancel(true)
//                .setSound(Uri.parse("content://media/internal/audio/media/29")) // TODO
                .setContentIntent(pendingIntent);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // check sensor available in devise. if available then get reading
        if (lightSensor == null) {
            showMsg("No light sensor :(", Toast.LENGTH_SHORT);
        } else {
            float max = lightSensor.getMaximumRange();
            mSensorHandler = new SensorHandler();
            sensorManager.registerListener(mSensorHandler,
                    lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }

        startForeground(NOTIFICATION_ID, mBuilder.build());

        broadcastStarted();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    protected void showMsg(String msg, int duration) {
        if (mToast != null) {

            // dismiss the previous message if exists.
            mToast.cancel();
        }

        mToast = Toast.makeText(getApplicationContext(), msg, duration);
        mToast.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mSensorHandler != null) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mSensorHandler);
        }

        showMsg("Service off", Toast.LENGTH_SHORT);
        broadcastStopped();
    }

    private void broadcastStarted() {
        // Answer the started
        Intent notifyStartedIntent = new Intent("from-service");
        notifyStartedIntent.putExtra("SERVICE_STARTED", true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyStartedIntent);
    }

    private void broadcastStopped() {
        // Answer the started
        Intent notifyStoppedIntent = new Intent("from-service");
        notifyStoppedIntent.putExtra("SERVICE_STARTED", false);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyStoppedIntent);
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            showMsg("Service on", Toast.LENGTH_SHORT);
//            stopSelf(msg.arg1);
        }
    }

    private final class SensorHandler implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float currentReading = event.values[0];

                Log.d("L", String.valueOf(currentReading));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
