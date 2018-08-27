package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVHotAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotActivity extends AppCompatActivity implements View.OnClickListener, MyOnClick {
    @BindView(R.id.slides_loading_view)
    public AVLoadingIndicatorView slidesLoading;
    @BindView(R.id.featured_loading_view)
    public AVLoadingIndicatorView featuredLoading;
    @BindView(R.id.recommended_loading_view)
    public AVLoadingIndicatorView recommendLoading;
    @BindView(R.id.card1)
    public CardView cardView1;
    @BindView(R.id.card2)
    public CardView cardView2;
    @BindView(R.id.card3)
    public CardView cardView3;
    @BindView(R.id.hot_carousel)
    public CarouselView hotCarousel;
    @BindView(R.id.hot_back)
    public ImageView hotBack;
    @BindView(R.id.hot_logo_1)
    public ImageView hotLogo1;
    @BindView(R.id.hot_logo_2)
    public ImageView hotLogo2;
    @BindView(R.id.hot_logo_3)
    public ImageView hotLogo3;
    @BindView(R.id.hotlist_group)
    public LinearLayout featuredHolder;
    @BindView(R.id.hot_recyclerview)
    public RecyclerView hotRecyclerView;
    @BindView(R.id.hot_address_1)
    public TextView hotAddress1;
    @BindView(R.id.hot_address_2)
    public TextView hotAddress2;
    @BindView(R.id.hot_address_3)
    public TextView hotAddress3;
    @BindView(R.id.hot_rewards_1)
    public TextView hotrewards1;
    @BindView(R.id.hot_rewards_2)
    public TextView hotrewards2;
    @BindView(R.id.hot_rewards_3)
    public TextView hotrewards3;
    @BindView(R.id.hot_title_1)
    public TextView hotTitle1;
    @BindView(R.id.hot_title_2)
    public TextView hotTitle2;
    @BindView(R.id.hot_title_3)
    public TextView hotTitle3;

    private ArrayList<Merchant> featured = new ArrayList<>();
    private ArrayList<Merchant> hotlist = new ArrayList<>();
    private ArrayList<Merchant> merchants = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RequestQueue requestQueue;
    private RVHotAdapter adapter;

    public ArrayList slides = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);
        ButterKnife.bind(this);

        requestQueue = PreferenceMngr.getInstance().getRequestQueue();

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RVHotAdapter(merchants, this, this);

        hotRecyclerView.setLayoutManager(linearLayoutManager);
        hotRecyclerView.setHasFixedSize(true);
        hotRecyclerView.setAdapter(adapter);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);

        getPrime();
        getMostRecent();

        hotBack.setOnClickListener(this);
        hotCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                goToMerchantPage(hotlist.get(position));
            }
        });
    }

    /**
     * Determine what loading view to hide
     * @param index
     */
    public void loading(int index) {
        switch (index) {
            case 0:
                slidesLoading.setVisibility(View.GONE);
                hotCarousel.setVisibility(View.VISIBLE);
                break;
            case 1:
                featuredLoading.setVisibility(View.GONE);
                featuredHolder.setVisibility(View.VISIBLE);
                break;
            case 2:
                recommendLoading.setVisibility(View.GONE);
                hotRecyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(HotActivity.this).load(slides.get(position)).into(imageView);
        }
    };

    /**
     * Request prime information from API
     */
    public void getPrime() {
        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_get_prime);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray slideObjects = object.getJSONArray("hotlist");

                    for (int x = 0; x < 3; x++) {
                        JSONObject featuredObject;
                        View view;
                        if (x == 0) {
                            featuredObject = object.getJSONObject("featured").getJSONObject("first");
                        } else if (x == 1) {
                            featuredObject = object.getJSONObject("featured").getJSONObject("second");
                        } else {
                            featuredObject = object.getJSONObject("featured").getJSONObject("third");
                        }

                        Merchant item = new Merchant();
                        item.setId(featuredObject.getString("_id"));
                        item.setBanner(featuredObject.getJSONObject("merchantInfo").getJSONObject("banner").getString("url"));
                        item.setLogo(featuredObject.getJSONObject("merchantInfo").getJSONObject("logo").getString("url"));
                        item.setAddress(featuredObject.getJSONObject("merchantInfo").getString("address") + " " + featuredObject.getJSONObject("merchantInfo").getString("city"));
                        item.setDetails(featuredObject.getJSONObject("merchantInfo").getString("companyDetails"));
                        item.setEmail(featuredObject.getString("email"));
                        item.setMobile(featuredObject.getJSONObject("merchantInfo").getString("mobileNumber"));
                        item.setTitle(featuredObject.getJSONObject("merchantInfo").getString("companyName"));
                        item.setReward(featuredObject.getJSONObject("merchantInfo").getJSONArray("rewards").getJSONObject(0).getString("name"));
                        item.setRewards(featuredObject.getJSONObject("merchantInfo").getJSONArray("rewards").toString());
                        item.setRewardsCount(featuredObject.getJSONObject("merchantInfo").getJSONArray("rewards").length());
                        item.setLatitude(featuredObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(1));
                        item.setLongitude(featuredObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(0));
                        setUpFeatured(x, item);
                        featured.add(item);
                    }
                    loading(1);

                    for (int x = 0; x < slideObjects.length(); x++) {
                        JSONObject slideObject = slideObjects.getJSONObject(x);
                        JSONObject merchantObject = slideObject.getJSONObject("id");

                        slides.add(slideObject.getString("url"));

                        Merchant item = new Merchant();
                        item.setId(merchantObject.getString("_id"));
                        item.setBanner(merchantObject.getJSONObject("merchantInfo").getJSONObject("banner").getString("url"));
                        item.setLogo(merchantObject.getJSONObject("merchantInfo").getJSONObject("logo").getString("url"));
                        item.setAddress(merchantObject.getJSONObject("merchantInfo").getString("address") + " " + merchantObject.getJSONObject("merchantInfo").getString("city"));
                        item.setDetails(merchantObject.getJSONObject("merchantInfo").getString("companyDetails"));
                        item.setEmail(merchantObject.getString("email"));
                        item.setMobile(merchantObject.getJSONObject("merchantInfo").getString("mobileNumber"));
                        item.setTitle(merchantObject.getJSONObject("merchantInfo").getString("companyName"));
                        item.setReward(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").getJSONObject(0).getString("name"));
                        item.setRewards(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").toString());
                        item.setRewardsCount(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").length());
                        item.setLatitude(merchantObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(1));
                        item.setLongitude(merchantObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(0));

                        hotlist.add(item);
                    }

                    hotCarousel.setImageListener(imageListener);
                    hotCarousel.setPageCount(slides.size());
                    loading(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
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

    public void getMostRecent() {
        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_merchant_recent);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray recentArray = new JSONArray(response);
                    for (int x = 0; x < recentArray.length(); x++) {
                        JSONObject merchantObject = recentArray.getJSONObject(x);

                        Merchant item = new Merchant();
                        item.setId(merchantObject.getString("_id"));
                        item.setBanner(merchantObject.getJSONObject("merchantInfo").getJSONObject("banner").getString("url"));
                        item.setLogo(merchantObject.getJSONObject("merchantInfo").getJSONObject("logo").getString("url"));
                        item.setAddress(merchantObject.getJSONObject("merchantInfo").getString("address") + " " + merchantObject.getJSONObject("merchantInfo").getString("city"));
                        item.setDetails(merchantObject.getJSONObject("merchantInfo").getString("companyDetails"));
                        item.setEmail(merchantObject.getString("email"));
                        item.setMobile(merchantObject.getJSONObject("merchantInfo").getString("mobileNumber"));
                        item.setTitle(merchantObject.getJSONObject("merchantInfo").getString("companyName"));
                        item.setReward(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").getJSONObject(0).getString("name"));
                        item.setRewards(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").toString());
                        item.setRewardsCount(merchantObject.getJSONObject("merchantInfo").getJSONArray("rewards").length());
                        item.setLatitude(merchantObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(1));
                        item.setLongitude(merchantObject.getJSONObject("merchantInfo").getJSONArray("location").getDouble(0));

                        merchants.add(item);
                    }

                    adapter.notifyDataSetChanged();
                    loading(2);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(HotActivity.this, "An erro", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyRecentError", error.toString());
                Log.v("VolleyRecentError", "" + error.networkResponse.statusCode);
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
     * Set the featured information
     * @param index
     * @param merchant
     */
    private void setUpFeatured(int index, Merchant merchant) {
        switch (index) {
            case 0:
                hotTitle1.setText(merchant.getTitle());
                hotAddress1.setText(merchant.getAddress());
                String rewardsText1 = merchant.getRewardsCount() == 1 ?
                    merchant.getReward() : merchant.getRewardsCount() + " REWARDS";
                hotrewards1.setText(rewardsText1);
                Glide.with(this).load(merchant.getLogo()).into(hotLogo1);
                break;
            case 1:
                hotTitle2.setText(merchant.getTitle());
                hotAddress2.setText(merchant.getAddress());
                String rewardsText2 = merchant.getRewardsCount() == 1 ?
                    merchant.getReward() : merchant.getRewardsCount() + " REWARDS";
                hotrewards2.setText(rewardsText2);
                Glide.with(this).load(merchant.getLogo()).into(hotLogo2);
                break;
            case 2:
                hotTitle3.setText(merchant.getTitle());
                hotAddress3.setText(merchant.getAddress());
                String rewardsText3 = merchant.getRewardsCount() == 1 ?
                    merchant.getReward() : merchant.getRewardsCount() + " REWARDS";
                hotrewards3.setText(rewardsText3);
                Glide.with(this).load(merchant.getLogo()).into(hotLogo3);
                break;
        }
    }

    public void goToMerchantPage(Merchant merchant) {
        Intent merchantIntent = new Intent(this, MerchantActivity.class);
        Bundle extra = new Bundle();
        extra.putSerializable("object", merchant);
        merchantIntent.putExtras(extra);
        startActivity(merchantIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hot_back:
                onBackPressed();
                break;
            case R.id.card1:
                goToMerchantPage(featured.get(0));
                break;
            case R.id.card2:
                goToMerchantPage(featured.get(1));
                break;
            case R.id.card3:
                goToMerchantPage(featured.get(2));
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.v("VolleyCheck", "" + position);
        goToMerchantPage(merchants.get(position));
    }
}
