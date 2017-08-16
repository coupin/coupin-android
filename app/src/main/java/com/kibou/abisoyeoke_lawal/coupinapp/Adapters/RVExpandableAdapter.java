package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 8/13/17.
 */

public class RVExpandableAdapter extends RecyclerView.Adapter<RVExpandableAdapter.ViewHolder> {
    public ArrayList<Reward> rewards;
    public Context context;
    public boolean drawerVisible = false;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button bodyPinBtn;
        public Button bodyRemoveBtn;
        public FrameLayout tickFrame;
        public RelativeLayout headDiscount;
        public TextView bodyDetails;
        public TextView bodyPriceOld;
        public TextView bodyPriceNew;
        public TextView headDetails;
        public TextView headPercentage;
        public TextView headPriceNew;
        public TextView headPriceOld;
        public TextView headTitle;
        public View head;
        public View body;

        public ViewHolder(View itemView) {
            super(itemView);

            body = (View) itemView.findViewById(R.id.body);
            bodyDetails = (TextView) body.findViewById(R.id.list_reward_full_details);
            bodyPinBtn = (Button) body.findViewById(R.id.btn_pin);
            bodyPriceNew = (TextView) body.findViewById(R.id.list_new_price);
            bodyPriceOld = (TextView) body.findViewById(R.id.list_old_price);
            bodyRemoveBtn = (Button) body.findViewById(R.id.btn_remove);
            head = (View) itemView.findViewById(R.id.head);
            headDiscount = (RelativeLayout) head.findViewById(R.id.discount);
            headDetails = (TextView) head.findViewById(R.id.list_reward_details);
            headPercentage = (TextView) head.findViewById(R.id.list_reward_percent);
            headPriceNew = (TextView) head.findViewById(R.id.list_new_price);
            headPriceOld = (TextView) head.findViewById(R.id.list_old_price);
            headTitle = (TextView) head.findViewById(R.id.list_reward_title);
            tickFrame = (FrameLayout) head.findViewById(R.id.tick_frame);
        }
    }

    public RVExpandableAdapter(ArrayList<Reward> rewards, Context context) {
        this.context = context;
        this.rewards = rewards;
    }

    @Override
    public RVExpandableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_expand_item, parent, false);

        RVExpandableAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RVExpandableAdapter.ViewHolder holder, int position) {
        Reward reward = rewards.get(position);

        holder.headDetails.setText(reward.getDetails());
        holder.bodyDetails.setText(reward.getDetails());
        if (reward.getIsDiscount()) {
            float oldPrice = reward.getOldPrice();
            float newPrice = reward.getNewPrice();
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            holder.headPercentage.setText(String.valueOf((int) discount) + "%");
            holder.headPriceNew.setText("N" + String.valueOf(((int) newPrice)));
            holder.headPriceOld.setText("N" + String.valueOf((int) oldPrice));
            holder.bodyPriceNew.setText("N" + String.valueOf(((int) newPrice)));
            holder.bodyPriceOld.setText("N" + String.valueOf((int) oldPrice));
        }

        holder.headTitle.setText(String.valueOf(reward.getTitle()));

        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerVisible) {
                    holder.body.setVisibility(View.GONE);
                    holder.headDiscount.setVisibility(View.VISIBLE);
                    drawerVisible = false;
                } else {
                    holder.headDiscount.setVisibility(View.GONE);
                    holder.body.setVisibility(View.VISIBLE);
                    drawerVisible = true;
                }
            }
        });

        holder.bodyPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.head.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                holder.tickFrame.setVisibility(View.VISIBLE);
                holder.headDetails.setTextColor(context.getResources().getColor(R.color.white));
                holder.headPercentage.setTextColor(context.getResources().getColor(R.color.white));
                holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.white));
                holder.headTitle.setTextColor(context.getResources().getColor(R.color.white));
                holder.body.setVisibility(View.GONE);
                holder.headDiscount.setVisibility(View.VISIBLE);
                holder.bodyPinBtn.setVisibility(View.GONE);
                holder.bodyRemoveBtn.setVisibility(View.VISIBLE);
                holder.body.setBackground(context.getResources().getDrawable(R.drawable.body_selected));
            }
        });

        holder.bodyRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.head.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.tickFrame.setVisibility(View.GONE);
                holder.headDetails.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                holder.headPercentage.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.text_lighter_grey));
                holder.headTitle.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                holder.body.setVisibility(View.GONE);
                holder.headDiscount.setVisibility(View.VISIBLE);
                holder.bodyRemoveBtn.setVisibility(View.GONE);
                holder.bodyPinBtn.setVisibility(View.VISIBLE);
                holder.body.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }
}
