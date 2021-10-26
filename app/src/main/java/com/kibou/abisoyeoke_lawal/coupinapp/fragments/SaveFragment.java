package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.activities.CoupinActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVBroAdapter;
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
public class SaveFragment extends Fragment implements MyOnClick {
    @BindView(R.id.later_loadingview)
    public AVLoadingIndicatorView laterLoadingView;
    @BindView(R.id.save_loading)
    public AVLoadingIndicatorView bottomLoadingView;
    @BindView(R.id.later_empty)
    public LinearLayout laterEmpty;
    @BindView(R.id.later_error)
    public LinearLayout laterError;
    @BindView(R.id.later_recyclerview)
    public RecyclerView laterRecyclerView;

    private ApiCalls apiCalls;
    public ArrayList<RewardsListItemV2> laterList = new ArrayList<>();
    private boolean isLoading = false;
    private int page = 0;
    private Handler handler;
    private LinearLayoutManager linearLayoutManager;
    public RVBroAdapter nowRvAdapter;
    public String url;


    public SaveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_save_for_later, container, false);
        ButterKnife.bind(this, rootView);

        apiCalls = ApiClient.getInstance().getCalls(getContext(), true);
        handler = new Handler();

        linearLayoutManager = new LinearLayoutManager(getContext());
        nowRvAdapter = new RVBroAdapter(laterList, this, getContext());
        laterRecyclerView.setLayoutManager(linearLayoutManager);
        laterRecyclerView.setHasFixedSize(true);
        laterRecyclerView.setAdapter(nowRvAdapter);

        getRewardsForLater();
        implementOnScrollListener();

        return rootView;
    }

    private void getRewardsForLater() {
        Call<ArrayList<RewardsListItemV2>> request = apiCalls.getCoupins(true, page);
        request.enqueue(new Callback<ArrayList<RewardsListItemV2>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ArrayList<RewardsListItemV2>> call, retrofit2.Response<ArrayList<RewardsListItemV2>> response) {
                if (response.isSuccessful()) {
                    for (RewardsListItemV2 item : response.body()) {
                        item.later = true;
                        laterList.add(item);
                    }

                    isLoading = false;
                    if (page > 0) loading(5);
                    nowRvAdapter.notifyDataSetChanged();
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
                if (laterList.size() == 0) {
                    loading(3);
                } else {
                    showErrorToast(false);
                }

                if (page > 0) loading(5);
            }
        });
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
     * Showing the appropriate view after and before loading
     * @param opt
     */
    public void loading(int opt) {
        switch (opt) {
            case 1:
                laterLoadingView.setVisibility(View.GONE);
                laterRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2:
                laterLoadingView.setVisibility(View.GONE);
                laterEmpty.setVisibility(View.VISIBLE);
                break;
            case 3:
                laterLoadingView.setVisibility(View.GONE);
                laterError.setVisibility(View.VISIBLE);
                break;
            case 4:
                bottomLoadingView.setVisibility(View.VISIBLE);
                break;
            case 5:
                bottomLoadingView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Implements scroll listener for the saved list
     * Using it to load new coupins
     */
    private void implementOnScrollListener() {
        laterRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading || (laterList.size() % 7) != 0 || laterList.size() < 7)
                    return;

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading(4);
                    page = laterList.size() / 7;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRewardsForLater();
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

    @Override
    public void onItemClick(int position, int quantity) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.getActivity(), CoupinActivity.class);
        intent.putExtra("coupin", laterList.get(position));
        startActivity(intent);
    }
}
