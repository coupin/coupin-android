package com.kibou.abisoyeoke_lawal.coupinapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.appevents.AppEventsLogger;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.UpdateDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.services.UpdateService;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NotificationUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements MyOnSelect {
    public static final String CHANNEL_ID = "3001";
    public static final int SERVICE_ID = 3002;
    private boolean check = false;
    private final int PERMISSION_ALL = 1;
    private int count = 0;
    private String[] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_CONTACTS
    };

    NotificationManager notificationManager;
    private RequestQueue requestQueue1;

    Handler handler = new Handler();
    UpdateDialog updateDialog;

    @BindView(R.id.gif_image)
    ImageView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        AppEventsLogger.activateApp(this);
        requestQueue1 = Volley.newRequestQueue(this);

        Glide.with(this)
            .load(R.raw.loading_gif)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .into(gifView);

        PreferenceMngr.setContext(getApplicationContext());
        if (PreferenceMngr.getInstance().getRequestQueue() == null) {
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
            PreferenceMngr.getInstance().setRequestQueue(requestQueue1);
        }

        startService(new Intent(getApplicationContext(), UpdateService.class));

        if (!permissionsCheck()) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        } else {
            if (PreferenceMngr.updateAvailable()) {
                updateDialog = new UpdateDialog(this, this);
                updateDialog.show();
            } else {
                proceed();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String perms[], int[] grantResults) {
        boolean valid = true;
        switch (requestCode) {
            case PERMISSION_ALL:
                for(int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        valid = false;
                    }
                }

                if(grantResults.length > 0 && valid) {
                    if (PreferenceMngr.updateAvailable()) {
                        updateDialog = new UpdateDialog(this, this);
                        updateDialog.show();
                    } else {
                        proceed();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permissions), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * Check if we have all permissions needed
     * @return true if we do and false otherwise.
     */
    private boolean permissionsCheck() {
        boolean goodToGo = true;

        for(String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                goodToGo = false;
                break;
            }
        }

        return goodToGo;
    }

    public void proceed() {
//        if (!PreferenceMngr.getNotificationSelection()[0]) {
            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_WEEK, 1);
//            calendar.set(Calendar.HOUR, 11);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 55);
            NotificationUtils.setReminder(SplashScreen.this, getApplicationContext(), true, calendar);
            PreferenceMngr.notificationSelection(true, true, false);
            PreferenceMngr.setLastChecked((new Date()).toString());
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getApplicationContext());
        notifBuilder.setSmallIcon(R.drawable.ic_clogo);
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        notifBuilder.setAutoCancel(true);
        if (1 > 0) {
//                    if (total > 0) {
            notifBuilder.setContentTitle("New Coupins!!!");
            notifBuilder.setContentText("There are " + 1 + " coupins matching your interests around you.");
        } else {
            notifBuilder.setContentTitle("Hello There!!!");
            notifBuilder.setContentText("Keep watching this space for new and upcoming rewards.");
        }


        notificationManager.notify(SERVICE_ID, notifBuilder.build());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceMngr.getInstance().isLoggedIn()) {
                    if (PreferenceMngr.getInstance().interestsSelected()) {
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, InterestsActivity.class));
                        finish();
                    }
                }else {
                    startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    public void onSelect(boolean selected, int version) {
        Log.v("VolleyUpdateAvailable", String.valueOf(selected));
        if (selected) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_link))));
        } else {
            PreferenceMngr.getInstance().setUpdate(false);
            PreferenceMngr.setLastUpdate(version);
            proceed();
        }
    }
}
