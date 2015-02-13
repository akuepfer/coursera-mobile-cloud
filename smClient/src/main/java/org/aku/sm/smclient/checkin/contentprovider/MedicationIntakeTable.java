package org.aku.sm.smclient.checkin.contentprovider;

import java.util.Date;

/**
 * Medication intake table definition for content provider
 */
public class MedicationIntakeTable {

    public static final String TABLE_NAME   = "intake";

    public static final String _ID          = "_id";
    public static final String CHECKIN_ID   = "checkin_id";
    public static final String NAME         = "name";
    public static final String TAKEN        = "taken";
    public static final String INTAKE_DATE  = "intake_date";

    public static final String DEFAULT_SORT_ORDER = _ID + " ASC";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + _ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHECKIN_ID    + " INTEGER REFERENCES " + SymptomCheckinTable.TABLE_NAME + "(" + SymptomCheckinTable._ID + ") ON DELETE CASCADE,"
            + NAME          + " TEXT, "
            + TAKEN         + " TEXT,"
            + INTAKE_DATE   + " TEXT"
            + ");";
}
