package org.aku.sm.smclient.checkin.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper for the symptom checkin content provider
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "symptom.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create a new database
     * @param database database reference
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SymptomCheckinTable.DATABASE_CREATE);
    }

    /**
     * Upgrade database - doesn't do anything yet.
     */

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        return;
    }
}


