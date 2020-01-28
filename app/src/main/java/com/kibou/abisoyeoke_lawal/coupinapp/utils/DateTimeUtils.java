package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by abisoyeoke-lawal on 9/2/17.
 */

public class DateTimeUtils {
    /**
     * Convert time stamps with timezone details
     * @param s Date String
     * @return date
     */
    public static Date convertZString(String s) {
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(tz);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setCalendar(cal);
            cal.setTime(sdf.parse(s));
            Date date = cal.getTime();
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
