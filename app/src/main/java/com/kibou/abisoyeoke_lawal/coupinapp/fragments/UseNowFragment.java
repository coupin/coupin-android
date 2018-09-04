package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.CoupinActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
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
    @BindView(R.id.active_loading)
    public AVLoadingIndicatorView bottomLoadingView;
    @BindView(R.id.now_empty)
    public LinearLayout nowEmpty;
    @BindView(R.id.now_error)
    public LinearLayout nowError;
    @BindView(R.id.now_recyclerview)
    public RecyclerView recyclerView;

    private ArrayList<RewardListItem> nowList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 0;
    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    private RequestQueue requestQueue;
    private RVAdapter rvAdapter;
    private String url;

    public UseNowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_use_now, container, false);
        ButterKnife.bind(this, rootView);

        handler = new Handler();
        requestQueue = Volley.newRequestQueue(getContext());

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVAdapter(nowList, this, getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rvAdapter);

        getRewardsForNow();
        implementOnScrollListener();

        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CoupinActivity.class);
        intent.putExtra("coupin", nowList.get(position));
        startActivity(intent);
    }

    private void getRewardsForNow() {
        url = getString(R.string.base_url) + getString(R.string.ep_get_rewards) + "?page=" + page;

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
                        item.setBookingShortCode(mainObject.getString("shortCode"));
                        item.setMerchantName(merchantObject.getString("companyName"));
                        item.setMerchantAddress(merchantObject.getString("address"));
                        item.setMerchantLogo(merchantObject.getJSONObject("logo").getString("url"));
                        item.setMerchantBanner(merchantObject.getJSONObject("banner").getString("url"));
                        item.setLatitude(merchantObject.getJSONArray("location").getDouble(1));
                        item.setLongitude(merchantObject.getJSONArray("location").getDouble(0));
                        item.setRewardDetails(rewardObjects.toString());
                        item.setVisited(mainObject.getBoolean("visited"));
                        item.setFavourited(mainObject.getBoolean("favourite"));


                        item.setRewardDetails(rewardObjects.toString());
                        item.setRewardCount(rewardObjects.length());

                        nowList.add(item);
                    }
                    isLoading = false;
                    if (page > 0) {
                        loading(5);
                    }
                    rvAdapter.notifyDataSetChanged();
                    if (jsonArray.length() == 0) {
                        if (nowList.size() < 1) {
                            loading(2);
                        } else {
                            showErrorToast(true);
                        }
                    } else {
                        loading(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (nowList.size() < 1) {
                        loading(3);
                    } else {
                        showErrorToast(false);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                isLoading = false;
                if (nowList.size() < 1) {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            loading(2);
                        } else {
                            loading(3);
                        }
                    } else {
                        loading(3);
                    }
                } else {
                    showErrorToast(false);
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
     * Show toast instead of changing view
     * @param isEmpty was it an empty return
     */
    public void showErrorToast(Boolean isEmpty) {
        if (isEmpty) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_coupins), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_coupins), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implements scroll listener for the active list
     * Using it to load new coupins
     */
    private void implementOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (nowList.size() % 7) != 0 || nowList.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = nowList.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRewardsForNow();
                        }
                    }, 2000);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        requestQueue.stop();
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
            case 4:
                bottomLoadingView.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoadingView.setVisibility(View.GONE);
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
