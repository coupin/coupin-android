package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.appevents.AppEventsLogger;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.UpdateDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.services.UpdateService;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PermissionsMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class SplashScreenActivity extends AppCompatActivity implements MyOnSelect {
    private ApiCalls apiCalls;
    private final boolean check = false;
    private boolean isLoggedIn = false;
    private boolean interestsSelected = false;
    private boolean isOnboardingDone = false;
    private Bundle extras;
    private final int PERMISSION_ALL = 1;
    private final int count = 0;
    private final String[] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    };

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
        AppEventsLogger.activateApp(getApplication());
        requestQueue1 = Volley.newRequestQueue(this);

        Glide.with(this)
            .load(R.raw.loading_gif)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .into(gifView);

        apiCalls = ApiClient.getInstance().getCalls(this, true);

        PreferenceMngr.setContext(getApplicationContext());
        if (PreferenceMngr.getRequestQueue() == null) {
            PreferenceMngr.setRequestQueue(requestQueue1);
        }

        isLoggedIn = PreferenceMngr.isLoggedIn();
        interestsSelected = PreferenceMngr.interestsSelected();
        isOnboardingDone = PreferenceMngr.isOnboardingDone();

        startService(new Intent(getApplicationContext(), UpdateService.class));

        if (!PermissionsMngr.permissionsCheck(permissions, this)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        } else {
            if (PreferenceMngr.updateAvailable()) {
                updateDialog = new UpdateDialog(this, this);
                updateDialog.show();
            } else {
                extras = getIntent().getExtras();
                if (PreferenceMngr.isLoggedIn()) {
                    updateUserInfo();
                } else {
                    proceed();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perms, grantResults);
        boolean valid = true;
        switch (requestCode) {
            case PERMISSION_ALL:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        valid = false;
                    }
                }

                if (grantResults.length > 0 && valid) {
                    if (PreferenceMngr.updateAvailable()) {
                        updateDialog = new UpdateDialog(this, this);
                        updateDialog.show();
                    } else {
                        if (PreferenceMngr.isLoggedIn()) {
                            updateUserInfo();
                        } else {
                            proceed();
                        }
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permissions), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public void proceed() {
        handler.postDelayed(() -> {
            if (isLoggedIn) {
                if (!interestsSelected) {
                    startActivity(new Intent(SplashScreenActivity.this, InterestsActivity.class));
                    finish();
                } else if(!isOnboardingDone) {
                    startActivity(new Intent(SplashScreenActivity.this, OnboardingActivity.class));
                    finish();
                } else if (extras != null) {
                    if ("hot".equals(extras.getString("navigateTo"))) {
                        startActivity(new Intent(SplashScreenActivity.this, HotActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    }
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                finish();
            }
        }, 2000);
    }

    @Override
    public void onSelect(boolean selected, int version) {
        if (selected) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_link))));
        } else {
            PreferenceMngr.setUpdate(false);
            PreferenceMngr.setLastUpdate(version);
            proceed();
        }
    }

    @Override
    public void onSelect(boolean selected, int index, int quantity) { }

    private void updateUserInfo() {
        Call<User> request = apiCalls.getCurrentUserInfo();
        request.enqueue(new Callback<User>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    PreferenceMngr.setCurrentUser(response.body());
                }

                proceed();
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                proceed();
            }
        });
    }
}
