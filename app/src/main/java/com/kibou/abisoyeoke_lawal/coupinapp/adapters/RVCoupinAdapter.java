package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.DetailsDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by abisoyeoke-lawal on 8/26/17.
 */

public class RVCoupinAdapter extends RecyclerView.Adapter<com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVCoupinAdapter.ItemViewHolder> {
    public List<Reward> rewardListItems;
    static public MyOnClick myOnClick;
    public Context context;

    @Override
    public com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVCoupinAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_expand_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVCoupinAdapter(List<Reward> rewardListItems, MyOnClick myOnClick, Context context) {
        RVCoupinAdapter.myOnClick = myOnClick;
        this.rewardListItems = rewardListItems;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Add data here
        final Reward reward = rewardListItems.get(position);

        try {
            holder.title.setText(reward.getTitle());
            holder.details.setText(reward.getDetails());
            if(reward.getIsDiscount()) {
                float oldPrice = reward.getOldPrice();
                float newPrice = reward.getNewPrice();
                float discount = ((oldPrice - newPrice) / oldPrice) * 100;
                holder.discount.setText(((int) discount) + "%");
                holder.priceNew.setText("N" + (((int) newPrice)));
                holder.priceOld.setText("N" + ((int) oldPrice));
                holder.priceOld.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                holder.quantity.setText("x " + reward.getQuantity());
            } else if (reward.getOldPrice() > 0) {
                String oldPriceString = "N" + (int) reward.getOldPrice();
                holder.priceOld.setText(oldPriceString);
            } else if (reward.getNewPrice() > 0) {
                String newPriceString = "N" + (int) reward.getNewPrice();
                holder.priceOld.setText(newPriceString);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            holder.expiry.setText(simpleDateFormat.format(reward.getExpires()));

            holder.head.setOnClickListener(view -> {
                DetailsDialog detailsDialog = new DetailsDialog(context, reward, false);
                detailsDialog.hideButtonGroup();
                detailsDialog.show();
            });
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
        public TextView expiry;
        public TextView priceNew;
        public TextView priceOld;
        public TextView title;
        public View head;
        public TextView quantity;


        public ItemViewHolder(View itemView) {
            super(itemView);
            head = (View) itemView.findViewById(R.id.head);
            details = (TextView) itemView.findViewById(R.id.list_reward_details);
            discount = (TextView) itemView.findViewById(R.id.list_reward_percent);
            expiry = (TextView) head.findViewById(R.id.expiry_text);
            priceNew = (TextView) itemView.findViewById(R.id.list_new_price);
            priceOld = (TextView) itemView.findViewById(R.id.list_old_price);
            title = (TextView) itemView.findViewById(R.id.list_reward_title);
            quantity = (TextView) itemView.findViewById(R.id.quantity_label);

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
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
