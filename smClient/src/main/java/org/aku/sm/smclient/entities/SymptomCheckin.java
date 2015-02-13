package org.aku.sm.smclient.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.mime.TypedFile;

public class SymptomCheckin implements Parcelable {

    public enum SymptomCheckinStatus { NOT_SAVED, SAVED, ERROR_DURING_SAVE };


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

        public static SymptomCheckinAnswer parseAnswer(String answer) {
            for (SymptomCheckinAnswer a : SymptomCheckinAnswer.values()) {
                if (a.getDescription().equals(answer)) {
                    return a;
                }
            }
            return UNDEFINED;
        }


        private final String description;
    }

    private long id;
    private long patientId;
    private Date checkinDate;
    private String mouthPain;
    private String painStopFromEating;
    private String medicationIntake;
    private SymptomCheckinStatus symptomCheckinStatus;
    private String symptomPhotoPath;

    private ArrayList<MedicationIntake> medicationIntakes;

    public SymptomCheckin() {
    }

    public SymptomCheckin(long patientId) {
        this.patientId = patientId;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(String checkinDate) {
        this.checkinDate = new Date(Long.valueOf(checkinDate));
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

    public String getSymptomPhotoPath() {
        return symptomPhotoPath;
    }

    public void setSymptomPhotoPath(String symptomPhotoPath) {
        this.symptomPhotoPath = symptomPhotoPath;
    }

    public ArrayList<MedicationIntake> getMedicationIntakes() {
        return medicationIntakes;
    }

    public void setMedicationIntakes(ArrayList<MedicationIntake> medicationIntakes) {
        this.medicationIntakes = medicationIntakes;
    }

    public SymptomCheckinStatus getSymptomCheckinStatus() {
        return symptomCheckinStatus;
    }

    public void setSymptomCheckinStatus(SymptomCheckinStatus symptomCheckinStatus) {
        this.symptomCheckinStatus = symptomCheckinStatus;
    }

    public TypedFile getTypedFile() {
        File file = new File(symptomPhotoPath);
        return new TypedFile("application/png", file);
    }


    /**
     * Initialized the three standard question fields from the checkin questions
     * Builds the medicationIntakes list from the checking questions
     */
    public void updateMedicationIntakes(ArrayList<CheckinQuestion> checkingQuenstions) {
        checkinDate = new Date();
        medicationIntakes = new ArrayList<MedicationIntake>();
        for (CheckinQuestion checkinQuestion : checkingQuenstions) {
            if (checkinQuestion.getQuestion().equals(SymptomCheckinAnswer.STANDARD_QUESTION_1.getDescription())) {
                // “well-controlled,” “moderate,” or “severe."
                mouthPain = SymptomCheckinAnswer.parseAnswer(checkinQuestion.getReply()).name();
            } else if (checkinQuestion.getQuestion().equals(SymptomCheckinAnswer.STANDARD_QUESTION_2.getDescription())) {
                // “no,” “some,” or “I can’t eat
                painStopFromEating = SymptomCheckinAnswer.parseAnswer(checkinQuestion.getReply()).name();
            } else if (checkinQuestion.getQuestion().equals(SymptomCheckinAnswer.STANDARD_QUESTION_3.getDescription())) {
                // “yes” or “no”.
                medicationIntake = SymptomCheckinAnswer.parseAnswer(checkinQuestion.getReply()).name();
            } else {
                medicationIntakes.add(new MedicationIntake(checkinQuestion));
            }
        }
    }


    public ArrayList<CheckinAnswer> createCheckinAnswer() {
        ArrayList<CheckinAnswer> checkinAnswers = new ArrayList<CheckinAnswer>();
        checkinAnswers.add(new CheckinAnswer(
                SymptomCheckinAnswer.STANDARD_QUESTION_1.getDescription(),
                SymptomCheckinAnswer.parseValue(mouthPain).getDescription(), null));
        checkinAnswers.add(new CheckinAnswer(
                SymptomCheckinAnswer.STANDARD_QUESTION_2.getDescription(),
                SymptomCheckinAnswer.parseValue(painStopFromEating).getDescription(), null));
        checkinAnswers.add(new CheckinAnswer(
                SymptomCheckinAnswer.STANDARD_QUESTION_3.getDescription(),
                SymptomCheckinAnswer.parseValue(medicationIntake).getDescription(), null));
        if (medicationIntakes != null) {
            for (MedicationIntake medicationIntake : medicationIntakes) {
                checkinAnswers.add(new CheckinAnswer(
                        SymptomCheckinAnswer.MEDICATIOM_QUESTION.getDescription() + medicationIntake.getName() + "?",
                        SymptomCheckinAnswer.parseValue(medicationIntake.getTaken()).getDescription(),
                        medicationIntake.getIntakeDate()));
            }
        }
        return checkinAnswers;
    }


    //
    // Implementation of Parcelable
    //
    public SymptomCheckin(Parcel in) {
        id = in.readLong();
        patientId = in.readLong();
        checkinDate = new Date(in.readLong());
        mouthPain = in.readString();
        painStopFromEating = in.readString();
        medicationIntake = in.readString();
        // Object[] voiceRecordings = in.re;
        //Object[] symptomPhotos;
        List<MedicationIntake> medicationIntakes = new ArrayList<MedicationIntake>();
        in.readList(medicationIntakes, MedicationIntake.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(patientId);
        dest.writeLong(checkinDate == null ? new Date().getTime() : checkinDate.getTime());
        dest.writeString(mouthPain);
        dest.writeString(painStopFromEating);
        dest.writeString(medicationIntake);
        dest.writeList(medicationIntakes);
    }

    public static final Parcelable.Creator<SymptomCheckin> CREATOR = new Parcelable.Creator<SymptomCheckin>() {
        public SymptomCheckin createFromParcel(Parcel in) {
            return new SymptomCheckin(in);
        }

        public SymptomCheckin[] newArray(int size) {
            return new SymptomCheckin[size];
        }
    };
}
