package org.aku.sm.smclient.patient;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.OnFragmentInteractionListener;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.UserService;
import org.aku.sm.smclient.settings.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PatientListFragment extends ListFragment {

    private long patientId;
    List<Patient> patientsList = null;
    private OnFragmentInteractionListener mListener;
    private Activity activity;

    public static PatientListFragment newInstance(String patientId) {
        PatientListFragment fragment = new PatientListFragment();
        Bundle args = new Bundle();
        args.putString(C.PATIENT_ID, patientId);
        fragment.setArguments(args);
        Log.i(C.TAG, "PatientListFragment newInstance " + patientId);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PatientListFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Patient> patients = savedInstanceState.getParcelableArrayList("patients");
            if (patients != null) {
                PatientListAdapter patientListAdapter = new PatientListAdapter(getActivity(), patients);
                setListAdapter(patientListAdapter);
                return;
            }
        }

        String patientId = getArguments().getString(C.PATIENT_ID);
        new LoadPatientsTask(this, activity, null).execute(Settings.getUserId(activity),
                patientId == null ? null : Long.parseLong(patientId));
    }


    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        if (getListAdapter() != null) {
            ArrayList<Patient> patients = ((PatientListAdapter) getListAdapter()).getPatients();
            savedState.putParcelableArrayList("patients", patients);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_patient, menu);

        final SearchHolder holder = new SearchHolder();
        holder.fragment = this;
        holder.activity = activity;

        MenuItem searchItem =  menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new LoadPatientsTask(holder.fragment, holder.activity, query)
                                .execute(Settings.getUserId(activity));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.patientlist_refresh:
                new LoadPatientsTask(this, activity, null).execute(Settings.getUserId(activity));
                return true;
            case R.id.menu_search:
                item.expandActionView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            PatientListAdapter patientListAdapter = (PatientListAdapter) getListAdapter();
            mListener.onFragmentInteraction(patientListAdapter.getItem(position));
        }
    }


    /**
     * Async task to retrieve patient list from backend
     */
    public static class LoadPatientsTask extends AsyncTask<Long, Void, ArrayList<Patient>> {

        private final String searchText;
        PatientListFragment patientListFragment;
        Activity activity;
        Exception exception = null;

        LoadPatientsTask(PatientListFragment patientListFragment, Activity activity, String searchText) {
            this.patientListFragment = patientListFragment;
            this.activity = activity;
            this.searchText = searchText;
        }

        @Override
        protected ArrayList<Patient> doInBackground(Long... params) {
            ArrayList<Patient> patients = new ArrayList<Patient>();
            try {
                AuthenticationInterceptor authenticationInterceptor = AuthenticationInterceptor.create(activity);
                if (params.length == 1 || params[1] == null) {
                    if (searchText == null) {
                        // retrieve whole list of patients assigned to the doctor
                        patients = new UserService().getPatientsByDoctorId(params[0], authenticationInterceptor);
                    } else {
                        patients = new UserService().findPatientByDoctorIdAndPatientNamePattern(
                                params[0], searchText, authenticationInterceptor);
                    }
                } else {
                    // retireve  on patient
                    Patient patient = new UserService().getPatientById(params[1], authenticationInterceptor);
                    patients.add(patient);
                }
            } catch (Exception ex) {
                this.exception = ex;
                patients.add(new Patient(ex.getMessage(), "", ""));
            }

            return patients;
        }

        @Override
        protected void onPostExecute(ArrayList<Patient> patients) {
            super.onPostExecute(patients);
            PatientListAdapter patientListAdapter = new PatientListAdapter(activity, patients);
            patientListFragment.setListAdapter(patientListAdapter);
            if (this.exception == null) {
                Toast.makeText(activity, "" + patients.size() + " items retrieved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }



    public void deleteSearchInput(View view) {
        return;
    }


public class SearchHolder {
    EditText searchInput;
    Button discardSearch;
    public PatientListFragment fragment;
    public Activity activity;
}

}
