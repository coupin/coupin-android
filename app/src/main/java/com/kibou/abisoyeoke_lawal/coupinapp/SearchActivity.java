package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVSearchAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.FilterNoDistDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;

public class SearchActivity extends AppCompatActivity implements MyOnClick, MyFilter {
    @BindView(R.id.search_bottom_loading)
    public AVLoadingIndicatorView bottomLoadingView;
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

    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Merchant> companyInfos;
    private boolean isLoading = false;
    private int page = 0;
    private LinearLayoutManager linearLayoutManager;
    private RequestQueue requestQueue;
    private RVSearchAdapter adapter;
    private String queryString;
    private String url;


    public Handler handler = new Handler();
    public Runnable queryRun;

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

        companyInfos = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RVSearchAdapter(companyInfos, this, this);

        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setAdapter(adapter);

        queryString = "";
//        query();
        implementOnScrollListener();

        queryRun = new Runnable() {
            @Override
            public void run() {
                loading(0);
                page = 0;
                query();
            }
        };

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
                    handler.removeCallbacks(queryRun);

                    queryString = s.toString();
                    handler.postDelayed(queryRun, 500);
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

    /**
     * Query for search
     */
    private void query() {
        url = getString(R.string.base_url) + getString(R.string.ep_api_merchant) + "/" + queryString + "/search?page=" + page;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (page == 0) {
                    companyInfos.clear();
                    adapter.notifyDataSetChanged();
                }
                try {
                    JSONArray resArr = new JSONArray(response);
                    if (resArr.length() > 0) {
                        for (int x = 0; x < resArr.length(); x++) {
                            JSONObject res = resArr.getJSONObject(x);

                            Merchant item = new Merchant();
                            item.setId(res.getString("_id"));
                            item.setEmail(res.getString("email"));
                            item.setBanner(res.getJSONObject("banner").getString("url"));
                            item.setLogo(res.getJSONObject("logo").getString("url"));
                            item.setAddress(res.getString("address"));
                            item.setDetails(res.getString("details"));
                            item.setMobile(res.getString("mobile"));
                            item.setTitle(res.getString("name"));
                            item.setReward(res.getJSONObject("reward").toString());
                            item.setRewards(res.getJSONArray("rewards").toString());
                            item.setRewardsCount(res.getInt("count"));
                            item.setResponse(res.toString());
                            item.setRating(res.getInt("rating"));
                            item.setFavourite(res.getBoolean("favourite"));
                            item.setVisited(res.getBoolean("visited"));

                            if (res.has("location")) {
                                item.setLatitude(res.getJSONObject("location").getDouble("lat"));
                                item.setLongitude(res.getJSONObject("location").getDouble("long"));
                            }
                            companyInfos.add(item);
                        }

                        isLoading = false;
                        if (page > 0) {
                            loading(5);
                        }
                        adapter.notifyDataSetChanged();
                        loading(1);
                    } else {
                        if (page == 0) {
                            loading(2);
                        } else {
                            showErrorToast(true);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (page == 0) {
                                loading(3);
                            } else {
                                showErrorToast(false);
                            }
                            isLoading = false;
                            if (page > 0) {
                                loading(5);
                            }
                        }
                    }, 2000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                if (page == 0) {
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
                } else {
                    showErrorToast(false);
                }

                if (page > 0) {
                    loading(5);
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

                params.put("query", queryString);
                params.put("categories", categories.toString());

                return params;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Implements scroll listener for the search list
     * Using it to load more merchants
     */
    private void implementOnScrollListener() {
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (companyInfos.size() % 7) != 0 || companyInfos.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = companyInfos.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            query();
                        }
                    }, 2000);
                }
            }
        });
    }

    /**
     * Show toast instead of changing view
     * @param isEmpty was it an empty return
     */
    public void showErrorToast(Boolean isEmpty) {
        if (isEmpty) {
            Toast.makeText(this, getResources().getString(R.string.empty_merchants), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_now), Toast.LENGTH_SHORT).show();
        }
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
                emptySearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingSearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.VISIBLE);
                break;
            case 3:
                loadingSearchView.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.VISIBLE);
                break;
            case 4:
                bottomLoadingView.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoadingView.setVisibility(View.GONE);
                break;
            default:
                searchRecyclerView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.GONE);
                errorSearchView.setVisibility(View.GONE);
                loadingSearchView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        requestQueue.stop();
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
