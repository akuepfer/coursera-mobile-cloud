package org.aku.sm.smclient.service;

import android.app.Activity;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.test.suitebuilder.annotation.Smoke;

import junit.framework.TestCase;

import org.aku.sm.smclient.entities.Patient;
import org.aku.sm.smclient.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armin on 01.11.14.
 */

@SmallTest
@MediumTest
@LargeTest
@Smoke
public class UserServiceTest extends TestCase {



    public void testNoAccount() {

        Activity activity = null;
        UserService userService = new UserService();
        // userService.b

        // not authorized
        String username = "hflower";
        String password = "po";
        String cloudUrl = Constants.URL_tethering;
        AuthenticationInterceptor authenticationInterceptor = AuthenticationInterceptor.create(activity, username, password, cloudUrl);
        UserService.UserServiceInterface userServiceInterface = UserService.buidUserServiceInterface(authenticationInterceptor);


        User user = userServiceInterface.getUserByUsername("hgreen");

        List<Patient> patients = userServiceInterface.findPatientByLastName(1, "hgreen");

        long doctorId = 0;
        ArrayList<Patient> patientsByDoctorId = userServiceInterface.findPatientsByDoctorId(doctorId);




    }

    // doctor can't post checkin
    // patient can't post medication
    public void testNotAuthorized() {

    }

    public void testPatient() {

    }


    public void testDoctor() {

    }


}
