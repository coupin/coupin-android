package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kibou.abisoyeoke_lawal.coupinapp.LandingActivity;
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

    /**
     * Method to set the token
     * @param token
     */
    public static void setToken(String token, String uid, String user) {
        preferences.edit().putString("token", token).apply();
        preferences.edit().putString("uid", uid).apply();
        preferences.edit().putString("user", user).apply();
        preferences.edit().putBoolean("category" + uid, true).apply();
    }

    /**
     * Method to get the token
     * @return token
     */

    public static String getToken() {
        return preferences.getString("token", null);
    }

    /**
     * Get mobile number
     * @return number
     */
    public static String getMobileNumber() {
        return preferences.getString("mobileNumber", null);
    }

    /**
     * Method to set the mobile number
     * @param number
     */
    public static void setMobileNumber(String number) {
        preferences.edit().putString("mobileNumber", number).apply();
    }

    /**
     * Method to check if category has been picked
     * @return
     */
    public static boolean categorySelected() {
        String user = preferences.getString("uid", null);
        return preferences.getBoolean("category" + user, false);
    }

    public static void setCategory(boolean value) {
        String user = preferences.getString("uid", null);
        preferences.edit().putBoolean("category" + user, value).apply();
    }

    public static String getUser() {
        return preferences.getString("user", null);
    }

    public static void setUser(String user) {
        preferences.edit().putString("user", user).apply();
    }

    /**
     * Method to see if user is logged in
     * @return true if logged and false otherwise
     */
    public static boolean isLoggedIn() {
        if (getToken() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to sign out
     * @param activity
     */
    public static void signOut(Activity activity) {
        preferences.edit().putString("token", null).apply();
        activity.startActivity(new Intent(activity, LandingActivity.class));
    }
}
