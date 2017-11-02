package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kibou.abisoyeoke_lawal.coupinapp.LandingActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import java.util.Date;

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

    /**
     * Method to know if user has set category at the signing up stage
     * @param value
     */
    public static void setCategory(boolean value) {
        String user = preferences.getString("uid", null);
        preferences.edit().putBoolean("category" + user, value).apply();
    }

    /**
     * Get user info
     * @return user data in a string format
     */
    public static String getUser() {
        return preferences.getString("user", null);
    }

    /**
     * Set the user info
     * @param user
     */
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
     * Get last checked date
     * @return
     */
    public static Date getLastChecked() {
        return new Date(preferences.getString("lastChecked", null));
    }

    /**
     * Set last checked date
     * @param notify
     * @param weekend
     */
    public static void notificationSelection(boolean notify, boolean weekend) {
        preferences.edit().putBoolean("notify", notify).apply();
        preferences.edit().putBoolean("weekend", weekend).apply();
    }

    /**
     * Get previous notification selection
     * @return array of boolean values
     */
    public static boolean[] getNotificationSelection() {
        return new boolean[]{preferences.getBoolean("notify", false), preferences.getBoolean("weekend", false)};
    }

    /**
     * Method to sign out
     * @param activity
     */
    public static void signOut(Activity activity) {
        preferences.edit().clear().apply();
        activity.startActivity(new Intent(activity, LandingActivity.class));
        activity.finish();
    }
}
