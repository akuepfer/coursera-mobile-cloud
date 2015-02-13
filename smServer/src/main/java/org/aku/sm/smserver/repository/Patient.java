package org.aku.sm.smserver.repository;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Patient extends User {
	private static final long serialVersionUID = -7531672740327203645L;

	@Column(unique=true)
	private String medicalRecordNumber;
	
	protected long doctorId;
	
    @OneToMany(mappedBy="patient", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private List<Medication> medications;

    /**
     * Reference is here to display it in ObjectAid UML reverse engineering drawings.
     * To retrieve the data use the service.
     */
	@Transient
	List<SymptomCheckin> symptomCheckins;

	/**
	 * mouthPain of latest symptomCheckin
	 */
	@Transient
	private String mouthPain;
	
	
	public Patient() {
		super();
	}

	public Patient(String firstName, String lastName, String medicalRecordNumber) {
		super();
		this.role = "ROLE_PATIENT";
		this.enabled = true;
		this.firstName = firstName;
		this.lastName = lastName;
		this.medicalRecordNumber = medicalRecordNumber;
	}

	public Patient(String username, String password, String firstName, String lastName, String medicalRecordNumber, long doctorId,
			List<Medication> medications) {
		super();
		this.role = "ROLE_PATIENT";
		this.enabled = true;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.medicalRecordNumber = medicalRecordNumber;
		this.doctorId = doctorId;
		this.medications = medications;
	}


	public String getMedicalRecordNumber() {
		return medicalRecordNumber;
	}

	public void setMedicalRecordNumber(String medicalRecordNumber) {
		this.medicalRecordNumber = medicalRecordNumber;
	}
	
	public long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(long doctorId) {
		this.doctorId = doctorId;
	}

	@JsonManagedReference
	public List<Medication> getMedications() {
		return medications;
	}

	public void setMedications(List<Medication> medications) {
		this.medications = medications;
	}

	public void setMouthPain(String mouthPain) {
		this.mouthPain = mouthPain;
	}

	public String getMouthPain() {
		return this.mouthPain;
	}


}
