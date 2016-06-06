package unknownmoon.cryforlight;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LightService extends Service {
    private final int NOTIFICATION_ID = 1;
    protected BroadcastReceiver mMessageReceiver;
    private Looper mServiceLooper;
    private SensorHandler mSensorHandler;
    private Notification.Builder mBuilder;
    private Toast mToast = null;
    private int mPrefSoundLevel;
    private String mPrefSoundFile;
    private int mPrefLightThreshold;
    private float mLastBrightness;
    private Boolean mIsCrying = false;
    private Ringtone mRingtone;
    private int mOriginalVol;

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

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                String changedKey = intent.getStringExtra("changedKey");
                syncPref(changedKey);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("on-configuration-changed"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mOriginalVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);

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

            mSensorHandler = new SensorHandler();
            sensorManager.registerListener(mSensorHandler,
                    lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }

        syncPrefs();

        startForeground(NOTIFICATION_ID, mBuilder.build());

        broadcastStarted();

        return START_NOT_STICKY;
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

        stopRingtone();

        if (mSensorHandler != null) {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(mSensorHandler);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

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

    private void syncPrefs() {
        syncPref("pref_light");
        syncPref("pref_sound_level");
        syncPref("pref_sound_file");
    }

    private void syncPref(String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        switch (key) {
            case "pref_light":
                updateLightThreshold(sharedPrefs.getInt(key, mPrefLightThreshold));
                break;
            case "pref_sound_level":
                updateSoundLevel(sharedPrefs.getInt(key, mPrefSoundLevel));
                break;
            case "pref_sound_file":
                updateSoundFile(sharedPrefs.getString(key, mPrefSoundFile));
                break;
            default:
                Log.d("sync", key);
        }
    }

    private void updateSoundLevel(int lvl) {
        mPrefSoundLevel = lvl;
        updateRingtone();
        Log.d("CFL", "sound_level - " + lvl);
    }

    private void updateSoundFile(String path) {
        mPrefSoundFile = path;
        updateRingtone();
        Log.d("CFL", "sound_file - " + path);
    }

    private void updateRingtone() {
        if (mPrefSoundFile != null && mPrefSoundFile.length() > 0) {

            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(mPrefSoundFile));

            if (ringtone != null) {
                stopRingtone();

                mRingtone = ringtone;

                setupRingtone();

                if (mIsCrying) {
                    mRingtone.play();
                }
            }
        }
    }

    private void stopRingtone() {
        if (mRingtone != null && mRingtone.isPlaying()) {
            mRingtone.stop();
        }
        resetVol();
    }

    private void setupRingtone() {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int curVol = Math.round(maxVol * mPrefSoundLevel / 100);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, curVol, 0);
    }

    private void resetVol() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.d("CFL", "Original volume: " + mOriginalVol);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, mOriginalVol, 0);
    }

    private void updateLightThreshold(int lvl) {
        mPrefLightThreshold = lvl;
        shouldWeCry();
        Log.d("CFL", "light - " + lvl);
    }

    private void shouldWeCry() {
        if (mLastBrightness < mPrefLightThreshold && !mIsCrying) {

            mIsCrying = true;
            Log.d("CFL", "I'm crying!!!");

            if (mRingtone != null) {
                mRingtone.play();
            }

        } else if (mLastBrightness >= mPrefLightThreshold && mIsCrying) {

            mIsCrying = false;
            Log.d("CFL", "Now I'm fine...");

            if (mRingtone != null && mRingtone.isPlaying()) {
                mRingtone.stop();
            }
        }
    }

    private final class SensorHandler implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                mLastBrightness = event.values[0];

                Log.d("CFL", String.valueOf(mLastBrightness));
                shouldWeCry();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
