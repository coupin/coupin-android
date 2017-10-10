package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by abisoyeoke-lawal on 10/5/17.
 */

public class RVHotAdapter extends RecyclerView.Adapter<RVHotAdapter.ItemViewHolder> {
    public List<RewardListItem> hotList;

    static  public MyOnClick myOnClick;

    public RVHotAdapter(List<RewardListItem> hotList, MyOnClick myOnClick) {
        this.hotList = hotList;
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
        RewardListItem hotItem = hotList.get(position);

        try {
            JSONArray rewardArray = new JSONArray(hotItem.getRewardDetails());

            holder.hotTitle.setText(hotItem.getMerchantName());
            holder.hotAddress.setText(hotItem.getMerchantAddress());

            if (hotItem.getRewardCount() > 1) {
                holder.hotRewards.setText(hotItem.getRewardCount() + " REWWARDS");
            } else {
                holder.hotRewards.setText(rewardArray.getJSONObject(0).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return hotList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView hotTitle;
        public TextView hotAddress;
        public TextView hotRewards;

        public ItemViewHolder(View view) {
            super(view);

            hotTitle = (TextView) view.findViewById(R.id.hot_title);
            hotAddress = (TextView) view.findViewById(R.id.hot_address);
            hotRewards = (TextView) view.findViewById(R.id.hot_rewards);
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
