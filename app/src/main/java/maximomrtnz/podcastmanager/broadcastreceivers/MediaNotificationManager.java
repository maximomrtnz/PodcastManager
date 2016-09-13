package maximomrtnz.podcastmanager.broadcastreceivers;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;


import maximomrtnz.podcastmanager.R;
import maximomrtnz.podcastmanager.cache.ImageLoader;
import maximomrtnz.podcastmanager.services.PlayerService;
import maximomrtnz.podcastmanager.ui.fragments.PlayerFragment;
import maximomrtnz.podcastmanager.utils.Constants;
import maximomrtnz.podcastmanager.utils.EpisodePlaylist;
import maximomrtnz.podcastmanager.utils.NotificationHelper;

/**
 * Created by maximo on 04/09/16.
 */

/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. This is required so that the music service
 * doesn't get killed during playback.
 */
public class MediaNotificationManager extends BroadcastReceiver{

    private static final int REQUEST_CODE = 100;

    private static final String ACTION_PAUSE = "maximomrtnz.podcastmanager.pause";
    private static final String ACTION_PLAY = "maximomrtnz.podcastmanager.play";
    private static final String ACTION_NEXT = "maximomrtnz.podcastmanager.next";
    private static final String ACTION_PREV = "maximomrtnz.podcastmanager.prev";

    private final PlayerService mService;

    private final NotificationHelper mNotificationHelper;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationCompat.Action mNextAction;
    private final NotificationCompat.Action mPrevAction;

    private boolean mStarted;

    public MediaNotificationManager(PlayerService service) {

        mService = service;

        String pkg = mService.getPackageName();

        PendingIntent playIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent pauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent nextIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent prevIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE, new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mPlayAction = new NotificationCompat.Action(R.drawable.ic_play, mService.getString(R.string.label_play), playIntent);

        mPauseAction = new NotificationCompat.Action(R.drawable.ic_pause, mService.getString(R.string.label_pause), pauseIntent);

        mNextAction = new NotificationCompat.Action(R.drawable.ic_skip_next, mService.getString(R.string.label_next), nextIntent);

        mPrevAction = new NotificationCompat.Action(R.drawable.ic_skip_previous, mService.getString(R.string.label_previous), prevIntent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREV);

        mService.registerReceiver(this, filter);

        mNotificationHelper = new NotificationHelper(mService);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationHelper.cancelAll();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_PAUSE:
                mService.mCallback.onPause();
                break;
            case ACTION_PLAY:
                mService.mCallback.onPlay();
                break;
            case ACTION_NEXT:
                mService.mCallback.onSkipToNext();
                break;
            case ACTION_PREV:
                mService.mCallback.onSkipToPrevious();
                break;
        }
    }

    public void update(MediaMetadataCompat metadata, PlaybackStateCompat state, MediaSessionCompat.Token token) {

        if (state == null || state.getState() == PlaybackStateCompat.STATE_STOPPED || state.getState() == PlaybackStateCompat.STATE_NONE) {
            mService.stopForeground(true);
            try {
                mService.unregisterReceiver(this);
            } catch (IllegalArgumentException ex) {
                // ignore receiver not registered
            }
            mService.stopSelf();
            return;
        }
        if (metadata == null) {
            return;
        }
        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;

        MediaDescriptionCompat description = metadata.getDescription();

        mNotificationHelper
                .setStyle(new NotificationCompat.MediaStyle().setMediaSession(token).setShowActionsInCompactView(0, 1, 2))
                .setSmallIcon(R.drawable.ic_play)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(createContentIntent())
                .setTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setOngoing(isPlaying)
                .setWhen(isPlaying ? System.currentTimeMillis() - state.getPosition() : 0)
                .setShowWhen(isPlaying)
                .setUsesChronometer(isPlaying);

        // Load Image Async
        new ImageLoader(mService).loadAsync(description.getIconUri().toString(), new ImageLoader.ImageLoadeListener() {
            @Override
            public void onImageLoader(Bitmap bitmap) {
                mNotificationHelper.setLargeIcon(bitmap);
                mNotificationHelper.build();
                mNotificationHelper.show(Constants.NOTIFICATION_ID.MEDIA_NOTIFICATION_MANAGER);
            }
        });

        // If skip to next action is enabled
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            mNotificationHelper.addAction(mPrevAction);
        }

        mNotificationHelper.addAction(isPlaying ? mPauseAction : mPlayAction);

        // If skip to prev action is enabled
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            mNotificationHelper.addAction(mNextAction);
        }

        Notification notification = mNotificationHelper.build();

        if (isPlaying && !mStarted) {
            mService.startService(new Intent(mService.getApplicationContext(), PlayerService.class));
            mService.startForeground(Constants.NOTIFICATION_ID.MEDIA_NOTIFICATION_MANAGER, notification);
            mStarted = true;
        } else {
            if (!isPlaying) {
                mService.stopForeground(false);
                mStarted = false;
            }
            mNotificationHelper.show(Constants.NOTIFICATION_ID.MEDIA_NOTIFICATION_MANAGER);
        }
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mService, PlayerFragment.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
