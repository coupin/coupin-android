package com.kibou.abisoyeoke_lawal.coupinapp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PermissionsMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements MyOnSelect {
    private boolean check = false;
    private final int PERMISSION_ALL = 1;
    private int count = 0;
    private String[] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
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
            PreferenceMngr.getInstance().setRequestQueue(requestQueue1);
        }

        startService(new Intent(getApplicationContext(), UpdateService.class));

        if (!PermissionsMngr.permissionsCheck(permissions, this)) {
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

    public void proceed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceMngr.isLoggedIn()) {
                    if (PreferenceMngr.interestsSelected()) {
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, InterestsActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    public void onSelect(boolean selected, int version) {
        if (selected) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_link))));
        } else {
            PreferenceMngr.getInstance().setUpdate(false);
            PreferenceMngr.setLastUpdate(version);
            proceed();
        }
    }
}
