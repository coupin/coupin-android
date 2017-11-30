package com.kibou.abisoyeoke_lawal.coupinapp.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.Services.NotificationService;

import java.util.Calendar;

/**
 * Created by abisoyeoke-lawal on 11/25/17.
 */

public class NotificationUtils {
    public static void setReminder(Activity activity, Context appContext, boolean weekend, Calendar calendar) {
        Context context = appContext.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
//                long interval = 1000 * 60 * 1440;
        long interval = 1000 * 60 * 60 * 24 * 7;

        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("weekend", weekend);
        PendingIntent servicePendingIntent = PendingIntent.getService(context, NotificationService.SERVICE_ID, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, servicePendingIntent);
        Toast.makeText(activity, "Notification Settings have been saved!", Toast.LENGTH_SHORT).show();
    }
}
