package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.models.ListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends Activity {
    @BindView(R.id.merchant_details_textview)
    public TextView merchantDetails;
    @BindView(R.id.merchant_name_textview)
    public TextView merchantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        ArrayList<String> values = new ArrayList<String>();

        try {
            JSONObject res = new JSONObject(extra.getBundle("info").getString("merchant"));
            ListItem item = new ListItem();
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
                values.add(resArray.getJSONObject(x).getString("name"));
            }
            values.add("One");
            values.add("Two");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
