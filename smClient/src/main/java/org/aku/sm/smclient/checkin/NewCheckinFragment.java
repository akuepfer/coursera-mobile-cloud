package org.aku.sm.smclient.checkin;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.aku.sm.smclient.OnFragmentInteractionListener;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.checkin.contentprovider.CheckinContentProviderFacade;
import org.aku.sm.smclient.checkin.contentprovider.CheckinPhotoContentProvider;
import org.aku.sm.smclient.checkin.tasks.CreateSymptomCheckinTask;
import org.aku.sm.smclient.entities.CheckinQuestion;
import org.aku.sm.smclient.entities.SymptomCheckin;
import org.aku.sm.smclient.settings.Settings;
import org.aku.sm.smclient.checkin.tasks.LoadMedicationTask;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewCheckinFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NewCheckinFragment extends Fragment {

    public static final String LOG_TAG = "org.aku.sm.checkin";

    /** Standard activity result: operation canceled. */
    public static final int RESULT_CANCELED    = 0;
    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK           = -1;
    public static final int CAMERA_PIC_REQUEST = 1;

    public static final String PATIENT_ID = "PATIENT_ID";

    private long patientId;
    private String role;

    private OnFragmentInteractionListener mListener;
    private Activity activity;
    private GestureDetectorCompat gestureDetector;

    private SymptomCheckin symptomCheckin;


    private GestureImageView checkinPhoto;
    private ListView checkinList;
    private Uri imageUri;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param patientId Parameter 1.
     * @param role Parameter 2.
     * @return A new instance of fragment NewCheckinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewCheckinFragment newInstance(long patientId, String role) {
        NewCheckinFragment fragment = new NewCheckinFragment();
        fragment.patientId = patientId;
        fragment.role = role;
        Bundle args = new Bundle();
        args.putLong(PATIENT_ID, patientId);
        args.putString(Settings.ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    public NewCheckinFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.patientId = getArguments().getLong(PATIENT_ID);
            this.role = getArguments().getString(Settings.ROLE);
        }
        if (savedInstanceState == null) {
            symptomCheckin = new SymptomCheckin(this.patientId);
            imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(R.drawable.head) + '/' +
                    getResources().getResourceTypeName(R.drawable.head) + '/' +
                    getResources().getResourceEntryName(R.drawable.head) );
        } else {
            symptomCheckin = savedInstanceState.getParcelable(SymptomCheckin.class.getSimpleName());
            String imageUriStr = savedInstanceState.getString("imageUri");
            if (imageUriStr != null && imageUriStr.length() > 0) {
                imageUri = Uri.parse(savedInstanceState.getString("imageUri"));
            }
        }
        gestureDetector = new GestureDetectorCompat(this.getActivity(), new SimpleGestureListener());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View headerView = View.inflate(getActivity(), R.layout.new_checkin_header, null);
        checkinPhoto = (GestureImageView) headerView.findViewById(R.id.checkin_photo_gesture);
        checkinPhoto.initGestureDetector(this);
        createScaledImage(imageUri, checkinPhoto);

        View view = inflater.inflate(R.layout.fragment_new_checkin, container, false);
        checkinList = (ListView) view.findViewById(R.id.checkin_list_new);
        checkinList.addHeaderView(headerView);
        // TODO handle layout resize
        new LoadMedicationTask(checkinList, getActivity(), symptomCheckin).execute(patientId);
        return view;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SymptomCheckin.class.getSimpleName(), symptomCheckin);
        outState.putString("imageUri", imageUri == null ? "" : imageUri.getPath());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListAbsListView$LayoutParamsener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_checkin, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                this.symptomCheckin.updateMedicationIntakes(buildCheckinQuestionList(checkinList.getAdapter()));
                this.symptomCheckin.setSymptomPhotoPath(  imageUri == null ? null : imageUri.getPath());
                new CreateSymptomCheckinTask(this, activity).execute(this.symptomCheckin);
                break;
            case R.id.action_delete:
                symptomCheckin = new SymptomCheckin(this.patientId);
                checkinPhoto.setImageDrawable(getResources().getDrawable(R.drawable.head));
                break;
            case R.id.action_take_picture:
                launchCameraIntent();
                break;
            case R.id.action_remove_photo:
                checkinPhoto.setImageDrawable(getResources().getDrawable(R.drawable.head));
                break;
            case R.id.checkin_list:
                String firstName = Settings.getFirstName(activity);
                String lastName = Settings.getLastName(activity);
                ArrayList<SymptomCheckin> symptomCheckins = new CheckinContentProviderFacade().getSymptomCheckins(activity);
                mListener.onFragmentInteraction(firstName, lastName, symptomCheckins);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieve list of checkin questions from adapter
     */
    private ArrayList<CheckinQuestion> buildCheckinQuestionList(ListAdapter checkinListAdapter) {
        ArrayList<CheckinQuestion> checkinQuestions = new ArrayList<CheckinQuestion>();
        for (int i = 0; i < checkinListAdapter.getCount(); i++) {
            if (checkinListAdapter.getItem(i) != null) {     // skip header row which returns  null
                checkinQuestions.add((CheckinQuestion) checkinListAdapter.getItem(i));
            }
        }
        return  checkinQuestions;
    }

    /**
     * Launch the camera to take a picture
     * Taken form iRemember
     */
    public void launchCameraIntent() {

        // Create a new intent to launch the MediaStore, Image capture function
        // Hint: use standard Intent from MediaStore class
        // See: http://developer.android.com/reference/android/provider/MediaStore.html
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Set the imagePath for this image file using the pre-made function
        // getOutputMediaFile to create a new filename for this specific image;
        // For future implementation: store videos in a separate directory
//        File mediaStorageDir = createMediaFile(activity);
//        imageUri = Uri.fromFile(mediaStorageDir);

        imageUri = CheckinPhotoContentProvider.getUriForCheckinImange(getActivity());



        // Add the filename to the Intent as an extra. Use the Intent-extra name
        // from the MediaStore class, EXTRA_OUTPUT
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // Start a new activity for result, using the new intent and the request
        // code CAMERA_PIC_REQUEST
        startActivityForResult(intent, CAMERA_PIC_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "CreateFragment onActivtyResult called. requestCode: "
                + requestCode + " resultCode:" + resultCode + "data:" + data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                // scaling by Android
                // checkinPhoto.setImageURI(imageUri);
                // Manual scaling
                createScaledImage(imageUri, checkinPhoto);

                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }


    /**
     * File will be created under /Android/data/org.aku.sm/files/Pictures/checkin.jpg
     * @param activity the activity
     * @return Returns a media file path for this application.
     */
    private static File createMediaFile(Activity activity) {
        return new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ViewCheckinFragment.IMAGE_FILE_PATH);
    }

    /**
     * TODO content provider
     * @param uri
     * @param checkinPhoto
     *
     *
     * 11-03 23:19:05.064    4032-4032/org.aku.sm E/org.aku.sm.checkin﹕ createScaledImage
     * java.io.FileNotFoundException: No content provider: /storage/emulated/0/org.aku.sm.checkin
     *
     */
    private void createScaledImage(Uri uri, ImageView checkinPhoto) {
        if (uri == null) return;
        Bitmap scaledBitmap = null;
        try {

            // Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            //double scaleFactor = 0.3;
            double scaleFactor = bitmap.getHeight() / 400.0;
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scaleFactor),
                    (int) (bitmap.getHeight() / scaleFactor), true);
            checkinPhoto.setImageBitmap(scaledBitmap);
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "createScaledImage", e);
        }
    }




    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            launchCameraIntent();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            checkinPhoto.setImageDrawable(getResources().getDrawable(R.drawable.head));
            return true;
        }
    }

}
