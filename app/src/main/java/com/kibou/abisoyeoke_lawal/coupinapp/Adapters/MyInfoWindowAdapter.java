package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 6/28/17.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    public final View myView;

    public MyInfoWindowAdapter(Activity activity) {
        myView = activity.getLayoutInflater().inflate(R.layout.info_window, null);
    }

    /**
     * Get info window
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        TextView discount_1 = ((TextView) myView.findViewById(R.id.discount_1));
        discount_1.setText(marker.getTitle());

        TextView discount_2 = ((TextView) myView.findViewById(R.id.discount_2));
        discount_1.setText(marker.getSnippet());

        ((Button)myView.findViewById(R.id.btn_now)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("VOLLEYBUTTON", "Now");
            }
        });

        ((Button)myView.findViewById(R.id.btn_later)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("VOLLEYBUTTON", "Later");
            }
        });

        return myView;
    }

    /**
     * Get info contents
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
