package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by abisoyeoke-lawal on 4/3/18.
 */

public class UpdateService extends Service {
    private static final int SERVICE_ID = 3001;
    private static final long NOTIFY_INTERVAL = AlarmManager.INTERVAL_DAY;

    private Handler handler = new Handler();
    public RequestQueue requestQueue;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer();
        }

        timer.scheduleAtFixedRate(new CheckVersion(), 0, NOTIFY_INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CheckVersion extends TimerTask {
        @Override
        public void run() {
            String url = getApplicationContext().getString(R.string.base_url) + "/mobile/version";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.isEmpty()) {
                        PreferenceMngr.setContext(getApplicationContext());
                        int code = getVersionCode(getApplicationContext());
                        int newCode = Integer.valueOf(response);
                        if (code < newCode && PreferenceMngr.getLastAttempt() < newCode) {
                            PreferenceMngr.getInstance().setUpdate(true);
                        } else {
                            PreferenceMngr.getInstance().setUpdate(false);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Cache-Control", "no-cache");

                    return headers;
                }
            };

            requestQueue.add(stringRequest);
        }

        private int getVersionCode(Context context) {
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                return pi.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
