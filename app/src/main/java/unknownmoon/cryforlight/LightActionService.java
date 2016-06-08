package unknownmoon.cryforlight;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class LightActionService extends IntentService {
    public static final String ACTION_PAUSE = "unknownmoon.cryforlight.action.PAUSE";
    public static final String ACTION_RESUME = "unknownmoon.cryforlight.action.RESUME";
    public static final String ACTION_EXIT = "unknownmoon.cryforlight.action.EXIT";
    public static String TAG = "LightActionService";


    public LightActionService() {
        super("LightActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(TAG, "onHandleIntent: " + action);
            broadcastAction(action);
        }
    }

    private void broadcastAction(String action) {
        Log.d(TAG, "broadcastAction: " + action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(action));
    }
}
