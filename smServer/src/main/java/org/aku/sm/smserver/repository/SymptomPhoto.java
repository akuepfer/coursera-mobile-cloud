package org.aku.sm.smserver.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SymptomPhoto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Long symptomReportId;

	
	public SymptomPhoto() {
		
	}
	
	public SymptomPhoto(Long symptomReportId) {
		super();
		this.symptomReportId = symptomReportId;
	}


	public long getId() {
		return id;
	}
	
	public Long getSymptomReportId() {
		return symptomReportId;
	}

	public void setSymptomReportId(Long symptomReportId) {
		this.symptomReportId = symptomReportId;
	}

	
	
	
}
