package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVExpandableAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends Activity implements MyOnSelect {
    @BindView(R.id.selected_btn_pin)
    public Button selectedBtnPin;
    @BindView(R.id.selected_btn_save)
    public Button selectedBtnSave;
    @BindView(R.id.button_holder)
    public LinearLayout buttonHolder;
    @BindView(R.id.selected_holder)
    public LinearLayout selectedHolder;
    @BindView(R.id.rewards_recycler_view)
    public RecyclerView rvRewards;
    @BindView(R.id.code)
    public TextView code;
    @BindView(R.id.merchant_details_textview)
    public TextView merchantDetails;
    @BindView(R.id.merchant_name_textview)
    public TextView merchantName;
    @BindView(R.id.selected_text)
    public TextView selectedText;

    public RequestQueue requestQueue;
    String url;

    public RVExpandableAdapter rvExpandableAdapter;
    public ArrayList<Reward> values;
    public ArrayList<String> selected;
    public Merchant item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        values = new ArrayList<>();
        selected = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvExpandableAdapter = new RVExpandableAdapter(values, this, this);
        rvRewards.setLayoutManager(linearLayoutManager);
        rvRewards.setHasFixedSize(true);
        rvRewards.setAdapter(rvExpandableAdapter);

        requestQueue = Volley.newRequestQueue(this);

        selectedBtnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_generate_code);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("VolleyResponse", response);
                            JSONObject tempArray = new JSONObject(response);
                            code.setText(tempArray.getString("shortCode"));
                            selectedText.setVisibility(View.GONE);
                            buttonHolder.setVisibility(View.GONE);
                            code.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("merchantId", item.getId());
                        params.put("rewardId", selected.toString());

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

        try {
            JSONObject res = new JSONObject(extra.getBundle("info").getString("merchant"));
            item = new Merchant();
            item.setId(res.getString("_id"));
            item.setPicture(R.drawable.slide1);
            item.setAddress(res.getString("address"));
            item.setDetails(res.getString("details"));
            item.setEmail(res.getString("email"));
            item.setMobile(res.getString("mobile"));
            item.setTitle(res.getString("name"));
            item.setRewards(res.getJSONArray("rewards"));
            item.setLatitude(res.getJSONObject("location").getDouble("lat"));
            item.setLongitude(res.getJSONObject("location").getDouble("long"));
            merchantName.setText(item.getTitle());
            merchantDetails.setText(item.getAddress());

            if (res.get("picture").toString() != "null") {
//                merchantImage.setImage
            }

            JSONArray resArray = res.getJSONArray("rewards");
            for (int x = 0; x < resArray.length(); x++) {
                Reward reward = new Reward();
                reward.setId(resArray.getJSONObject(x).getString("_id"));
                reward.setTitle(resArray.getJSONObject(x).getString("name"));
                reward.setDetails(resArray.getJSONObject(x).getString("description"));
                if (resArray.getJSONObject(x).has("price")) {
                    reward.setIsDiscount(true);
                    reward.setNewPrice(resArray.getJSONObject(x).getJSONObject("price").getInt("new"));
                    reward.setOldPrice(resArray.getJSONObject(x).getJSONObject("price").getInt("old"));
                } else {
                    reward.setIsDiscount(false);
                }
//                reward.setExpires(new Date(resArray.getJSONObject(x).getString("endDate")));
                values.add(reward);
            }

            rvExpandableAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSelect(boolean selected, int index) {
        if (selected) {
            this.selected.add(values.get(index).getId());
            selectedText.setText(this.selected.size() + " Selected");
            if (selectedHolder.getVisibility() == View.GONE) {
                selectedHolder.setVisibility(View.VISIBLE);
            }
        } else {
            this.selected.remove(values.get(index).getId());
            selectedText.setText(this.selected.size() + " Selected");
            if (this.selected.size() == 0) {
                selectedHolder.setVisibility(View.GONE);
            }
        }
    }
}
