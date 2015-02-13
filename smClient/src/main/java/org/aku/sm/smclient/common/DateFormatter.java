package org.aku.sm.smclient.common;

import android.util.Log;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.service.DateTypeAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Convert date
 */
public class DateFormatter {


    private static final DateFormat iso8601Format = DateTypeAdapter.buildIso8601Format();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());


    public static Date parseDate(String dateString) {
        if (dateString == null) return  null;

        try {
            return iso8601Format.parse(dateString);
        } catch (ParseException e) {
            Log.e(C.TAG, "invalid date format " + dateString, e);
        }
        return null;
    }


    public static String toString(Date date) {
        if (date == null) return  null;
        return iso8601Format.format(date);
    }

    public static String formatDate(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }
}
