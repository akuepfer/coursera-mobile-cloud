package org.aku.sm.smclient.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.aku.sm.smclient.MainActivity;
import org.aku.sm.smclient.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Requires <receiver android:name="org.aku.sm.alarm.AlarmBroadcastReceiver"></receiver> in AndroidManifest.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "org.aku.sm";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Sending notification at:" + DateFormat.getDateTimeInstance().format(new Date()));

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker("Symptom Management - Health Status")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Symptom Management")
                .setContentText("Please report your health status.")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,
                notificationBuilder.build());
   }

}