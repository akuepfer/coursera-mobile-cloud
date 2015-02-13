package org.aku.sm.smclient.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.MainActivity;
import org.aku.sm.smclient.R;

/**
 * Service to handle the GCM message.
 *
 * Example from https://developer.android.com/google/gcm/client.html
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "gcm";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), "", "", "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(), "", "", "");
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                String patientId = extras.getString("patientId");
                String medicalRecordNumber = extras.getString("medicalRecordNumber");
                String firstName = extras.getString("firstName");
                String lastName = extras.getString("lastName");

                Log.i(TAG, "Received: " + extras.toString());
                sendNotification(patientId, medicalRecordNumber, firstName, lastName);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String patientId, String medicalRecordNumber, String firstName, String lastName) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(C.PATIENT_ID, patientId);
        notificationIntent.putExtra(C.MEDICAL_RECORD_NUMBER, medicalRecordNumber);
        notificationIntent.putExtra(C.FIRST_NAME, firstName);
        notificationIntent.putExtra(C.LAST_NAME, lastName);
        Log.i(TAG, String.format("Service args: %s %s %s %s\n", patientId, firstName, lastName, medicalRecordNumber));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setTicker(getString(R.string.health_status_ticker))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle(firstName.substring(0, 1) + ". " + lastName + ", " + medicalRecordNumber)
                    .setContentText(getString(R.string.health_status))
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
