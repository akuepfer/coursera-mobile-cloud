package org.aku.sm.smclient.settings;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.aku.sm.smclient.C;

import java.io.IOException;

/**
 * Google Cloud Messaging Registration
 */
public class GcmRegistration {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = "gcm";

    public final String PROJECT_ID = "syma-gcm-0001";
    public final String PROJECT_NUMBER = "483774918053";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = PROJECT_NUMBER;

    private final String ANDROID_API_KEY  = "AIzaSyDyWYQWyFKuS98isxpaQZXtHh_79MZhw6I";


    private Activity activity;
    GoogleCloudMessaging gcm;
    String regid;


    public GcmRegistration(Activity activity) {
        this.activity = activity;
    }


    /**
     * Check device for Play Services APK. If check succeeds, proceed with
     * GCM registration.
     */
    public String register() throws IOException {
    if (checkPlayServices()) {
        gcm = GoogleCloudMessaging.getInstance(activity);
        regid = gcm.register(PROJECT_NUMBER);
        Log.i(TAG, "Registration: " + regid);
    } else {
        Log.i(TAG, "No valid Google Play Services APK found.");
    }

    return regid;
}

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(C.TAG, "Google play service is not avilable " + GooglePlayServicesUtil.getErrorString(resultCode));
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Looper.prepare();
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

}
