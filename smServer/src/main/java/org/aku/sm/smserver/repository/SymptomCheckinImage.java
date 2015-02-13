package org.aku.sm.smserver.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class SymptomCheckinImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	

	private long symptomCheckinId;
	
    private String imageName;
     
    @Lob
    @Column(length=16777215)
    private byte[] data; 
    
    
	public SymptomCheckinImage() {
	}

	
	public SymptomCheckinImage(long symptomCheckinId, byte[] data) {
		this.symptomCheckinId = symptomCheckinId;
		this.data = data;
	}
    
    public SymptomCheckinImage(long symptomCheckinId) {
		this.symptomCheckinId = symptomCheckinId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSymptomCheckinId() {
		return symptomCheckinId;
	}

	public void setSymptomCheckinId(long symptomCheckinId) {
		this.symptomCheckinId = symptomCheckinId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}	

}
