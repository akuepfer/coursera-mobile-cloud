package org.aku.sm.smclient.checkin.contentprovider;


// A string that defines the SQL statement for creating a table

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class that actually creates and manages the provider's underlying data repository.
 */
class SmDatabaseHelper extends SQLiteOpenHelper {

    // Defines the database name
    private static final String DBNAME = "org.aku.sm.db";


    private static final String SQL_CREATE_CHECKIN = "CREATE TABLE " +
            "checkin " +                       // Table's name
            "(" +                           // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            " WORD TEXT" +
           " FREQUENCY INTEGER " +
            " LOCALE TEXT )";


    private static final String SQL_CREATE_INTAKE = "CREATE TABLE " +
            "checkin " +                       // Table's name
            "(" +                           // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            " CHECKIN_ID INTEGER" +
            " FREQUENCY INTEGER " +
            " LOCALE TEXT )";


    /*
     * Instantiates an open helper for the provider's SQLite data repository
     * Do not do database creation and upgrade here.
     */
    SmDatabaseHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    /*
     * Creates the data repository. This is called when the provider attempts to open the
     * repository and SQLite reports that it doesn't exist.
     */
    public void onCreate(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master " +
                        "WHERE type='table' AND name='" +
                        SymptomCheckinTable.TABLE_NAME + "' ", null);
        try {
            if (cursor.getCount()==0) {
                db.execSQL(SymptomCheckinTable.DATABASE_CREATE);
                db.execSQL(MedicationIntakeTable.DATABASE_CREATE);
            }
        }
        finally {
            cursor.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        return;
    }
}