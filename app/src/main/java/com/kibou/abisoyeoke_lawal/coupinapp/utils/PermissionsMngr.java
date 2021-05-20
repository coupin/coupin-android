package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class PermissionsMngr {
    /**
     * Check if we have all permissions needed
     * @return true if we do and false otherwise.
     */
    public static boolean permissionsCheck(String[] permissions, Context context) {
        boolean goodToGo = true;

        for(String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                goodToGo = false;
                break;
            }
        }

        return goodToGo;
    }
}
