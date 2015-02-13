package org.aku.sm.smclient.medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Medication;

/**
 * Dialog to edit the selected medication or to create a new one
 * Two choices either Cancel or Save.
 */
public class EditMedicationDialog extends DialogFragment {

    public static enum Action { NEW, EDIT }

    private MedicationListAdapter medicationListAdapter;
    private Action action;
    private long patientId;
    private int position;


    public EditMedicationDialog() {

    }

    public static EditMedicationDialog newInstance(MedicationListAdapter medicationListAdapter, Action action, long patientId) {
        EditMedicationDialog fragment = new EditMedicationDialog();
        fragment.medicationListAdapter = medicationListAdapter;
        fragment.action = action;
        fragment.patientId = patientId;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.medication_element_input, null);
        final EditText medicationName = (EditText) view.findViewById(R.id.medication_name);
        final EditText medicationDescription = (EditText) view.findViewById(R.id.medication_description);

        if (action.equals(Action.EDIT)) {
            position = medicationListAdapter.getSelectedIndex();
            Medication medication = medicationListAdapter.getSelectedMedication();
            medicationName.setText(medication.getName());
            medicationDescription.setText(medication.getDescription());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = medicationName.getText().toString();
                        String description = medicationDescription.getText().toString();
                        if (action.equals(Action.NEW)) {
                            medicationListAdapter.addMedication(patientId, name, description);
                        } else {
                            medicationListAdapter.updateMedication(patientId, position, name, description);
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}