package com.kibou.abisoyeoke_lawal.coupinapp.services;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private RequestQueue requestQueue;

    @Override
    public void onMessageReceived(RemoteMessage message) { super.onMessageReceived(message); }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PreferenceMngr.setContext(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        sendToServer(token);
    }

    private void sendToServer(final String token) {
        String url = getApplicationContext().getResources().getString(R.string.base_url) +
            getApplicationContext().getResources().getString(R.string.ep_api_user_notifications, PreferenceMngr.getUserId());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Coupin Update", "Notification token updated");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(
                    MyFirebaseMessagingService.this,
                    "Failed to update notification id.",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
