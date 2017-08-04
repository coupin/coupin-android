package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.models.ListItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 6/3/17.
 */

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.IconListViewHolder> {
    public ArrayList<ListItem> iconList = new ArrayList<>();
    private CustomClickListener.OnItemClickListener mClickListener;

    public IconListAdapter(ArrayList<ListItem> items) {
        iconList = items;
    }

    public IconListAdapter(ArrayList<ListItem> items, CustomClickListener.OnItemClickListener mClickListener) {
        iconList = items;
        this.mClickListener = mClickListener;
    }

    @Override
    public IconListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create View
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_list_item, parent, false);

        IconListViewHolder viewHolder = new IconListViewHolder(v, mClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IconListViewHolder holder, int position) {
        holder.iconView.setImageResource(iconList.get(position).getPicture());
    }

    public void setClickListener(CustomClickListener.OnItemClickListener callback) {
        mClickListener = callback;
    }

    public void setIconList(ArrayList<ListItem> item) {
        iconList = item;
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class IconListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView iconView;

        public IconListViewHolder(View v, CustomClickListener.OnItemClickListener onClickListener) {
            super(v);
            iconView = (RoundedImageView) v.findViewById(R.id.icon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.OnClick(v, getAdapterPosition());
            }
        }
    }
}
