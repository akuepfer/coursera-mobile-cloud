package org.aku.sm.smclient.entities;


import org.aku.sm.smclient.common.DateFormatter;

import java.util.Date;

/**
 * To display the answer of the patients questions on the list of the ViewCheckinFragment.
 */
public class CheckinAnswer {

    String question;
    String answer;
    Date intakeDate;

    public CheckinAnswer(String question, String answer, Date intakeDate) {
        this.question = question;
        this.answer = answer;
        this.intakeDate = intakeDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }


    public String getFormattedIntakeDate() {
        return DateFormatter.formatDate(intakeDate);
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }
}
