package org.aku.sm.smserver.repository;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.aku.sm.smserver.common.JsonDateDeserializer;
import org.aku.sm.smserver.common.JsonDateSerializer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class MedicationIntake {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name="SYMPTOM_CHECKIN_ID")
	private SymptomCheckin symptomCheckin;
	
	// Name of medicine
	private String name;
	
	// “Did you take your pain medication?” to which a Patient
	//		can respond “yes” or “no”
	private String taken;
	
	// time and date he or she took the specified medicine
	@JsonSerialize(using=JsonDateSerializer.class)
	@JsonDeserialize(using=JsonDateDeserializer.class)
	private Date intakeDate;

	
	public MedicationIntake() {
		super();
	}


	public MedicationIntake(SymptomCheckin symptomCheckin, String name, String taken, Date intakeDate) {
		super();
		this.symptomCheckin = symptomCheckin;
		this.name = name;
		this.taken = taken;
		this.intakeDate = intakeDate;
	}

	public long getId() {
		return id;
	}
	
	@JsonBackReference
	public SymptomCheckin getSymptomCheckin() {
		return symptomCheckin;
	}

	public void setSymptomCheckin(SymptomCheckin symptomCheckin) {
		this.symptomCheckin = symptomCheckin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaken() {
		return taken;
	}

	public void setTaken(String taken) {
		this.taken = taken;
	}

	public Date getIntakeDate() {
		return intakeDate;
	}

	public void setIntakeDate(Date intakeDate) {
		this.intakeDate = intakeDate;
	}

}
