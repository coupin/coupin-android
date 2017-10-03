package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaveFragment extends Fragment implements MyOnClick {
    @BindView(R.id.later_loadingview)
    public AVLoadingIndicatorView laterLoadingView;
    @BindView(R.id.later_recyclerview)
    public RecyclerView laterRecyclerView;

    public ArrayList<RewardListItem> laterList = new ArrayList<>();
    public RequestQueue requestQueue;
    public RVAdapter rvAdapter;
    public String url;


    public SaveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_save_for_later, container, false);
        ButterKnife.bind(this, rootView);

        requestQueue = Volley.newRequestQueue(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVAdapter(laterList, this);
        laterRecyclerView.setLayoutManager(linearLayoutManager);
        laterRecyclerView.setHasFixedSize(true);
        laterRecyclerView.setAdapter(rvAdapter);

        url = getString(R.string.base_url) + getString(R.string.ep_rewards_for_later);

        getRewardsForLater();

        return rootView;
    }

    private void getRewardsForLater() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject mainObject = jsonArray.getJSONObject(x);
                        JSONObject merchantObject = mainObject.getJSONObject("merchantId").getJSONObject("merchantInfo");
                        JSONArray rewardObjects = mainObject.getJSONArray("rewardId");

                        RewardListItem item = new RewardListItem();

                        item.setBookingId(mainObject.getString("_id"));
                        item.setLater(true);
                        item.setMerchantName(merchantObject.getString("companyName"));
                        item.setMerchantAddress(merchantObject.getString("address"));
                        item.setMerchantLogo(merchantObject.getString("logo"));


                        item.setRewardDetails(rewardObjects.toString());

                        laterList.add(item);
                    }
                    rvAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse.statusCode > 400 && error.networkResponse.statusCode < 500) {
//                    try {
//                        JSONObject object = new JSONObject(new String(error.networkResponse.data));
//                        Log.v("VolleyError", object.getString("message"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.v("Volley Error", error.toString());
//                }
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

    @Override
    public void onItemClick(int position) {

    }
}
