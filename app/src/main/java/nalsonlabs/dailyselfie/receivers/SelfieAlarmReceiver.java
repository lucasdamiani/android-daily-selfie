package nalsonlabs.dailyselfie.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import nalsonlabs.dailyselfie.R;
import nalsonlabs.dailyselfie.activities.DailySelfieActivity;
import nalsonlabs.dailyselfie.activities.SelfieGalleryActivity;

/**
 * Receiver for the new selfie alarm.
 *
 * @author Lucas Damiani
 */
public class SelfieAlarmReceiver extends BroadcastReceiver {

    public static final int SELFIE_REQUEST_CODE = 3245424;
    private static final int SELFIE_NOTIFICATION = 3453445;
    private static final long[] VIBRATE_PATTERN = {0, 200, 200, 300};
    private static final Uri SOUND_URI = Uri.parse("android.resource://nalsonlabs.dailyselfie/" + R.raw.alarm_rooster);

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent selfieIntent = PendingIntent.getActivity(context, SELFIE_REQUEST_CODE, new Intent(context, SelfieGalleryActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_camera_enhance_white_24dp)
                .setContentTitle(context.getResources().getString(R.string.notification_content_title))
                .setContentText(context.getResources().getString(R.string.notification_content_text))
                .setAutoCancel(true)
                .setVibrate(VIBRATE_PATTERN)
                .setSound(SOUND_URI)
                .setContentIntent(selfieIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SELFIE_NOTIFICATION, builder.build());
    }

}
