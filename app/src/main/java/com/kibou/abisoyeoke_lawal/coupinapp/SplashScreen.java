package com.kibou.abisoyeoke_lawal.coupinapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.signed.Signature;
import com.cloudinary.android.signed.SignatureProvider;
import com.facebook.appevents.AppEventsLogger;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.UpdateDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.services.UpdateService;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements MyOnSelect {
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

    private RequestQueue requestQueue1;

    Handler handler = new Handler();
    UpdateDialog updateDialog;

    @BindView(R.id.test_image)
    ImageView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        AppEventsLogger.activateApp(this);
        requestQueue1 = Volley.newRequestQueue(this);

        Glide.with(this).load(R.raw.loading_gif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)).into(testView);

        PreferenceMngr.setContext(getApplicationContext());
        if (PreferenceMngr.getInstance().getRequestQueue() == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            PreferenceMngr.getInstance().setRequestQueue(requestQueue);
        }

        getSignature();

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

    /**
     * Get signature for cloudinary uploads.
     */
    public void getSignature() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String url = getString(R.string.base_url) + "/signature";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    MediaManager.init(getApplicationContext(), new SignatureProvider() {
                        @Override
                        public Signature provideSignature(Map options) {
                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            long temp = timestamp.getTime();
                            PreferenceMngr.setTimestamp(String.valueOf(temp));
                            return new Signature(response, getString(R.string.cloudinary_api_key), temp);
                        }

                        @Override
                        public String getName() {
                            return null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: Handle Error
                error.printStackTrace();
                Log.v("VolleyInit", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("timestamp", String.valueOf(timestamp.getTime()));

                return params;
            }
        };

        requestQueue1.add(stringRequest);
    }
}
