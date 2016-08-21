package maximomrtnz.podcastmanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by maximo on 14/08/16.
 */

public class NotificationHelper {

    private String LOG_TAG = "NotificationHelper";

    private Notification mNotification;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private RemoteViews mContentView;

    public NotificationHelper(Context context){
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext);
        mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    public NotificationHelper setIcon(int icon){
        mBuilder.setSmallIcon(icon);
        return this;
    }

    public NotificationHelper setTitle(String title){
        mBuilder.setContentTitle(title);
        return this;
    }

    public NotificationHelper setAutoCancel(boolean isAutocancel){
        mBuilder.setAutoCancel(isAutocancel);
        return this;
    }

    public NotificationHelper setIntent(Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        return this;

    }

    public NotificationHelper setContentText(String contentText){
        mBuilder.setContentText(contentText);
        return this;
    }

    public NotificationHelper setProgress(int max, int current, boolean isIndeterminate){
        mBuilder.setProgress(max,current,isIndeterminate);
        return this;
    }

    public NotificationHelper setContentView(RemoteViews contentView){
        mContentView = contentView;
        mBuilder.setContent(contentView);
        return this;
    }

    public NotificationHelper show(int notificationId){
        mNotification = mBuilder.build();
        mNotificationManager.notify(notificationId, mNotification);
        return this;
    }

    public Notification getNotification(){
        return mNotification;
    }

    public RemoteViews getContentView(){
        return mContentView;
    }

}
