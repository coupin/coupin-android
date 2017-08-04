package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RewardListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.RewardInfoDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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
    @BindView(R.id.now_listview)
    public ListView useNowListView;
    @BindView(R.id.now_recyclerview)
    public RecyclerView recyclerView;

    public ArrayList<RewardListItem> nowList = new ArrayList<>();
    public RewardListAdapter adapter;
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


        adapter = new RewardListAdapter(getContext(), nowList);
        useNowListView.setAdapter(adapter);

//        useNowListView.setOnItemClickListener(this);

        url = getString(R.string.base_url) + getString(R.string.ep_rewards_for_now);

        getRewardsForNow();

        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        RewardInfoDialog dialog = new RewardInfoDialog(getContext(), nowList.get(position));
        dialog.show();
    }

    private void getRewardsForNow() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyNow", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.v("VolleyNow", "length " + jsonArray.length());
                    for (int x = 0; x < jsonArray.length(); x++) {
                        JSONObject mainObject = jsonArray.getJSONObject(x);
                        JSONObject merchantObject = mainObject.getJSONObject("merchant");
                        JSONObject rewardObject = mainObject.getJSONObject("reward");

                        RewardListItem item = new RewardListItem();

                        item.setBookingId(mainObject.getString("bookingId"));
                        item.setBookingShortCode(mainObject.getString("shortCode"));
                        item.setExpiresDate(new Date(rewardObject.getLong("expirationDate")));
                        item.setRewardName(rewardObject.getString("name"));
                        item.setRewardDescription(rewardObject.getString("description"));
                        item.setMerchantName(merchantObject.getString("name"));
                        item.setMerchantAddress(merchantObject.getString("address"));
                        item.setMerchantLogo(merchantObject.getString("logo"));

                        nowList.add(item);
                    }
                    rvAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
//                    AnimateUtils.crossFadeViews(useNowListView, loadingView);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
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
