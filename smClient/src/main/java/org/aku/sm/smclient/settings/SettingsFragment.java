package org.aku.sm.smclient.settings;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.aku.sm.smclient.common.Global;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.service.UserService;
import org.aku.sm.smclient.common.SimpleTextWatcher;


public class SettingsFragment extends Fragment {
    private static final String TAG = "gcs";

    private String username;
    private String password;
    private String cloudUrl;

    AutoCompleteTextView editUsername;
    EditText editPassword;
    AutoCompleteTextView editCloudUrl;
    private Button settingsTest;
    private View view;

    private UserService userService;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        userService = new UserService();

        editUsername = (AutoCompleteTextView) view.findViewById(R.id.editUsername);
        editUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveSharedPreference(getActivity(), Settings.USERNAME, editUsername.getText().toString());
            }
        });
        editUsername.setText(Settings.getSharedPreference(getActivity(), Settings.USERNAME));
        String[] users = getResources().getStringArray(R.array.user_names);
        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, users);
        editUsername.setAdapter(userAdapter);

        editPassword = (EditText) view.findViewById(R.id.editPassword);
        editPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveSharedPreference(getActivity(), Settings.PASSWORD, editPassword.getText().toString());
            }
        });
        editPassword.setText(Settings.getSharedPreference(getActivity(), Settings.PASSWORD));


        editCloudUrl = (AutoCompleteTextView) view.findViewById(R.id.editCloudUrl);
        editCloudUrl.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Settings.saveSharedPreference(getActivity(), Settings.CLOUD_URL, editCloudUrl.getText().toString());
            }
        });
        editCloudUrl.setText(Settings.getSharedPreference(getActivity(), Settings.CLOUD_URL));
        String[] cloudUrls = getResources().getStringArray(R.array.cloud_urls);
        ArrayAdapter<String> urlAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, cloudUrls);
        editCloudUrl.setAdapter(urlAdapter);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_login:
                // connect to GCM to retrieve registration ID
                // connect to server
                Global.setJSessionId(null);
                userService.getUserByUsername(getActivity(),
                        editUsername.getText().toString(),
                        editPassword.getText().toString(),
                        editCloudUrl.getText().toString());
                return true;
            case R.id.settings_logout:
                Global.setJSessionId(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
        // mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
