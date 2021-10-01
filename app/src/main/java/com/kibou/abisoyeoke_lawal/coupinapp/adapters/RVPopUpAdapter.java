package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.isDarkModePref;

import android.content.Context;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.DetailsDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by abisoyeoke-lawal on 8/13/17.
 */

public class RVPopUpAdapter extends RecyclerView.Adapter<RVPopUpAdapter.ViewHolder> {
    public Set<String> blacklist;
    public ArrayList<Reward> rewards;
    public Context context;
    public boolean drawerVisible = false;
    private boolean isCart = false;
    static public MyOnSelect myOnSelect;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout tickFrame;
        public RelativeLayout headDiscount;
        public TextView headDetails;
        public TextView headExpiry;
        public TextView headExpiryLabel;
        public TextView headPercentage;
        public TextView headPriceNew;
        public TextView headPriceOld;
        public TextView headTitle;
        public View head;
        public View rewardDivider;
        public LinearLayout backgroud;
        public TextView quantityLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            head = (View) itemView.findViewById(R.id.head);
            headDiscount = (RelativeLayout) head.findViewById(R.id.discount);
            headDetails = (TextView) head.findViewById(R.id.list_reward_details);
            headExpiry = (TextView) head.findViewById(R.id.expiry_text);
            headExpiryLabel = (TextView) head.findViewById(R.id.expiry_label);
            headPercentage = (TextView) head.findViewById(R.id.list_reward_percent);
            headPriceNew = (TextView) head.findViewById(R.id.list_new_price);
            headPriceOld = (TextView) head.findViewById(R.id.list_old_price);
            headTitle = (TextView) head.findViewById(R.id.list_reward_title);
            rewardDivider = (View) head.findViewById(R.id.reward_divider);
            tickFrame = (FrameLayout) head.findViewById(R.id.tick_frame);
            backgroud = head.findViewById(R.id.background);
            quantityLabel = head.findViewById(R.id.quantity_label);
        }
    }

    public RVPopUpAdapter(ArrayList<Reward> rewards, Context context, MyOnSelect myOnSelect, boolean isCart) {
        this.context = context;
        this.myOnSelect = myOnSelect;
        this.rewards = rewards;
        this.isCart = isCart;
    }

    @Override
    public RVPopUpAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_expand_item, parent, false);

        RVPopUpAdapter.ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RVPopUpAdapter.ViewHolder holder, final int position) {
        final Reward reward = rewards.get(position);

        holder.headDetails.setText(reward.getDetails());
        if (reward.getIsDiscount()) {
            float oldPrice = reward.getOldPrice();
            float newPrice = reward.getNewPrice();
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            holder.headPercentage.setText(StringUtils.currencyFormatter((int) discount) + "%");
            holder.headPriceNew.setText("N" + StringUtils.currencyFormatter((int) newPrice));
            holder.headPriceOld.setText("N" + StringUtils.currencyFormatter((int) oldPrice));
            holder.headPriceOld.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else if (reward.getOldPrice() > 0) {
            String priceString = "N" + StringUtils.currencyFormatter((int) reward.getOldPrice());
            holder.headPriceOld.setText(priceString);
            holder.headPercentage.setVisibility(View.GONE);
        } else if (reward.getNewPrice() > 0) {
            String priceString = "N" + StringUtils.currencyFormatter((int) reward.getNewPrice());
            holder.headPriceOld.setText(priceString);
            holder.headPriceOld.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.headPercentage.setVisibility(View.GONE);
        }

        holder.headTitle.setText(String.valueOf(reward.getTitle()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        holder.headExpiry.setText(simpleDateFormat.format(reward.getExpires()));

        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blacklist.contains(reward.getId())) {
                    myOnSelect.onSelect(false, -1);
                    return;
                }

                DetailsDialog detailsDialog = new DetailsDialog(context, reward, isCart);

                detailsDialog.setClickListener(new MyOnClick() {
                    @Override
                    public void onItemClick(int position) { }

                    @Override
                    public void onItemClick(int place, int quantity) {
                        if (place == 0) {
                            holder.backgroud.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                            holder.head.setBackgroundColor(context.getResources().getColor(R.color.text_color_3));
                            holder.rewardDivider.setBackgroundColor(context.getResources().getColor(R.color.darkTick));
                            holder.tickFrame.setVisibility(View.VISIBLE);
                            holder.headDetails.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headExpiry.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headExpiryLabel.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headPercentage.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.white));
                            holder.headTitle.setTextColor(context.getResources().getColor(R.color.white));
                            holder.quantityLabel.setTextColor(context.getResources().getColor(R.color.white));
                            holder.quantityLabel.setText("x " + quantity);
                            reward.setIsSelected(true);
                            myOnSelect.onSelect(true, position, quantity);

                        } else {
                            Boolean isDarkMode = PreferenceMngr.getBoolean(isDarkModePref);
                            if(isDarkMode){
                                holder.backgroud.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                                holder.rewardDivider.setBackgroundColor(context.getResources().getColor(R.color.darkTick));

                            }else{
                                holder.backgroud.setBackgroundColor(context.getResources().getColor(R.color.white));
                                holder.rewardDivider.setBackgroundColor(context.getResources().getColor(R.color.lightGrey));
                            }
                            holder.head.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
                            holder.tickFrame.setVisibility(View.GONE);
                            holder.headDetails.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.headExpiry.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.headExpiryLabel.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.headPercentage.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.colorAccent));
                            holder.headTitle.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.quantityLabel.setTextColor(context.getResources().getColor(R.color.text_color_1));
                            holder.quantityLabel.setText("");
                            reward.setIsSelected(false);
                            myOnSelect.onSelect(false, position, quantity);
                        }
                    }
                });
                detailsDialog.show();
            }
        });

        if(reward.isSelected()){
            holder.backgroud.setBackgroundColor(context.getResources().getColor(R.color.darkGrey));
            holder.head.setBackgroundColor(context.getResources().getColor(R.color.text_color_3));
            holder.rewardDivider.setBackgroundColor(context.getResources().getColor(R.color.darkTick));
            holder.tickFrame.setVisibility(View.VISIBLE);
            holder.headDetails.setTextColor(context.getResources().getColor(R.color.white));
            holder.headExpiry.setTextColor(context.getResources().getColor(R.color.white));
            holder.headExpiryLabel.setTextColor(context.getResources().getColor(R.color.white));
            holder.headPercentage.setTextColor(context.getResources().getColor(R.color.white));
            holder.headPriceNew.setTextColor(context.getResources().getColor(R.color.white));
            holder.headTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.quantityLabel.setTextColor(context.getResources().getColor(R.color.white));
            holder.quantityLabel.setText("x " + reward.getSelectedQuantity());
        }
    }

    public void setBlacklist(Set<String> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

}
