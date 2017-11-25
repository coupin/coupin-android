package com.kibou.abisoyeoke_lawal.coupinapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.FaqModel;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 11/18/17.
 */

public class RVFaqAdapter extends RecyclerView.Adapter<RVFaqAdapter.ViewHolder> {
    public ArrayList<FaqModel> faqs;
    public Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView faqIndicator;
        public TextView faqDetails;
        public TextView faqTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            faqIndicator = (ImageView) itemView.findViewById(R.id.indicator_img);
            faqDetails = (TextView) itemView.findViewById(R.id.faq_details);
            faqTitle = (TextView) itemView.findViewById(R.id.faq_title);
        }
    }

    public RVFaqAdapter(ArrayList<FaqModel> faqs, Context context) {
        this.context = context;
        this.faqs = faqs;
    }

    @Override
    public RVFaqAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RVFaqAdapter.ViewHolder viewHolder, int i) {
        final FaqModel faq = faqs.get(i);

        viewHolder.faqDetails.setText(faq.getDetail());
        viewHolder.faqTitle.setText(faq.getTitle());

        viewHolder.faqTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(faq.isOpen()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    viewHolder.faqDetails.setLayoutParams(params);
                    viewHolder.faqIndicator.setImageResource(R.drawable.ic_down_angle_1);
                    faq.setOpen(false);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    viewHolder.faqDetails.setLayoutParams(params);
                    viewHolder.faqIndicator.setImageResource(R.drawable.ic_up_angle_1);
                    faq.setOpen(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqs.size();
    }
}
