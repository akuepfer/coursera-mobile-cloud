package org.aku.sm.smclient.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MedicationIntake implements Parcelable {

    // Name of medicine
    private String name;

    // “Did you take your pain medication?” to which a Patient
    //		can respond “yes” or “no”
    private String taken;

    // time and date he or she took the specified medicine
    private Date intakeDate;

    public MedicationIntake(String name, String taken, Date intakeDate) {
        this.name = name;
        this.taken = taken;
        this.intakeDate = intakeDate;
    }

    public MedicationIntake(CheckinQuestion checkinQuestion) {
        this.name = checkinQuestion.getMedication();
        this.taken = SymptomCheckin.SymptomCheckinAnswer.parseAnswer(checkinQuestion.getReply()).name();
        this.intakeDate = checkinQuestion.getIntakeDate();
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




    //
    // Implementation of Parcelable
    //
    public MedicationIntake(Parcel in) {
        name = in.readString();
        taken = in.readString();
        long time = in.readLong();
        intakeDate =  time == 0 ?  null : new Date(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(taken);
        dest.writeLong(intakeDate == null ? 0 : intakeDate.getTime());
    }

    public static final Parcelable.Creator<MedicationIntake> CREATOR = new Parcelable.Creator<MedicationIntake>() {
        public MedicationIntake createFromParcel(Parcel in) {
            return new MedicationIntake(in);
        }

        public MedicationIntake[] newArray(int size) {
            return new MedicationIntake[size];
        }
    };
}
