package org.aku.sm.smserver.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert date
 */
public class DateFormatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static Date parseDate(String dateString) {
        if (dateString == null) return  null;

        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static String toString(Date date) {
        if (date == null) return  null;
        return dateFormat.format(date);
    }

    public static String formatDate(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }
}
