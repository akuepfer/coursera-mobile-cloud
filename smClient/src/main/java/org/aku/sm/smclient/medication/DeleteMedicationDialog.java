package org.aku.sm.smclient.medication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Medication;

/**
 * Dialog to delete the selected medication.
 * Two choices either Cancel or Delete.
 */
public class DeleteMedicationDialog extends DialogFragment {

    private MedicationListAdapter medicationListAdapter;
    private long patientId;
    private int position;


    public DeleteMedicationDialog() {

    }


    public static DeleteMedicationDialog newInstance(MedicationListAdapter medicationListAdapter, long patientId) {
        DeleteMedicationDialog fragment = new DeleteMedicationDialog();
        fragment.medicationListAdapter = medicationListAdapter;
        fragment.patientId = patientId;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        position = medicationListAdapter.getSelectedIndex();
        Medication medication = medicationListAdapter.getSelectedMedication();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete medication " + medication.getName())
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        medicationListAdapter.deletePosition(patientId, position);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}