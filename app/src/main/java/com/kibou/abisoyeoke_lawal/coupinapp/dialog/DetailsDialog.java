package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Image;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardV2;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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
    public TextView quantityTextView;
    public ConstraintLayout quantityLayout;
    public EditText quantityEditText;
    public ImageView addBtn;
    public ImageView subtractBtn;
    public View bottomButtonsBarrier;

    public MyOnClick myOnClick;
    private GalleryDialog imageDialog;

    private Context context;
    private RewardV2 reward;

    private boolean isCart = false;

    // Applicable days
    private ArrayList<Image> pictures;
    public int[] applicableDays = new int[]{R.id.full_day0, R.id.full_day1,
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

    public DetailsDialog(@NonNull Context context, RewardV2 reward, boolean isCart) {
        super(context);
        this.context = context;
        this. reward = reward;
        this.isCart = isCart;
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
        quantityTextView = findViewById(R.id.available_status);
        quantityEditText = findViewById(R.id.quantity_edittext);
        quantityLayout = findViewById(R.id.quantity_layout);
        bottomButtonsBarrier = findViewById(R.id.bottom_button_barrier);
        addBtn = findViewById(R.id.add_quantity);
        subtractBtn = findViewById(R.id.subtract_quantity);
        addBtn.setOnClickListener(this);
        subtractBtn.setOnClickListener(this);

        fullDescription.setText(reward.description);
        fullHeader.setText(reward.name);

        fullOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        // Discount
        if (reward.isDiscount) {
            float oldPrice = reward.price.oldPrice;
            float newPrice = reward.price.newPrice;
            float discount = ((oldPrice - newPrice) / oldPrice) * 100;
            fullPercentage.setText(StringUtils.currencyFormatter((int) discount) + "%");
            fullNewPrice.setText("N" + StringUtils.currencyFormatter(((int) newPrice)));
            fullOldPrice.setText("N" + StringUtils.currencyFormatter((int) oldPrice));
            fullPercentage.setVisibility(View.VISIBLE);
            fullNewPrice.setVisibility(View.VISIBLE);
            fullOldPrice.setVisibility(View.VISIBLE);
            String quantityString = String.valueOf(reward.quantity);
            quantityTextView.setText(quantityString);
            bottomButtonsBarrier.setVisibility(View.VISIBLE);
        } else if (reward.price.oldPrice > 0) {
            String priceString = "N" + StringUtils.currencyFormatter((int) reward.price.oldPrice);
            fullOldPrice.setText(priceString);
            fullOldPrice.setVisibility(View.VISIBLE);
            fullPercentage.setVisibility(View.INVISIBLE);
            bottomButtonsBarrier.setVisibility(View.VISIBLE);
        } else if (reward.price.newPrice > 0) {
            String priceString = "N" + StringUtils.currencyFormatter((int) reward.price.newPrice);
            fullNewPrice.setText(priceString);
            fullNewPrice.setVisibility(View.VISIBLE);
            fullPercentage.setVisibility(View.INVISIBLE);
            bottomButtonsBarrier.setVisibility(View.VISIBLE);
        }

        pictures = reward.pictures;
        ImageView[] holders = new ImageView[]{photo1, photo2, photo3, photo4};

        if (pictures != null) {
            try {
                if (pictures.size() >= 4) {
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

                for (int x = 0; x < pictures.size(); x++) {
                    thumbnails.add(pictures.get(x).url);
                    Glide.with(context).load(pictures.get(x).url).into(holders[x]);
                    holders[x].setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(isCart){
            fullRemove.setVisibility(View.GONE);
            fullPin.setVisibility(View.VISIBLE);
        }else if (reward.isSelected) {
            fullPin.setVisibility(View.GONE);
            fullRemove.setVisibility(View.VISIBLE);
        } else {
            fullRemove.setVisibility(View.GONE);
            fullPin.setVisibility(View.VISIBLE);
        }

        // Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        fullDateEnd.setText(simpleDateFormat.format(Objects.requireNonNull(TypeUtils.stringToDate(reward.endDate))));
        fullDateStart.setText(simpleDateFormat.format(Objects.requireNonNull(TypeUtils.stringToDate(reward.startDate))));

        // Reusable
        if (reward.isMultiple) {
            quantityEditText.setText(String.valueOf(reward.quantity));
            addBtn.setEnabled(true);
            subtractBtn.setEnabled(true);
            String quantityString = reward.quantity + " in stock";
            quantityTextView.setText(quantityString);
        } else {
            quantityTextView.setText("Limited to 1 per Customer");
            addBtn.setEnabled(false);
            subtractBtn.setEnabled(false);
            quantityEditText.setText(String.valueOf(1));
        }

        fullDelivery.setText(reward.isDelivery ? "YES" : "No");

        if (hideButton) {
            buttonHolder.setVisibility(View.GONE);
            quantityLayout.setVisibility(View.GONE);
        }

        for (int x = 0; x < reward.days.size(); x++) {
            try {
                int day = reward.days.get(x);
                if (day == 0) {
                    ((TextView) findViewById(applicableDays[day]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[day]))
                        .setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.days_left_background, null));
                } else if (day == 6) {
                    ((TextView) findViewById(applicableDays[day]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[day]))
                        .setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.days_right_background, null));
                } else {
                    ((TextView) findViewById(applicableDays[day]))
                        .setTextColor(getContext().getResources().getColor(R.color.black));
                    ((TextView) findViewById(applicableDays[day]))
                        .setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.days_background, null));
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
            if (pictures.size() > 0) {
                imageDialog = new GalleryDialog(context, pictures.get(position).url, thumbnails);
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
                String enteredQuantity = quantityEditText.getText().toString().trim();
                if(isQuantityValid(enteredQuantity)){
                    myOnClick.onItemClick(0, Integer.parseInt(enteredQuantity));
                    dismiss();
                }
                break;
            case R.id.btn_remove:
                myOnClick.onItemClick(1, 0);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.add_quantity:
                int quantityInEdittext = Integer.parseInt(quantityEditText.getText().toString().trim());
                int newQuantity = quantityInEdittext + 1;
                quantityEditText.setText(String.valueOf(newQuantity));
                break;
            case R.id.subtract_quantity:
                int quantityInEdittextSubtract = Integer.parseInt(quantityEditText.getText().toString().trim());
                if(quantityInEdittextSubtract>1){
                    int newQuantitySubtract = quantityInEdittextSubtract - 1;
                    quantityEditText.setText(String.valueOf(newQuantitySubtract));
                }
                break;
        }
    }

    private boolean isQuantityValid(String enteredQuantity){
        int availableQuantity = reward.quantity;
        if(enteredQuantity.isEmpty()){
            quantityEditText.setError("Enter a Quantity");
            return false;
        }

        if(!(Integer.parseInt(enteredQuantity) <= availableQuantity)){
            quantityEditText.setError("Quantity is not valid");
            return false;
        }
        return true;
    }

}
