package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVCoupinAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoupinActivity extends AppCompatActivity implements MyOnClick {
    @BindView(R.id.list_back)
    public ImageButton listBack;
    @BindView(R.id.list_view)
    public RecyclerView listView;
    @BindView(R.id.list_toolbar)
    public RelativeLayout listToolbar;
    @BindView(R.id.list_code)
    public TextView listCode;

    ArrayList<Reward> coupinRewards;
    RewardListItem coupin;
    RVCoupinAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        coupin = (RewardListItem) getIntent().getSerializableExtra("coupin");
        coupinRewards = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAdapter = new RVCoupinAdapter(
            coupinRewards, this);
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.setAdapter(rvAdapter);

        try {
            JSONArray res = new JSONArray(coupin.getRewardDetails());
            listCode.setText(coupin.getBookingShortCode());
            Log.v("VolleyCheck", coupin.getRewardDetails());
            for(int x = 0; x < res.length(); x++) {
                JSONObject object = res.getJSONObject(x);
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
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CoupinActivity.this, HomeActivity.class));
    }

    @Override
    public void onItemClick(int position) {

    }
}
