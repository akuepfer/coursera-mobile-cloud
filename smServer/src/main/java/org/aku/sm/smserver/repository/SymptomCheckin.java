package org.aku.sm.smserver.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.aku.sm.smserver.common.JsonDateDeserializer;
import org.aku.sm.smserver.common.JsonDateSerializer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties({ "mouthPainSevere" } )
@Entity
public class SymptomCheckin implements Comparable<SymptomCheckin>{

    public enum SymptomCheckinAnswer {

        UNDEFINED("undefined"),

        STANDARD_QUESTION_1("How bad is your mouth pain/sore throat?"),
        // ” to which a patient can respond, “well-controlled,” “moderate,” or “severe."
        WELL_CONTROLLED("well-controlled"),
        MODERATE("moderate"),
        SEVERE("severe"),

        STANDARD_QUESTION_2("Does your pain stop you from eating/drinking?"),
        //  To this, the patient can respond, “no,” “some,” or “I can’t eat.
        CAN_NOT_EAT("I can't eat"),
        SOME("some"),

        STANDARD_QUESTION_3("Did you take your pain medication?"),
        //  to which a Patient can respond “yes” or “no”.

        MEDICATIOM_QUESTION("Did you take "),

        YES("yes"),
        NO("no");


        SymptomCheckinAnswer(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }

        public static SymptomCheckinAnswer parseValue(String value) {
            try {
                return valueOf(value);
            } catch (Exception ex) {
                return UNDEFINED;
            }
        }

        private final String description;
    }
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	
	private long patientId;

	@JsonSerialize(using=JsonDateSerializer.class)
	@JsonDeserialize(using=JsonDateDeserializer.class)
	private Date checkinDate;		// Date and time
	
	// “How bad is your mouth pain/sore throat?”
	// “well­controlled,” “moderate,” or “severe.
	private String mouthPain;
	
	// “Does your pain stop you from eating/drinking?”
	// To this, the patient can respond, “no,” “some,” or “I can’t eat.
    // 	no, some, yes
	private String painStopFromEating;
	
	//  “Did you take your pain medication?” to which a Patient can respond “yes” or “no”.
	private String medicationIntake;	
	
	@OneToMany(mappedBy="symptomCheckin", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private List<MedicationIntake> medicationIntakes;
	
	@Transient
	private List<VoiceRecording> voiceRecordings;

	@Transient
	private List<SymptomPhoto> symptomPhotos;


	public SymptomCheckin() {
		this.voiceRecordings = new ArrayList<VoiceRecording>();
		this.symptomPhotos = new ArrayList<SymptomPhoto>();
	}
	
	
	public SymptomCheckin(long patientId, Date checkinDate,
			String mouthPain, String painStopFromEating, String medicationIntake) {
		super();
		this.patientId = patientId;
		this.checkinDate = checkinDate;
		this.mouthPain = mouthPain;
		this.painStopFromEating = painStopFromEating;
		this.medicationIntake = medicationIntake;
		this.medicationIntakes = new ArrayList<MedicationIntake>();
		this.voiceRecordings = new ArrayList<VoiceRecording>();
		this.symptomPhotos = new ArrayList<SymptomPhoto>();
	}

	
	
	public long getId() {
		return id;
	}
	
	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}

	public String getMouthPain() {
		return mouthPain;
	}

	public void setMouthPain(String mouthPain) {
		this.mouthPain = mouthPain;
	}

	public String getPainStopFromEating() {
		return painStopFromEating;
	}

	public void setPainStopFromEating(String painStopFromEating) {
		this.painStopFromEating = painStopFromEating;
	}

	public String getMedicationIntake() {
		return medicationIntake;
	}


	public void setMedicationIntake(String medicationIntake) {
		this.medicationIntake = medicationIntake;
	}

	@JsonManagedReference
	public List<MedicationIntake> getMedicationIntakes() {
		return medicationIntakes;
	}

	public void setMedicationIntakes(
			List<MedicationIntake> medicationIntakes) {
		this.medicationIntakes = medicationIntakes;
	}

	public List<VoiceRecording> getVoiceRecordings() {
		return voiceRecordings;
	}

	public void setVoiceRecordings(List<VoiceRecording> voiceRecordings) {
		this.voiceRecordings = voiceRecordings;
	}

	public List<SymptomPhoto> getSymptomPhotos() {
		return symptomPhotos;
	}

	public void setSymptomPhotos(List<SymptomPhoto> symptomPhotos) {
		this.symptomPhotos = symptomPhotos;
	}


	public boolean isMouthPainSevere() {
		return mouthPain != null 
				&& mouthPain.equals(SymptomCheckin.SymptomCheckinAnswer.SEVERE.name());
	}
	
	@Override
	public int compareTo(SymptomCheckin o) {
		return checkinDate.compareTo(o.getCheckinDate());
	}
}
