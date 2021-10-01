package com.kibou.abisoyeoke_lawal.coupinapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.SearchActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavFragment extends Fragment implements MyOnClick {
    @BindView(R.id.search_fav)
    public ImageView favSearch;
    @BindView(R.id.fav_empty)
    public LinearLayout favEmpty;
    @BindView(R.id.fav_error)
    public LinearLayout favError;
    @BindView(R.id.fav_recyclerview)
    public RecyclerView favRecyclerView;
    @BindView(R.id.fav_loadingview)
    public RelativeLayout favLoadingView;
    @BindView(R.id.fav_total)
    public TextView favTotal;

    public ArrayList<RewardListItem> favList;
    public RequestQueue requestQueue;
    public RVAdapter rvAdapter;
    public String url;
    public ArrayList<String> responses = new ArrayList<>();

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_fav, container, false);
        ButterKnife.bind(this, root);

        favList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(getContext());
        url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user_favourite);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVAdapter(favList, this, getContext());
        favRecyclerView.setLayoutManager(linearLayoutManager);
        favRecyclerView.setHasFixedSize(true);
        favRecyclerView.setAdapter(rvAdapter);

        favSearch.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        getFavourites();

        return root;
    }

    public void getFavourites() {
        favList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int total = jsonArray.length();
                    favTotal.setText("MERCHANTS - " + total);

                    for (int x = 0; x < total; x++) {
                        JSONObject mainObject = jsonArray.getJSONObject(x);
                        responses.add(mainObject.toString());
                        JSONArray rewardObjects = mainObject.getJSONArray("rewards");

                        RewardListItem item = new RewardListItem();

                        item.setFav(true);
                        item.setMerchantName(mainObject.getString("name"));
                        item.setMerchantAddress(mainObject.getString("address"));
                        item.setMerchantBanner(mainObject.getJSONObject("banner").getString("url"));
                        item.setMerchantLogo(mainObject.getJSONObject("logo").getString("url"));
                        item.setMerchantPhone(mainObject.getString("mobile"));
                        item.setRewardDetails(rewardObjects.toString());
                        item.setRewardCount(rewardObjects.length());
                        item.setVisited(mainObject.getBoolean("visited"));

                        favList.add(item);
                    }

                    rvAdapter.notifyDataSetChanged();
                    if (jsonArray.length() == 0) {
                        favLoadingView.setVisibility(View.GONE);
                        favEmpty.setVisibility(View.VISIBLE);
                    } else {
                        favLoadingView.setVisibility(View.GONE);
                        favRecyclerView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (FavFragment.this.isVisible()) {
                    if (error != null && error.networkResponse.statusCode == 404) {
                        favLoadingView.setVisibility(View.GONE);
                        favEmpty.setVisibility(View.VISIBLE);
                    } else {
                        favLoadingView.setVisibility(View.GONE);
                        favError.setVisibility(View.VISIBLE);
                    }
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MerchantActivity.class);
        Bundle extra = new Bundle();
        extra.putString("merchant", responses.get(position));
        intent.putExtras(extra);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, int quantity) {

    }
}
