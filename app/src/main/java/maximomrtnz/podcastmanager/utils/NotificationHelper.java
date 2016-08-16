package maximomrtnz.podcastmanager.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by maximo on 14/08/16.
 */

public class NotificationHelper {

    private String LOG_TAG = "NotificationHelper";

    private Context mContext;
    private String mTitle;
    private String mText;
    private int mIcon;

    public NotificationHelper(Context context, String title, String text, int icon){
        this.mContext = context;
        this.mTitle = title;
        this.mText = text;
        this.mIcon = icon;
    }

    public void show(int notificationId, Class activityToOpen){

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(mIcon)
                        .setContentTitle(mText)
                        .setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, activityToOpen);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(activityToOpen);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, mBuilder.build());

    }

}
