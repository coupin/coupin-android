package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Location;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.CustomClickListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 6/3/17.
 */

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.IconListViewHolder> {
    public ArrayList<?> iconList = new ArrayList<>();
    private boolean isV2 = false;
    private Context context;
    private CustomClickListener.OnItemClickListener mClickListener;
    private int previousPosition;
    private View parent;
    private View previousView;

    public IconListAdapter(ArrayList<?> items, CustomClickListener.OnItemClickListener mClickListener, Context context) {
        iconList = items;
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @Override
    public IconListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        // Create View
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_list_item, parent, false);

        IconListViewHolder viewHolder = new IconListViewHolder(v, mClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IconListViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_placeholder);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        int picture;
        String logoUrl = null;

        if (isV2) {
            MerchantV2 merchantV2 = (MerchantV2) iconList.get(position);
            if (merchantV2.logo != null)
                logoUrl = merchantV2.logo.url;
            picture = merchantV2.picture;
        } else {
            Merchant merchant = (Merchant) iconList.get(position);
            if (merchant.logo != null)
                logoUrl = merchant.logo;
            picture = merchant.picture;
        }

        if (logoUrl != null && !logoUrl.isEmpty()) {
            Glide.with(context)
                .load(logoUrl)
                .apply(requestOptions)
                .into(holder.iconView);
        } else {
            holder.iconView.setImageResource(picture);
        }
    }

    public void setClickListener(CustomClickListener.OnItemClickListener callback) {
        mClickListener = callback;
    }

    public void setIconList(ArrayList<?> item) {
        iconList = item;
        isV2 = item.get(0) instanceof MerchantV2;
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
            if (getAdapterPosition() != 0) {
                ImageView x = (ImageView) v.findViewById(R.id.icon_x);
                FrameLayout y = (FrameLayout) v.findViewById(R.id.image_frame);

                if (previousView != null && previousPosition != getAdapterPosition()) {
                    ((ImageView) previousView.findViewById(R.id.icon_x)).setVisibility(View.GONE);
                    ((FrameLayout) previousView.findViewById(R.id.image_frame)).setBackgroundColor(parent.getResources().getColor(android.R.color.transparent));
                    previousView = null;
                }

                if (x.getVisibility() != View.VISIBLE) {
                    x.setVisibility(View.VISIBLE);
                    y.setBackground(ResourcesCompat.getDrawable(parent.getResources(), R.drawable.round_edges_active, null));
                    previousView = v;
                    previousPosition = getBindingAdapterPosition();
                } else {
                    x.setVisibility(View.GONE);
                    y.setBackgroundColor(parent.getResources().getColor(android.R.color.transparent));
                }
            }

            if (mClickListener != null) {
                mClickListener.OnClick(v, getBindingAdapterPosition());
            }
        }
    }

    public void clear() {
        previousView = null;
        previousPosition = 0;
        iconList.clear();
    }

    public void clearPreviousView() {
        if (previousView != null) {
            ((ImageView) previousView.findViewById(R.id.icon_x)).setVisibility(View.GONE);
            ((FrameLayout) previousView.findViewById(R.id.image_frame)).setBackgroundColor(parent.getResources().getColor(android.R.color.transparent));
            previousView = null;
        }
    }
}
