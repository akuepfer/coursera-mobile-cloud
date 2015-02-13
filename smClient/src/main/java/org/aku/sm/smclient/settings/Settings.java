package org.aku.sm.smclient.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.aku.sm.smclient.MainActivity;

/**
 * Access to shared preferences
 */
public class Settings {

    public static final String PREFS_NAME = "org.aku.sm.prefs";
    public static final String USERNAME   = "USERNAME";
    public static final String PASSWORD   = "PASSWORD";
    public static final String USER_ID    = "USER_ID" ;
    public static final String ROLE       = "ROLE";
    public static final String CLOUD_URL  = "CLOUD_URL";

    public static final String ALARM_1  = "ALARM_2";
    public static final String ALARM_2  = "ALARM_3";
    public static final String ALARM_3  = "ALARM_4";
    public static final String ALARM_4  = "ALARM_5";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";



    public static void saveSharedPreference(Activity activity, String key, String value) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedPreference(Activity activity, String key) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }


    public static void saveRole(Activity activity, String role) {
        saveSharedPreference(activity, ROLE, role);
    }

    public static String getRole(Activity activity) {
        return getSharedPreference(activity, ROLE);
    }

    public static boolean isRoleDoctor(Activity activity) {
        String role = getSharedPreference(activity, ROLE);
        return (role.equals("ROLE_DOCTOR"));
    }


    public static String getUsername(Activity activity) {
        return getSharedPreference(activity, USERNAME);
    }

    public static String getPassword(Activity activity) {
        return getSharedPreference(activity, PASSWORD);
    }

    public static String getCloudUrl(Activity activity) {
        return getSharedPreference(activity, CLOUD_URL);
    }


    public static String getFirstName(Activity activity) {
        return getSharedPreference(activity, FIRST_NAME);
    }

    public static String getLastName(Activity activity) {
        return getSharedPreference(activity, LAST_NAME);
    }



    public static void saveUserId(Activity activity, long id) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(USER_ID, id);
        editor.apply();
    }

    public static long getUserId(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getLong(USER_ID, 0);
    }


    public static void saveAlarm(Activity activity, int id, String value) {
        saveSharedPreference(activity, "ALARM_" + id, value);
    }

    public static String getAlarm(Activity activity, int id) {
        return getSharedPreference(activity, "ALARM_" + id);
    }


    /**
     * GCM registration
     */
    private static final String TAG = "gcm";

    private static final String PROPERTY_REG_ID = "reg-id";
    private static final String PROPERTY_APP_VERSION = "app-version";


    /**
     * From GCM client
     * This sample app persists the registration ID in shared preferences, but
     * how you store the regID in your app is up to you.
     *
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = Settings.getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }





}
