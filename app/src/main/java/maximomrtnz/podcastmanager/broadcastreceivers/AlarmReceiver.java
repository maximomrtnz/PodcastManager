package maximomrtnz.podcastmanager.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maximomrtnz.podcastmanager.services.SynchronizeService;

/**
 * Created by maximo on 12/08/16.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static String LOG_TAG = "AlarmReceiver";
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "Launch SynchronizeService");

        // Start synchronize services to keep podcasts up to date
        Intent i = new Intent(context, SynchronizeService.class);
        context.startService(i);

    }

}
