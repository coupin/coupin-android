package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 4/22/17.
 */

public class PreferenceMngr {
    private static PreferenceMngr ourInstance = new PreferenceMngr();
    private static SharedPreferences preferences = null;
    private static Context currentContext = null;

    public static PreferenceMngr getInstance() {
        if(ourInstance == null) {
            ourInstance = new PreferenceMngr();
        }

        return  ourInstance;
    }

    public static void setContext(Context context) {
        currentContext = context;
        preferences = context.getSharedPreferences(context.getString(R.string.main_package), Context.MODE_PRIVATE);
    }

    public static void setToken(String token) {
        preferences.edit().putString("token", token).apply();
    }

    public static String getToken() {
        return preferences.getString("token", null);
    }
}
