package maximomrtnz.podcastmanager.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maximomrtnz.podcastmanager.services.PlayerService;

/**
 * Created by maximo on 15/10/16.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    public static final String LOG_TAG = "NotificationBroadcast";
    public static final String ACTION_PLAY_PAUSE = "maximomrtnz.podcastmanager.action.NOTIFICATION.PLAY_PAUSE";
    public static final String ACTION_SKIP_NEXT = "maximomrtnz.podcastmanager.action.NOTIFICATION.SKIP_NEXT";
    public static final String ACTION_SKIP_PREVIOUS = "maximomrtnz.podcastmanager.action.NOTIFICATION.SKIP_PREVIUOS";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.d(LOG_TAG,action);

        if(action.equalsIgnoreCase(ACTION_PLAY_PAUSE)){
            doAction(context,PlayerService.ACTION_TOGGLE_PLAYBACK);
        }else if(action.equalsIgnoreCase(ACTION_SKIP_NEXT)){
            doAction(context,PlayerService.ACTION_SKIP_NEXT);
        }else if(action.equalsIgnoreCase(ACTION_SKIP_PREVIOUS)){
            doAction(context,PlayerService.ACTION_SKIP_PREVIOUS);
        }

    }

    public void doAction(Context context, String action){
        Intent i = new Intent(context,PlayerService.class);
        i.setAction(action);
        context.startService(i);
    }
}
