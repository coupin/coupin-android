package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.DateTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by abisoyeoke-lawal on 10/9/17.
 */

public class RVBroAdapter extends RecyclerView.Adapter<RVBroAdapter.ItemViewHolder> {
    private Context context;
    private List<RewardListItem> rewardListItems;

    static public MyOnClick myOnClick;

    @Override
    public RVBroAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_reward, parent, false);
        RVBroAdapter.ItemViewHolder itemViewHolder = new RVBroAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    public RVBroAdapter(List<RewardListItem> rewardListItems, MyOnClick myOnClick, Context context) {
        this.context = context;
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
            Glide.with(context).load(reward.getMerchantLogo()).into(holder.favLogo);
            Glide.with(context).load(reward.getMerchantBanner()).into(holder.favBanner);

            if (reward.hasVisited()) {
                holder.visitedIcon.setVisibility(View.VISIBLE);
            }

            if (reward.isFavourited()) {
                holder.favIcon.setVisibility(View.VISIBLE);
            }

            if (reward.hasVisited() && reward.isFavourited()) {
                holder.divide.setVisibility(View.VISIBLE);
            }

            holder.code.setText("REDEEM REWARDS");
            holder.status.setVisibility(View.GONE);

            for (int x = 0 ; x < reward.getRewardCount(); x++) {
                JSONObject object = rewardArray.getJSONObject(x).getJSONObject("id");
                if (x == 0) {
                    temp = DateTimeUtils.convertZString(object.getString("endDate"));
                } else {
                    if (temp.after(DateTimeUtils.convertZString(object.getString("endDate")))) {
                        temp = DateTimeUtils.convertZString(object.getString("endDate"));
                    }
                }
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            holder.activeExpiration.setText(simpleDateFormat.format(temp));

            JSONObject first = rewardArray.getJSONObject(0).getJSONObject("id");
            holder.rewardOne.setText(first.getString("description"));
            if (first.has("price")) {
                float oldPrice = first.getJSONObject("price").getInt("old");
                float newPrice = first.getJSONObject("price").getInt("new");
                float discount = ((oldPrice - newPrice) / oldPrice) * 100;
                holder.rewardOnePercent.setText(((int) discount) + "%");
            } else {
                holder.rewardOnePercent.setVisibility(View.INVISIBLE);
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
                } else {
                    holder.rewardTwoPercent.setVisibility(View.INVISIBLE);
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

        public ImageView favBanner;
        public ImageView favIcon;
        public ImageView favLogo;
        public ImageView visitedIcon;
        public LinearLayout expiryHolder;
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
        public View divide;
        public TextView status;


        public ItemViewHolder(View itemView) {
            super(itemView);
            activeExpiration = (TextView) itemView.findViewById(R.id.active_expiration);
            activeFavHolder = (RelativeLayout) itemView.findViewById(R.id.active_fav_address_holder);
            activeRewardHolder = (RelativeLayout) itemView.findViewById(R.id.text_holder_1);
            activeRewardHolder2 = (RelativeLayout) itemView.findViewById(R.id.text_holder_2);
            code = (TextView) itemView.findViewById(R.id.active_code);
            divide = (View) itemView.findViewById(R.id.divide);
            expiryHolder = (LinearLayout) itemView.findViewById(R.id.expiry_holder);
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
