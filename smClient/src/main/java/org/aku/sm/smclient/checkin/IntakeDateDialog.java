package org.aku.sm.smclient.checkin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.adapters.CheckinQuestionListAdapter;
import org.aku.sm.smclient.entities.CheckinQuestion;

import java.util.GregorianCalendar;

/**
 * Dialog to retrieve the date of checking medication question.
 * Two choices either Cancel or Save.
 */
public class IntakeDateDialog extends DialogFragment {

    public static enum Action { NEW, EDIT }
    CheckinQuestionListAdapter adapter;
    private CheckinQuestion checkinQuestion;


    public IntakeDateDialog() {

    }

    public static IntakeDateDialog newInstance(CheckinQuestionListAdapter adapter, CheckinQuestion checkinQuestion) {
        IntakeDateDialog fragment = new IntakeDateDialog();
        fragment.adapter = adapter;
        fragment.checkinQuestion = checkinQuestion;
        return fragment;
    }

    /**
     * Creates the Date/Time-Picker AlertDialog  and returns it
     * {@InheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.medication_intake_date, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        checkinQuestion.cancelChecked();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GregorianCalendar calendar = new GregorianCalendar(
                                datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());
                        checkinQuestion.setIntakeDate(calendar.getTime());
                        adapter.notifyDataSetChanged();
                    }
                });

        return builder.create();
    }
}