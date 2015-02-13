package org.aku.sm.smserver.repository;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Doctor extends User {
	private static final long serialVersionUID = 8851439985845528934L;

	@Column(unique=true)
	String doctorIdentificationId;
	String degree;
	
	public Doctor() {
		super();
	}

	public Doctor(String firstName, String lastName, String degree) {
		super();
		this.role = "ROLE_DOCTOR";
		this.enabled = true;
		this.firstName = firstName;
		this.lastName = lastName;
		this.degree = degree;
	}
	

	public Doctor(String username, String password, String firstName, String lastName, String degree, String doctorIdentificationId) {
		super();
		this.role = "ROLE_DOCTOR";
		this.enabled = true;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.degree = degree;
		this.doctorIdentificationId = doctorIdentificationId;
	}
	
		
	

	
	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}


	
}
