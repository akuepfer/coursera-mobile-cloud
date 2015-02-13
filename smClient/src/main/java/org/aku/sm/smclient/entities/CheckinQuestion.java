package org.aku.sm.smclient.entities;


import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.aku.sm.smclient.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CheckinQuestion implements Parcelable, RadioGroup.OnCheckedChangeListener {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private long id;
    private String question;
    private String medication;
    private String answer1;
    private String answer2;
    private String answer3;
    private boolean radio1Checked;
    private boolean radio2Checked;
    private boolean radio3Checked;
    private String reply;
    private Date intakeDate;

    public CheckinQuestion(String question, String answer1, String answer2, String answer3) {
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
    }


    public CheckinQuestion(Medication medication) {
        this.id = id;
        this.medication = medication.getName();
        this.answer1 = SymptomCheckin.SymptomCheckinAnswer.YES.getDescription();
        this.answer2 = SymptomCheckin.SymptomCheckinAnswer.NO.getDescription();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        if (question == null) {
            return SymptomCheckin.SymptomCheckinAnswer.MEDICATIOM_QUESTION.getDescription() + medication + "?";
        } else {
            return question;
        }
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public boolean isRadio1Checked() {
        return radio1Checked;
    }

    public boolean isRadio2Checked() {
        return radio2Checked;
    }

    public boolean isRadio3Checked() {
        return radio3Checked;
    }

    public String getReply() {
        return reply;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getFormattedIntakeDate() {
        return intakeDate == null ? "" : dateFormat.format(intakeDate);
    }

    /**
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
        switch (checkedId) {
            case R.id.radio_answer_1:
                radio1Checked = true;
                radio2Checked = radio3Checked = false;
                break;
            case R.id.radio_answer_2:
                radio2Checked = true;
                radio1Checked = radio3Checked = false;
                break;
            case R.id.radio_answer_3:
                radio3Checked = true;
                radio1Checked = radio2Checked = false;
                break;
        }
        reply = radioButton.getText().toString();
    }


    public void cancelChecked() {
        radio1Checked = radio2Checked = radio3Checked = false;
    }




    //
    // Implementation of Parcelable
    //
    public CheckinQuestion(Parcel in) {
        id = in.readLong();
        question = in.readString();
        medication = in.readString();
        answer1 = in.readString();
        answer2 = in.readString();
        answer3 = in.readString();
        radio1Checked = in.readInt() == 1;
        radio2Checked = in.readInt() == 1;
        radio3Checked = in.readInt() == 1;
        reply = in.readString();
        intakeDate = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(question);
        dest.writeString(medication);
        dest.writeString(answer1);
        dest.writeString(answer2);
        dest.writeString(answer3);
        dest.writeInt(radio1Checked ? 1 : 0);
        dest.writeInt(radio2Checked ? 1 : 0);
        dest.writeInt(radio3Checked ? 1: 0);
        dest.writeString(reply);
        dest.writeLong(intakeDate.getTime());
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
