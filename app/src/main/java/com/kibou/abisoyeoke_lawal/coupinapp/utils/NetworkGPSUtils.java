package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by abisoyeoke-lawal on 11/13/17.
 */

public class NetworkGPSUtils {
    /**
     * Check if phone is connected
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeConnectection = connectivityManager.getActiveNetworkInfo();
        return activeConnectection != null && activeConnectection.isConnected();
    }

    /**
     * Check if phone location is available
     * @param context
     * @return
     */
    public static boolean isLocationAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gps_enabled || network_enabled;
    }
}
