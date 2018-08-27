package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVBroAdapter;
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
public class SaveFragment extends Fragment implements MyOnClick {
    @BindView(R.id.later_loadingview)
    public AVLoadingIndicatorView laterLoadingView;
    @BindView(R.id.save_loading)
    public AVLoadingIndicatorView bottomLoadingView;
    @BindView(R.id.later_empty)
    public LinearLayout laterEmpty;
    @BindView(R.id.later_error)
    public LinearLayout laterError;
    @BindView(R.id.later_recyclerview)
    public RecyclerView laterRecyclerView;

    public ArrayList<RewardListItem> laterList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 0;
    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    public RequestQueue requestQueue;
    public RVBroAdapter nowRvAdapter;
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
        handler = new Handler();

        linearLayoutManager = new LinearLayoutManager(getContext());
        nowRvAdapter = new RVBroAdapter(laterList, this);
        laterRecyclerView.setLayoutManager(linearLayoutManager);
        laterRecyclerView.setHasFixedSize(true);
        laterRecyclerView.setAdapter(nowRvAdapter);

        getRewardsForLater();
        implementOnScrollListener();

        return rootView;
    }

    private void getRewardsForLater() {
        url = getString(R.string.base_url) + getString(R.string.ep_get_rewards) + "?saved=true&page=" + page;

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
                    isLoading = false;
                    if (page > 0) {
                        loading(5);
                    }
                    nowRvAdapter.notifyDataSetChanged();
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
                isLoading = false;
                if (error != null) {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        loading(2);
                    } else {
                        loading(3);
                    }
                } else {
                    loading(3);
                }

                if (page > 0) {
                    loading(5);
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
            case 1:
                laterLoadingView.setVisibility(View.GONE);
                laterRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                laterLoadingView.setVisibility(View.GONE);
                laterEmpty.setVisibility(View.VISIBLE);
                break;
            case 3:
                laterLoadingView.setVisibility(View.GONE);
                laterError.setVisibility(View.VISIBLE);
                break;
            case 4:
                bottomLoadingView.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoadingView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Implements scroll listener for the saved list
     * Using it to load new coupins
     */
    private void implementOnScrollListener() {
        laterRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (laterList.size() % 7) != 0 || laterList.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = laterList.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRewardsForLater();
                        }
                    }, 2000);
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CoupinActivity.class);
        intent.putExtra("coupin", laterList.get(position));
        startActivity(intent);
    }
}
