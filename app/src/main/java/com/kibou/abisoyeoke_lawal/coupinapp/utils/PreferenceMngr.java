package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.LandingActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.services.AlarmReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by abisoyeoke-lawal on 4/22/17.
 */

public class PreferenceMngr {
    private static PreferenceMngr ourInstance = new PreferenceMngr();
    private static SharedPreferences preferences = null;
    private static RequestQueue requestQueue;

//    public static PreferenceMngr getInstance() {
//        if(ourInstance == null) {
//            ourInstance = new PreferenceMngr();
//        }
//
//        return  ourInstance;
//    }

    public static void setContext(Context context) {
        preferences = context.getSharedPreferences(context.getString(R.string.main_package), Context.MODE_PRIVATE);
    }

    /**
     * Method to set the current user details
     * @param user User object containing all details
     */
    public static void setCurrentUserDetails(User user) {
        String uid = user.id;
        preferences.edit().putBoolean("category" + uid, true).apply();
        preferences.edit().putStringSet("blacklist", new HashSet<>(user.blacklist)).apply();
        preferences.edit().putStringSet("favourites", new HashSet<>(user.favourites)).apply();
        preferences.edit().putString("token", user.notification.token).apply();
        preferences.edit().putString("uid", uid).apply();
        preferences.edit().putString("userV2", TypeUtils.objectToString(user)).apply();
    }

    public static void setCurrentUser(User user) {
        preferences.edit().putString("userV2", TypeUtils.objectToString(user)).apply();
    }

    public static User getCurrentUser() {
        String userString = preferences.getString("userV2", null);
        return userString == null ? null : (User) TypeUtils.stringToObject(userString);
    }

    /**
     * Method to set the token
     * @param token
     */
    public static void setToken(String token, String uid, String user, Set<String> favourites, Set<String> blacklist) {
        preferences.edit().putBoolean("category" + uid, true).apply();
        preferences.edit().putStringSet("blacklist", blacklist).apply();
        preferences.edit().putStringSet("favourites", favourites).apply();
        preferences.edit().putString("token", token).apply();
        preferences.edit().putString("uid", uid).apply();
        preferences.edit().putString("user", user).apply();
    }

    public static void putBoolean(String key, Boolean value){
        preferences.edit().putBoolean(key, value).apply();
    }

    public static Boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }

    public static Set<String> getBlacklist() {
        return preferences.getStringSet("blacklist", new HashSet<String>());
    }

    public static void setBlacklist(Set<String> blacklist) {
        preferences.edit().putStringSet("blacklist", blacklist).apply();
    }

    public static Set<String> getFavourites() {
        return preferences.getStringSet("favourites", new HashSet<String>());
    }

    public static void setFavourites(Set<String> favourites) {
        preferences.edit().putStringSet("favourites", favourites).apply();
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

    public static void setOnboardingDone(Boolean isFirstRun) {
        preferences.edit().putBoolean("isOnboardingDone", isFirstRun).apply();
    }

    public static Boolean isOnboardingDone() {
        return preferences.getBoolean("isOnboardingDone", false);
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
    public static String getUserId() {
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
    public static void setUpdate(boolean isAvaialble) {
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
        return PreferenceMngr.getToken() != null;
    }

    public static String getNotificationToken() {
        return preferences.getString("notificationId", null);
    }

    public static void setNotificationToken(String token) {
        preferences.edit().putString("notificationId", token).apply();
    }

    /**
     * Get last checked date
     * @return
     */
    public static String getLastChecked() {
        return preferences.getString("lastChecked", null);
    }

    /**
     * Set last checked date
     * @param notify notify or not
     * @param isWeekend true if weekends
     */
    public static void notificationSelection(boolean notify, boolean isWeekend) {
        String days = isWeekend ? "weekends" : "weekdays";
        preferences.edit().putString("notifyDays", days).apply();
        preferences.edit().putBoolean("notify", notify).apply();
    }

    /**
     * Get previous notification selection
     * @return array of boolean values
     */
    public static boolean[] getNotificationSelection() {
        Boolean weekends =  "weekends".equals(preferences.getString("notifyDays", null));
        return new boolean[]{preferences.getBoolean("notify", false), weekends};
    }

    public static void setRequestQueue(RequestQueue newQueue) {
        requestQueue = newQueue;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * Method to sign out
     * @param activity
     */
    public static void signOut(Activity activity) {
        NotificationScheduler.cancelReminder(activity, AlarmReceiver.class);
        preferences.edit().clear().apply();
        activity.startActivity(new Intent(activity, LandingActivity.class));
        activity.finish();
    }
}
