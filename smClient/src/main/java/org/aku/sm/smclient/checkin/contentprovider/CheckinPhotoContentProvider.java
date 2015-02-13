package org.aku.sm.smclient.checkin.contentprovider;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Defintions for the checkin photo conten provider which is using the Android
 * android.support.v4.content.FileProvider
 */
public class CheckinPhotoContentProvider {
    public static final String CHECKIN_NAME = "checkin";
    public static final String CHECKIN_EXT = ".jpg";

    public static final String AUTHORITY="org.akue.sm.Provider.checkin.image";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, AUTHORITY, file);
    }

    public static Uri getUriForFileName(Context context, String fileName) {
        return getUriForFile(context, new File(context.getFilesDir(),fileName));
    }

    /**
     * Returns an URI of the form content:// org.akue.sm.symptomcheckin.image/checkin_images/checkin_yyyy_MM_dd_HH_mm_ss.jpg
     * See https://developer.android.com/reference/android/support/v4/content/FileProvider.html#GetUri
     *
     * @param context the android context
     * @return URI to a new file
     */
    public static Uri getUriForCheckinImange(Context context) {
        File imagePath = new File(context.getFilesDir(), "images");
        String fileName = CHECKIN_NAME + "_" + formatter.format(new Date()) + CHECKIN_EXT;
        File newFile = new File(imagePath, fileName);
        Uri contentUri = FileProvider.getUriForFile(context, AUTHORITY, newFile);
        return contentUri;
    }

}
