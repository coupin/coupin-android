package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVPopUpAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.ExperienceDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.GalleryDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.RewardInfoDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.DateTimeUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.blackListIntent;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.expiryDateIntent;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.intentExtraGoToPayment;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.merchantIntent;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.rewardObjectsIntent;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.rewardQuantityIntent;
import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.rewardsIntent;

public class MerchantActivity extends AppCompatActivity implements MyOnSelect, MyOnClick, View.OnClickListener {
    @BindView(R.id.rewards_loading)
    public AVLoadingIndicatorView bottomLoadingView;
    @BindView(R.id.rewards_loadingview)
    AVLoadingIndicatorView loadingRewards;
    @BindView(R.id.selected_btn_pin)
    public Button selectedBtnPin;
    @BindView(R.id.selected_btn_save)
    public Button selectedBtnSave;
    @BindView(R.id.merchant_navigation)
    public FloatingActionButton navigationBtn;
    @BindView(R.id.banner_holder)
    public ImageView bannerHolder;
    @BindView(R.id.photo_1)
    public ImageView photo1;
    @BindView(R.id.photo_2)
    public ImageView photo2;
    @BindView(R.id.photo_3)
    public ImageView photo3;
    @BindView(R.id.photo_4)
    public ImageView photo4;
    @BindView(R.id.button_holder)
    public LinearLayout buttonHolder;
    @BindView(R.id.reward_empty)
    public LinearLayout EmptyHolder;
    @BindView(R.id.reward_error)
    public LinearLayout ErrorHolder;
    @BindView(R.id.selected_holder)
    public LinearLayout selectedHolder;
    @BindView(R.id.merchant_rating)
    public RatingBar merchantRating;
    @BindView(R.id.rewards_recycler_view)
    public RecyclerView rvRewards;
    @BindView(R.id.merchant_address_textview)
    public TextView merchantAddress;
    @BindView(R.id.merchant_phone_textview)
    public TextView merchantPhone;
    @BindView(R.id.merchant_name_textview)
    public TextView merchantName;
    @BindView(R.id.rating_text)
    public TextView ratingText;
    @BindView(R.id.selected_text)
    public TextView selectedText;
    @BindView(R.id.merchant_toolbar)
    public Toolbar merchantToolbar;

    public ExperienceDialog experienceDialog;
    public RewardInfoDialog infoDialog;
    public RequestQueue requestQueue;
    String url;

    private ArrayList<Date> expiryDates;
    private Set<String> tempBlackList = new HashSet<>();
    private Set<Reward> selectedRewards = new HashSet<>();
    //    private ArrayList<String> favourites;
    private ArrayList<String> pictures;
    private ArrayList<String> selected;
    private ArrayList<Reward> values;
    private boolean favourite = false;
    private boolean isLoading = false;
    private boolean requestGenderNumber = false;
    private Date expiryDate;
    private GalleryDialog imageDialog;
    private Handler handler;
    private int page = 0;
    private JSONArray userFavourites;
    private LinearLayoutManager linearLayoutManager;
//    private Merchant item;
    private MerchantV2 item;
    private JSONArray resArray;
    private JSONObject res;
    private JSONObject user;
    private RVPopUpAdapter rvPopUpAdapter;
    private Set<String> favourites;
    private String merchantId;
    private String rewardHolder;
    private String logTag = "MerchantActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);

