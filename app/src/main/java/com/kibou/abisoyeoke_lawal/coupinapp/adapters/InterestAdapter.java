package com.kibou.abisoyeoke_lawal.coupinapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Interest;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 9/10/17.
 */

public class InterestAdapter extends BaseAdapter {
    public ImageView icon;
    public LinearLayout interestHolder;
    public TextView label;

    public Context context;
    public ArrayList<Interest> interests;

    public InterestAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        this.interests = interests;
    }

    @Override
    public int getCount() {
        return interests.size();
    }

    @Override
    public Object getItem(int position) {
        return interests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.interest_default_item, parent, false);

        final Interest interest = interests.get(position);

        icon = (ImageView) root.findViewById(R.id.interest_img);
        interestHolder = (LinearLayout) root.findViewById(R.id.interest_holder);
        label = (TextView) root.findViewById(R.id.interest_text);

        icon.setImageResource(interest.getIcon());
        label.setText(interest.getLabel());

        if (position == interests.size() - 1) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            float d = context.getResources().getDisplayMetrics().density;
            params.setMargins(0, 0, 0, (int)(70* d));
            interestHolder.setLayoutParams(params);
        }

        return root;
    }
}
