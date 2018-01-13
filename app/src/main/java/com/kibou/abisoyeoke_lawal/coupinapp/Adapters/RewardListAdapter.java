package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abisoyeoke-lawal on 7/10/17.
 */

public class RewardListAdapter extends BaseAdapter {
    private Context context;
    private List<RewardListItem> itemList;

    public RewardListAdapter(Context context, List<RewardListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RewardListItem reward = itemList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_header, null);
        }

        CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.expandable_image);
        if (reward.getMerchantLogo() != "null") {
            Glide.with(context).load(reward.getMerchantLogo()).into(imageView);
        }

        TextView title = (TextView) convertView.findViewById(R.id.expandable_title);
        title.setText(reward.getRewardName());

        return convertView;
    }

}
