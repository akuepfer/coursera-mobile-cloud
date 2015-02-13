package org.aku.sm.smclient.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.aku.sm.smclient.R;
import org.aku.sm.smclient.common.SimpleTextWatcher;
import org.aku.sm.smclient.settings.Settings;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link org.aku.sm.smclient.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class AlarmFragment extends Fragment {

    private static final String TAG = "org.aku.sm";
    private View view;
    private AlarmManager alarmMgr;

    private EditText editAlarm1Id;
    private EditText editAlarm2Id;
    private EditText editAlarm3Id;
    private EditText editAlarm4Id;


    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        return fragment;
    }
    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        editAlarm1Id = (EditText) view.findViewById(R.id.editAlarm1Id);
        editAlarm1Id.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveAlarm(getActivity(), 1, editAlarm1Id.getText().toString());
            }
        });
        editAlarm1Id.setText(Settings.getAlarm(getActivity(), 1));
        editAlarm1Id.setFilters(buildTimeFilter());

        editAlarm2Id = (EditText) view.findViewById(R.id.editAlarm2Id);
        editAlarm2Id.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveAlarm(getActivity(), 2, editAlarm2Id.getText().toString());
            }
        });
        editAlarm2Id.setText(Settings.getAlarm(getActivity(), 2));
        editAlarm2Id.setFilters(buildTimeFilter());

        editAlarm3Id = (EditText) view.findViewById(R.id.editAlarm3Id);
        editAlarm3Id.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveAlarm(getActivity(), 3, editAlarm3Id.getText().toString());
            }
        });
        editAlarm3Id.setText(Settings.getAlarm(getActivity(), 3));
        editAlarm3Id.setFilters(buildTimeFilter());

        editAlarm4Id = (EditText) view.findViewById(R.id.editAlarm4Id);
        editAlarm4Id.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveAlarm(getActivity(), 4, editAlarm4Id.getText().toString());
            }
        });
        editAlarm4Id.setText(Settings.getAlarm(getActivity(), 4));
        editAlarm4Id.setFilters(buildTimeFilter());

        alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alarm, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_create:
                setAlarm(1, editAlarm1Id.getText().toString());
                setAlarm(2, editAlarm2Id.getText().toString());
                setAlarm(3, editAlarm3Id.getText().toString());
                setAlarm(4, editAlarm4Id.getText().toString());
                break;
            case R.id.action_delete:
                cancelAlarm(1);
                cancelAlarm(2);
                cancelAlarm(3);
                cancelAlarm(4);
                break;
            case R.id.action_refresh:
                boolean alarmUp1 = (PendingIntent.getBroadcast(getActivity(), 1,
                        new Intent(getActivity(), AlarmBroadcastReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
                boolean alarmUp2 = (PendingIntent.getBroadcast(getActivity(), 2,
                        new Intent(getActivity(), AlarmBroadcastReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
                boolean alarmUp3 = (PendingIntent.getBroadcast(getActivity(), 3,
                        new Intent(getActivity(), AlarmBroadcastReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
                boolean alarmUp4 = (PendingIntent.getBroadcast(getActivity(), 4,
                        new Intent(getActivity(), AlarmBroadcastReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);

                Toast.makeText(getActivity(), "A1 " + alarmUp1 + ", A2 " + alarmUp2 + " ,A3 " + alarmUp3 + " ,A4 " + alarmUp4,
                        Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Return a filter to allow only time input in the format: hh:mm
     */
    InputFilter[] buildTimeFilter() {
        InputFilter[] timeFilter = new InputFilter[1];
        timeFilter[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {


                if (source.length() == 0) {
                    return null;// deleting, keep original editing
                }
                String result = "";
                result += dest.toString().substring(0, dstart);
                result += source.toString().substring(start, end);
                result += dest.toString().substring(dend, dest.length());

                if (result.length() > 5) {
                    return "";// do not allow this edit
                }
                boolean allowEdit = true;
                char c;
                if (result.length() > 0) {
                    c = result.charAt(0);
                    allowEdit &= (c >= '0' && c <= '2' && !(Character.isLetter(c)));
                }
                if (result.length() > 1) {
                    c = result.charAt(1);
                    allowEdit &= (c >= '0' && c <= '9' && !(Character.isLetter(c)));
                }
                if (result.length() > 2) {
                    c = result.charAt(2);
                    allowEdit &= (c == ':' && !(Character.isLetter(c)));
                }
                if (result.length() > 3) {
                    c = result.charAt(3);
                    allowEdit &= (c >= '0' && c <= '5' && !(Character.isLetter(c)));
                }
                if (result.length() > 4) {
                    c = result.charAt(4);
                    allowEdit &= (c >= '0' && c <= '9' && !(Character.isLetter(c)));
                }
                return allowEdit ? null : "";
            }
        };
        return timeFilter;
    }

    public void setAlarm(int alarmId, String time) {

        if (time.length() > 4) {
            String[] values = time.split(":");
            int hour = Integer.parseInt(values[0]);
            int min = Integer.parseInt(values[1]);

            Intent intent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), alarmId, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Log.i(TAG, "setAlarm before " + alarmId + " " + time + " " + calendar.toString());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            Log.i(TAG, "setAlarm after " + alarmId + " " + time + " " + calendar.toString());

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);

            //Toast.makeText(getActivity(), "set timers " + calendar.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    public void cancelAlarm(int alarmId) {
        Intent intent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarmId, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        //Toast.makeText(getActivity(), "cancel timers", Toast.LENGTH_SHORT).show();
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
  //      mListener = null;
    }

}
