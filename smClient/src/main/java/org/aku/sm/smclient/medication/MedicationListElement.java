package org.aku.sm.smclient.medication;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.aku.sm.smclient.R;

/**
 * Layout of a medication list element to support double tab and fling action
 * Double tab should start the edit dialog, fling should delete the element.
 */
public class MedicationListElement extends RelativeLayout {
    private static final String DEBUG_TAG = "Gesture";

    private final Context context;
    private ListView listView;
    private GestureDetectorCompat gestureDetector;

    public MedicationListElement(Context context) {
        super(context);
        this.context = context;
    }

    public MedicationListElement(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MedicationListElement(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }



    public void initGestureDetector(ListView listView) {
        this.listView = listView;
        gestureDetector = new GestureDetectorCompat(context, new SimpleGestureListener());
        gestureDetector.setIsLongpressEnabled(false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
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
            // listView.
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            // listView.
            return true;
        }
    }

}
