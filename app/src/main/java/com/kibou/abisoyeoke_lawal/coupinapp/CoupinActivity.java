package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
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
import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVCoupinAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.DateTimeUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.StringUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoupinActivity extends AppCompatActivity implements MyOnClick, View.OnClickListener {
    @BindView(R.id.navigate)
    public FloatingActionButton naviagteBtn;
    @BindView(R.id.share)
    public FloatingActionButton shareBtn;
    @BindView(R.id.list_back)
    public ImageButton listBack;
    @BindView(R.id.list_logo)
    public ImageView merchantLogo;
    @BindView(R.id.coupin_fav)
    public ImageView coupinFav;
    @BindView(R.id.coupin_visited)
    public ImageView coupinVisited;
    @BindView(R.id.coupin_banner)
    public ImageView merchantBanner;
    @BindView(R.id.coupin_activate_holder)
    public LinearLayout activateHolder;
    @BindView(R.id.coupin_code_holder)
    public LinearLayout codeHolder;
    @BindView(R.id.list_view)
    public RecyclerView listView;
    @BindView(R.id.list_toolbar)
    public RelativeLayout listToolbar;
    @BindView(R.id.list_code)
    public TextView listCode;
    @BindView(R.id.list_count)
    public TextView listCount;
    @BindView(R.id.list_merchant_name)
    public TextView merchantName;
    @BindView(R.id.list_merchant_address)
    public TextView merchantAddress;

    ArrayList<Reward> coupinRewards;
    RewardListItem coupin;
    RVCoupinAdapter rvAdapter;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);

        coupin = (RewardListItem) getIntent().getSerializableExtra("coupin");

        merchantName.setText(coupin.getMerchantName());
        merchantAddress.setText(coupin.getMerchantAddress());
        if (coupin.hasVisited()) {
            coupinVisited.setVisibility(View.VISIBLE);
        }
        if (coupin.isFav()) {
            coupinFav.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(coupin.getMerchantBanner()).into(merchantBanner);
        Glide.with(this).load(coupin.getMerchantLogo()).into(merchantLogo);

        coupinRewards = new ArrayList<>();

//        Glide.with(this).load("http://res.cloudinary.com/saintlawal/image/upload/v1510416300/Mask_Group_3_iv3arp.png").into(merchantBanner);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAdapter = new RVCoupinAdapter(
            coupinRewards, this, this);
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.setAdapter(rvAdapter);

        if (coupin.getBookingShortCode() == null || coupin.getBookingShortCode().equals("")) {
            codeHolder.setVisibility(View.GONE);
            activateHolder.setVisibility(View.VISIBLE);
        }

        naviagteBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        try {
            JSONArray res = new JSONArray(coupin.getRewardDetails());
            listCode.setText(coupin.getBookingShortCode());

            int total = res.length();
            listCount.setText("ACTIVE REWARDS - " + total);

            for(int x = 0; x < total; x++) {
                JSONObject object = res.getJSONObject(x).getJSONObject("id");

                Reward reward = new Reward();
                reward.setId(object.getString("_id"));
                reward.setTitle(object.getString("name"));
                reward.setDetails(object.getString("description"));

                if (object.has("price")) {
                    reward.setIsDiscount(true);
                    reward.setNewPrice(object.getJSONObject("price").getInt("new"));
                    reward.setOldPrice(object.getJSONObject("price").getInt("old"));
                } else {
                    reward.setIsDiscount(false);
                }

                reward.setExpires(DateTimeUtils.convertZString(object.getString("endDate")));
                reward.setStarting(DateTimeUtils.convertZString(object.getString("startDate")));

                // Multiple Use details
                if (object.getJSONObject("multiple").getBoolean("status")) {
                    reward.setMultiple(true);
                } else {
                    reward.setMultiple(false);
                }

                // Applicable days
                reward.setDays(object.getJSONArray("applicableDays"));

                coupinRewards.add(reward);
            }

            rvAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        activateHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_rewards_use_saved).replace("id", coupin.getBookingId());

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            listCode.setText(object.getString("shortCode"));
                            activateHolder.setVisibility(View.GONE);
                            codeHolder.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", coupin.getBookingId());

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
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CoupinActivity.this, HomeActivity.class));
    }

    @Override
    public void onItemClick(int position) {

    }

    /**
     * Navigate to address
     */
    private void naviage() {
        String parsedAddress = coupin.getMerchantAddress().replace(" ", "+");
        parsedAddress = parsedAddress.replace(",", "");

        Intent navigateIntent = new Intent(Intent.ACTION_VIEW);
        navigateIntent.setData(Uri.parse("geo:" + coupin.getLatitude() + "," + coupin.getLongitude() +
            "?q=" + coupin.getLatitude() + "," + coupin.getLongitude()));
        startActivity(navigateIntent);
    }

    /**
     * Share details of coupin
     */
    private void share() {
        String msg = "Coupin rewards for " + StringUtils.capitalize(coupin.getMerchantName()) + " to get " +
            coupinRewards.get(0).getDetails() + "! https://www.example.com/tobe/replacedby/reallink";
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Coupin Share!");
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(sendIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigate:
                naviage();
                break;
            case R.id.share:
                share();
                break;
        }
    }
}
