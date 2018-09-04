package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.kibou.abisoyeoke_lawal.coupinapp.LandingActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 4/22/17.
 */

public class PreferenceMngr {
    private static PreferenceMngr ourInstance = new PreferenceMngr();
    private static SharedPreferences preferences = null;
    private static Context currentContext = null;
    private RequestQueue requestQueue;

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
     * Add to total number of coupins generated
     * @param uid
     */
    public static void addToTotalCoupinsGenerated(String uid) {
        int total = preferences.getInt("totalCoupins" + uid, 0);
        preferences.edit().putInt("totalCoupins" + uid, total + 1).apply();
    }

    /**
     * Get total number of coupins generated
     * @param uid
     * @return
     */
    public static int getToTotalCoupinsGenerated(String uid) {
        return preferences.getInt("totalCoupins" + uid, 0);
    }

    /**
     * Method to get the token
     * @return token
     */

    public static String getToken() {
        return preferences.getString("token", null);
    }

    /**
     * Method to get the token
     * @return token
     */

    public static String getTimestamp() {
        return preferences.getString("timestamp", null);
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
     * Method to set the mobile number
     * @param timestamp
     */
    public static void setTimestamp(String timestamp) {
        preferences.edit().putString("timestamp", timestamp).apply();
    }

    /**
     * Method to check if category has been picked
     * @return
     */
    public static boolean interestsSelected() {
        String user = preferences.getString("uid", null);
        return preferences.getBoolean("category" + user, false);
    }

    /**
     * Method to know if user has set category at the signing up stage
     * @param value
     */
    public static void setInterests(boolean value) {
        String user = preferences.getString("uid", null);
        preferences.edit().putBoolean("category" + user, value).apply();
    }

    /**
     * Convert User interest to arraylist and return it
     * @return User Interests
     */
    public static ArrayList<String> getUserInterests() {
        try {
            ArrayList<String> temp = new ArrayList<>();

            JSONObject user = new JSONObject(PreferenceMngr.getUser());
            JSONArray userInterests = user.getJSONArray("interests");

            for (int i = 0; i < userInterests.length(); i++) {
                temp.add("\"" + userInterests.getString(i) + "\"");
            }

            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get user info
     * @return user data in a string format
     */
    public String getUserId() {
        return preferences.getString("uid", null);
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
     * Set the updateAvailable based on if update is available or not
     * @param isAvaialble
     */
    public void setUpdate(boolean isAvaialble) {
        preferences.edit().putBoolean("updateAvailable", isAvaialble).apply();
    }

    public static void setLastUpdate(int update) {
        preferences.edit().putInt("lastUpdateAttempt", update).apply();
    }

    /**
     * Check if update is available
     * @return true if update is available and false otherwise
     */
    public static boolean updateAvailable() {
        return preferences.getBoolean("updateAvailable", false);
    }

    public static int getLastAttempt() {
        return preferences.getInt("lastUpdateAttempt", 0);
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
    public static String getLastChecked() {
        return preferences.getString("lastChecked", null);
    }

    public static void setLastChecked(String dateString) {
        preferences.edit().putString("lastChecked", dateString).apply();
    }

    /**
     * Set last checked date
     * @param notify
     * @param weekend
     */
    public static void notificationSelection(boolean notify, boolean weekend, boolean weekday) {
        preferences.edit().putBoolean("notify", notify).apply();
        preferences.edit().putBoolean("weekend", weekend).apply();
        preferences.edit().putBoolean("weekday", weekday).apply();
    }

    /**
     * Get previous notification selection
     * @return array of boolean values
     */
    public static boolean[] getNotificationSelection() {
        return new boolean[]{preferences.getBoolean("notify", false), preferences.getBoolean("weekend", false), preferences.getBoolean("weekday", false)};
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public RequestQueue getRequestQueue() {
        return this.requestQueue;
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