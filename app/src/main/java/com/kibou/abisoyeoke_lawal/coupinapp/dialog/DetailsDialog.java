package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

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
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Reward;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public ImageView photo4;
    public LinearLayout buttonHolder;
    public ArrayList<String> thumbnails = new ArrayList<>();
    public TextView fullDateEnd;
    public TextView fullDateStart;
    public TextView fullDelivery;
    public TextView fullDescription;
    public TextView fullHeader;
    public TextView fullNewPrice;
    public TextView fullOldPrice;
    public TextView fullPercentage;
    public TextView fullReusable;

    public MyOnClick myOnClick;
    private GalleryDialog imageDialog;

    private Context context;
    private Reward reward;

    // Applicable days
    public int applicableDays[] = new int[]{R.id.full_day0, R.id.full_day1,
        R.id.full_day2, R.id.full_day3, R.id.full_day4, R.id.full_day5,
        R.id.full_day6};
    public boolean hideButton = false;
    private JSONArray pictures;

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
        photo4 = (ImageView) findViewById(R.id.photo_4);
        photo1.setOnClickListener(this);
        photo2.setOnClickListener(this);
        photo3.setOnClickListener(this);
        photo4.setOnClickListener(this);

        buttonHolder = (LinearLayout) findViewById(R.id.details_button_group);

        fullDateEnd = (TextView) findViewById(R.id.full_date_end);
        fullDateStart = (TextView) findViewById(R.id.full_date_start);
        fullDelivery = (TextView) findViewById(R.id.full_delivers);
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
            fullPercentage.setVisibility(View.VISIBLE);
            fullNewPrice.setVisibility(View.VISIBLE);
            fullOldPrice.setVisibility(View.VISIBLE);
        }

        pictures = reward.getPictures();
        ImageView holders[] = new ImageView[]{photo1, photo2, photo3, photo4};

        if (pictures != null) {
            try {
                if (pictures.length() >= 4) {
                    int dim = (int)context.getResources().getDimension(R.dimen.image_size);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dim,
                        dim,
                        1.0f
                    );
                    photo1.setLayoutParams(params);
                    photo2.setLayoutParams(params);
                    photo3.setLayoutParams(params);
                    photo4.setLayoutParams(params);
                }

                for (int x = 0; x < pictures.length(); x++) {
                    thumbnails.add(pictures.getJSONObject(x).getString("url"));
                    Glide.with(context).load(pictures.getJSONObject(x).getString("url")).into(holders[x]);
                    holders[x].setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        if (reward.getIsDelivery()) {
            fullDelivery.setText("YES");
        } else {
            fullDelivery.setText("NO");
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

    public void showImageDialog(int position) {
        try {
            if (pictures.length() > 0) {
                imageDialog = new GalleryDialog(context, pictures.getJSONObject(position).getString("url"), thumbnails);
                imageDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_1:
                showImageDialog(0);
                break;
            case R.id.photo_2:
                showImageDialog(1);
                break;
            case R.id.photo_3:
                showImageDialog(2);
                break;
            case R.id.photo_4:
                showImageDialog(3);
                break;
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
