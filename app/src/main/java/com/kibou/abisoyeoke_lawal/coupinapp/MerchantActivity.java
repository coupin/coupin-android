package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVExpandableAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.RewardInfoDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.DateTimeUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantActivity extends AppCompatActivity implements MyOnSelect, MyOnClick {
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
    @BindView(R.id.merchant_details_textview)
    public TextView merchantDetails;
    @BindView(R.id.merchant_name_textview)
    public TextView merchantName;
    @BindView(R.id.selected_text)
    public TextView selectedText;
    @BindView(R.id.merchant_toolbar)
    public Toolbar merchantToolbar;

    public RewardInfoDialog infoDialog;
    public RequestQueue requestQueue;
    String url;

    public ArrayList<Reward> values;
    public ArrayList<String> selected;
    public ArrayList<String> favourites;
    public boolean favourite = false;
    public JSONObject user;
    public JSONArray userFavourites;
    public Merchant item;
    public JSONArray resArray;
    public JSONObject res;
    public RVExpandableAdapter rvExpandableAdapter;
    public String rewardHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);

        setSupportActionBar(merchantToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        if (extra.getString("merchant", null) == null) {
            try {
                item = (Merchant) extra.getSerializable("object");
                resArray = new JSONArray(item.getRewards());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        values = new ArrayList<>();
        selected = new ArrayList<>();

        infoDialog = new RewardInfoDialog(this, this);

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
                            JSONObject object = new JSONObject(response);

                            RewardListItem coupin = new RewardListItem();
                            coupin.setBookingId(object.getString("_id"));
                            coupin.setBookingShortCode(object.getString("shortCode"));
                            coupin.setMerchantName(item.getTitle());
                            coupin.setMerchantAddress(item.getAddress());
                            // TODO: Sort out the merchant logo

                            coupin.setRewardDetails(object.getJSONArray("rewardId").toString());

                            Log.v("VolleyCheck", response);

                            Intent intent = new Intent(MerchantActivity.this, CoupinActivity.class);
                            intent.putExtra("coupin", coupin);
                            startActivity(intent);

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

        selectedBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_rewards_for_later);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO: Show that it has been saved
                        MerchantActivity.super.onBackPressed();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("VolleyError", error.toString());
                        Log.v("VolleyStatue", "" + error.networkResponse.statusCode);
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
            if (item == null) {
                res = new JSONObject(extra.getString("merchant"));
                item = new Merchant();
                item.setId(res.getString("_id"));
                item.setPicture(R.drawable.slide1);
                item.setAddress(res.getString("address"));
                item.setDetails(res.getString("details"));
                item.setEmail(res.getString("email"));
                item.setMobile(res.getString("mobile"));
                item.setTitle(res.getString("name"));
                item.setRewards(res.getJSONArray("rewards").toString());
                item.setLatitude(res.getJSONObject("location").getDouble("lat"));
                item.setLongitude(res.getJSONObject("location").getDouble("long"));
            }
            merchantName.setText(item.getTitle());
            merchantDetails.setText(item.getAddress());

            user = new JSONObject(PreferenceMngr.getInstance().getUser());
            userFavourites = user.getJSONArray("favourites");
            String temp = userFavourites.toString();
            favourites = new ArrayList<String>(Arrays.asList(temp.substring(1, temp.length() - 1).replaceAll("\"", "").split(",")));
            if (favourites.contains(item.getId())) {
                favourite = true;
            }

//            if (res.get("picture").toString() != "null") {
//                merchantImage.setImage
//            }

            if (resArray == null) {
                resArray = res.getJSONArray("rewards");
            }

            for (int x = 0; x < resArray.length(); x++) {
                Reward reward = new Reward();
                JSONObject object = resArray.getJSONObject(x);
                reward.setId(object.getString("_id"));
                reward.setTitle(object.getString("name"));
                reward.setDetails(object.getString("description"));

                // Date
                reward.setExpires(DateTimeUtils.convertZString(object.getString("endDate")));
                reward.setStarting(DateTimeUtils.convertZString(object.getString("startDate")));

                // Price Details
                if (object.has("price")) {
                    reward.setIsDiscount(true);
                    reward.setNewPrice(object.getJSONObject("price").getInt("new"));
                    reward.setOldPrice(object.getJSONObject("price").getInt("old"));
                } else {
                    reward.setIsDiscount(false);
                }

                // Mutliple Use details
                if (object.getJSONObject("multiple").getBoolean("status")) {
                    reward.setMultiple(true);
                } else {
                    reward.setMultiple(false);
                }

                // Applicable days
                reward.setDays(object.getJSONArray("applicableDays"));

                values.add(reward);
            }

            rvExpandableAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Custom on select being used for the recycler view that covers
     * rewards selected and unselected
     * @param selected
     * @param index
     */
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

    /**
     * Custom on item click
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            infoDialog.dismiss();
        } else {
            this.selected.clear();
            onBackPressed();
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
            addToFav(item.getId());
            favourite = true;
            invalidateOptionsMenu();
        } else {
            removeFromFav(item.getId());
            favourite = false;
            invalidateOptionsMenu();
        }
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
                Toast.makeText(MerchantActivity.this, "Added Successfully.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
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
                favourites.add(id);
                Toast.makeText(MerchantActivity.this, "Removed Successfully.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
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
}