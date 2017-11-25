package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Interest;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 11/17/17.
 */

public class InterestEditAdapter extends BaseAdapter {
    public ImageView icon;
    public LinearLayout interestHolder;
    public TextView label;

    public Context context;
    public ArrayList<Interest> interests;
    public ArrayList<String> previouslySelected = new ArrayList<>();

    public InterestEditAdapter(Context context, ArrayList<Interest> interests) {
        this.context = context;
        this.interests = interests;
        previouslySelected = PreferenceMngr.getUserInterests();
        Log.v("VolleyPrevious", previouslySelected.toString());
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
        View root = LayoutInflater.from(context).inflate(R.layout.interest_edit_item, parent, false);

        final Interest interest = interests.get(position);

        LinearLayout holder = (LinearLayout) root.findViewById(R.id.interest_holder);
        icon = (ImageView) root.findViewById(R.id.interest_img);
        interestHolder = (LinearLayout) root.findViewById(R.id.interest_holder);
        label = (TextView) root.findViewById(R.id.interest_text);
        ImageView tick = (ImageView) root.findViewById(R.id.interest_tick);

        icon.setImageResource(interest.getIcon());
        label.setText(interest.getLabel());

        if (position == interests.size() - 1) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            float d = context.getResources().getDisplayMetrics().density;
            params.setMargins(0, 0, 0, (int)(70* d));
            interestHolder.setLayoutParams(params);
        }

        if (previouslySelected.contains(interest.getValue())) {
            holder.setBackground(context.getResources().getDrawable(R.drawable.interest_default));
            tick.setVisibility(View.VISIBLE);
            interest.setSelected(true);
        }

        return root;
    }
}
