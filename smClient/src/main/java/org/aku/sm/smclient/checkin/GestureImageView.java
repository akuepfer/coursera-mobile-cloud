package org.aku.sm.smclient.checkin;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import org.aku.sm.smclient.R;

/**
 * Image view with double tab and fling detection
 * myView.setOnTouchListener(new OnTouchListener()) would probably have worked too,
 * if returning true and not super.on..() from onTouchEvent callback.
 */
public class GestureImageView extends ImageView {
    private static final String DEBUG_TAG = "Gestures";

    Context context;
    GestureDetectorCompat gestureDetector;
    NewCheckinFragment newCheckinFragment;

    public GestureImageView(Context context) {
        super(context);
        this.context = context;
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }


    public void initGestureDetector(NewCheckinFragment newCheckinFragment) {
        this.newCheckinFragment = newCheckinFragment;
        gestureDetector = new GestureDetectorCompat(context, new SimpleGestureListener());
        gestureDetector.setIsLongpressEnabled(false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.d(DEBUG_TAG, "onTouchEvent: " + event.toString());
        gestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        // only works if returning true here
        return true;
    }


    /**
     * SimpleGestureListener to detect double tab and fling
     */
    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            newCheckinFragment.launchCameraIntent();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            GestureImageView.this.setImageDrawable(getResources().getDrawable(R.drawable.head));
            return true;
        }
    }

}
