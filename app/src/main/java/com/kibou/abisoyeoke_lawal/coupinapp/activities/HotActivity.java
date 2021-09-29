package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVHotAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.models.LocationV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Prime;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class HotActivity extends AppCompatActivity implements View.OnClickListener, MyOnClick {
    @BindView(R.id.slides_loading_view)
    public AVLoadingIndicatorView slidesLoading;
    @BindView(R.id.featured_loading_view)
    public AVLoadingIndicatorView featuredLoading;
    @BindView(R.id.recommended_loading_view)
    public AVLoadingIndicatorView recommendLoading;
    @BindView(R.id.hot_bottom_loading)
    public AVLoadingIndicatorView bottomLoading;
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
    @BindView(R.id.hot_fav_1)
    public ImageView hotFav1;
    @BindView(R.id.hot_fav_2)
    public ImageView hotFav2;
    @BindView(R.id.hot_fav_3)
    public ImageView hotFav3;
    @BindView(R.id.hot_logo_1)
    public ImageView hotLogo1;
    @BindView(R.id.hot_logo_2)
    public ImageView hotLogo2;
    @BindView(R.id.hot_logo_3)
    public ImageView hotLogo3;
    @BindView(R.id.hot_visited_1)
    public ImageView hotVisited1;
    @BindView(R.id.hot_visited_2)
    public ImageView hotVisited2;
    @BindView(R.id.hot_visited_3)
    public ImageView hotVisited3;
    @BindView(R.id.featured_empty)
    public LinearLayout featuredEmpty;
    @BindView(R.id.hotlist_group)
    public LinearLayout featuredHolder;
    @BindView(R.id.hot_empty)
    public LinearLayout hotEmpty;
    @BindView(R.id.hot_error)
    public LinearLayout hotError;
    @BindView(R.id.hot_recyclerview)
    public RecyclerView hotRecyclerView;
    @BindView(R.id.slides_empty)
    public LinearLayout slidesEmpty;
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

    private ApiCalls apiCalls;
    private ArrayList<MerchantV2> featuredV2 = new ArrayList<>();
    private ArrayList<MerchantV2> hotlistV2 = new ArrayList<>();
    private ArrayList<MerchantV2> merchantsV2 = new ArrayList<>();
    private ArrayList<String> slides = new ArrayList<>();
    private Handler handler = new Handler();
    private LinearLayoutManager linearLayoutManager;
    private RequestQueue requestQueue;
    private RVHotAdapter adapter;
    private Set<String> favourites;

    private boolean isLoading = false;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);
        ButterKnife.bind(this);

        PreferenceMngr.setContext(getApplicationContext());
        apiCalls = ApiClient.getInstance().getCalls(this, true);
        requestQueue = Volley.newRequestQueue(this);

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RVHotAdapter(merchantsV2, this, this);
        try {
            favourites = PreferenceMngr.getFavourites();
        }catch (Exception e){
            e.printStackTrace();
        }

        hotRecyclerView.setLayoutManager(linearLayoutManager);
        hotRecyclerView.setHasFixedSize(true);
        hotRecyclerView.setAdapter(adapter);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);

        getPrime();
        getMostRecentV2();
        implementOnScrollListener();

        hotBack.setOnClickListener(this);
        hotCarousel.setImageClickListener(position -> goToMerchantV2Page(hotlistV2.get(position)));
    }

    /**
     * Determine what loading view to hide
     * @param index
     */
    public void loading(int index) {
        switch (index) {
            case 0:
                slidesEmpty.setVisibility(View.GONE);
                slidesLoading.setVisibility(View.GONE);
                hotCarousel.setVisibility(View.VISIBLE);
                break;
            case 1:
                featuredEmpty.setVisibility(View.GONE);
                featuredLoading.setVisibility(View.GONE);
                featuredHolder.setVisibility(View.VISIBLE);
                break;
            case 2:
                hotError.setVisibility(View.GONE);
                recommendLoading.setVisibility(View.GONE);
                hotRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 3:
                hotCarousel.setVisibility(View.GONE);
                slidesLoading.setVisibility(View.GONE);
                slidesEmpty.setVisibility(View.VISIBLE);
                featuredHolder.setVisibility(View.GONE);
                featuredLoading.setVisibility(View.GONE);
                featuredEmpty.setVisibility(View.VISIBLE);
//                recommendLoading.setVisibility(View.GONE);
//                hotEmpty.setVisibility(View.VISIBLE);
                break;
            case 4:
                recommendLoading.setVisibility(View.GONE);
                hotError.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoading.setVisibility(View.VISIBLE);
                break;
            case 6:
                bottomLoading.setVisibility(View.GONE);
                break;
            case 7:
                featuredLoading.setVisibility(View.GONE);
                featuredHolder.setVisibility(View.GONE);
                featuredEmpty.setVisibility(View.VISIBLE);
                break;
            case 8:
                recommendLoading.setVisibility(View.GONE);
                hotRecyclerView.setVisibility(View.GONE);
                hotEmpty.setVisibility(View.VISIBLE);
                break;
        }
    }

    ImageListener imageListener = (position, imageView) -> Glide.with(HotActivity.this).load(slides.get(position))
        .apply(RequestOptions.fitCenterTransform()).into(imageView);

    /**
     * Request Prime V2
     */
    public void getPrime() {
        Call<Prime> request = apiCalls.getFavouriteMerchants(page);
        request.enqueue(new Callback<Prime>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<Prime> call, retrofit2.Response<Prime> response) {
                if (response.isSuccessful()) {
                    Prime prime = response.body();

                    // Sort Featured Merchant Section
                    for (int x = 0; x < 3; x++) {
                        if (x == 0 && prime.featured.first != null) {
                            MerchantV2 merchantV2 = convertInnerItemToMerchantV2(prime.featured.first, prime.visited.first);
                            setUpFeaturedV2(x, merchantV2);
                            featuredV2.add(merchantV2);
                        } else if (x == 1 && prime.featured.second != null) {
                            MerchantV2 merchantV2 = convertInnerItemToMerchantV2(prime.featured.second, prime.visited.second);
                            setUpFeaturedV2(x, merchantV2);
                            featuredV2.add(merchantV2);
                        } else if (x == 2 && prime.featured.third != null) {
                            MerchantV2 merchantV2 = convertInnerItemToMerchantV2(prime.featured.third, prime.visited.third);
                            setUpFeaturedV2(x, merchantV2);
                            featuredV2.add(merchantV2);
                        }
                    }

                    loading(prime.featured.first != null ? 1 : 7);

                    for (Prime.HotList hotList : prime.hotList) {
                        slides.add(hotList.url);
                        hotlistV2.add(convertInnerItemToMerchantV2(hotList.merchant, false));
                    }
                    hotCarousel.setImageListener(imageListener);
                    hotCarousel.setPageCount(slides.size());
                    loading(0);

                } else {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(HotActivity.this, error.message, Toast.LENGTH_SHORT).show();
                    loading(error.statusCode == 404 ? 7 : 3);
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<Prime> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(HotActivity.this,
                        "Something went wrong while getting your featured information.",
                        Toast.LENGTH_SHORT).show();
                HotActivity.this.onBackPressed();
            }
        });
    }

    /**
     * Convert inner item into merchant v2
     * @param item
     * @param visited
     * @return
     */
    private MerchantV2 convertInnerItemToMerchantV2(Prime.InnerItem item, boolean visited) {
        MerchantV2 merchant = new MerchantV2();
        Prime.MerchantInfo merchantInfo = item.merchantInfo;

        merchant.banner = merchantInfo.banner;
        merchant.logo = merchantInfo.logo;
        merchant.address = merchantInfo.address;
        merchant.details = merchantInfo.companyDetails;
        merchant.email = item.email;
        merchant.mobile = merchantInfo.mobileNumber;
        merchant.title = merchantInfo.companyName;
        if (merchantInfo.rewards != null && merchantInfo.rewards.size() > 0) {
            merchant.reward = merchantInfo.rewards.get(0);
            merchant.rewards = merchantInfo.rewards;
            merchant.rewardsCount = merchantInfo.rewards.size();
        } else {
            merchant.rewardsCount = 0;
        }
        merchant.rating = merchantInfo.rating.value;
        merchant.location = new LocationV2();
        merchant.location.longitude = merchantInfo.location[0];
        merchant.location.latitude = merchantInfo.location[1];

        merchant.id = item.id;
        merchant.favourite = favourites.contains(item.id);
        merchant.visited = visited;

        return merchant;
    }

    /**
     * Get most recent merchants
     */
    public void getMostRecentV2() {
        Call<ArrayList<Prime.InnerItem>> request = apiCalls.getMostRecentMerchants(page);
        request.enqueue(new Callback<ArrayList<Prime.InnerItem>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ArrayList<Prime.InnerItem>> call, retrofit2.Response<ArrayList<Prime.InnerItem>> response) {
                if (response.isSuccessful()) {
                    for (Prime.InnerItem item : response.body()) {
                        MerchantV2 merchantV2 = convertInnerItemToMerchantV2(item, item.visited);
                        merchantV2.favourite = item.favourite;
                        merchantsV2.add(merchantV2);
                    }

                    isLoading = false;
                    adapter.notifyDataSetChanged();
                    if (merchantsV2.size() > 0) {
                        loading(2);
                    } else {
                        loading(8);
                    }

                    if (page > 0) {
                        loading(6);
                    }
                } else {
                    isLoading = false;
                    ApiError error = ApiClient.parseError(response);

                    if (page > 0) {
                        loading(6);
                    } else {
                        loading(error.statusCode == 404 ? 8 : 4);
                    }
                    Toast.makeText(HotActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ArrayList<Prime.InnerItem>> call, Throwable t) {
                isLoading = false;
                if (page > 0) {
                    loading(6);
                    Toast.makeText(HotActivity.this,
                            "Something went wrong while getting your featured information.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    loading(4);
                }
            }
        });
    }

    /**
     * Implements scroll listener for the search list
     * Using it to load more merchants
     */
    private void implementOnScrollListener() {
        hotRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (merchantsV2.size() % 10) != 0 || merchantsV2.size() < 10)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(5);
                    page = merchantsV2.size() / 10;
                    handler.postDelayed(() -> getMostRecentV2(), 2000);
                }
            }
        });
    }

    /**
     * Set the featured information
     * @param index
     * @param merchant MerchantV2
     */
    private void setUpFeaturedV2(int index, MerchantV2 merchant) {
        switch (index) {
            case 0:
                hotTitle1.setText(merchant.title);
                hotAddress1.setText(merchant.address);
                String rewardsText1 = "";
                if (merchant.rewardsCount == 1) {
                    rewardsText1 = merchant.rewards.get(0).name;
                } else if (merchant.rewardsCount > 1) {
                    rewardsText1 = merchant.rewardsCount + " REWARDS";
                } else {
                    rewardsText1 = "No Rewards";
                }
                hotrewards1.setText(rewardsText1);
                if (merchant.visited) {
                    hotVisited1.setVisibility(View.VISIBLE);
                }
                if(merchant.favourite) {
                    hotFav1.setVisibility(View.VISIBLE);
                }
                Glide.with(this).load(merchant.logo.url).into(hotLogo1);
                break;
            case 1:
                hotTitle2.setText(merchant.title);
                hotAddress2.setText(merchant.address);
                String rewardsText2 = "";
                if (merchant.rewardsCount == 1) {
                    rewardsText2 = merchant.rewards.get(0).name;
                } else if (merchant.rewardsCount > 1) {
                    rewardsText2 = merchant.rewardsCount + " REWARDS";
                } else {
                    rewardsText2 = "No Rewards";
                }
                hotrewards2.setText(rewardsText2);
                if (merchant.visited) {
                    hotVisited2.setVisibility(View.VISIBLE);
                }
                if(merchant.favourite) {
                    hotFav2.setVisibility(View.VISIBLE);
                }
                Glide.with(this).load(merchant.logo.url).into(hotLogo2);
                break;
            case 2:
                hotTitle3.setText(merchant.title);
                hotAddress3.setText(merchant.address);
                String rewardsText3 = "";
                if (merchant.rewardsCount == 1) {
                    rewardsText3 = merchant.rewards.get(0).name;
                } else if (merchant.rewardsCount > 1) {
                    rewardsText3 = merchant.rewardsCount + " REWARDS";
                } else {
                    rewardsText3 = "No Rewards";
                }
                hotrewards3.setText(rewardsText3);
                if (merchant.visited) {
                    hotVisited3.setVisibility(View.VISIBLE);
                }
                if(merchant.favourite) {
                    hotFav3.setVisibility(View.VISIBLE);
                }
                Glide.with(this).load(merchant.logo.url).into(hotLogo3);
                break;
        }
    }

    /**
     * Navigate user to merchant page
     * @param merchant
     */
    public void goToMerchantV2Page(MerchantV2 merchant) {
        Intent merchantIntent = new Intent(this, MerchantActivity.class);
        Bundle extra = new Bundle();
        extra.putString("merchant", TypeUtils.objectToString(merchant));
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
                goToMerchantV2Page(featuredV2.get(0));
                break;
            case R.id.card2:
                goToMerchantV2Page(featuredV2.get(1));
                break;
            case R.id.card3:
                goToMerchantV2Page(featuredV2.get(2));
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requestQueue.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
    }

    @Override
    public void onItemClick(int position) {
        goToMerchantV2Page(merchantsV2.get(position));
    }

    @Override
    public void onItemClick(int position, int quantity) { }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
