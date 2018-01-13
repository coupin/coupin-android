package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by abisoyeoke-lawal on 10/18/17.
 */

public class RVSearchAdapter extends RecyclerView.Adapter<RVSearchAdapter.ItemViewHolder> {
    public List<Merchant> searchList;
    public static MyOnClick myOnClick;

    public RVSearchAdapter(List<Merchant> searchList, MyOnClick myOnClick) {
        this.searchList = searchList;
        this.myOnClick = myOnClick;
    };

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_hot, parent, false);
        ItemViewHolder itemHolder = new ItemViewHolder(v);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Merchant item = searchList.get(position);

        try {
            JSONArray rewardArray = new JSONArray(item.getRewards());

            holder.searchTitle.setText(item.getTitle());
            holder.searchAddress.setText(item.getAddress());

            if (rewardArray.length() > 1) {
                holder.searchRewards.setText(rewardArray.length() + " REWARDS");
            } else {
                holder.searchRewards.setText(rewardArray.getJSONObject(0).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView searchFav;
        public ImageView searchLogo;
        public ImageView searchVisited;
        public TextView searchTitle;
        public TextView searchAddress;
        public TextView searchRewards;

        public ItemViewHolder(View view) {
            super(view);

            searchTitle = (TextView) view.findViewById(R.id.hot_title);
            searchAddress = (TextView) view.findViewById(R.id.hot_address);
            searchRewards = (TextView) view.findViewById(R.id.hot_rewards);
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
