package com.kibou.abisoyeoke_lawal.coupinapp.Services;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

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
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

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
                    Log.v("VolleyUpdate", response);
                    int code = getVersionCode(getApplicationContext());
                    int newCode = Integer.valueOf(response);
                    if (code < newCode && PreferenceMngr.getLastAttempt() < newCode) {
                        PreferenceMngr.setUpdate(true);
                    } else {
                        PreferenceMngr.setUpdate(false);
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

            PreferenceMngr.getInstance().getRequestQueue().add(stringRequest);
        }

        private int getVersionCode(Context context) {
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                return pi.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
