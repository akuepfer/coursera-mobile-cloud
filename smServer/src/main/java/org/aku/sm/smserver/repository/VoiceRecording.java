package org.aku.sm.smserver.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class VoiceRecording {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private Long SymptomReportId;
	
	// placeholder for the binary path of the voice recording
	private String path;


	public long getId() {
		return id;
	}	
	
	public Long getSymptomReportId() {
		return SymptomReportId;
	}

	public void setSymptomReportId(Long symptomReportId) {
		SymptomReportId = symptomReportId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	
}
