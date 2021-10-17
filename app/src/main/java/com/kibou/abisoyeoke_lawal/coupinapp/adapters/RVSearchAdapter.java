package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abisoyeoke-lawal on 10/18/17.
 */

public class RVSearchAdapter extends RecyclerView.Adapter<RVSearchAdapter.ItemViewHolder> {
    private final Context context;
    private final List<MerchantV2> searchList;
    private static MyOnClick myOnClick;

    public RVSearchAdapter(List<MerchantV2> searchList, MyOnClick myOnClick, Context context) {
        this.context = context;
        this.searchList = searchList;
        RVSearchAdapter.myOnClick = myOnClick;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_hot, parent, false);
        ItemViewHolder itemHolder = new ItemViewHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        MerchantV2 item = searchList.get(position);
        ArrayList<RewardV2> rewardArray = item.rewards;

        Glide.with(context).load(item.logo.url).into(holder.searchLogo);
        holder.searchTitle.setText(item.name);
        holder.searchAddress.setText(item.address);

        if (item.favourite) {
            holder.searchFavourite.setVisibility(View.VISIBLE);
        }

        if (item.visited) {
            holder.searchVisited.setVisibility(View.VISIBLE);
        }

        if (rewardArray.size() > 1) {
            String lengthString = rewardArray.size() + " REWARDS";
            holder.searchRewards.setText(lengthString);
        } else {
            holder.searchRewards.setText(rewardArray.get(0).name);
        }

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView searchFavourite;
        public ImageView searchLogo;
        public ImageView searchVisited;
        public TextView searchTitle;
        public TextView searchAddress;
        public TextView searchRewards;

        public ItemViewHolder(View view) {
            super(view);

            searchAddress = (TextView) view.findViewById(R.id.hot_address);
            searchFavourite = (ImageView) view.findViewById(R.id.hot_fav);
            searchLogo = (ImageView) view.findViewById(R.id.hot_logo);
            searchRewards = (TextView) view.findViewById(R.id.hot_rewards);
            searchTitle = (TextView) view.findViewById(R.id.hot_title);
            searchVisited = (ImageView) view.findViewById(R.id.hot_visited);
        }

        public void bind(final int position) {
            itemView.setOnClickListener(v -> myOnClick.onItemClick(position));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
