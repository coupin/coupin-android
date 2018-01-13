package com.kibou.abisoyeoke_lawal.coupinapp.Layouts;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by abisoyeoke-lawal on 7/1/17.
 */

public class MapWrapperLayout extends RelativeLayout {
    // Reference to the googlemap object
    private GoogleMap googleMap;

    private int bottomOffsetPixels;

    // Currently selected Marker
    private Marker marker;

    private View infoWindow;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.googleMap = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = false;

        // Make sure window is showing and we have all the references
        if (marker != null && marker.isInfoWindowShown() && googleMap != null && infoWindow != null) {
            // Get marker position on the screen
            Point point = googleMap.getProjection().toScreenLocation(marker.getPosition());

            // Make a copy of the MOtionEvent and adjust it's location, relative to left top corner
            MotionEvent copyEv = MotionEvent.obtain(ev);
            copyEv.offsetLocation(-point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetPixels);

            // Dispatch the adjusted MotionEvent to the infowindow
            ret = infoWindow.dispatchTouchEvent(copyEv);
        }

        // If infoWindow is consumed by touch event, then trturn true else pass the event to the super class
        return ret || super.dispatchTouchEvent(ev);
    }
}
