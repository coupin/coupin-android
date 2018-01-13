package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    public ImageView fullHeaderImage;
    public ImageView photo1;
    public ImageView photo2;
    public ImageView photo3;
    public LinearLayout buttonHolder;
    public TextView fullDateEnd;
    public TextView fullDateStart;
    public TextView fullDescription;
    public TextView fullHeader;
    public TextView fullNewPrice;
    public TextView fullOldPrice;
    public TextView fullPercentage;
    public TextView fullReusable;

    public MyOnClick myOnClick;

    private Context context;
    private Reward reward;

    // Applicable days
    public int applicableDays[] = new int[]{R.id.full_day0, R.id.full_day1,
        R.id.full_day2, R.id.full_day3, R.id.full_day4, R.id.full_day5,
        R.id.full_day6};
    public boolean hideButton = false;

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
        this.context = context;
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

        photo1 = (ImageView) findViewById(R.id.photo_1);
        photo2 = (ImageView) findViewById(R.id.photo_2);
        photo3 = (ImageView) findViewById(R.id.photo_3);

        buttonHolder = (LinearLayout) findViewById(R.id.details_button_group);

        fullDateEnd = (TextView) findViewById(R.id.full_date_end);
        fullDateStart = (TextView) findViewById(R.id.full_date_start);
        fullDescription = (TextView) findViewById(R.id.full_description);
        fullHeader = (TextView) findViewById(R.id.full_header);
        fullHeaderImage = (ImageView) findViewById(R.id.full_header_tick);
        fullNewPrice = (TextView) findViewById(R.id.full_new_price);
        fullOldPrice = (TextView) findViewById(R.id.full_old_price);
        fullPercentage = (TextView) findViewById(R.id.full_percentage);
        fullReusable = (TextView) findViewById(R.id.full_reusable);

        fullDescription.setText(reward.getDetails());
        fullHeader.setText(reward.getTitle());

        fullOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        // Discount
        if (reward.getIsDiscount()) {
            float oldPrice = reward.getOldPrice();
            float newPrice = reward.getNewPrice();
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            fullPercentage.setText(String.valueOf((int) discount) + "%");
            fullNewPrice.setText("N" + String.valueOf(((int) newPrice)));
            fullOldPrice.setText("N" + String.valueOf((int) oldPrice));
        }

        Glide.with(context).load("http://res.cloudinary.com/mybookingngtest/image/upload/v1510409658/Mask_Group_1_ucjx1i.png").into(photo1);
        Glide.with(context).load("http://res.cloudinary.com/mybookingngtest/image/upload/v1510409660/Mask_Group_2_odbzxx.png").into(photo2);
        Glide.with(context).load("http://res.cloudinary.com/mybookingngtest/image/upload/v1510409666/Mask_Group_mc9jlu.png").into(photo3);

        if (reward.isSelected()) {
            fullHeader.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            fullHeaderImage.setColorFilter(context.getResources().getColor(R.color.white));
            fullHeader.setTextColor(context.getResources().getColor(R.color.white));
            fullPin.setVisibility(View.GONE);
            fullRemove.setVisibility(View.VISIBLE);
        } else {
            fullHeader.setBackgroundColor(context.getResources().getColor(R.color.white));
            fullHeaderImage.setColorFilter(context.getResources().getColor(R.color.white));
            fullHeader.setTextColor(context.getResources().getColor(R.color.text_dark_grey));
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

        if (hideButton) {
            buttonHolder.setVisibility(View.GONE);
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

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void hideButtonGroup() {
        hideButton = true;
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
