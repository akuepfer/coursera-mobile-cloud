package org.aku.sm.smclient;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.alarm.AlarmFragment;
import org.aku.sm.smclient.checkin.CheckinListFragment;
import org.aku.sm.smclient.checkin.NewCheckinFragment;
import org.aku.sm.smclient.checkin.ViewCheckinFragment;
import org.aku.sm.smclient.common.Global;
import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.medication.MedicationListFragment;
import org.aku.sm.smclient.patient.PatientListFragment;
import org.aku.sm.smclient.service.UserService;
import org.aku.sm.smclient.settings.Settings;
import org.aku.sm.smclient.settings.SettingsFragment;

import java.util.ArrayList;

/**
 * Fragment and save state
 * http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
 */
public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnFragmentInteractionListener {

    private static final String TAG = "org.aku.sm";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Refrence of the active fragment
     */
    private Fragment fragment;

    /**
     *
     */
    private  boolean isRoleDoctor;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Patient selectedPatient = null;
    private String patientId;


    /**
     *  {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to be called before setContentView, the latter trigger the call of onNavigationDrawerItemSelected
        if (getIntent() != null) {
            this.patientId = getIntent().getStringExtra(C.PATIENT_ID);
            String medicalRecordNumber = getIntent().getStringExtra(C.MEDICAL_RECORD_NUMBER);
            String firstName = getIntent().getStringExtra(C.FIRST_NAME);
            String lastName = getIntent().getStringExtra(C.LAST_NAME);
            Log.i(TAG, String.format("Intent args: %s %s %s %s\n", patientId, firstName, lastName, medicalRecordNumber));
        }
        setContentView(R.layout.activity_main);

        if (TextUtils.isEmpty(Settings.getUsername(this)) || TextUtils.isEmpty(Settings.getPassword(this)) ||
                TextUtils.isEmpty(Settings.getCloudUrl(this)))
        {
            Log.i(C.TAG, "only perform auto-login if all username, password an url settings are defined");
        } else {
            Global.setJSessionId(null);
            new UserService().getUserByUsername(this,
                    Settings.getUsername(this),
                    Settings.getPassword(this),
                    Settings.getCloudUrl(this));
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        isRoleDoctor = Settings.isRoleDoctor(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(C.IS_ROLE_DOCTOR, isRoleDoctor);
    }


    /**
     * Inially called before onCreate
     * @param position selected navigation drawer
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        String tag = "unknown";

        long userId = Settings.getUserId(this);
        String role = Settings.getRole(this);

        switch (position) {
            case 0:
                if (Settings.isRoleDoctor(this)) {
                    tag = getString(R.string.title_patient);
                    mTitle = getString(R.string.title_patient);
                    fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment == null) {
                        fragment = PatientListFragment.newInstance(patientId);
                    }

                } else {
                    mTitle = getString(R.string.title_checkin);
                    tag = getString(R.string.title_checkin);
                    fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment == null) {
                        fragment = NewCheckinFragment.newInstance(userId, role);
                    }
                }
                break;
            case 1:
                if (Settings.isRoleDoctor(this)) {
                    // TODO enable search
                    tag = getString(R.string.title_checkin);
                    mTitle = getString(R.string.title_checkin);
                    fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment == null) {
                        fragment = CheckinListFragment.newInstance(selectedPatient);
                    }
                } else {
                    tag = getString(R.string.title_alarm);
                    mTitle = getString(R.string.title_alarm);
                    fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment == null) {
                        fragment = AlarmFragment.newInstance();
                    }
                }
                break;

            case 2:
                tag = getString(R.string.titel_settings);
                mTitle = getString(R.string.titel_settings);
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = SettingsFragment.newInstance("s1", "s2");
                }
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit();
    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.title_patient);
//                break;
//            case 2:
//                mTitle = getString(R.string.title_checkin);
//                break;
//            case 3:
//                mTitle = getString(R.string.titel_settings);
//                break;
//        }
//    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    /**
     * Display the list of symptom checkins assigned to a patient
     * @param patient
     */
    @Override
    public void onFragmentInteraction(Patient patient) {
        mTitle = getString(R.string.title_patient);
        selectedPatient = patient;
        FragmentManager fragmentManager = getFragmentManager();
        fragment = CheckinListFragment.newInstance(patient);
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack("patientList").commit();
    }


    /**
     * Display list of symptom checkins
     * @param firstName
     * @param lastName
     * @param symptomCheckins
     */
    @Override
    public void onFragmentInteraction(String firstName, String lastName, ArrayList<SymptomCheckin> symptomCheckins) {
        mTitle = getString(R.string.title_checkin);
        // selectedPatient = patient;
        FragmentManager fragmentManager = getFragmentManager();
        fragment = CheckinListFragment.newInstance(firstName, lastName, symptomCheckins);
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack("checkinList").commit();
    }


    /**
     * Display the symptom checkin details
     * @param firstName
     * @param lastName
     * @param symptomCheckin
     */
    @Override
    public void onFragmentInteraction(String firstName, String lastName, SymptomCheckin symptomCheckin) {
        mTitle = getString(R.string.title_checkin);
        FragmentManager fragmentManager = getFragmentManager();
        fragment = ViewCheckinFragment.newInstance(firstName, lastName, symptomCheckin);
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack("checkingList").commit();
    }

    /**
     * Display list of medications
     * @param medications medications to display
     */
    @Override
    public void onFragmentInteraction(ArrayList<Medication> medications) {
        mTitle = getString(R.string.title_medication);
        FragmentManager fragmentManager = getFragmentManager();
        fragment = MedicationListFragment.newInstance(selectedPatient, medications);
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .addToBackStack("medicationList").commit();
    }

    @Override
    public void onFragmentInteraction(Medication medication) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.menu_patient, menu);
            //restoreActionBar();
            //MenuInflater inflater = getMenuInflater();
            //fragment.onCreateOptionsMenu(menu, inflater);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


}
