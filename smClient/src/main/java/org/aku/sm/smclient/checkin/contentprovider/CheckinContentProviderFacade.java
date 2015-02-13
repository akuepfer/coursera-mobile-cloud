package org.aku.sm.smclient.checkin.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.common.DateFormatter;
import org.aku.sm.smclient.entities.MedicationIntake;
import org.aku.sm.smclient.entities.SymptomCheckin;

import java.util.ArrayList;

/**
 * Provides similiar access to local store symptom checking as the service based access
 */
public class CheckinContentProviderFacade {


    private static final String CHECHIN_URL = "org.akue.sm.Provider.checkin";
    private static final String INTAKE_URL  = "org.akue.sm.Provider.checkin.intake";
    private static final String IMAGE_URL   = "org.akue.sm.Provider.checkin.image";


    public static final Uri CONTENT_URI = Uri.parse("content://" +  CheckinContentProvider.AUTHORITY);
    public static final Uri CONTENT_URI_CHECKIN = Uri.parse("content://" +  CheckinContentProvider.AUTHORITY + "/" + CheckinContentProvider.CHECKIN_PATH);
    public static final Uri CONTENT_URI_INTAKE = Uri.parse("content://" +  CheckinContentProvider.AUTHORITY + "/" + CheckinContentProvider.INTAKE_PATH);


    public ArrayList<SymptomCheckin> getSymptomCheckins(Context context) {
        ArrayList<SymptomCheckin> symptomCheckins = new ArrayList<SymptomCheckin>();

        String[] projection = {
                SymptomCheckinTable._ID,
                SymptomCheckinTable.CHECKIN_DATE,
                SymptomCheckinTable.MOUTH_PAIN,
                SymptomCheckinTable.PAIN_STOP_FROM_EATING,
                SymptomCheckinTable.MEDICATION_INTAKE,
                SymptomCheckinTable.SYMPTOM_PHOTO_PATH
        };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = SymptomCheckinTable.CHECKIN_DATE + " DESC";

        Cursor cursor = context.getContentResolver().query(CONTENT_URI_CHECKIN, projection, selection, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            SymptomCheckin symptomCheckin = new SymptomCheckin();
            int pos = 0;
            symptomCheckin.setId(cursor.getLong(pos++));
            symptomCheckin.setCheckinDate(DateFormatter.parseDate(cursor.getString(pos++)));
            symptomCheckin.setMouthPain(cursor.getString(pos++));
            symptomCheckin.setPainStopFromEating(cursor.getString(pos++));
            symptomCheckin.setMedicationIntake(cursor.getString(pos++));
            symptomCheckin.setSymptomPhotoPath(cursor.getString(pos++));
            symptomCheckin.setMedicationIntakes(getMedicationIntakes(context, symptomCheckin.getId()));
            symptomCheckins.add(symptomCheckin);
        }
        return symptomCheckins;
    }



