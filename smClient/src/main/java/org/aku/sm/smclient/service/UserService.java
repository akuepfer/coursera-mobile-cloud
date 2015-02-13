package org.aku.sm.smclient.service;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.User;
import org.aku.sm.smclient.settings.GcmRegistration;
import org.aku.sm.smclient.settings.Settings;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

public class UserService {

    public interface UserServiceInterface {

        @GET("/login/{username}")
        User getUserByUsername(@Path("username") String username);

        @GET("/login/{username}/{gcmregid}")
        User getUserByUsernameAndGcmRegId(@Path("username") String username, @Path("gcmregid") String gcmregid);

        @GET("/doctor/{doctorId}/patient/name/{lastName}")
        ArrayList<Patient> findPatientByLastName(@Path("doctorId") long doctorId,
                                                 @Path("lastName") String lastname);

        @GET("/doctor/{doctorId}/patient")
        ArrayList<Patient> findPatientsByDoctorId(@Path("doctorId") long doctorId);

        @GET("/patient/{id}")
        Patient findPatientById(@Path("id") long id);

    }


    public void getUserByUsername(Activity activity,  String username, String password, String cloudUrl) {
        new ConnectionTask(activity).execute(username, password, cloudUrl);
    }

    public User getUserByUsername(String username, AuthenticationInterceptor authenticationInterceptor) {
        UserServiceInterface userServiceInterface = buidUserServiceInterface(authenticationInterceptor);
        return userServiceInterface.getUserByUsername(username);
    }

    public ArrayList<Patient> getPatientsByDoctorId(long userId, AuthenticationInterceptor authenticationInterceptor) {
        UserServiceInterface userServiceInterface = buidUserServiceInterface(authenticationInterceptor);
        return userServiceInterface.findPatientsByDoctorId(userId);
    }

    public ArrayList<Patient> findPatientByDoctorIdAndPatientNamePattern(long doctorId, String patientNamePattern, AuthenticationInterceptor authenticationInterceptor) {
        UserServiceInterface userServiceInterface = buidUserServiceInterface(authenticationInterceptor);
        return userServiceInterface.findPatientByLastName(doctorId, patientNamePattern);
    }

    public Patient getPatientById(long id, AuthenticationInterceptor authenticationInterceptor) {
        UserServiceInterface userServiceInterface = buidUserServiceInterface(authenticationInterceptor);
        return userServiceInterface.findPatientById(id);
    }


    /**
     * Connects first to Google Cloud Messaging to get a registration id.
     * Connects secondly to the server to login and send the GCM registration id.
     */
    private class ConnectionTask extends AsyncTask<String, Void, User> {

        private Activity activity;
        private String error = null;

        public ConnectionTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected User doInBackground(String... params) {
            String gcmRegistrationId = null;
            try {
                gcmRegistrationId = new GcmRegistration(activity).register();
            } catch (IOException exception) {
                error = exception.getMessage();
            }

            AuthenticationInterceptor authenticationInterceptor =
                    AuthenticationInterceptor.create(activity, params[0], params[1], params[2]);
            UserServiceInterface userServiceInterface = buidUserServiceInterface(authenticationInterceptor);
            User user = null;
            try {
                user = userServiceInterface.getUserByUsernameAndGcmRegId(params[0], gcmRegistrationId);
            } catch (Exception exception) {
                error = exception.getMessage();
            }
            return user;
        }


        @Override
        protected void onPostExecute(User user) {
            if (error == null) {
                Settings.saveSharedPreference(activity, Settings.FIRST_NAME, user.getFirstName());
                Settings.saveSharedPreference(activity, Settings.LAST_NAME, user.getLastName());
                Settings.saveSharedPreference(activity, Settings.ROLE, user.getRole());
                Toast.makeText(activity, user.getFirstName() + " " + user.getLastName() + " has role: "
                        + user.getRole(), Toast.LENGTH_LONG).show();

                Settings.saveUserId(activity, user.getId());
                Settings.saveRole(activity, user.getRole());
                if ("ROLE_DOCTOR".equals(user.getRole())) {
                    activity.getActionBar().setIcon(R.drawable.ic_doctor);
                } else {
                    activity.getActionBar().setIcon(R.drawable.ic_patient);
                }

            } else {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        }
    }


    public static UserServiceInterface buidUserServiceInterface(AuthenticationInterceptor authenticationInterceptor) {
        UserServiceInterface userServiceInterface = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(authenticationInterceptor)
                .setClient(authenticationInterceptor)
                .setEndpoint(authenticationInterceptor.getCloudUrl())
                .build()
                .create(UserServiceInterface.class);
        return userServiceInterface;
    }


}
