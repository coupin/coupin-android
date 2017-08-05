package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import java.util.List;

/**
 * Created by abisoyeoke-lawal on 8/5/17.
 */

public class RVRewardsAdapter extends RecyclerView.Adapter<RVRewardsAdapter.ItemViewHolder> {
    public List<RewardListItem> rewardListItemList;

    static  public MyOnClick myOnClick;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_single_reward, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Add data here
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return rewardListItemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