        setSupportActionBar(merchantToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        photo1.setOnClickListener(this);
        photo2.setOnClickListener(this);
        photo3.setOnClickListener(this);
        photo4.setOnClickListener(this);

        expiryDates = new ArrayList<>();
        pictures = new ArrayList<>();
        selected = new ArrayList<>();
        values = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        rvPopUpAdapter = new RVPopUpAdapter(values, this, this, false);
        rvRewards.setLayoutManager(linearLayoutManager);
        rvRewards.setHasFixedSize(true);
        rvRewards.setAdapter(rvPopUpAdapter);

        Bundle extra = getIntent().getExtras();
        try {
            assert extra != null;
            item = (MerchantV2) TypeUtils.stringToObject(extra.getString("merchant"));

            assert item != null;
            merchantId = item.id;
            Glide.with(this).load(item.banner.url).into(bannerHolder);
            merchantName.setText(item.title);
            merchantAddress.setText(item.address);
            String merchantPhoneString = "Tel: " + item.mobile;
            merchantPhone.setText(merchantPhoneString);
            merchantRating.setRating(item.rating);
            String ratingString = String.valueOf(item.rating) + "/5";
            ratingText.setText(ratingString);

            user = new JSONObject(PreferenceMngr.getUser());
            tempBlackList.addAll(PreferenceMngr.getInstance().getBlacklist());
            rvPopUpAdapter.setBlacklist(tempBlackList);

            // Show Mobile and Gender Dialog
            if (PreferenceMngr.getToTotalCoupinsGenerated(user.getString("_id")) > 0
                && (!user.has("mobileNumber") || !user.has("ageRange"))) {
                experienceDialog = new ExperienceDialog(this, this, user);
                requestGenderNumber = true;
                experienceDialog.show();
            }

            favourites = PreferenceMngr.getInstance().getFavourites();
            if (favourites.contains(item.id)) {
                favourite = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        implementOnScrollListener();
        loadRewards();

        infoDialog = new RewardInfoDialog(this, this);
        merchantPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = item.mobile;
                if (!"0".equals(String.valueOf(phone.charAt(0)))) {
                    phone = "+234" + phone;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        handler = new Handler();

        navigationBtn.setOnClickListener(view -> navigate());

        selectedBtnPin.setOnClickListener(v -> {
            if (requestGenderNumber) {
                experienceDialog.show();
            } else {
                if(!expiryDates.isEmpty()){
                    expiryDate = expiryDates.get(0);
                    for (int i = 1; i < expiryDates.size(); i++) {
                        if (expiryDates.get(i).after(expiryDate)) {
                            expiryDate = expiryDates.get(i);
                        }
                    }
                    try {
                        getCoupin(expiryDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        selectedBtnSave.setOnClickListener(v -> {
            toggleClickableButtons(true);
            url = getResources().getString(R.string.base_url)
                + getResources().getString(R.string.ep_generate_code)
                + "?saved=true";

            if(!expiryDates.isEmpty()){
                expiryDate = expiryDates.get(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MerchantActivity.super.onBackPressed();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toggleClickableButtons(false);
                        error.printStackTrace();
                        Toast.makeText(
                                MerchantActivity.this,
                                getResources().getString(R.string.error_saved_failed),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("merchantId", item.id);
                        params.put("rewardId", selected.toString());
                        params.put("useNow", String.valueOf(false));
                        params.put("expiryDate", expiryDate.toString());

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

    /**
     * Navigate to restaurant
     */
    private void navigate() {
        String parsedAddress = item.address.replace(" ", "+");
        parsedAddress = parsedAddress.replace(",", "");

        Intent navigateIntent = new Intent(Intent.ACTION_VIEW);
        navigateIntent.setData(Uri.parse("geo:" + item.location.latitude + "," + item.location.latitude +
            "?q=" + item.location.latitude + "," + item.location.latitude));
        startActivity(navigateIntent);
    }

    /**
     * Load Merchant Rewards
     */
    public void loadRewards() {
        String rewardUrl = getString(R.string.base_url) + getString(R.string.ep_api_reward) + "/" +
            merchantId + "?page=" + page;

        Log.d(logTag, "get request url : " + rewardUrl);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, rewardUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    resArray = new JSONArray(response);
                    Log.d(logTag, "rewards resArray: " + resArray);
                    for (int x = 0; x < resArray.length(); x++) {
                        Reward reward = new Reward();
                        JSONObject object = resArray.getJSONObject(x);
                        reward.setId(object.getString("_id"));
                        reward.setTitle(object.getString("name"));
                        reward.setDetails(object.getString("description"));

                        if(object.has("quantity")){
                            reward.setQuantity(object.getInt("quantity"));
                        }else {
                            reward.setQuantity(1);
                        }

                        // Date
                        reward.setExpires(DateTimeUtils.convertZString(object.getString("endDate")));
                        reward.setStarting(DateTimeUtils.convertZString(object.getString("startDate")));

                        // Price Details
                        if (object.has("price")
                            && !object.getJSONObject("price").isNull("new")
                            && !object.getJSONObject("price").isNull("old")) {
                            reward.setIsDiscount(true);
                            reward.setNewPrice(object.getJSONObject("price").getInt("new"));
                            reward.setOldPrice(object.getJSONObject("price").getInt("old"));
                        } else {
                            reward.setIsDiscount(false);
                        }

                        // Multiple Use details
                        if (object.getJSONObject("multiple").getBoolean("status")) {
                            reward.setMultiple(true);
                        } else {
                            reward.setMultiple(false);
                        }

                        reward.setIsDelivery(object.getBoolean("delivery"));

                        // Applicable days
                        reward.setDays(object.getJSONArray("applicableDays"));

                        if (object.has("pictures") && !object.isNull("pictures")) {
                            JSONArray jsonPictures = object.getJSONArray("pictures");
                            reward.setPictures(jsonPictures);
                            for(int j = 0; j < jsonPictures.length(); j++) {
                                pictures.add(jsonPictures.getJSONObject(j).getString("url"));
                            }
                        }

                        values.add(reward);
                    }
                    Log.d(logTag, "rewards values: " + values);
                    isLoading = false;
                    if (page == 0) {
                        if (values.size() == 0) {
                            toggleViews(1);
                        } else {
                            toggleViews(0);
                        }

                        if (pictures.size() > 0) {
                            setupRandomImages();
                        }
                    }

                    if (page > 0) {
                        bottomLoading(false);
                    }
                    rvPopUpAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (page == 0) {
                        toggleViews(2);
                    } else {
                        showErrorToast(false);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                isLoading = false;
                if (page > 0) {
                    bottomLoading(false);
                }

                if (page == 0) {
                    if (values.size() == 0) {
                        toggleViews(2);
                    } else {
                        showErrorToast(false);
                    }
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("page", String.valueOf(page));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                Log.d(logTag, "headers Authorization: " + PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Set random images from rewards
     */
    private void setupRandomImages() {
        ArrayList<Integer> indexes = new ArrayList<>();
        int max = pictures.size();
        int min = 0;
        int range = max - 1 - min + 1;

        if (max >= 4) {
            int dim = (int)getResources().getDimension(R.dimen.image_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dim,
                dim,
                1.0f
            );
            photo1.setLayoutParams(params);
            photo2.setLayoutParams(params);
            photo3.setLayoutParams(params);
            photo4.setLayoutParams(params);
        }

        for (int k = 0; k < 4 && k < max; k++) {
            int index = 0;

            while (indexes.contains(index)) {
                index = (int) (Math.random() * range) + min;
            }

            indexes.add(index);

            switch (k) {
                case 0:
                    Glide.with(MerchantActivity.this).load(pictures.get(index)).into(photo1);
                    photo1.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    Glide.with(MerchantActivity.this).load(pictures.get(index)).into(photo2);
                    photo2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    Glide.with(MerchantActivity.this).load(pictures.get(index)).into(photo3);
                    photo3.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    Glide.with(MerchantActivity.this).load(pictures.get(index)).into(photo4);
                    photo4.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    /**
     * Show toast instead of changing view
     * @param isEmpty was it an empty return
     */
    public void showErrorToast(Boolean isEmpty) {
        if (isEmpty) {
            Toast.makeText(this, getResources().getString(R.string.empty_reward), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_reward), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implements scroll listener for the active list
     * Using it to load new coupins
     */
    private void implementOnScrollListener() {
        rvRewards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (values.size() % 10) != 0 || values.size() < 10)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    bottomLoading(true);
                    page = values.size() / 10;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadRewards();
                        }
                    }, 2000);
                }
            }
        });
    }

    /**
     * Determine whether or not to show bottom loading view
     * @param b
     */
    private void bottomLoading(boolean b) {
        if (b) {
            bottomLoadingView.setVisibility(View.VISIBLE);
        } else {
            bottomLoadingView.setVisibility(GONE);
        }
    }

    public void toggleViews(int opt) {
        switch (opt) {
            case 0:
                loadingRewards.setVisibility(GONE);
                rvRewards.setVisibility(View.VISIBLE);
                break;
            case 1:
                loadingRewards.setVisibility(GONE);
                EmptyHolder.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingRewards.setVisibility(GONE);
                ErrorHolder.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Override onBackPressed to show dialog on first try
     */
    @Override
    public void onBackPressed() {
        if (selected.size() > 0) {
            infoDialog.show();
        } else {
            if (infoDialog != null && infoDialog.isShowing()) {
                infoDialog.dismiss();
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onSelect(boolean selected, int index) {
        if (index == -1) {
            Toast.makeText(MerchantActivity.this, "Sorry this reward can only be used once. ", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Custom on select being used for the recycler view that covers
     * rewards selected and unselected
     * @param selected
     * @param index
     */
    @Override
    public void onSelect(boolean selected, int index, int quantity) {
        Reward reward = values.get(index);
        if (selected) {
            this.selected.add(reward.getId());
            this.expiryDates.add(reward.getExpires());
            selectedText.setText(this.selected.size() + " Items Selected");
            if (selectedHolder.getVisibility() == GONE) {
                selectedHolder.setVisibility(View.VISIBLE);
            }
            if (!reward.getMultiple()) {
                tempBlackList.add(reward.getId());
            }
            selectedRewards.add(reward);
            reward.setSelectedQuantity(quantity);
            reward.setIsSelected(true);
        } else {
            this.selected.remove(reward.getId());
            this.expiryDates.remove(reward.getExpires());
            selectedText.setText(this.selected.size() + " Items Selected");
            if (this.selected.size() == 0) {
                selectedHolder.setVisibility(GONE);
            }
            if (tempBlackList.contains(reward.getId())) {
                tempBlackList.remove(reward.getId());
            }
            if(selectedRewards.contains(reward)){
                selectedRewards.remove(reward);
            }
            reward.setIsSelected(false);
            reward.setSelectedQuantity(1);
        }
    }

    @Override
    public void onItemClick(int position, int quantity) { }

    /**
     * Custom on item click
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        if (position == -2) {
            requestGenderNumber = false;
            return;
        } else if (position == 0) {
            infoDialog.dismiss();
        } else if (position == 1) {
            this.selected.clear();
            onBackPressed();
        } else {
            try {
                if(!expiryDates.isEmpty()){
                    expiryDate = expiryDates.get(0);
                    for (int i = 1; i < expiryDates.size(); i++) {
                        if (expiryDates.get(i).after(expiryDate)) {
                            expiryDate = expiryDates.get(i);
                        }
                    }
                    getCoupin(expiryDate);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Override on creation to make due for favourites
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.merchant_menu, menu);

        MenuItem favTrue = menu.findItem(R.id.merchant_fav_yes);
        MenuItem favFalse = menu.findItem(R.id.merchant_fav_no);

        if (favourite) {
            favTrue.setVisible(true);
            favFalse.setVisible(false);
        } else {
            favTrue.setVisible(false);
            favFalse.setVisible(true);
        }

        return true;
    }

    /**
     * Override the listener for the menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return  true;
            case R.id.merchant_fav_yes:
                makeFav(false);
                return true;
            case R.id.merchant_fav_no:
                makeFav(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Carry out processes to add to or remove from favourites
     * @param like a boolean that determines if the user has liked or not
     */
    private void makeFav(boolean like) {
        if (like) {
            addToFav(item.id);
            favourite = true;
            invalidateOptionsMenu();
        } else {
            removeFromFav(item.id);
            favourite = false;
            invalidateOptionsMenu();
        }
    }

    private void toggleClickableButtons(boolean isLoading) {
        if (isLoading) {
            selectedBtnPin.setClickable(false);
            selectedBtnSave.setClickable(false);
        } else {
            selectedBtnPin.setClickable(true);
            selectedBtnSave.setClickable(true);
        }
    }

    private void getCoupin(Date expiryDate){
        if(!selectedRewards.isEmpty()){
            ArrayList<Boolean> isDeliverableList = new ArrayList<>();
            ArrayList<String> rewardIds = new ArrayList<>();

            if(!values.isEmpty()){
                for(Reward reward : selectedRewards){
                    rewardIds.add(reward.getId());
                    isDeliverableList.add(reward.getIsDelivery());
                }
            }

            Gson gson = new Gson();
            String rewards = gson.toJson(selectedRewards);
            String merchant = gson.toJson(item);
            String blackList = gson.toJson(tempBlackList);

            Intent intent = new Intent(this, GetCoupinActivity.class);
            intent.putExtra(rewardsIntent, rewards);
            intent.putExtra(merchantIntent, merchant);
            intent.putExtra(expiryDateIntent, expiryDate.toString());
            intent.putExtra(blackListIntent, blackList);

            if(!isDeliverableList.contains(true)){
                intent.putExtra(intentExtraGoToPayment, true);
            }

            startActivity(intent);
        }
    }

    private RewardListItem getRewardListItem(){
        RewardListItem coupin = new RewardListItem();
        //TODO: put rewardId and shortcode
//        coupin.setBookingId(object.getString("_id"));
//        coupin.setBookingShortCode(object.getString("shortCode"));
        coupin.setMerchantName(item.title);
        coupin.setMerchantAddress(item.address);
        coupin.setLatitude(item.location.latitude);
        coupin.setLongitude(item.location.longitude);
        coupin.setMerchantLogo(item.logo.url);
        coupin.setMerchantBanner(item.banner.url);
        coupin.setFavourited(item.favourite);
        coupin.setVisited(item.visited);

        // TODO: put rewardId details
//        coupin.setRewardDetails(object.getJSONArray("rewardId").toString());
        return coupin;
    }

    /**
     * Method for adding to favourite
     * Persisting in the database
     * @param id merchant id
     */
    private void addToFav(final String id) {
        url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user_favourite);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                favourites.add(id);
                PreferenceMngr.getInstance().setFavourites(favourites);
                Toast.makeText(MerchantActivity.this, "Added Successfully.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                favourite = false;
                invalidateOptionsMenu();
                Toast.makeText(MerchantActivity.this, "Added Unsuccessfully.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("merchantId", id);

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

    /**
     * Method for removing from favourite
     * Persisting in the database
     * @param id
     */
    private void removeFromFav(final String id) {
        url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user_favourite);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                favourites.remove(id);
                PreferenceMngr.getInstance().setFavourites(favourites);
                Toast.makeText(MerchantActivity.this, "Removed Successfully.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MerchantActivity.this, "An error occured whilte removing this merchant from your favourite list.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("merchantId", id);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_1:
                (new GalleryDialog(MerchantActivity.this, pictures.get(0), pictures)).show();
                break;
            case R.id.photo_2:
                (new GalleryDialog(MerchantActivity.this, pictures.get(1), pictures)).show();
                break;
            case R.id.photo_3:
                (new GalleryDialog(MerchantActivity.this, pictures.get(2), pictures)).show();
                break;
            case R.id.photo_4:
                (new GalleryDialog(MerchantActivity.this, pictures.get(3), pictures)).show();
                break;
        }
    }
}