package org.aku.sm.smclient.checkin.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.IntakeDateDialog;
import org.aku.sm.smclient.entities.CheckinQuestion;

import java.util.ArrayList;

/**
 * Adapter for the list symptom questions a patient has to answer per checkin
 *
 * Used by LoadMedicationTask and indirect by NewcheckinFragment
 */
public class CheckinQuestionListAdapter extends ArrayAdapter<CheckinQuestion> {

    private final Activity activity;
    private LayoutInflater inflater;

    public class ViewHolder implements RadioGroup.OnCheckedChangeListener {
        CheckinQuestion checkinQuestion;
        View rowView;
        RadioGroup radioGroup;
        TextView questionText;
        RadioButton radioAnswer1;
        RadioButton radioAnswer2;
        RadioButton radioAnswer3;
        TextView intakeDate;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkinQuestion != null) {
                checkinQuestion.onCheckedChanged(group, checkedId);
                if (checkinQuestion.getMedication() != null) {
                    if (checkedId == R.id.radio_answer_1) {
                        IntakeDateDialog intakeDateDialog = IntakeDateDialog.newInstance(CheckinQuestionListAdapter.this, checkinQuestion);
                        intakeDateDialog.show(activity.getFragmentManager(), "IntakeDateDialog");
                    } else if (checkedId == R.id.radio_answer_2) {
                        checkinQuestion.setIntakeDate(null);
                    }
                }
            }
        }
    }



    public CheckinQuestionListAdapter(Activity activity) {
        super(activity, R.layout.checkin_three_anser_question);
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        CheckinQuestion checkinQuestion = getItem(position);
        boolean medicationQuestion = checkinQuestion.getMedication() != null;

        // verify view is of the right type
        boolean correctViewType = false;
        if (convertView != null) {
            if (getItemViewType(position) == 0 && convertView.getId() == R.id.three_answer_question) {
                correctViewType = true;
            }
            if (getItemViewType(position) == 1 && convertView.getId() == R.id.two_answer_question) {
                correctViewType = true;
            }
        }

        if (convertView == null || !correctViewType) {
            if (getItemViewType(position) == 0) {
                convertView = inflater.inflate(R.layout.checkin_three_anser_question, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.checkin_two_anser_question, parent, false);
            }

            holder = new ViewHolder();
            holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.question_radio_group);
            holder.questionText = (TextView) convertView.findViewById(R.id.question_text);
            holder.radioAnswer1 = (RadioButton) convertView.findViewById(R.id.radio_answer_1);
            holder.radioAnswer2 = (RadioButton) convertView.findViewById(R.id.radio_answer_2);
            if (getItemViewType(position) == 0) {
                holder.radioAnswer3 = (RadioButton) convertView.findViewById(R.id.radio_answer_3);
            } else {
                holder.intakeDate = (TextView) convertView.findViewById(R.id.intake_date);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // disable activation of onCheckedChanged(..) callback when calling setChecked
        holder.radioGroup.setOnCheckedChangeListener(null);
        holder.questionText.setText(checkinQuestion.getQuestion());
        holder.radioAnswer1.setText(checkinQuestion.getAnswer1());
        holder.radioAnswer1.setChecked(checkinQuestion.isRadio1Checked());
        holder.radioAnswer2.setText(checkinQuestion.getAnswer2());
        holder.radioAnswer2.setChecked(checkinQuestion.isRadio2Checked());
        if (getItemViewType(position) == 0) {
            holder.radioAnswer3.setText(checkinQuestion.getAnswer3());
            holder.radioAnswer3.setChecked(checkinQuestion.isRadio3Checked());
        } else {
            holder.intakeDate.setText(checkinQuestion.getFormattedIntakeDate());
        }
        holder.radioGroup.setOnCheckedChangeListener(holder);
        holder.checkinQuestion = checkinQuestion;

        return convertView;
    }

    /**
     * Returns the view type
     * 0 three answer question
     * 1 two answer question
     * @param position position in adapter array
     */
    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getAnswer3() != null) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Returns count of different layouts
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }



}
