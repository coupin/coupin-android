package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.DetailsDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 8/13/17.
 */

public class RVExpandableAdapter extends RecyclerView.Adapter<RVExpandableAdapter.ViewHolder> {
    public ArrayList<Reward> rewards;
    public Context context;
    public boolean drawerVisible = false;

    static public MyOnSelect myOnSelect;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout tickFrame;
        public RelativeLayout headDiscount;
        public TextView headDetails;
        public TextView headExpiry;
        public TextView headPercentage;
        public TextView headPriceNew;
        public TextView headPriceOld;
        public TextView headTitle;
        public View head;

        public ViewHolder(View itemView) {
            super(itemView);
            head = (View) itemView.findViewById(R.id.head);
            headDiscount = (RelativeLayout) head.findViewById(R.id.discount);
            headDetails = (TextView) head.findViewById(R.id.list_reward_details);
            headExpiry = (TextView) head.findViewById(R.id.expiry_text);
            headPercentage = (TextView) head.findViewById(R.id.list_reward_percent);
            headPriceNew = (TextView) head.findViewById(R.id.list_new_price);
            headPriceOld = (TextView) head.findViewById(R.id.list_old_price);
            headTitle = (TextView) head.findViewById(R.id.list_reward_title);
            tickFrame = (FrameLayout) head.findViewById(R.id.tick_frame);
        }
    }

    public RVExpandableAdapter(ArrayList<Reward> rewards, Context context, MyOnSelect myOnSelect) {
        this.context = context;
        this.myOnSelect = myOnSelect;
        this.rewards = rewards;
    }

    @Override
    public RVExpandableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_expand_item, parent, false);

        RVExpandableAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RVExpandableAdapter.ViewHolder holder, final int position) {
        final Reward reward = rewards.get(position);

        holder.headDetails.setText(reward.getDetails());
        if (reward.getIsDiscount()) {
            float oldPrice = reward.getOldPrice();
            float newPrice = reward.getNewPrice();
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            holder.headPercentage.setText(String.valueOf((int) discount) + "%");
            holder.headPriceNew.setText("N" + String.valueOf(((int) newPrice)));
            holder.headPriceOld.setText("N" + String.valueOf((int) oldPrice));
            holder.headPriceOld.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }

        holder.headTitle.setText(String.valueOf(reward.getTitle()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        holder.headExpiry.setText(simpleDateFormat.format(reward.getExpires()));

        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsDialog detailsDialog = new DetailsDialog(context, reward);

                detailsDialog.setClickListener(new MyOnClick() {
                    @Override
                    public void onItemClick(int place) {
                        if (place == 0) {
                            holder.head.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                            holder.tickFrame.setVisibility(View.VISIBLE);
                            holder.headDetails.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headPercentage.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headTitle.setTextColor(context.getResources().getColor(R.color.white));
                            reward.setIsSelected(true);
                            myOnSelect.onSelect(true, position);
                        } else {
                            holder.head.setBackgroundColor(context.getResources().getColor(R.color.white));
                            holder.tickFrame.setVisibility(View.GONE);
                            holder.headDetails.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                            holder.headPercentage.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                            holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.text_lighter_grey));
                            holder.headTitle.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
                            reward.setIsSelected(false);
                            myOnSelect.onSelect(false, position);
                        }
                    }
                });
                detailsDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }
}
