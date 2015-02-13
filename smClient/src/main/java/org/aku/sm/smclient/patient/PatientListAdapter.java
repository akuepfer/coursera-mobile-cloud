package org.aku.sm.smclient.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.SymptomCheckin;

import java.util.ArrayList;

/**
 *
 */
public class PatientListAdapter extends ArrayAdapter<Patient> {

    public PatientListAdapter(Context context, ArrayList<Patient> patients) {
        super(context, R.layout.patient_list_entry, patients);
    }


    public ArrayList<Patient> getPatients() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for (int i = 0; i<getCount(); i++) {
            patients.add(getItem(i));
        }
        return  patients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO use recyclet views
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.patient_list_entry, parent, false);
        }

        LayoutInflater inflater = (LayoutInflater) convertView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.patient_list_entry, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.patientListImage);
        TextView patientListName = (TextView) rowView.findViewById(R.id.patientListName);
        TextView patientListMedicalRecordNumber = (TextView) rowView.findViewById(R.id.patientListmedicalRecordNumber);

        Patient patient = getItem(position);
        if (patient.getMouthPain() != null && patient.getMouthPain().equals(SymptomCheckin.SymptomCheckinAnswer.SEVERE.name())) {
            imageView.setImageResource(R.drawable.pain_severe);
        } else if (patient.getMouthPain() != null && patient.getMouthPain().equals(SymptomCheckin.SymptomCheckinAnswer.MODERATE.name())) {
            imageView.setImageResource(R.drawable.pain_some);
        } else {
            imageView.setImageResource(R.drawable.pain_none);
        }
        patientListName.setText(patient.getFirstName() + " " + patient.getLastName());
        patientListMedicalRecordNumber.setText(patient.getMedicalRecordNumber());

        return rowView;
    }
}
