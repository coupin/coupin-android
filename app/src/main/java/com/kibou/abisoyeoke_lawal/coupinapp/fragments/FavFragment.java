package com.kibou.abisoyeoke_lawal.coupinapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.SearchActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVFavAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Favourite;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardMini;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardsListItemV2;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class FavFragment extends Fragment implements MyOnClick {
    @BindView(R.id.search_fav)
    public ImageView favSearch;
    @BindView(R.id.fav_empty)
    public LinearLayout favEmpty;
    @BindView(R.id.fav_error)
    public LinearLayout favError;
    @BindView(R.id.fav_recyclerview)
    public RecyclerView favRecyclerView;
    @BindView(R.id.fav_loadingview)
    public RelativeLayout favLoadingView;
    @BindView(R.id.fav_total)
    public TextView favTotal;

    private ApiCalls apiCalls;
    public ArrayList<Favourite> favList;
    public RVFavAdapter rvAdapter;
    public String url;
    public ArrayList<String> responses = new ArrayList<>();

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_fav, container, false);
        ButterKnife.bind(this, root);

        apiCalls = ApiClient.getInstance().getCalls(getContext(), true);
        favList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdapter = new RVFavAdapter(favList, this, getContext());
        favRecyclerView.setLayoutManager(linearLayoutManager);
        favRecyclerView.setHasFixedSize(true);
        favRecyclerView.setAdapter(rvAdapter);

        favSearch.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        getFavourites();

        return root;
    }

    public void getFavourites() {
        favList.clear();

        Call<ArrayList<Favourite>> request = apiCalls.getFavourites();
        request.enqueue(new Callback<ArrayList<Favourite>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ArrayList<Favourite>> call, retrofit2.Response<ArrayList<Favourite>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    favList.addAll(response.body());
                    int total = favList.size();
                    String totalString = "MERCHANTS - " + total;
                    favTotal.setText(totalString);

                    rvAdapter.notifyDataSetChanged();
                    if (favList.size() == 0) {
                        favLoadingView.setVisibility(View.GONE);
                        favEmpty.setVisibility(View.VISIBLE);
                    } else {
                        favLoadingView.setVisibility(View.GONE);
                        favRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    ApiError error = ApiClient.parseError(response);
                    if (error.statusCode == 404) {
                        favLoadingView.setVisibility(View.GONE);
                        favEmpty.setVisibility(View.VISIBLE);
                    } else {
                        favLoadingView.setVisibility(View.GONE);
                        favError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ArrayList<Favourite>> call, Throwable t) {
                t.printStackTrace();
                if (isVisible()) {
                    favLoadingView.setVisibility(View.GONE);
                    favError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public MerchantV2 convertToMerchantV2(Favourite favourite)  {
        MerchantV2 merchant = new MerchantV2();

        merchant.title = favourite.name;
        merchant.id = favourite.id;
        merchant.address = favourite.address;
        merchant.favourite = true;
        merchant.visited = favourite.visited;
        merchant.location = favourite.location;
        merchant.rating = favourite.rating;
        merchant.mobile = favourite.mobile;
        merchant.email = favourite.email;
        merchant.details = favourite.details;
        merchant.logo = favourite.logo;
        merchant.banner = favourite.banner;
        merchant.picture = favourite.picture;
        merchant.category = favourite.category;

        ArrayList<RewardV2> rewardV2s = new ArrayList<>();
        for (RewardMini rewardMini: favourite.rewards) {
            RewardV2 item = new RewardV2();
            item.name = rewardMini.name;
            item.pictures = rewardMini.pictures;
            item.days = rewardMini.days;
            item.categories = rewardMini.categories;
            item.isActive = rewardMini.isActive;
            item.isDelivery = rewardMini.isDelivery;
            item.quantity = rewardMini.quantity;
            item.multiple = rewardMini.multiple;
            item.price = rewardMini.price;
            item.id = rewardMini.id;
            item.description = rewardMini.description;
            item.endDate = rewardMini.endDate;
            item.startDate = rewardMini.startDate;
            item.createdDate = rewardMini.createdDate;
            item.modifiedDate = rewardMini.modifiedDate;
            item.status = rewardMini.status;
            rewardV2s.add(item);
        }

        merchant.rewardsCount = rewardV2s.size();
        merchant.rewards = rewardV2s;
        return merchant;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MerchantActivity.class);
        Bundle extra = new Bundle();
        MerchantV2 merchantV2 = convertToMerchantV2(favList.get(position));
        extra.putString("merchant", TypeUtils.objectToString(merchantV2));
        intent.putExtras(extra);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, int quantity) {}
}
