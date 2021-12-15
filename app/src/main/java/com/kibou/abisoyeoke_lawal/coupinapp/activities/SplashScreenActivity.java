package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.BuildConfig;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.UpdateDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.services.UpdateService;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PermissionsMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class SplashScreenActivity extends AppCompatActivity implements MyOnSelect {
    private ApiCalls apiCalls;
    private AppUpdateManager appUpdateManager;
    private boolean isLoggedIn = false;
    private boolean interestsSelected = false;
    private boolean isOnboardingDone = false;
    private Bundle extras;
    private Handler handler = new Handler();
    private InstallStateUpdatedListener installStateUpdatedListener;
    private UpdateDialog updateDialog;

    private final boolean check = false;
    private final int PERMISSION_ALL = 1;
    private final int count = 0;
    private final String[] permissions = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;

    @BindView(R.id.gif_image)
    ImageView gifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        AppEventsLogger.activateApp(getApplication());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setProjectId(BuildConfig.FIREBASE_P_ID)
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .build();
        FirebaseApp.initializeApp(this, options, BuildConfig.FIREBASE_NAME);

        Glide.with(this)
            .load(R.raw.loading_gif)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
            .into(gifView);

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        PreferenceManager.setContext(getApplicationContext());

        isLoggedIn = PreferenceManager.isLoggedIn();
        interestsSelected = PreferenceManager.interestsSelected();
        isOnboardingDone = PreferenceManager.isOnboardingDone();

        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);

        // TODO: Use GCM notification instead
        startService(new Intent(getApplicationContext(), UpdateService.class));

        if (!PermissionsMngr.permissionsCheck(permissions, this)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        } else {
            if (PreferenceManager.updateAvailable()) {
                updateDialog = new UpdateDialog(this, this);
                updateDialog.show();
            } else {
                extras = getIntent().getExtras();
                if (PreferenceManager.isLoggedIn()) {
                    updateUserInfo();
                } else {
                    proceed();
                }
            }
        }
    }

    // Check update from playstore
    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else {
                proceed();;
            }
        }).addOnFailureListener(e -> {
            Log.v("Update Attempt", "Failed to check update.");
            proceed();;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Update Cancelled!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Update Complete!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        }
        proceed();;
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
                    if (PreferenceManager.updateAvailable()) {
                        updateDialog = new UpdateDialog(this, this);
                        updateDialog.show();
                    } else {
                        if (PreferenceManager.isLoggedIn()) {
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

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New app is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    } else {
                        proceed();;
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .show();
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

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    FLEXIBLE_APP_UPDATE_REQ_CODE
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Sentry.captureException(e);
            proceed();
        }
    }

    @Override
    public void onSelect(boolean selected, int version) {
        if (selected) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_link))));
        } else {
            PreferenceManager.setUpdate(false);
            PreferenceManager.setLastUpdate(version);
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
                    PreferenceManager.setCurrentUser(response.body());
                }

                checkUpdate();
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                checkUpdate();
            }
        });
    }
}
