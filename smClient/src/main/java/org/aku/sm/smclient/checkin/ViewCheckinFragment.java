package org.aku.sm.smclient.checkin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.OnFragmentInteractionListener;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.adapters.CheckinAnswerAdapter;
import org.aku.sm.smclient.checkin.contentprovider.CheckinContentProviderFacade;
import org.aku.sm.smclient.common.DateFormatter;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.service.AuthenticationInterceptor;
import org.aku.sm.smclient.service.MedicationService;
import org.aku.sm.smclient.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewCheckinFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewCheckinFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ViewCheckinFragment extends Fragment {
    public static final String IMAGE_FILE_PATH = "checkin.jpg";

    private String firstName;
    private String lastName;
    private long checkinId;
    private SymptomCheckin symptomCheckin;
    private Activity activity;
    private ImageView checkinPhoto;
    private TextView name;
    private TextView checkinDate;
    private File fileToRemove;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param checkinId ID of symptom checking to display
     * @return A new instance of fragment ViewCheckinFragment.
     */
    public static ViewCheckinFragment newInstance(String firstName, String lastName,
            SymptomCheckin symptomCheckin) {
        ViewCheckinFragment fragment = new ViewCheckinFragment();
        fragment.symptomCheckin = symptomCheckin;
        Bundle args = new Bundle();
        args.putString(C.FIRST_NAME, firstName);
        args.putString(C.LAST_NAME, lastName);
        args.putLong(C.CHECKIN_ID, symptomCheckin.getId());
        args.putParcelable(C.CHECKIN, symptomCheckin);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewCheckinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            firstName = getArguments().getString(C.FIRST_NAME);
            lastName = getArguments().getString(C.LAST_NAME);
            checkinId = getArguments().getLong(C.CHECKIN_ID);
            symptomCheckin = getArguments().getParcelable(C.CHECKIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // header of list with photo
        View headerView = View.inflate(getActivity(), R.layout.view_checkin_header, null);
        checkinPhoto = (ImageView) headerView.findViewById(R.id.checkin_photo);
        name = (TextView) headerView.findViewById(R.id.name);
        checkinDate = (TextView) headerView.findViewById(R.id.clCheckinDate);
        name.setText(firstName + " " + lastName);
        checkinDate.setText(DateFormatter.formatDate(symptomCheckin.getCheckinDate()));

        // list
        View view = inflater.inflate(R.layout.fragment_view_checkin, container, false);
        ListView checkinList = (ListView) view.findViewById(R.id.checkin_list);
        checkinList.addHeaderView(headerView);
        checkinList.setAdapter(new CheckinAnswerAdapter(this.getActivity(), symptomCheckin.createCheckinAnswer()));
        new LoadCheckinImageTask(checkinPhoto, this, getActivity()).execute(symptomCheckin);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (Settings.isRoleDoctor(activity)) {
            inflater.inflate(R.menu.menu_doctor_checkin_list, menu);
        } else {
            inflater.inflate(R.menu.menu_patient_view_checkin, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkin_list:
                ArrayList<SymptomCheckin> symptomCheckins = new CheckinContentProviderFacade().getSymptomCheckins(activity);
                mListener.onFragmentInteraction(firstName, lastName, symptomCheckins);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        if (fileToRemove != null && fileToRemove.exists()) {
            fileToRemove.delete();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        mListener = null;
    }

    /**
     * Async task to retrieve checkin image, which might be:
     * - from local storage, after checkin
     * - from content provider if patient views local list
     * - from network if doctor views list
     */
    private static class LoadCheckinImageTask extends AsyncTask<SymptomCheckin, Void, File> {


        ImageView checkinPhoto;
        ViewCheckinFragment viewCheckinFragment;
        Activity activity;

        LoadCheckinImageTask(ImageView checkinPhoto, ViewCheckinFragment viewCheckinFragment, Activity activity) {
            this.checkinPhoto = checkinPhoto;
            this.viewCheckinFragment = viewCheckinFragment;
            this.activity = activity;
        }

        @Override
        protected File doInBackground(SymptomCheckin... params) {
            SymptomCheckin symptomCheckin = params[0];
            File imageFile = null;
            if (symptomCheckin.getSymptomPhotoPath() == null) {
                try {
                    InputStream in = new MedicationService().getSymptomCheckinImage(params[0].getId(), AuthenticationInterceptor.create(activity));
                    OutputStream out = activity.openFileOutput(IMAGE_FILE_PATH, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[10000];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    imageFile = activity.getFileStreamPath(IMAGE_FILE_PATH);
                } catch (Exception ex) {
                    Log.e(C.TAG, "LoadCheckinImageTask error reading input stream", ex);
                }
            } else {
                imageFile = new File(symptomCheckin.getSymptomPhotoPath());
            }
            return imageFile;

        }

        @Override
        protected void onPostExecute(File file) {
            // todo send back path instead of accessing variable
            viewCheckinFragment.fileToRemove = file;
            createScaledImage(file, checkinPhoto);
            super.onPostExecute(file);
        }

        /**
         * Creates an image with the hight of 400 pt
         * @param file
         * @param checkinPhoto
         */
        private void createScaledImage(File file, ImageView checkinPhoto) {
            if (file == null) return;
            Bitmap scaledBitmap = null;
            try {
                // Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.fromFile(file));
                double scaleFactor = bitmap.getHeight() / 400.0;
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scaleFactor),
                        (int) (bitmap.getHeight() / scaleFactor), true);
                checkinPhoto.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(C.TAG, "createScaledImage", e);
            }
        }
    }
}
