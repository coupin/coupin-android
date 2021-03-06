package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 10/5/17.
 */

public class RVHotAdapter extends RecyclerView.Adapter<RVHotAdapter.ItemViewHolder> {
    public ArrayList<Merchant> merchants;
    public Context context;

    static  public MyOnClick myOnClick;

    public RVHotAdapter(ArrayList<Merchant> merchants, Context context, MyOnClick myOnClick) {
        this.context = context;
        this.merchants = merchants;
        this.myOnClick = myOnClick;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_hot, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Merchant hotItem = merchants.get(position);

        try {
            JSONArray rewardArray = new JSONArray(hotItem.getRewards());

            holder.hotTitle.setText(hotItem.getTitle());
            holder.hotAddress.setText(hotItem.getAddress());
            Glide.with(context).load(hotItem.getLogo()).into(holder.hotLogo);

            holder.hotVisited.setVisibility(View.VISIBLE);
            if (!hotItem.isVisited()) {
                holder.hotVisited.setImageAlpha(0);
            }

            holder.hotFavourite.setVisibility(View.VISIBLE);
            if (!hotItem.isFavourite()) {
                holder.hotFavourite.setImageAlpha(0);
            }

            if (hotItem.getRewardsCount() == 1){
                holder.hotRewards.setText(rewardArray.getJSONObject(0).getString("name"));
            } else if (hotItem.getRewardsCount() > 1) {
                holder.hotRewards.setText(hotItem.getRewardsCount() + " REWARDS");
            } else {
                holder.hotRewards.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return merchants.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView hotFavourite;
        public ImageView hotVisited;
        public RoundedImageView hotLogo;
        public TextView hotTitle;
        public TextView hotAddress;
        public TextView hotRewards;

        public ItemViewHolder(View itemView) {
            super(itemView);

            hotFavourite = (ImageView) itemView.findViewById(R.id.hot_fav);
            hotVisited = (ImageView) itemView.findViewById(R.id.hot_visited);
            hotLogo = (RoundedImageView) itemView.findViewById(R.id.hot_logo);
            hotTitle = (TextView) itemView.findViewById(R.id.hot_title);
            hotAddress = (TextView) itemView.findViewById(R.id.hot_address);
            hotRewards = (TextView) itemView.findViewById(R.id.hot_rewards);
        }

        public void bind(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myOnClick.onItemClick(position);
                }
            });
        }
    }
}
