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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by abisoyeoke-lawal on 10/9/17.
 */

public class RVBroAdapter extends RecyclerView.Adapter<RVBroAdapter.ItemViewHolder> {
    public List<RewardListItem> rewardListItems;

    static public MyOnClick myOnClick;

    @Override
    public RVBroAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reward, parent, false);
        RVBroAdapter.ItemViewHolder itemViewHolder = new RVBroAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVBroAdapter(List<RewardListItem> rewardListItems, MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
        this.rewardListItems = rewardListItems;
    }

    @Override
    public void onBindViewHolder(RVBroAdapter.ItemViewHolder holder, int position) {
        // Add data here
        RewardListItem reward = rewardListItems.get(position);
        Date temp = new Date();

        try {
            holder.merchantName.setText(reward.getMerchantName());

            JSONArray rewardArray = new JSONArray(reward.getRewardDetails());

            holder.code.setText("REDEEM REWARDS");

            for (int x = 0 ; x < reward.getRewardCount(); x++) {
                if (x == 0) {
                    temp = new Date(rewardArray.getJSONObject(0).getJSONObject("id").getString("endDate"));
                } else {
                    if (temp.after(new Date(rewardArray.getJSONObject(x).getJSONObject("id").getString("endDate")))) {
                        temp = new Date(rewardArray.getJSONObject(0).getJSONObject("id").getString("endDate"));
                    }
                }
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            holder.activeExpiration.setText(simpleDateFormat.format(temp));

            JSONObject first = rewardArray.getJSONObject(0).getJSONObject("id");
            holder.rewardOne.setText(first.getString("description"));
            if (first.has("price") && first.getJSONObject("price").has("old") &&
                first.getJSONObject("price").has("new")) {
                float oldPrice = first.getJSONObject("price").getInt("old");
                float newPrice = first.getJSONObject("price").getInt("new");
                float discount = ((oldPrice - newPrice) / oldPrice) * 100;
                holder.rewardOnePercent.setText(String.valueOf((int) discount) + "%");
            }

            if (rewardArray.length() > 1) {
                JSONObject second = rewardArray.getJSONObject(1).getJSONObject("id");
                holder.rewardTwo.setText(second.getString("description"));
                if (second.has("price") && second.getJSONObject("price").has("old") &&
                    second.getJSONObject("price").has("new")) {
                    float oldPrice = second.getJSONObject("price").getInt("old");
                    float newPrice = second.getJSONObject("price").getInt("new");
                    float discount = ((oldPrice - newPrice) / oldPrice) * 100;
                    holder.rewardTwoPercent.setText(String.valueOf((int) discount) + "%");
                }
            } else {
                holder.activeRewardHolder2.setVisibility(View.GONE);
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
        public TextView activeExpiration;
        public TextView code;
        public TextView favAddress;
        public TextView favCode;
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
            activeExpiration = (TextView) itemView.findViewById(R.id.active_expiration);
            favAddress = (TextView) itemView.findViewById(R.id.active_fav_address);
            favCode = (TextView) itemView.findViewById(R.id.fav_code);
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
}
