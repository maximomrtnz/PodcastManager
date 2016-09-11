package maximomrtnz.podcastmanager.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat.MediaStyle;
import android.widget.RemoteViews;

/**
 * Created by maximo on 14/08/16.
 */

public class NotificationHelper {

    private String LOG_TAG = "NotificationHelper";

    private Notification mNotification;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat mNotificationManager;
    private RemoteViews mContentView;

    public NotificationHelper(Context context){
        mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext);
        mNotificationManager = NotificationManagerCompat.from(mContext);
    }

    public NotificationHelper setIcon(int icon){
        mBuilder.setSmallIcon(icon);
        return this;
    }

    public NotificationHelper setTitle(CharSequence title){
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

    public NotificationHelper setContentText(CharSequence contentText){
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
        mNotificationManager.notify(notificationId, mNotification);
        return this;
    }

    public RemoteViews getContentView(){
        return mContentView;
    }

    public void cancelAll(){
        mNotificationManager.cancelAll();
    }

    public NotificationHelper addAction(NotificationCompat.Action action){
        mBuilder.addAction(action);
        return this;
    }

    public NotificationHelper setStyle(NotificationCompat.MediaStyle mediaStyle){
        mBuilder.setStyle(mediaStyle);
        return this;
    }

    public NotificationHelper setSmallIcon(int resource){
        mBuilder.setSmallIcon(resource);
        return this;
    }

    public NotificationHelper setVisibility(int visibility){
        mBuilder.setVisibility(visibility);
        return this;
    }

    public NotificationHelper setContentIntent(PendingIntent contentIntent){
        mBuilder.setContentIntent(contentIntent);
        return this;
    }

    public Notification build(){
        mNotification = mBuilder.build();
        return mNotification;
    }

    public NotificationHelper setLargeIcon(Bitmap largeIcon){
        mBuilder.setLargeIcon(largeIcon);
        return this;
    }

    public NotificationHelper setOngoing(boolean ongoing){
        mBuilder.setOngoing(ongoing);
        return this;
    }

    public NotificationHelper setWhen(long when){
        mBuilder.setWhen(when);
        return this;
    }

    public NotificationHelper setShowWhen(boolean showWhen){
        mBuilder.setShowWhen(showWhen);
        return this;
    }

    public NotificationHelper setUsesChronometer(boolean usesChronometer){
        mBuilder.setUsesChronometer(usesChronometer);
        return this;
    }

}
