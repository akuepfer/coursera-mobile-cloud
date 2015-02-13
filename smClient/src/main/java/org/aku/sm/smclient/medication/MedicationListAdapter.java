package org.aku.sm.smclient.medication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.okhttp.internal.spdy.FrameReader;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.MedicationService;

import java.util.ArrayList;

/**
 * Adapter for the medication list
 */
public class MedicationListAdapter extends ArrayAdapter<Medication> {

    private final ArrayList<Medication> medications;
    private final LayoutInflater inflater;
    private int selectedIndex;


    public static final int CREATE_MED = 1;
    public static final int UPDATE_MED = 2;
    public static final int DELETE_MED = 3;
    public static final int TERMINATE_CMD  = 4;

    private enum Command {
        CREATE(CREATE_MED),
        UPDATE(UPDATE_MED),
        DELETE(DELETE_MED),
        TERMINATE(TERMINATE_CMD);

        Command(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

        private int val;
    }

    MedicationCommandLooperThread looperThread;
    public static Handler handler;


    private static class ViewHolder {
        View view;
        TextView name;
        TextView description;
    }


    public MedicationListAdapter(Activity activity, ArrayList<Medication> medications) {
        super(activity, R.layout.medication_list_element, medications);
        this.medications = medications;
        this.inflater = LayoutInflater.from(getContext());
        selectedIndex = NO_SELECTION;

        AuthenticationInterceptor authenticationInterceptor = AuthenticationInterceptor.create(activity);

        looperThread = new MedicationCommandLooperThread(authenticationInterceptor);
        looperThread.start();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.medication_list_element, parent, false);
            holder = new ViewHolder();
            holder.view = convertView;
            holder.name = (TextView) convertView.findViewById(R.id.medication_name);
            holder.description = (TextView) convertView.findViewById(R.id.medication_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(medications.get(position).getName());
        holder.description.setText(medications.get(position).getDescription());

        if(selectedIndex != NO_SELECTION && position == selectedIndex) {
            holder.view.setBackgroundColor(0xff27C3D8);
        } else {
            holder.view.setBackgroundColor(0xfff3f3f3);
        }

        return convertView;
    }


    public ArrayList<Medication> getMedications() {
        return this.medications;
    }


    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex =  (selectedIndex-1 == this.selectedIndex ? NO_SELECTION :  selectedIndex-1);
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public boolean hasSelection() {
        return selectedIndex != NO_SELECTION;
    }

    public Medication getSelectedMedication() {
        return medications.get(selectedIndex);
    }

    /**
     * Add medication to local list and send command server to add medication
     * @param patientId
     * @param name name of medication
     * @param description description of medication
     */

    public void addMedication(long patientId, String name , String description) {
        Medication medication = new Medication(name, description);
        medications.add(medication);
        execute(Command.CREATE, patientId, medication);
        notifyDataSetChanged();
    }

    /**
     * Update a medication of the local list an send command to server to update medication.
     * @param index position in adapter list
     * @param name name of medication
     * @param description description of medication
     */
    public void updateMedication(long patientId, int index, String name, String description) {
        Medication medication = medications.get(index);
        medication.setName(name);
        medication.setDescription(description);
        execute(Command.UPDATE, patientId, medication);
        notifyDataSetChanged();
    }

    /**
     * Delete a medication from the local list an send command to server to delete medication.
     * @param position position in adapter list
     */
    public void deletePosition(long patientId, int position) {
        Medication medication = medications.get(position);
        medications.remove(position);
        execute(Command.DELETE, patientId, medication);
        notifyDataSetChanged();
    }


    public void execute(Command command, long patientId, Medication medication) {
        Message message = Message.obtain();
        message.arg1 = command.getVal();
        Bundle bundle = new Bundle();
        bundle.putLong("patientId", patientId);
        bundle.putParcelable("medication", medication);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    /**
     * Execute medication update or delete command where no return value is requested;
     */
    public static class MedicationCommandLooperThread extends Thread {

        private AuthenticationInterceptor authenticationInterceptor;

        MedicationCommandLooperThread(AuthenticationInterceptor authenticationInterceptor) {
            this.authenticationInterceptor = authenticationInterceptor;
        }

        public void run() {
            Looper.prepare();
            handler = new MedicationCommandHandler(authenticationInterceptor);
            Looper.loop();
        }

    }

    public static class MedicationCommandHandler extends Handler {

        AuthenticationInterceptor authenticationInterceptor;

        public MedicationCommandHandler(AuthenticationInterceptor authenticationInterceptor) {
            this.authenticationInterceptor = authenticationInterceptor;
        }

        public void handleMessage(Message msg) {

            final MedicationService medicationService = new MedicationService();
            Bundle bundle = msg.getData();
            long patientId = bundle.getLong("patientId");
            Medication medication = bundle.getParcelable("medication");
            switch (msg.arg1) {
                case CREATE_MED:
                    medicationService.createMedication(patientId, medication, authenticationInterceptor);
                    break;
                case UPDATE_MED:
                    medicationService.updateMedication(patientId, medication, authenticationInterceptor);
                    break;
                case DELETE_MED:
                    medicationService.deleteMedication(patientId, medication, authenticationInterceptor);
                    break;
                case TERMINATE_CMD: // terminate handler thread
                    break;
            }
        }
    }



}