    public SymptomCheckin getSymptomCheckin(Context context, Uri uri) {
        ArrayList<SymptomCheckin> symptomCheckins = new ArrayList<SymptomCheckin>();

         Uri CONTENT_URI_CHECKIN = Uri.parse("content://" +  CheckinContentProvider.AUTHORITY + "/" + CheckinContentProvider.CHECKIN_PATH);

        String[] projection = {
                SymptomCheckinTable._ID,
                SymptomCheckinTable.CHECKIN_DATE,
                SymptomCheckinTable.MOUTH_PAIN,
                SymptomCheckinTable.PAIN_STOP_FROM_EATING,
                SymptomCheckinTable.MEDICATION_INTAKE,
                SymptomCheckinTable.SYMPTOM_PHOTO_PATH
        };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder  =  null;

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor.moveToNext()) {
            SymptomCheckin symptomCheckin = new SymptomCheckin();
            int pos = 0;
            symptomCheckin.setId(cursor.getLong(pos++));
            symptomCheckin.setCheckinDate(DateFormatter.parseDate(cursor.getString(pos++)));
            symptomCheckin.setMouthPain(cursor.getString(pos++));
            symptomCheckin.setPainStopFromEating(cursor.getString(pos++));
            symptomCheckin.setMedicationIntake(cursor.getString(pos++));
            symptomCheckin.setSymptomPhotoPath(cursor.getString(pos++));
            symptomCheckin.setMedicationIntakes(getMedicationIntakes(context, symptomCheckin.getId()));
            symptomCheckins.add(symptomCheckin);
            return  symptomCheckin;
        } else {
            return null;
        }
    }



    public Uri saveSymptomCheckin(Context context, SymptomCheckin symptomCheckin) {
        Uri mNewUri;

        // Save SymptonCheckin
        ContentValues mNewValues = new ContentValues();
        //             SymptomCheckinTable._ID
        mNewValues.put(SymptomCheckinTable.CHECKIN_DATE, DateFormatter.toString(symptomCheckin.getCheckinDate()));
        mNewValues.put(SymptomCheckinTable.MOUTH_PAIN, symptomCheckin.getMouthPain());
        mNewValues.put(SymptomCheckinTable.PAIN_STOP_FROM_EATING, symptomCheckin.getPainStopFromEating());
        mNewValues.put(SymptomCheckinTable.MEDICATION_INTAKE, symptomCheckin.getMedicationIntake());
        mNewValues.put(SymptomCheckinTable.SYMPTOM_PHOTO_PATH, symptomCheckin.getSymptomPhotoPath());

        mNewUri = context.getContentResolver().insert(CONTENT_URI_CHECKIN, mNewValues);
        Log.i(C.TAG, "saveSymptomCheckin " + mNewUri.toString());

        // Save MedicationIntake
        String uriString =  mNewUri.toString();
        int pos = uriString.indexOf("/");
        long symptomCheckinId = Long.parseLong(uriString.substring(pos+1));
        for (MedicationIntake medicationIntake : symptomCheckin.getMedicationIntakes()) {
            saveMedicationIntake(context, symptomCheckinId, medicationIntake);
        }

        // return complete Uri to resource
        return Uri.parse("content://" +  CheckinContentProvider.AUTHORITY + "/" + mNewUri.toString());
    }



    public ArrayList<MedicationIntake> getMedicationIntakes(Context context, long symptomCheckinId) {
        ArrayList<MedicationIntake> medicationIntakes = new ArrayList<MedicationIntake>();

        String[] projection = {
                MedicationIntakeTable.NAME,
                MedicationIntakeTable.TAKEN,
                MedicationIntakeTable.INTAKE_DATE,
        };
        String selection = MedicationIntakeTable.CHECKIN_ID + " = ?";
        String[] selectionArgs = { Long.toString(symptomCheckinId) };
        String sortOrder = SymptomCheckinTable._ID + " DESC";

        Cursor cursor = context.getContentResolver().query(CONTENT_URI_INTAKE, projection, selection, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            int pos = 0;
            MedicationIntake medicationIntake = new MedicationIntake(
                    cursor.getString(pos++),
                    cursor.getString(pos++),
                    DateFormatter.parseDate(cursor.getString(pos++)));
            medicationIntakes.add(medicationIntake);
        }
        return medicationIntakes;
    }


    public Uri saveMedicationIntake(Context context, long checkinId, MedicationIntake medicationIntake) {
        Uri mNewUri;

        ContentValues mNewValues = new ContentValues();
        mNewValues.put(MedicationIntakeTable.CHECKIN_ID, checkinId);
        mNewValues.put(MedicationIntakeTable.NAME, medicationIntake.getName());
        mNewValues.put(MedicationIntakeTable.TAKEN, medicationIntake.getTaken());
        mNewValues.put(MedicationIntakeTable.INTAKE_DATE, DateFormatter.toString(medicationIntake.getIntakeDate()));

        mNewUri = context.getContentResolver().insert(CONTENT_URI_INTAKE, mNewValues);
        Log.i(C.TAG, "saveSymptomCheckin " + mNewUri.toString());

        // return complete Uri to resource
        return Uri.parse("content://" +  CheckinContentProvider.AUTHORITY + "/" + mNewUri.toString());
    }



}
