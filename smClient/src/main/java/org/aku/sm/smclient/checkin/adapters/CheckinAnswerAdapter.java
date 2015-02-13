package org.aku.sm.smclient.checkin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.entities.CheckinAnswer;

import java.util.ArrayList;

/**
 * Adapter for the list answers a patient gave to the list of symptom questions.
 * A single chechin holds a list of questions / answers.
 *
 * Used by ViewCheckinFragment
 */
public class CheckinAnswerAdapter extends ArrayAdapter<CheckinAnswer> {

    ArrayList<CheckinAnswer> checkinAnswers;

    public CheckinAnswerAdapter(Context context, ArrayList<CheckinAnswer> medicationIntakes) {
        super(context, R.layout.checkin_three_anser_question, medicationIntakes);
        this.checkinAnswers = medicationIntakes;
    }

    public ArrayList<CheckinAnswer> getCheckinQuestions() {
        return checkinAnswers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO use recyclet views
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkin_answer, parent, false);
        }

        CheckinAnswer checkinAnswer = checkinAnswers.get(position);
        LayoutInflater inflater = (LayoutInflater) convertView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.checkin_answer, parent, false);
        TextView questionText = (TextView) rowView.findViewById(R.id.question_text);
        questionText.setText(checkinAnswer.getQuestion());
        TextView answerText = (TextView) rowView.findViewById(R.id.answer_1);
        answerText.setText(checkinAnswer.getAnswer());
        TextView answerDate = (TextView) rowView.findViewById(R.id.intake_date);
        answerDate.setText(checkinAnswer.getFormattedIntakeDate());
        return rowView;
    }

}
