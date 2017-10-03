package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by abisoyeoke-lawal on 7/27/17.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder> {
    public List<RewardListItem> rewardListItems;

    static public MyOnClick myOnClick;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reward, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVAdapter(List<RewardListItem> rewardListItems, MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
        this.rewardListItems = rewardListItems;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Add data here
        RewardListItem reward = rewardListItems.get(position);

        try {
            holder.merchantName.setText(reward.getMerchantName());

            JSONArray rewardArray = new JSONArray(reward.getRewardDetails());

            if (reward.isFav()) {
                holder.favAddress.setText(reward.getMerchantAddress());
                holder.activeRewardHolder.setVisibility(View.GONE);
                holder.activeFavHolder.setVisibility(View.VISIBLE);
                holder.activeRewardHolder2.setVisibility(View.GONE);

                if (reward.getRewardCount() > 1) {
                    holder.code.setText(reward.getRewardCount() + " REWARDS");
                } else {
                    holder.code.setText(rewardArray.getJSONObject(0).getString("name"));
                }

            } else {
                if (reward.isLater()) {
                    holder.code.setText("REDEEM REWARDS");
                } else {
                    holder.code.setText("Code: " + reward.getBookingShortCode());
                }

                JSONObject first = rewardArray.getJSONObject(0);
                holder.rewardOne.setText(first.getString("description"));

                if (rewardArray.length() > 1) {
                    JSONObject second = rewardArray.getJSONObject(1);
                    holder.rewardTwo.setText(second.getString("description"));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return rewardListItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public RelativeLayout activeFavHolder;
        public RelativeLayout activeRewardHolder;
        public RelativeLayout activeRewardHolder2;
        public TextView code;
        public TextView favAddress;
        public TextView merchantName;
        public TextView rewardOne;
        public TextView rewardOnePercent;
        public TextView rewardTwo;
        public TextView rewardTwoPercent;


        public ItemViewHolder(View itemView) {
            super(itemView);
            activeFavHolder = (RelativeLayout) itemView.findViewById(R.id.active_fav_address_holder);
            activeRewardHolder = (RelativeLayout) itemView.findViewById(R.id.text_holder_1);
            activeRewardHolder2 = (RelativeLayout) itemView.findViewById(R.id.text_holder_2);
            code = (TextView) itemView.findViewById(R.id.active_code);
            favAddress = (TextView) itemView.findViewById(R.id.active_fav_address);
            merchantName = (TextView) itemView.findViewById(R.id.active_merchant_name);
            rewardOne = (TextView) itemView.findViewById(R.id.active_reward_1);
            rewardOnePercent = (TextView) itemView.findViewById(R.id.active_percent_1);
            rewardTwo = (TextView) itemView.findViewById(R.id.active_reward_2);
            rewardTwoPercent = (TextView) itemView.findViewById(R.id.active_percent_2);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
