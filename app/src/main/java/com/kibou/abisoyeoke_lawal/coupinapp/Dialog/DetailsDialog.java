package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import java.text.SimpleDateFormat;

/**
 * Created by abisoyeoke-lawal on 9/1/17.
 */

public class DetailsDialog extends Dialog implements View.OnClickListener {
    public Button cancel;
    public Button fullPin;
    public Button fullRemove;
    public TextView fullDateEnd;
    public TextView fullDateStart;
    public TextView fullDescription;
    public TextView fullHeader;
    public TextView fullNewPrice;
    public TextView fullOldPrice;
    public TextView fullPercentage;
    public TextView fullReusable;

    public MyOnClick myOnClick;

    private Reward reward;

    // Applicable days
    public int applicableDays[] = new int[]{R.id.full_day0, R.id.full_day1,
        R.id.full_day2, R.id.full_day3, R.id.full_day4, R.id.full_day5,
        R.id.full_day6};

    public DetailsDialog(@NonNull Context context) {
        super(context);
    }

    public DetailsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected DetailsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DetailsDialog(@NonNull Context context, Reward reward) {
        super(context);
        this. reward = reward;
    }

    public void setClickListener(MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_full_reward_details);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        fullPin = (Button) findViewById(R.id.btn_pin);
        fullPin.setOnClickListener(this);
        fullRemove = (Button) findViewById(R.id.btn_remove);
        fullRemove.setOnClickListener(this);

        fullDateEnd = (TextView) findViewById(R.id.full_date_end);
        fullDateStart = (TextView) findViewById(R.id.full_date_start);
        fullDescription = (TextView) findViewById(R.id.full_description);
        fullHeader = (TextView) findViewById(R.id.full_header);
        fullNewPrice = (TextView) findViewById(R.id.full_new_price);
        fullOldPrice = (TextView) findViewById(R.id.full_old_price);
        fullPercentage = (TextView) findViewById(R.id.full_percentage);
        fullReusable = (TextView) findViewById(R.id.full_reusable);

        fullDescription.setText(reward.getDetails());
        fullHeader.setText(reward.getTitle());

        // Discount
        if (reward.getIsDiscount()) {
            float oldPrice = reward.getOldPrice();
            float newPrice = reward.getNewPrice();
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            fullPercentage.setText(String.valueOf((int) discount) + "%");
            fullNewPrice.setText("N" + String.valueOf(((int) newPrice)));
            fullOldPrice.setText("N" + String.valueOf((int) oldPrice));
        }

        if (reward.isSelected()) {
            Log.v("VolleyTime", "Is Selected");
            fullPin.setVisibility(View.GONE);
            fullRemove.setVisibility(View.VISIBLE);
        } else {
            Log.v("VolleyTime", "Is Not Selected");
            fullRemove.setVisibility(View.GONE);
            fullPin.setVisibility(View.VISIBLE);
        }

        // Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy");
        fullDateEnd.setText(simpleDateFormat.format(reward.getExpires()));
        fullDateStart.setText(simpleDateFormat.format(reward.getStarting()));

        // Reusable
        if (reward.getMultiple()) {
            fullReusable.setText("YES");
        } else {
            fullReusable.setText("NO");
        }

        for (int x = 0; x < reward.getDays().length(); x++) {
            try {
                if (reward.getDays().getInt(x) == 0) {
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setBackground(getContext().getResources().getDrawable(R.drawable.days_left_background));
                } else if (reward.getDays().getInt(x) == 6) {
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setBackground(getContext().getResources().getDrawable(R.drawable.days_right_background));
                } else {
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[reward.getDays().getInt(x)]))
                        .setBackground(getContext().getResources().getDrawable(R.drawable.days_background));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pin:
                myOnClick.onItemClick(0);
                dismiss();
                break;
            case R.id.btn_remove:
                myOnClick.onItemClick(1);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
