package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends Activity {
    @BindView(R.id.merchant_image_imageview)
    public CircularImageView merchantImage;
    @BindView(R.id.merchant_rewards_listview)
    public ListView rewardsListView;
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
            merchantName.setText(res.getString("name"));
            merchantDetails.setText(res.getString("details"));

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        rewardsListView.setAdapter(adapter);
    }
}
