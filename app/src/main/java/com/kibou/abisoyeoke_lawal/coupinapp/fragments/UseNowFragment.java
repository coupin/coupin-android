package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.activities.CoupinActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardsListItemV2;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class UseNowFragment extends Fragment implements MyOnClick {
    @BindView(R.id.now_loadingview)
    public AVLoadingIndicatorView loadingView;
    @BindView(R.id.active_loading)
    public AVLoadingIndicatorView bottomLoadingView;
    @BindView(R.id.now_empty)
    public LinearLayout nowEmpty;
    @BindView(R.id.now_error)
    public LinearLayout nowError;
    @BindView(R.id.now_recyclerview)
    public RecyclerView recyclerView;

    private ApiCalls apiCalls;
    private final ArrayList<RewardsListItemV2> nowList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 0;
    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    private RVAdapter rvAdapter;
    private String url;

    public UseNowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_use_now, container, false);
        ButterKnife.bind(this, rootView);

        apiCalls = ApiClient.getInstance().getCalls(getContext(), true);
        handler = new Handler();

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVAdapter(nowList, this, getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(rvAdapter);

        getRewardsForNow();
        implementOnScrollListener();

        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CoupinActivity.class);
        intent.putExtra("coupin", nowList.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, int quantity) { }

    private void getRewardsForNow() {
        if(this.isAdded()) {
            Call<ArrayList<RewardsListItemV2>> request = apiCalls.getCoupins(false, page);
            request.enqueue(new Callback<ArrayList<RewardsListItemV2>>() {
                @EverythingIsNonNull
                @Override
                public void onResponse(Call<ArrayList<RewardsListItemV2>> call, retrofit2.Response<ArrayList<RewardsListItemV2>> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        for (RewardsListItemV2 itemV2: response.body()) {
                            itemV2.rewardCount = itemV2.rewards.size();
                            nowList.add(itemV2);
                        }

                        isLoading = false;
                        if (page > 0) loading(5);
                        rvAdapter.notifyDataSetChanged();
                        if (page == 0) loading(1);
                    } else {
                        ApiError error = ApiClient.parseError(response);

                        if (error.statusCode == 404 && page == 0) {
                            loading(2);
                        } else if (page == 0) {
                            loading(3);
                        } else {
                            Toast.makeText(getContext(), error.message, Toast.LENGTH_SHORT).show();
                        }

                        if (page > 0) loading(5);
                    }
                }

                @EverythingIsNonNull
                @Override
                public void onFailure(Call<ArrayList<RewardsListItemV2>> call, Throwable t) {
                    t.printStackTrace();

                    isLoading = false;
                    if (nowList.size() == 0) {
                        loading(3);
                    } else {
                        showErrorToast(false);
                    }

                    if (page > 0) loading(5);
                }
            });
        }
    }

    /**
     * Show toast instead of changing view
     * @param isEmpty was it an empty return
     */
    public void showErrorToast(Boolean isEmpty) {
        if (isEmpty) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_coupins), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_coupins), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implements scroll listener for the active list
     * Using it to load new coupins
     */
    private void implementOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (nowList.size() % 7) != 0 || nowList.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = nowList.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRewardsForNow();
                        }
                    }, 2000);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Showing the appropriate view after and before loading
     * @param opt
     */
    public void loading(int opt) {
        switch (opt) {
            case 1:
                loadingView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingView.setVisibility(View.GONE);
                nowEmpty.setVisibility(View.VISIBLE);
                break;
            case 3:
                loadingView.setVisibility(View.GONE);
                nowError.setVisibility(View.VISIBLE);
                break;
            case 4:
                bottomLoadingView.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoadingView.setVisibility(View.GONE);
                break;
            default:
                recyclerView.setVisibility(View.GONE);
                nowEmpty.setVisibility(View.GONE);
                nowError.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
