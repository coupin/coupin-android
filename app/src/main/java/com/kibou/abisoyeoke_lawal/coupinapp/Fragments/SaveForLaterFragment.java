package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveForLaterFragment extends Fragment {
    public String url;
    public RequestQueue requestQueue;


    public SaveForLaterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_save_for_later, container, false);

        requestQueue = Volley.newRequestQueue(getContext());

        url = getString(R.string.base_url) + getString(R.string.ep_rewards_for_later);

        getRewardsForLater();

        return rootView;
    }

    private void getRewardsForLater() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyNow", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode > 400 && error.networkResponse.statusCode < 500) {
                    try {
                        JSONObject object = new JSONObject(new String(error.networkResponse.data));
                        Log.v("VolleyError", object.getString("message"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v("Volley Error", error.toString());
                }
            }
        }) {
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
