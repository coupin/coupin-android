package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.CoupinActivity;
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
public class UseNowFragment extends Fragment implements MyOnClick {
    @BindView(R.id.now_loadingview)
    public AVLoadingIndicatorView loadingView;
    @BindView(R.id.now_empty)
    public LinearLayout nowEmpty;
    @BindView(R.id.now_error)
    public LinearLayout nowError;
    @BindView(R.id.now_recyclerview)
    public RecyclerView recyclerView;

    public ArrayList<RewardListItem> nowList = new ArrayList<>();
    public RequestQueue requestQueue;
    public RVAdapter rvAdapter;
    public String url;

    public UseNowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_use_now, container, false);
        ButterKnife.bind(this, rootView);

        requestQueue = Volley.newRequestQueue(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVAdapter(nowList, this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rvAdapter);

        url = getString(R.string.base_url) + getString(R.string.ep_get_rewards);

        getRewardsForNow();

        Log.v("VolleyNow", "" + getUserVisibleHint());

        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CoupinActivity.class);
        intent.putExtra("coupin", nowList.get(position));
        startActivity(intent);
    }

    private void getRewardsForNow() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyInsideNow", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject mainObject = jsonArray.getJSONObject(x);
                        JSONObject merchantObject = mainObject.getJSONObject("merchantId").getJSONObject("merchantInfo");
                        JSONArray rewardObjects = mainObject.getJSONArray("rewardId");

                        RewardListItem item = new RewardListItem();

                        item.setBookingId(mainObject.getString("_id"));
                        item.setBookingShortCode(mainObject.getString("shortCode"));
                        item.setMerchantName(merchantObject.getString("companyName"));
                        item.setMerchantAddress(merchantObject.getString("address"));
                        item.setMerchantLogo(merchantObject.getString("logo"));


                        item.setRewardDetails(rewardObjects.toString());

                        nowList.add(item);
                    }
                    rvAdapter.notifyDataSetChanged();
                    if (jsonArray.length() == 0) {
                        loading(2);
                    } else {
                        loading(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 404) {
                        loading(2);
                    } else {
                        loading(3);
                    }
                } else {
                    loading(3);
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

    /**
     * Showing the appropriate view after and before loading
     * @param opt
     */
    public void loading(int opt) {
        switch (opt) {
            case 0:
                recyclerView.setVisibility(View.GONE);
                nowEmpty.setVisibility(View.GONE);
                nowError.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                break;
            case 1:
                loadingView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingView.setVisibility(View.GONE);
                nowEmpty.setVisibility(View.VISIBLE);
                break;
            case 3:
                loadingView.setVisibility(View.GONE);
                nowError.setVisibility(View.VISIBLE);
                break;
            default:
                recyclerView.setVisibility(View.GONE);
                nowEmpty.setVisibility(View.GONE);
                nowError.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
