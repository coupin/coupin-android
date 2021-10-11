package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Favourite;

import java.util.List;

public class RVFavAdapter extends RecyclerView.Adapter<RVFavAdapter.ItemViewHolder> {
    public Context context;
    public List<Favourite> rewardListItems;

    static public MyOnClick myOnClick;

    @Override
    public RVFavAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reward, parent, false);
        RVFavAdapter.ItemViewHolder itemViewHolder = new RVFavAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVFavAdapter(List<Favourite> rewardListItems, MyOnClick myOnClick, Context context) {
        this.context = context;
        RVFavAdapter.myOnClick = myOnClick;
        this.rewardListItems = rewardListItems;
    }

    @Override
    public void onBindViewHolder(RVFavAdapter.ItemViewHolder holder, int position) {
        // Add data here
        Favourite reward = rewardListItems.get(position);
        holder.merchantName.setText(reward.name);

        Glide.with(context).load(reward.logo.url).into(holder.favLogo);
        Glide.with(context).load(reward.banner.url).into(holder.favBanner);

        if (reward.visited) holder.visitedIcon.setVisibility(View.VISIBLE);
        if (reward.favourite) holder.favIcon.setVisibility(View.VISIBLE);
        if (reward.visited && reward.favourite) holder.divide.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            myOnClick.onItemClick(position);
        });

        holder.favAddress.setText(reward.address);
        holder.activeRewardHolder.setVisibility(View.GONE);
        holder.activeFavHolder.setVisibility(View.VISIBLE);
        holder.activeRewardHolder2.setVisibility(View.GONE);
        holder.code.setVisibility(View.GONE);
        holder.activeExpiration.setVisibility(View.GONE);
        holder.expiryLabel.setVisibility(View.GONE);
        holder.expiryHolder.setVisibility(View.GONE);


        int rewardCount = reward.rewards.size();
        if (rewardCount > 1) {
            String favCodeString = rewardCount + " REWARDS";
            holder.favCode.setText(favCodeString);
        } else if (rewardCount == 0) {
            String favCodeString = "No Rewards";
            holder.favCode.setText(favCodeString);
        } else {
            holder.favCode.setText(reward.reward.name);
            holder.activeRewardHolder2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rewardListItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView favBanner;
        public ImageView favIcon;
        public ImageView favLogo;
        public ImageView visitedIcon;
        public LinearLayout expiryHolder;
        public RelativeLayout activeFavHolder;
        public RelativeLayout activeRewardHolder;
        public RelativeLayout activeRewardHolder2;
        public RelativeLayout expiryLabel;
        public TextView activeExpiration;
        public TextView code;
        public TextView favAddress;
        public TextView favCode;
        public TextView merchantName;
        public TextView rewardOne;
        public TextView rewardOnePercent;
        public TextView rewardTwo;
        public TextView rewardTwoPercent;
        public TextView status;
        public View divide;


        public ItemViewHolder(View itemView) {
            super(itemView);
            activeFavHolder = (RelativeLayout) itemView.findViewById(R.id.active_fav_address_holder);
            activeRewardHolder = (RelativeLayout) itemView.findViewById(R.id.text_holder_1);
            activeRewardHolder2 = (RelativeLayout) itemView.findViewById(R.id.text_holder_2);
            activeExpiration = (TextView) itemView.findViewById(R.id.active_expiration);
            code = (TextView) itemView.findViewById(R.id.active_code);
            divide = (View) itemView.findViewById(R.id.divide);
            expiryHolder = (LinearLayout) itemView.findViewById(R.id.expiry_holder);
            expiryLabel = (RelativeLayout) itemView.findViewById(R.id.expiry_label);
            favAddress = (TextView) itemView.findViewById(R.id.active_fav_address);
            favBanner = (ImageView) itemView.findViewById(R.id.fav_banner);
            favCode = (TextView) itemView.findViewById(R.id.fav_code);
            favIcon = (ImageView) itemView.findViewById(R.id.favourite);
            favLogo = (ImageView) itemView.findViewById(R.id.fav_logo);
            merchantName = (TextView) itemView.findViewById(R.id.active_merchant_name);
            rewardOne = (TextView) itemView.findViewById(R.id.active_reward_1);
            rewardOnePercent = (TextView) itemView.findViewById(R.id.active_percent_1);
            rewardTwo = (TextView) itemView.findViewById(R.id.active_reward_2);
            rewardTwoPercent = (TextView) itemView.findViewById(R.id.active_percent_2);
            visitedIcon = (ImageView) itemView.findViewById(R.id.visited);
            status = itemView.findViewById(R.id.status);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
