package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.CompanyInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abisoyeoke-lawal on 7/21/17.
 */

public class SimpleListAdapter extends BaseAdapter {
    public ArrayList<CompanyInfo> merchantList = new ArrayList<>();
    public Context context;
    public static LayoutInflater inflater;

    public SimpleListAdapter(Context context, ArrayList<CompanyInfo> merchantList) {
        this.context = context;
        this.merchantList = merchantList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return merchantList.size();
    }

    @Override
    public Object getItem(int position) {
        return merchantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Holder {
        TextView name;
    }

    public void setMerchantList(ArrayList<CompanyInfo> merchantList) {
        this.merchantList.clear();
        this.merchantList = merchantList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CompanyInfo item = merchantList.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_header, null);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.expandable_title);
        textView.setText(item.getName());

        CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.expandable_image);

        if (item.getLogo() != null) {
            Glide.with(context).load(item.getLogo()).into(imageView);
        } else {
            Glide.with(context).load(R.drawable.hut).into(imageView);
        }

        return convertView;
    }
}
