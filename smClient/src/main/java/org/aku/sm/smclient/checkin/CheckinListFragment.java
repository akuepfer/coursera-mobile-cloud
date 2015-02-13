package org.aku.sm.smclient.checkin;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.aku.sm.smclient.C;
import org.aku.sm.smclient.OnFragmentInteractionListener;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.adapters.SymptomCheckingListAdapter;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.MedicationService;
import org.aku.sm.smclient.settings.Settings;

import java.util.ArrayList;

/**
 * Fragment an View inflaiton
 * http://stackoverflow.com/questions/11353075/how-can-i-maintain-fragment-state-when-added-to-the-back-stack
 */
public class CheckinListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private Activity activity;
    private Patient patient;
    private String firstName;
    private String lastName;
    private ArrayList<SymptomCheckin> symptomCheckins;


    /**
     * Displays a checkin list fragment. listing the checkings of patient with patientId
     */
    public static CheckinListFragment newInstance(Patient patient) {
        CheckinListFragment fragment = new CheckinListFragment();
        Bundle args = new Bundle();
        args.putString(C.FIRST_NAME, patient.getFirstName());
        args.putString(C.LAST_NAME, patient.getLastName());
        args.putParcelable(C.PATIENT, patient);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Display the list of checkins of the current patient using the content provider
     */

    public static Fragment newInstance(String firstName, String lastName,
                                       ArrayList<SymptomCheckin> symptomCheckins) {
        CheckinListFragment fragment = new CheckinListFragment();
        Bundle args = new Bundle();
        args.putString(C.FIRST_NAME, firstName);
        args.putString(C.LAST_NAME, lastName);
        args.putParcelableArrayList(C.CHECKINS, symptomCheckins);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CheckinListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            patient = getArguments().getParcelable(C.PATIENT);
            firstName = getArguments().getString(C.FIRST_NAME);
            lastName = getArguments().getString(C.LAST_NAME);
            symptomCheckins = getArguments().getParcelableArrayList(C.CHECKINS);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        View header = View.inflate(activity, R.layout.name_list_header, null);
        TextView name = (TextView) header.findViewById(R.id.name);
        name.setText(firstName + " " + lastName);
        listView.addHeaderView(header);

        if (patient == null) {
            SymptomCheckingListAdapter patientListAdapter = new SymptomCheckingListAdapter(activity, symptomCheckins);
            setListAdapter(patientListAdapter);
        } else {
            if (savedInstanceState == null) {
                new LoadCheckingListTask(this, activity).execute(Settings.getUserId(activity), patient.getId());
            } else {
                ArrayList<SymptomCheckin> symptomCheckins = savedInstanceState.getParcelableArrayList(C.CHECKINS);
                SymptomCheckingListAdapter patientListAdapter = new SymptomCheckingListAdapter(activity, symptomCheckins);
                setListAdapter(patientListAdapter);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if (getListAdapter() != null) {
            SymptomCheckingListAdapter symptomCheckingListAdapter = (SymptomCheckingListAdapter) getListAdapter();
            ArrayList<SymptomCheckin>  symptomCheckins = symptomCheckingListAdapter.getSymptomCheckins();
            savedState.putParcelableArrayList(C.CHECKINS, symptomCheckins);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (Settings.isRoleDoctor(activity)) {
            inflater.inflate(R.menu.menu_doctor_checkin_list, menu);
        } else {
            inflater.inflate(R.menu.menu_patient_checkin_list, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.medication_list:
                mListener.onFragmentInteraction(patient.getMedications());
                return true;
            case R.id.checkin_list_refresh:
                new LoadCheckingListTask(this, activity).execute(Settings.getUserId(activity), patient.getId());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            SymptomCheckingListAdapter symptomCheckingListAdapter = (SymptomCheckingListAdapter) getListAdapter();
            mListener.onFragmentInteraction(firstName, lastName,
                    // -1 to compensate header entry
                    symptomCheckingListAdapter.getSymptomCheckins().get(position - 1));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    /**
     * Async task to retrieve patient list from backend
     */
    private class LoadCheckingListTask extends AsyncTask<Long, Void, ArrayList<SymptomCheckin>> {

        CheckinListFragment checkinListFragment;
        Activity activity;
        ArrayList<SymptomCheckin> symptomCheckins = new ArrayList<SymptomCheckin>();
        Exception exception = null;

        LoadCheckingListTask(CheckinListFragment patientListFragment, Activity activity) {
            this.checkinListFragment = patientListFragment;
            this.activity = activity;
        }

        @Override
        protected ArrayList<SymptomCheckin> doInBackground(Long... params) {
            try {
                symptomCheckins = new MedicationService().findCheckinByDoctorIdAndPatientId(
                        params[0], params[1], AuthenticationInterceptor.create(activity));
            } catch (Exception ex) {
                this.exception = ex;
                symptomCheckins.add(new SymptomCheckin());
            }

            return symptomCheckins;
        }

        @Override
        protected void onPostExecute(ArrayList<SymptomCheckin> symptomCheckins) {
            super.onPostExecute(symptomCheckins);
            SymptomCheckingListAdapter patientListAdapter = new SymptomCheckingListAdapter(activity, symptomCheckins);
            checkinListFragment.setListAdapter(patientListAdapter);
            if (this.exception == null) {
                Toast.makeText(activity, "" + symptomCheckins.size() + " items retrieved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }


}
