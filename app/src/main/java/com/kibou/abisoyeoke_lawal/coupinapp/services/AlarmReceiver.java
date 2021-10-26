package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kibou.abisoyeoke_lawal.coupinapp.activities.HomeActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NotificationScheduler;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceriver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Log.v(TAG, "Boot Completed");
                return;
            }
        }

        Log.v(TAG, "OnReceive: ");
        updateCheck(context);
    }

    private void updateCheck(final Context context) {
        HashMap<String, String> body = new HashMap<>();
        body.put("lastChecked", PreferenceManager.getLastChecked());

        Call<HashMap<String, Integer>> request = ApiClient
                .getInstance()
                .getCalls(context, true)
                .getNewMerchantsCount(body);

        request.enqueue(new Callback<HashMap<String, Integer>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<HashMap<String, Integer>> call, Response<HashMap<String, Integer>> response) {
                if (response.isSuccessful()) {
                    int total = response.body() != null && response.body().containsKey("total") ? response.body().get("total")
                            : 0;
                    NotificationScheduler.showNotification(
                            context,
                            HomeActivity.class,
                            total
                    );
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<HashMap<String, Integer>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
