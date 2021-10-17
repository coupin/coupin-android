package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * Created by abisoyeoke-lawal on 4/3/18.
 */

public class UpdateService extends Service {
    private static final long NOTIFY_INTERVAL = AlarmManager.INTERVAL_DAY;

    private ApiCalls apiCalls;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        apiCalls = ApiClient.getInstance().getCalls(getApplicationContext(), false);

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
            // Until We Switch Notification Tokens for this
            Call<HashMap<String, String>> request = apiCalls.getLatestVersionNumber();
            request.enqueue(new Callback<HashMap<String, String>>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                    if (response.isSuccessful()) {
                        PreferenceManager.setContext(getApplicationContext());
                        if (response.body() != null) {
                            int code = getVersionCode(getApplicationContext());
                            String newCodeString = response.body().get("version");
                            assert newCodeString != null;
                            int newCode = Integer.parseInt(newCodeString);
                            PreferenceManager.setUpdate(code < newCode && PreferenceManager.getLastAttempt() < newCode);
                        }

                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
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
