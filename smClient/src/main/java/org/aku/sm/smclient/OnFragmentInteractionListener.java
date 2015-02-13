package org.aku.sm.smclient;

import android.net.Uri;

import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.SymptomCheckin;

import java.util.ArrayList;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(Uri uri);

    public void onFragmentInteraction(String id);

    void onFragmentInteraction(Patient patient);

    void onFragmentInteraction(String firstName, String lastName, SymptomCheckin symptomCheckin);

    public void onFragmentInteraction(ArrayList<Medication> medication);

    void onFragmentInteraction(Medication medication);

    void onFragmentInteraction(String firstName, String lastName, ArrayList<SymptomCheckin> symptomCheckins);
}