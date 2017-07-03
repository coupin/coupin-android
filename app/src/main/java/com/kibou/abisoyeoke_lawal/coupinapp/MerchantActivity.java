package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends Activity {
    @BindView(R.id.merchant_rewards_listview)
    public ListView rewardsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);

        String values[] = new String[]{"One", "Two", "Three", "Four"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        rewardsListView.setAdapter(adapter);
    }
}
