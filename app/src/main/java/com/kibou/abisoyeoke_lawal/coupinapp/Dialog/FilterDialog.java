package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.CustomViews.DistanceSeekBar;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abisoyeoke-lawal on 10/31/17.
 */

public class FilterDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.distance)
    public DistanceSeekBar distanceSeekBar;
    @BindView(R.id.fil_ent)
    public LinearLayout fillEnt;
    @BindView(R.id.fil_food)
    public LinearLayout fillFood;
    @BindView(R.id.fil_gadget)
    public LinearLayout fillGadget;
    @BindView(R.id.fil_health)
    public LinearLayout fillHealth;
    @BindView(R.id.fil_shopping)
    public LinearLayout fillShop;
    @BindView(R.id.fil_tickets)
    public LinearLayout fillTickets;
    @BindView(R.id.fil_travel)
    public LinearLayout fillTravel;
    @BindView(R.id.filter_cancel)
    public TextView fillCancel;
    @BindView(R.id.filter_save)
    public TextView fillSave;

    public ArrayList<String> selected = new ArrayList<>();

    private Context context;
    private MyFilter myFilter;

    public FilterDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public FilterDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected FilterDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setInterface(MyFilter myFilter) {
        this.myFilter = myFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter);
        ButterKnife.bind(this);

        distanceSeekBar.setProgress(30);
        fillEnt.setOnClickListener(this);
        fillFood.setOnClickListener(this);
        fillGadget.setOnClickListener(this);
        fillHealth.setOnClickListener(this);
        fillShop.setOnClickListener(this);
        fillTickets.setOnClickListener(this);
        fillTravel.setOnClickListener(this);
        fillCancel.setOnClickListener(this);
        fillSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fil_ent:
                toggleTag(fillEnt);
                break;
            case R.id.fil_food:
                toggleTag(fillFood);
                break;
            case R.id.fil_gadget:
                toggleTag(fillGadget);
                break;
            case R.id.fil_health:
                toggleTag(fillHealth);
                break;
            case R.id.fil_shopping:
                toggleTag(fillShop);
                break;
            case R.id.fil_tickets:
                toggleTag(fillTickets);
                break;
            case R.id.fil_travel:
                toggleTag(fillTravel);
                break;
            case R.id.filter_cancel:
                dismiss();
            case R.id.filter_save:
                filterMerchants();
        }
    }

    private void filterMerchants() {
        myFilter.onFilterSelected(selected, distanceSeekBar.getProgress());
        dismiss();
    }

    private void toggleTag(LinearLayout fillEnt) {
        ImageView imageView = (ImageView) fillEnt.getChildAt(0);
        TextView textView = (TextView) fillEnt.getChildAt(1);

        String query = textView.getText().toString().toLowerCase();
        if (selected.contains(getArrayString(query))) {
            selected.remove(getArrayString(query));
            fillEnt.setBackground(context.getResources().getDrawable(R.drawable.round_edges_light_grey));
            textView.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
            imageView.setColorFilter(Color.argb(255, 53, 52, 61));
        } else {
            selected.add(getArrayString(query));
            fillEnt.setBackground(context.getResources().getDrawable(R.drawable.round_edges_grey));
            textView.setTextColor(context.getResources().getColor(R.color.white));
            imageView.setColorFilter(Color.argb(255, 255, 255, 255));
        }
    }

    private String getArrayString(String x) {
        return "\"" + x + "\"";
    }
}
