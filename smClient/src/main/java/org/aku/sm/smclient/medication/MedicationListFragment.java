package org.aku.sm.smclient.medication;



import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.entities.Patient;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MedicationListFragment extends Fragment {

    private static final String PATIENT = "patient";
    private static final String MEDICATIONS = "medications";
    private Activity activity;
    private Patient patient;
    private ArrayList<Medication> medications;
    MedicationListAdapter medicationListAdapter;
    ListView listView;


    /**
     * Factory to create a MedicationListFragment
     *
     * @param patientId ID of the patient who's medication list should be displayed.
     * @return A new instance of fragment ViewCheckinFragment.
     */
    public static MedicationListFragment newInstance(Patient patient, ArrayList<Medication> medications) {
        MedicationListFragment fragment = new MedicationListFragment();
        fragment.medications = medications;
        Bundle args = new Bundle();
        args.putParcelable(PATIENT, patient);
        args.putParcelableArrayList(MEDICATIONS, medications);
        fragment.setArguments(args);
        return fragment;
    }


    public MedicationListFragment() {
        // Required empty public constructor
    }

    /**
     *  When a fragment is first attached to its activity.
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * Initial creation of the fragment. Is called after onAttach.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            patient = getArguments().getParcelable(PATIENT);
            medications = getArguments().getParcelableArrayList(MEDICATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_medication_list, container, false);
        listView = (ListView) frameLayout.findViewById(R.id.medication_list);
        //  java.lang.ClassCastException: android.widget.FrameLayout$LayoutParams cannot be cast to android.widget.AbsListView$LayoutParams
        //RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.name_list_header, container, false);
        View header = View.inflate(activity, R.layout.name_list_header, null);
        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText(patient.getFirstName() + " " + patient.getLastName());
        listView.addHeaderView(header);

        if (savedInstanceState != null) {
            ArrayList<Medication> medications = savedInstanceState.getParcelableArrayList("medications");
//            if (medications == null) {
//                new LoadMedicationsTask(this, activity).execute(patientId);
//            } else {
//                MedicationListAdapter medicationListAdapter = new MedicationListAdapter(getActivity(), medications);
//                listView.setAdapter(medicationListAdapter);
//            }
        }
        medicationListAdapter = new MedicationListAdapter(getActivity(), medications);
        listView.setAdapter(medicationListAdapter);
        // OnItemSelectedListener is used for Spinners, and OnItemClickListener is used for ListViews
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                medicationListAdapter.setSelectedIndex(position);
            }
        });
        return frameLayout;
    }


    /**
     * To save current dynamic state
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (medications != null) {
            outState.putParcelableArrayList("patients", medications);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_doctor_medication_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.medication_create:
                EditMedicationDialog createMedicationDialogDialog = EditMedicationDialog.newInstance(medicationListAdapter,
                        EditMedicationDialog.Action.NEW, patient.getId());
                createMedicationDialogDialog.show(getFragmentManager(), "CreateMedicationDialog");
                return true;

            case R.id.medication_update:
                if (!medicationListAdapter.hasSelection()) return true;
                EditMedicationDialog editedicationDialogDialog = EditMedicationDialog.newInstance(medicationListAdapter,
                        EditMedicationDialog.Action.EDIT, patient.getId());
                editedicationDialogDialog.show(getFragmentManager(), "EditMedicationDialog");
                return true;

            case R.id.medication_delete:
                if (!medicationListAdapter.hasSelection()) return true;
                DeleteMedicationDialog deleteMedicationDialogDialog = DeleteMedicationDialog.newInstance(medicationListAdapter,
                        patient.getId());
                deleteMedicationDialogDialog.show(getFragmentManager(), "DeleteMedicationDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * LoadMedicationsTask - async task to load the list of medications
     */
//    public class LoadMedicationsTask extends AsyncTask<Long, Void, ArrayList<Medication>> {
//
//        MedicationListFragment medicationListFragment;
//        Activity activity;
//        Exception exception;
//
//        public LoadMedicationsTask(MedicationListFragment medicationListFragment, Activity activity) {
//
//        }
//
//        /**
//         * Runs on the UI thread before.
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected ArrayList<Medication> doInBackground(Long... params) {
//
//            ArrayList<Medication> patients = new ArrayList<Medication>();
//            try {
//                patients = new MedicationService().findMedicationByPatientId(params[0],
//                        AuthenticationInterceptor.create(activity));
//            } catch (Exception ex) {
//                this.exception = ex;
//                patients.add(new Medication(ex.getMessage()));
//            }
//
//            return patients;
//        }
//
//        /**
//         * Runs on the UI thread after.
//         * @param medications
//         */
//        @Override
//        protected void onPostExecute(ArrayList<Medication> medications) {
//            super.onPostExecute(medications);
//            MedicationListAdapter medicationListAdapter = new MedicationListAdapter(getActivity(), medications);
//            listView.setAdapter(medicationListAdapter);
//        }
//    }





}
