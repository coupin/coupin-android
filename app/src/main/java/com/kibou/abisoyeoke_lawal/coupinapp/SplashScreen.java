package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.signed.Signature;
import com.cloudinary.android.signed.SignatureProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class SplashScreen extends Activity {
    int count = 0;

    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        final RequestQueue requestQueue1 = Volley.newRequestQueue(this);

        PreferenceMngr.setContext(getApplicationContext());
        if (PreferenceMngr.getInstance().getRequestQueue() == null) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            PreferenceMngr.getInstance().setRequestQueue(requestQueue);
        }

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String url = getString(R.string.base_url) + "/signature";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                MediaManager.init(getApplicationContext(), new SignatureProvider() {
                    @Override
                    public Signature provideSignature(Map options) {
                        PreferenceMngr.setTimestamp(String.valueOf(timestamp.getTime()));
                        return new Signature(response, getString(R.string.cloudinary_api_key), timestamp.getTime());
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                });
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

                params.put("invalidate", String.valueOf(true));
                params.put("public_id", PreferenceMngr.getInstance().getUserId());
                params.put("timestamp", String.valueOf(timestamp.getTime()));

                return params;
            }
        };

        requestQueue1.add(stringRequest);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceMngr.getInstance().isLoggedIn()) {
                    Log.v("VolleyOPref", "" + PreferenceMngr.getInstance().interestsSelected());
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
}
