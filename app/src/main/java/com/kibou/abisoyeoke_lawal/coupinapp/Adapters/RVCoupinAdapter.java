package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import java.util.List;

/**
 * Created by abisoyeoke-lawal on 8/26/17.
 */

public class RVCoupinAdapter extends RecyclerView.Adapter<com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVCoupinAdapter.ItemViewHolder> {
    public List<Reward> rewardListItems;

    static public MyOnClick myOnClick;

    @Override
    public com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVCoupinAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_expand_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVCoupinAdapter(List<Reward> rewardListItems, MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
        this.rewardListItems = rewardListItems;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Add data here
        Reward reward = rewardListItems.get(position);

        try {
            holder.title.setText(reward.getTitle());
            holder.details.setText(reward.getDetails());
            if(reward.getIsDiscount()) {
                float oldPrice = reward.getOldPrice();
                float newPrice = reward.getNewPrice();
                float discount = ((oldPrice - newPrice) / oldPrice) * 100;
                holder.discount.setText(String.valueOf((int) discount) + "%");
                holder.priceNew.setText("N" + String.valueOf(((int) newPrice)));
                holder.priceOld.setText("N" + String.valueOf((int) oldPrice));
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
        public TextView details;
        public TextView discount;
        public TextView priceNew;
        public TextView priceOld;
        public TextView title;


        public ItemViewHolder(View itemView) {
            super(itemView);
            details = (TextView) itemView.findViewById(R.id.list_reward_details);
            discount = (TextView) itemView.findViewById(R.id.list_reward_percent);
            priceNew = (TextView) itemView.findViewById(R.id.list_new_price);
            priceOld = (TextView) itemView.findViewById(R.id.list_old_price);
            title = (TextView) itemView.findViewById(R.id.list_reward_title);
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
