package org.aku.sm.smclient.checkin.tasks;

/**
 * Created by armin on 02.11.14.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import org.aku.sm.smclient.checkin.adapters.CheckinQuestionListAdapter;
import org.aku.sm.smclient.entities.CheckinQuestion;
import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.MedicationService;

import java.util.ArrayList;

/**
 * Async task to retrieve patient list from backend
 */
public class LoadMedicationTask extends AsyncTask<Long, Void, ArrayList<Medication>> {

    ListView listView;
    Activity activity;
    ArrayList<Medication> medications = new ArrayList<Medication>();
    SymptomCheckin symptomCheckin;
    Exception exception = null;


    public LoadMedicationTask(ListView listView, Activity activity, SymptomCheckin symptomCheckin) {
        this.listView = listView;
        this.activity = activity;
        this.symptomCheckin = symptomCheckin;
    }

    @Override
    protected ArrayList<Medication> doInBackground(Long... params) {
        try {
            medications = new MedicationService().findMedicationByPatientId(params[0],
                    AuthenticationInterceptor.create(activity));
        } catch (Exception ex) {
            this.exception = ex;
            medications.add(new Medication(ex));
        }

        return medications;
    }


    @Override
    protected void onPostExecute(ArrayList<Medication> medications) {
        super.onPostExecute(medications);

        CheckinQuestionListAdapter checkinQuestionListAdapter = new CheckinQuestionListAdapter(activity);
        ArrayList<CheckinQuestion> checkinQuestions = buildStandardQuestions();
        for (CheckinQuestion checkinQuestion : checkinQuestions) {
            checkinQuestionListAdapter.add(checkinQuestion);
        }
        if (this.exception == null) {
            Toast.makeText(activity, "" + medications.size() + " items retrieved", Toast.LENGTH_SHORT).show();
            for (Medication medication : medications) {
                checkinQuestionListAdapter.add(new CheckinQuestion(medication));
            }
        } else {
            Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
        listView.setAdapter(checkinQuestionListAdapter);

    }


    /**
     * Add the three standard questions to the list of asked medication questions
     */
    private static ArrayList<CheckinQuestion> buildStandardQuestions() {
        ArrayList<CheckinQuestion> checkinQuestions = new ArrayList<CheckinQuestion>();
        checkinQuestions.add(
                new CheckinQuestion(SymptomCheckin.SymptomCheckinAnswer.STANDARD_QUESTION_1.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.WELL_CONTROLLED.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.MODERATE.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.SEVERE.getDescription()));
        checkinQuestions.add(
                new CheckinQuestion(SymptomCheckin.SymptomCheckinAnswer.STANDARD_QUESTION_2.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.NO.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.SOME.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.getDescription()));
        checkinQuestions.add(
                new CheckinQuestion(SymptomCheckin.SymptomCheckinAnswer.STANDARD_QUESTION_3.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.YES.getDescription(),
                        SymptomCheckin.SymptomCheckinAnswer.NO.getDescription(), null));
        return checkinQuestions;
    }
}