package org.aku.sm.smclient.entities;

/**
 * Created by armin on 19.10.14.
 */
public class Patient extends User {

    public Patient() {
        firstName = "not";
        lastName = "connected";
        medicalRecordNumber = "check config";
    }

    public Patient(String firstName, String lastName, String medicalRecordNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.medicalRecordNumber = medicalRecordNumber;
    }




}
