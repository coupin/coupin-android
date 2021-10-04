package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVCoupinAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.BookingResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.InnerItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Prime;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardsListItemV2;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.DateTimeUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

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
    @BindView(R.id.coupin_vertical_divided)
    public View divider;

    private ApiCalls apiCalls;
    private ArrayList<RewardV2> coupinRewards;
    private RewardsListItemV2 coupin;
    private RVCoupinAdapter rvAdapter;
    private Set<String> tempBlackList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        coupin = (RewardsListItemV2) getIntent().getSerializableExtra("coupin");

        boolean activityFromPurchase = getIntent().getBooleanExtra("fromPurchase", false);

        InnerItem.MerchantInfo merchantInfo = coupin.merchant.merchantInfo;
        merchantName.setText(merchantInfo.companyName);
        merchantAddress.setText(merchantInfo.address);
        if (coupin.visited) coupinVisited.setVisibility(View.VISIBLE);
        if (coupin.favourite) coupinFav.setVisibility(View.VISIBLE);
        if (coupin.favourite && coupin.visited) divider.setVisibility(View.VISIBLE);

        Glide.with(this).load(merchantInfo.banner.url).into(merchantBanner);
        Glide.with(this).load(merchantInfo.logo.url).into(merchantLogo);

        coupinRewards = new ArrayList<>();
        tempBlackList.addAll(PreferenceMngr.getBlacklist());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAdapter = new RVCoupinAdapter(coupinRewards, this, this);
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.setAdapter(rvAdapter);

        if (coupin.shortCode == null || coupin.shortCode.equals("")) {
            codeHolder.setVisibility(View.GONE);
            activateHolder.setVisibility(View.VISIBLE);
        }

        naviagteBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);

        ArrayList<RewardsListItemV2.RewardWrapper> res = coupin.rewards;
        String status = coupin.status;

        if(status.equals("awaiting_payment")){
            if(activityFromPurchase){
                listCode.setText("View Coupins");
                listCode.setOnClickListener(v -> {
                    Intent intent = new Intent(CoupinActivity.this, HomeActivity.class);
                    intent.putExtra("fromCoupin", true);
                    startActivity(intent);
                    finishAffinity();
                });
            }else {
                listCode.setText("Awaiting Payment");
            }
        }else {
            listCode.setText(coupin.shortCode);
        }

        int total = res.size();
        listCount.setText("ACTIVE REWARDS - " + total);

        for (RewardsListItemV2.RewardWrapper item : coupin.rewards) {
            coupinRewards.add(item.reward);
        }

        rvAdapter.notifyDataSetChanged();

        listBack.setOnClickListener(v -> onBackPressed());

        activateHolder.setOnClickListener(v -> {
            Call<BookingResponse> request = apiCalls.activateCoupin(coupin.bookingId);
            request.enqueue(new Callback<BookingResponse>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<BookingResponse> call, retrofit2.Response<BookingResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        listCode.setText(response.body().data.booking.shortCode);
                        activateHolder.setVisibility(View.GONE);
                        codeHolder.setVisibility(View.VISIBLE);
                    } else {
                        ApiError error = ApiClient.parseError(response);
                        Toast.makeText(CoupinActivity.this, error.message, Toast.LENGTH_SHORT).show();
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<BookingResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(CoupinActivity.this, getString(R.string.error_general), Toast.LENGTH_SHORT).show();
                }
            });
        });
        reviewApplication();
    }

    private void reviewApplication(){
        ReviewManager reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = reviewManager.launchReviewFlow(CoupinActivity.this, reviewInfo);
                flow.addOnCompleteListener(task1 -> Log.d("CoupinActivity", "Review Success"));
            }else {
                Log.d("CoupinActivity", "review exception : " + task.getException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CoupinActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onItemClick(int position) { }

    @Override
    public void onItemClick(int position, int quantity) { }

    /**
     * Navigate to address
     */
    private void navigate() {
        Intent navigateIntent = new Intent(Intent.ACTION_VIEW);
        double longitude = coupin.merchant.merchantInfo.location[0];
        double latitude = coupin.merchant.merchantInfo.location[1];
        navigateIntent.setData(Uri.parse("geo:" + longitude + "," + latitude +
            "?q=" + latitude + "," + longitude));
        startActivity(navigateIntent);
    }

    /**
     * Share details of coupin
     */
    private void share() {
        String msg = "Coupin rewards for " + StringUtils.capitalize(coupin.merchant.merchantInfo.companyName) + " to get " +
            coupinRewards.get(0).description + "! https://coupinapp.com/";
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
                navigate();
                break;
            case R.id.share:
                share();
                break;
        }
    }
}
