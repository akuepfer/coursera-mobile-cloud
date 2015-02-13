package org.aku.sm.smclient.checkin.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content provider for SymptomCheckin data
 *
 * So, a content Uri of content://constants/5 represents the constants instance with an identifier of 5.
 *
 * content://checkin/5
 * content://checkin/5/intake/
 * content://checkin/5/intake/7
 *
 */
public class CheckinContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String AUTHORITY = "org.akue.sm.Provider.checkin";
    public static final String CHECKIN_PATH = "checkin";
    public static final String INTAKE_PATH = "intake";
    private static final int CHECKIN = 11;
    private static final int CHECKIN_ID = 12;
    private static final int INTAKE = 13;
    private static final int INTAKE_ID = 14;

    static {
        uriMatcher.addURI(AUTHORITY, CHECKIN_PATH, CHECKIN);
        uriMatcher.addURI(AUTHORITY, CHECKIN_PATH + "/#", CHECKIN_ID);
        uriMatcher.addURI(AUTHORITY, INTAKE_PATH, INTAKE);
        uriMatcher.addURI(AUTHORITY, INTAKE_PATH + "/#", INTAKE_ID);
    }

    private SmDatabaseHelper dbHelper;

    // Holds the database object
   // private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new SmDatabaseHelper(getContext());
        return((dbHelper == null) ? false : true);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        int uriType = uriMatcher.match(uri);
        switch (uriType) {

            case CHECKIN:
                queryBuilder.setTables(SymptomCheckinTable.TABLE_NAME);
                break;
            case CHECKIN_ID:
                // adding the ID to the original query
                queryBuilder.setTables(SymptomCheckinTable.TABLE_NAME);
                queryBuilder.appendWhere(SymptomCheckinTable._ID + "=" + uri.getLastPathSegment());
                break;
            case INTAKE:
                queryBuilder.setTables(MedicationIntakeTable.TABLE_NAME);
                break;
            case INTAKE_ID:
                queryBuilder.setTables(MedicationIntakeTable.TABLE_NAME);
                queryBuilder.appendWhere(MedicationIntakeTable._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        // notify listeners
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        long id = 0;
        String path;
        switch (uriType) {
            case CHECKIN:
                id = sqlDB.insertOrThrow(SymptomCheckinTable.TABLE_NAME, null, values);
                path = CHECKIN_PATH;
                break;
            case INTAKE:
                id = sqlDB.insert(MedicationIntakeTable.TABLE_NAME, null, values);
                path = INTAKE_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CHECKIN:
                rowsDeleted = sqlDB.delete(SymptomCheckinTable.TABLE_NAME, selection, selectionArgs);
                break;
            case CHECKIN_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(SymptomCheckinTable.TABLE_NAME, SymptomCheckinTable._ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(SymptomCheckinTable.TABLE_NAME, SymptomCheckinTable._ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CHECKIN:
                rowsUpdated = sqlDB.update(SymptomCheckinTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHECKIN_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(SymptomCheckinTable.TABLE_NAME, values, SymptomCheckinTable._ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(SymptomCheckinTable.TABLE_NAME, values, SymptomCheckinTable._ID + "=" + id
                                    + " and " + selection, selectionArgs);
                }
            case INTAKE:
                rowsUpdated = sqlDB.update(SymptomCheckinTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INTAKE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MedicationIntakeTable.TABLE_NAME, values, SymptomCheckinTable._ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(MedicationIntakeTable.TABLE_NAME, values, SymptomCheckinTable._ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
