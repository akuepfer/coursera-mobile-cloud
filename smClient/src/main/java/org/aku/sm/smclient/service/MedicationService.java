package org.aku.sm.smclient.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.aku.sm.smclient.entities.Medication;
import org.aku.sm.smclient.entities.SymptomCheckin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * Created by armin on 21.10.14.
 */
public class MedicationService {

    interface MedicationServiceInterface {

        /**
         * Medication list
         */

        @GET("/patient/{patientId}/medication")
        public ArrayList<Medication> findMedicationByPatientId(@Path("patientId") long patientId);

        @POST("/patient/{patientId}/medication")
        public Medication createMedication(@Path("patientId") long patientId,
                                           @Body Medication medication);

        @PUT("/patient/{patientId}/medication")
        public Medication updateMedication(@Path("patientId") long patientId,
                                           @Body Medication medication);

        @DELETE("/patient/{patientId}/medication/{id}")
        public Medication deleteMedication(@Path("patientId") long patientId,
                                           @Path("id") long id);


        /**
         * Symptom Checkin
         */

        @GET("/doctor/{doctorId}/patient/{patientId}/checkin")
        public ArrayList<SymptomCheckin> findCheckinByDoctorIdAndPatientId(@Path("doctorId") long doctorId,
                                                                           @Path("patientId") long patientId);
        @POST("/checkin")
        public SymptomCheckin createCheckin(@Body SymptomCheckin symptomCheckin);

        /**
         * Symptom Checkin Image
         */
        // @Headers({"Content-Type: image/jpeg"})
        @GET("/checkin/{symptomCheckinId}/image" )
        @Streaming
        public Response getCheckinImage(@Path("symptomCheckinId") long symptomCheckinId);


        @Multipart
        @POST("/checkin/{symptomCheckinId}/image")
        public SymptomCheckin createImageCheckin(@Path("symptomCheckinId") long symptomCheckinId, @Part("image") TypedFile image);


    }


    //
    // Medication list
    //

    public ArrayList<Medication> findMedicationByPatientId(long patientId, AuthenticationInterceptor
            authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.findMedicationByPatientId(patientId);
    }

    public Medication createMedication(long patientId, Medication medication, AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.createMedication(patientId, medication);
    }


    public Medication updateMedication(long patientId, Medication medication, AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.updateMedication(patientId, medication);

    }

    public void deleteMedication(long patientId, Medication medication, AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        Medication deletedMedication = medicationServiceInterface.deleteMedication(patientId, medication.getId());
    }


    //
    // Checkins
    //

    public ArrayList<SymptomCheckin> findCheckinByDoctorIdAndPatientId(long doctorId, long patientId,
            AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.findCheckinByDoctorIdAndPatientId(doctorId, patientId);
    }


    public SymptomCheckin createSymptomCheckin(SymptomCheckin symptomCheckin, AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.createCheckin(symptomCheckin);
    }

    public SymptomCheckin createMultipartSymptomCheckin(long symptomCheckinId, String path, AuthenticationInterceptor authenticationInterceptor) {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        return medicationServiceInterface.createImageCheckin(symptomCheckinId, new TypedFile("application/png", new File(path)));
    }

    public InputStream getSymptomCheckinImage(long symptomCheckinId, AuthenticationInterceptor authenticationInterceptor) throws IOException {
        MedicationServiceInterface medicationServiceInterface = buildMedicationServiceInterface(authenticationInterceptor);
        Response response = medicationServiceInterface.getCheckinImage(symptomCheckinId);
        return response.getBody().in();
    }

    /**
     * Medication service builder
     * @param authenticationInterceptor
     * @return
     */

    private static MedicationServiceInterface buildMedicationServiceInterface(AuthenticationInterceptor authenticationInterceptor) {

        Gson gson = new GsonBuilder()
                // int date: "2014-10-10T15:00:01Z"
                // "Nov 2, 2014 2:19:06 PM
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .setExclusionStrategies(new AttributeExclusionStrategy())
                .create();

        MedicationServiceInterface medicationServiceInterface = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new ServiceLogAdapter())
                .setRequestInterceptor(authenticationInterceptor)
                .setClient(authenticationInterceptor)
                .setEndpoint(authenticationInterceptor.getCloudUrl())
                .setConverter(new GsonConverter(gson))
                .build()
                .create(MedicationServiceInterface.class);
        return medicationServiceInterface;
    }


}
