package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.util.Date;

/**
 * Created by abisoyeoke-lawal on 10/11/17.
 */

public class NotificationService extends IntentService {
    public static final int SERVICE_ID = 3002;

    public NotificationService() {
        super("NotificationService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_merchant_new);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //TODO: The Showing of the notification
//                try {
//                    JSONObject object = new JSONObject(response);
//                    int total = object.getInt("total");

                    NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getApplicationContext());
                    notifBuilder.setSmallIcon(R.drawable.ic_clogo);
                    if (1 > 0) {
//                    if (total > 0) {
                        notifBuilder.setContentTitle("New Coupins!!!");
                        notifBuilder.setContentText("There are " + 1 + " coupins matching your interests around you.");
                    } else {
                        notifBuilder.setContentTitle("Hello There!!!");
                        notifBuilder.setContentText("Keep watching this space for new and upcoming rewards.");
                    }

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(SERVICE_ID, notifBuilder.build());
                    PreferenceMngr.setLastChecked((new Date()).toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("VolleyError", error.toString());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//
//                params.put("lastChecked", PreferenceMngr.getLastChecked());
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", PreferenceMngr.getToken());
//
//                return headers;
//            }
//        };
//
//        requestQueue.add(stringRequest);
    }
}
