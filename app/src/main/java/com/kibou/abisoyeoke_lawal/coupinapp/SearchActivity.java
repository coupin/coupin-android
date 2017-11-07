package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVSearchAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.FilterNoDistDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;

public class SearchActivity extends AppCompatActivity implements MyOnClick, MyFilter {
    @BindView(R.id.search_edittext)
    public EditText searchTextView;
    @BindView(R.id.search_back)
    public ImageView back;
    @BindView(R.id.search_filter)
    public ImageView searchFilter;
    @BindView(R.id.empty_search)
    public LinearLayout emptySearchView;
    @BindView(R.id.search_error)
    public LinearLayout errorSearchView;
    @BindView(R.id.search_loading)
    public LinearLayout loadingSearchView;
    @BindView(R.id.search_recyclerview)
    public RecyclerView searchRecyclerView;
    @BindView(R.id.filter_tags)
    public TagContainerLayout filterTags;
    @BindView(R.id.search_street)
    public TextView searchStreet;

    public ArrayList<String> categories = new ArrayList<>();
    public String queryString;
    public String latitude;
    public String longitude;
    public String url;
    public RequestQueue requestQueue;

    public ArrayList<Merchant> companyInfos;
    public RVSearchAdapter adapter;
    public LinearLayoutManager linearLayoutManager;

    public Handler handler = new Handler();

    public int icons[] = new int[]{R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        final FilterNoDistDialog filterDialog = new FilterNoDistDialog(this, R.style.Filter_Dialog);
        filterDialog.setInterface(this);
        Window window = filterDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = this.getWindow().getAttributes().width;
        wlp.gravity = Gravity.TOP | Gravity.LEFT | Gravity.START;
        wlp.windowAnimations = R.style.PauseDialogAnimation;

        Bundle extra = getIntent().getExtras();
        searchStreet.setText("Near Me - " + extra.getString("street"));

        latitude = String.valueOf(extra.getDouble("lat"));
        longitude = String.valueOf(extra.getDouble("long"));

        companyInfos = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RVSearchAdapter(companyInfos, this);

        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setAdapter(adapter);

        queryString = "";
        query();

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO: Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    loading(0);
                    queryString = s.toString();
                    query();
                }
            }
        });

        searchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void query() {
        Log.v("VolleyQuery", queryString);
        url = getString(R.string.base_url) + getString(R.string.ep_api_merchant) + "/search";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                companyInfos.clear();
                adapter.notifyDataSetChanged();
                try {
                    JSONArray resArr = new JSONArray(response);
                    if (resArr.length() > 0) {
                        for (int x = 0; x < resArr.length(); x++) {
                            //TODO: Do the cards
                            JSONObject res = resArr.getJSONObject(x);
                            JSONObject object = res.getJSONObject("merchantInfo");
                            Merchant item = new Merchant();
                            item.setId(res.getString("_id"));
                            item.setEmail(res.getString("email"));
                            item.setPicture(icons[x]);
                            item.setAddress(object.getString("address"));
                            item.setDetails(object.getString("companyDetails"));
                            item.setMobile(object.getString("mobileNumber"));
                            item.setTitle(object.getString("companyName"));
                            item.setRewards(object.getJSONArray("rewards").toString());
                            item.setResponse(res.toString());

                            if (object.has("location")) {
                                item.setLatitude(object.getJSONArray("location").getDouble(1));
                                item.setLongitude(object.getJSONArray("location").getDouble(0));
                            }
                            companyInfos.add(item);
                        }
                        Log.v("VolleyList", companyInfos.toString());

                        adapter.notifyDataSetChanged();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading(1);
                            }
                        }, 2000);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading(2);
                            }
                        }, 2000);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading(3);
                        }
                    }, 2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    loading(3);
                } else {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        if (error.networkResponse.statusCode == 404) {
                            loading(2);
                        }
                    } else {
                        loading(3);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("long", longitude);
                params.put("lat", latitude);
                params.put("query", queryString);
                params.put("categories", categories.toString());

                return params;
            }
        };

        requestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Intent merchantIntent = new Intent(this, MerchantActivity.class);
        Bundle extra = new Bundle();
        extra.putSerializable("object", companyInfos.get(position));
        merchantIntent.putExtras(extra);
        startActivity(merchantIntent);
    }

    /**
     * Showing the appropriate view after and before loading
     * @param opt
     */
    public void loading(int opt) {
        switch (opt) {
            case 0:
                searchRecyclerView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.GONE);
                loadingSearchView.setVisibility(View.VISIBLE);
                break;
            case 1:
                loadingSearchView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingSearchView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.VISIBLE);
                break;
            case 3:
                loadingSearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.VISIBLE);
                break;
            default:
                searchRecyclerView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.GONE);
                loadingSearchView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * After filer selection has been made
     * @param selection
     * @param distance
     */
    @Override
    public void onFilterSelected(ArrayList<String> selection, int distance) {
        categories = selection;
        filterTags.setTags(categories);
        query();
    }
}
