package org.aku.sm.smclient.checkin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.SymptomCheckin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * List adapter for the list of Checkins (SymptomCheckin) of a patient
 * Used by CheckinListFragment
 */
public class SymptomCheckingListAdapter extends ArrayAdapter<SymptomCheckin> {

    ArrayList<SymptomCheckin> symptomCheckins;
    private final SimpleDateFormat dateFormat;

    public SymptomCheckingListAdapter(Context context, ArrayList<SymptomCheckin> symptomCheckins) {
        super(context, R.layout.symtomp_checking_list_element, symptomCheckins);
        this.symptomCheckins = symptomCheckins;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public ArrayList<SymptomCheckin> getSymptomCheckins() {
        return symptomCheckins;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO use recyclet views
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.patient_list_entry, parent, false);
        }

        LayoutInflater inflater = (LayoutInflater) convertView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.symtomp_checking_list_element, parent, false);

        ImageView clCheckinImage = (ImageView) rowView.findViewById(R.id.clCheckinImage);
        TextView clMouthPain = (TextView) rowView.findViewById(R.id.clCheckinPain);
        TextView clPainStopFromEating = (TextView) rowView.findViewById(R.id.clPainStopFromEating);
        TextView clMedicationIntake = (TextView) rowView.findViewById(R.id.clMedicationIntake);
        TextView clCheckinDate = (TextView) rowView.findViewById(R.id.clCheckinDate);

        SymptomCheckin symptomCheckin = symptomCheckins.get(position);
        if (symptomCheckin.getMouthPain() == null || symptomCheckin.getMouthPain().equals("severe")
                || symptomCheckin.getMouthPain().equals(SymptomCheckin.SymptomCheckinAnswer.SEVERE.name())) {
            clCheckinImage.setImageResource(R.drawable.pain_severe);
        }  else if (symptomCheckin.getMouthPain().equals("moderate")
                || symptomCheckin.getMouthPain().equals(SymptomCheckin.SymptomCheckinAnswer.MODERATE.name())) {
            clCheckinImage.setImageResource(R.drawable.pain_some);
        } else {            // well­controlled
            clCheckinImage.setImageResource(R.drawable.pain_none);
        }
        clMouthPain.setText("Pain " + SymptomCheckin.SymptomCheckinAnswer.parseValue(symptomCheckin.getMouthPain()).getDescription());
        if (symptomCheckin.getCheckinDate() == null) {
            clCheckinDate.setText(dateFormat.format(new Date()));
        } else {
            clCheckinDate.setText(dateFormat.format(symptomCheckin.getCheckinDate()));

        }
        clPainStopFromEating.setText("Eating pain " + SymptomCheckin.SymptomCheckinAnswer.parseValue(symptomCheckin.getPainStopFromEating()).getDescription());
        clMedicationIntake.setText(" Med. intake " + SymptomCheckin.SymptomCheckinAnswer.parseValue(symptomCheckin.getMedicationIntake()).getDescription());
        return rowView;
    }
}
