package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVSearchAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.FilterNoDistDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

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

    private ApiCalls apiCalls;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<MerchantV2> companyInfosV2;
    private boolean isLoading = false;
    private int page = 0;
    private LinearLayoutManager linearLayoutManager;
    private RVSearchAdapter adapter;
    private String queryString;


    public Handler handler = new Handler();
    public Runnable queryRun;

    public int[] icons = new int[]{R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
        R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        final FilterNoDistDialog filterDialog = new FilterNoDistDialog(this, R.style.Filter_Dialog);
        filterDialog.setInterface(this);
        Window window = filterDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = this.getWindow().getAttributes().width;
        wlp.gravity = Gravity.TOP | Gravity.LEFT | Gravity.START;
        wlp.windowAnimations = R.style.PauseDialogAnimation;

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        companyInfosV2 = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new RVSearchAdapter(companyInfosV2, this, this);

        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setAdapter(adapter);

        queryString = "";
        implementOnScrollListener();

        queryRun = new Runnable() {
            @Override
            public void run() {
                loading(0);
                page = 0;
                searchForMerchant();
            }
        };

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    handler.removeCallbacks(queryRun);

                    queryString = s.toString().replace(" ", "&");
                    handler.postDelayed(queryRun, 500);
                }
            }
        });

        searchFilter.setOnClickListener(v -> filterDialog.show());
        back.setOnClickListener(v -> onBackPressed());
    }

    private void searchForMerchant() {
        Call<ArrayList<MerchantV2>> request = apiCalls.searchMerchants(queryString, page, categories.toString());
        request.enqueue(new Callback<ArrayList<MerchantV2>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ArrayList<MerchantV2>> call, retrofit2.Response<ArrayList<MerchantV2>> response) {
                if (response.isSuccessful()) {
                    if (page == 0) {
                        companyInfosV2.clear();
                        adapter.notifyDataSetChanged();
                    }

                    assert response.body() != null;
                    if (response.body().size() > 0) {
                        companyInfosV2.addAll(response.body());
                    }

                    isLoading = false;
                    if (page > 0) {
                        loading(5);
                    }
                    adapter.notifyDataSetChanged();
                    loading(1);
                } else {
                    ApiError error = ApiClient.parseError(response);
                    if (page == 0) {
                        toggleLoading(2, error.message);
                    } else {
                        toggleLoading(5, error.message);
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ArrayList<MerchantV2>> call, Throwable t) {
                t.printStackTrace();
                if (page == 0) {
                    loading(3);
                } else {
                    showErrorToast(false);
                    loading(5);
                }
            }
        });
    }

    private void toggleLoading(int option, String message) {
        loading(option);
        if (message != null)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                if (isLoading || (companyInfosV2.size() % 7) != 0 || companyInfosV2.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = companyInfosV2.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchForMerchant();
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
        extra.putString("merchant", TypeUtils.objectToString(companyInfosV2.get(position)));
        merchantIntent.putExtras(extra);
        startActivity(merchantIntent);
    }

    @Override
    public void onItemClick(int position, int quantity) { }

    /**
     * Showing the appropriate view after and before loading
     * @param opt
     */
    public void loading(int opt) {
        switch (opt) {
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

    /**
     * After filer selection has been made
     * @param selection
     * @param distance
     */
    @Override
    public void onFilterSelected(ArrayList<String> selection, int distance) {
        categories = selection;
        filterTags.setTags(categories);
        searchForMerchant();
    }
}
