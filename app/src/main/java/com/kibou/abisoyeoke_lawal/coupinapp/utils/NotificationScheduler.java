package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kibou.abisoyeoke_lawal.coupinapp.HotActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationScheduler {
    public static final int WEEKLY_REMINDER_REQUEST_CODE = 100;
    public static final long WEEKLY_INTERVAL = 1000 * 60 * 60 * 24 * 7;
    public static final String CHANNEL_ID = "3001";
    public static final String TAG = "NotificationScheduler";

    public static  void setReminder(Context context, Class<?> cls, Calendar calendar) {
        cancelReminder(context, cls);

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        );

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            WEEKLY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            WEEKLY_INTERVAL,
            pendingIntent
        );
    }

    public static void cancelReminder(Context context, Class<?> cls) {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        );

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            WEEKLY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, int total) {
        Log.v("Notification", "attempting to show notification");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Coupin Channel";
            String description = "Channel for coupin";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }

        Intent intent = new Intent(context.getApplicationContext(), HotActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context.getApplicationContext(),
            WEEKLY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setContentIntent(pendingIntent);
        notifBuilder.setSmallIcon(R.drawable.notification_icon_w);
        notifBuilder.setColor(context.getResources().getColor(R.color.colorAccent));
        notifBuilder.setPriority(Notification.PRIORITY_HIGH);
        notifBuilder.setDefaults(Notification.DEFAULT_LIGHTS |
            Notification.DEFAULT_SOUND |
            Notification.FLAG_AUTO_CANCEL
        );
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        if (total > 0) {
            String text = "There are " + total + " coupins matching your interests around you.";
            notifBuilder.setContentTitle("New Coupins!!!");
            notifBuilder.setContentText(text);
            notifBuilder.setStyle(bigTextStyle.bigText(text));
        } else {
            String text = "Keep watching this space for new and upcoming rewards.";
            notifBuilder.setContentTitle("Hello There!!!");
            notifBuilder.setContentText(text);
            notifBuilder.setStyle(bigTextStyle.bigText(text));
        }

        notificationManager.notify(WEEKLY_REMINDER_REQUEST_CODE, notifBuilder.build());
    }
}
