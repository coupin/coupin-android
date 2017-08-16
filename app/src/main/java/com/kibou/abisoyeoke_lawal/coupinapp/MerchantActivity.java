package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVExpandableAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends Activity {
    @BindView(R.id.rewards_recycler_view)
    public RecyclerView rvRewards;
    @BindView(R.id.merchant_details_textview)
    public TextView merchantDetails;
    @BindView(R.id.merchant_name_textview)
    public TextView merchantName;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvExpandableAdapter = new RVExpandableAdapter(values, this);
        rvRewards.setLayoutManager(linearLayoutManager);
        rvRewards.setHasFixedSize(true);
        rvRewards.setAdapter(rvExpandableAdapter);


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
}
