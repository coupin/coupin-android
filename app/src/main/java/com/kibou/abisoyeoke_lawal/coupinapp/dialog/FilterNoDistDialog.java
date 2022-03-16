package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyFilter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abisoyeoke-lawal on 11/7/17.
 */

public class FilterNoDistDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.filter_nd_cancel)
    public Button fillNDCancel;
    @BindView(R.id.filter_nd_save)
    public Button fillNDSave;
    @BindView(R.id.fil_nd_ent)
    public LinearLayout fillNDEnt;
    @BindView(R.id.fil_groceries)
    public LinearLayout fillNDGroceries;
    @BindView(R.id.fil_nd_food)
    public LinearLayout fillNDFood;
    @BindView(R.id.fil_nd_gadget)
    public LinearLayout fillNDGadget;
    @BindView(R.id.fil_nd_health)
    public LinearLayout fillNDHealth;
    @BindView(R.id.fil_nd_shopping)
    public LinearLayout fillNDShop;
    @BindView(R.id.fil_nd_tickets)
    public LinearLayout fillNDTickets;
    @BindView(R.id.fil_nd_travel)
    public LinearLayout fillNDTravel;

    public ArrayList<String> selected = new ArrayList<>();


    private Context context;
    private MyFilter myFilter;

    public FilterNoDistDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public FilterNoDistDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected FilterNoDistDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setInterface(MyFilter myFilter) {
        this.myFilter = myFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter_no_dist);
        ButterKnife.bind(this);

        fillNDEnt.setOnClickListener(this);
        fillNDFood.setOnClickListener(this);
        fillNDGadget.setOnClickListener(this);
        fillNDGroceries.setOnClickListener(this);
        fillNDHealth.setOnClickListener(this);
        fillNDShop.setOnClickListener(this);
        fillNDTickets.setOnClickListener(this);
        fillNDTravel.setOnClickListener(this);
        fillNDCancel.setOnClickListener(this);
        fillNDSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fil_nd_ent:
                toggleTag(fillNDEnt, "entertainment");
                break;
            case R.id.fil_nd_food:
                toggleTag(fillNDFood, "foodndrink");
                break;
            case R.id.fil_nd_gadget:
                toggleTag(fillNDGadget, "technology");
                break;
            case R.id.fil_nd_health:
                toggleTag(fillNDHealth, "healthnbeauty");
                break;
            case R.id.fil_groceries:
                toggleTag(fillNDGroceries, "groceries");
                break;
            case R.id.fil_nd_shopping:
                toggleTag(fillNDShop, "shopping");
                break;
            case R.id.fil_nd_tickets:
                toggleTag(fillNDTickets, "tickets");
                break;
            case R.id.fil_nd_travel:
                toggleTag(fillNDTravel, "travel");
                break;
            case R.id.filter_nd_cancel:
                dismiss();
            case R.id.filter_nd_save:
                filterMerchants();
        }
    }

    private void filterMerchants() {
        myFilter.onFilterSelected(selected, 0);
        dismiss();
    }

    private void toggleTag(LinearLayout fillEnt, String query) {
        ImageView imageView = (ImageView) fillEnt.getChildAt(0);
        TextView textView = (TextView) fillEnt.getChildAt(1);

        if (selected.contains(getArrayString(query))) {
            selected.remove(getArrayString(query));
            fillEnt.setBackground(context.getResources().getDrawable(R.drawable.round_edges_light_grey));
            textView.setTextColor(context.getResources().getColor(R.color.text_color_1));
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
