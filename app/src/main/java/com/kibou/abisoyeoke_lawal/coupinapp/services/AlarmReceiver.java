package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.HomeActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NotificationScheduler;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceriver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Log.v(TAG, "Boot Completed");
                Calendar calendar = Calendar.getInstance();
                boolean[] options = PreferenceMngr.getNotificationSelection();
                if (options != null && options[0]) {
                    if (options[1]) {
                        calendar.set(Calendar.DAY_OF_WEEK, 6);
                        calendar.set(Calendar.HOUR_OF_DAY, 11);
                        calendar.set(Calendar.MINUTE, 00);
                    } else {
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        calendar.set(Calendar.HOUR_OF_DAY, 11);
                        calendar.set(Calendar.MINUTE, 00);
                    }
                    NotificationScheduler.setReminder(context, AlarmReceiver.class, calendar);
                }
                return;
            }
        }

        Log.v(TAG, "OnReceive: ");
        updateCheck(context);
    }

    private void updateCheck(final Context context) {
        Log.v("Testing", "notification service");
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.base_url)
            + context.getResources().getString(R.string.ep_api_merchant_new);
        StringRequest stringRequest = new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        int total = object.getInt("total");
                        NotificationScheduler.showNotification(context, HomeActivity.class, total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("VolleyError", error.toString());
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("lastChecked", PreferenceMngr.getLastChecked());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }


}
