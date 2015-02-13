package org.aku.sm.smclient.checkin.tasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.NewCheckinFragment;
import org.aku.sm.smclient.checkin.ViewCheckinFragment;
import org.aku.sm.smclient.checkin.contentprovider.CheckinContentProviderFacade;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.MedicationService;
import org.aku.sm.smclient.settings.Settings;

import java.io.File;

/**
 * Task to post the result of a symptom checkin to the server
 */
public class CreateSymptomCheckinTask extends AsyncTask<SymptomCheckin, Void, SymptomCheckin> {

    NewCheckinFragment newCheckinFragment;
    Activity activity;


    public CreateSymptomCheckinTask(NewCheckinFragment newCheckinFragment, Activity activity) {
        this.newCheckinFragment = newCheckinFragment;
        this.activity = activity;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected SymptomCheckin doInBackground(SymptomCheckin... params) {
        MedicationService medicationService = new MedicationService();
        AuthenticationInterceptor authenticationInterceptor = AuthenticationInterceptor.create(activity);
//        SymptomCheckin symptomCheckin = medicationService.createSymptomCheckin(params[0], authenticationInterceptor);
//        if (params[0].getSymptomPhotoPath() != null) {
//            medicationService.createSymptomCheckinImage(params[0], authenticationInterceptor);
//        }
//        params[0].setSymptomCheckinStatus(SymptomCheckin.SymptomCheckinStatus.SAVED);
        SymptomCheckin symptomCheckin = null;
        try {
            // save to content provider
            Uri uri = new CheckinContentProviderFacade().saveSymptomCheckin(activity, params[0]);

            // TODO remove
            symptomCheckin = new CheckinContentProviderFacade().getSymptomCheckin(activity, uri);

            // save to cloud
            symptomCheckin = medicationService.createSymptomCheckin(params[0], authenticationInterceptor);
            if (new File(params[0].getSymptomPhotoPath()).exists()) {
                medicationService.createMultipartSymptomCheckin(symptomCheckin.getId(), params[0].getSymptomPhotoPath(), authenticationInterceptor);
                symptomCheckin.setSymptomCheckinStatus(SymptomCheckin.SymptomCheckinStatus.SAVED);
            } else {
                Log.w(C.TAG, "File not found " + params[0].getSymptomPhotoPath());
            }
        } catch (Exception exception) {
            Log.e(C.TAG, "CreateSymptomCheckinTask " + exception.getMessage(), exception);
            //symptomCheckin.setSymptomCheckinStatus(SymptomCheckin.SymptomCheckinStatus.ERROR_DURING_SAVE);
        }
        return symptomCheckin;
    }


    @Override
    protected void onPostExecute(SymptomCheckin symptomCheckin) {

        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragment = ViewCheckinFragment.newInstance(
                Settings.getFirstName(activity), Settings.getLastName(activity), symptomCheckin);
        // don't add fragment to backstack
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        super.onPostExecute(symptomCheckin);
    }
}
