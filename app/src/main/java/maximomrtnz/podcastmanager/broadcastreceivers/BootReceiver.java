package maximomrtnz.podcastmanager.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import maximomrtnz.podcastmanager.services.SynchronizeService;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.Utils;

/**
 * Created by maximo on 12/08/16.
 */

public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Utils.scheduleTask(context, Constants.SYNCHRONIZE_SERVICE.REPEAT_TIME);

        }

    }

}
