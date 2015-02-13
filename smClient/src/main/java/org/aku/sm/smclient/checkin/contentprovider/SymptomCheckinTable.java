package org.aku.sm.smclient.checkin.contentprovider;

/**
 * Symptom checkin table definition for content provider
 */
public class SymptomCheckinTable {


    public static final String TABLE_NAME = "checkin";

    public static final String _ID = "_id";
    public static final String CHECKIN_DATE = "checkin_date";
    public static final String MOUTH_PAIN = "mouth_pain";
    public static final String PAIN_STOP_FROM_EATING = "pain_stop_from_eating";
    public static final String MEDICATION_INTAKE = "medication_intake";
    public static final String SYMPTOM_PHOTO_PATH = "symptom_photo";

    public static final String DEFAULT_SORT_ORDER = CHECKIN_DATE + " DESC";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + _ID                       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHECKIN_DATE              + " TEXT, "
            + MOUTH_PAIN                + " TEXT, "
            + PAIN_STOP_FROM_EATING     + " TEXT, "
            + MEDICATION_INTAKE         + " TEXT, "
            + SYMPTOM_PHOTO_PATH        + " TEXT"
            + ");";



}
