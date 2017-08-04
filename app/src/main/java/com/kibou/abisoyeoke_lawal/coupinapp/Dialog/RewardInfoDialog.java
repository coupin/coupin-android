package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.RewardListItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abisoyeoke-lawal on 7/22/17.
 */

public class RewardInfoDialog extends Dialog implements View.OnClickListener{
    @BindView(R.id.btn_reward_dialog_go)
    public Button rewardGo;
    @BindView(R.id.dialog_reward_code)
    public TextView rewardCode;
    @BindView(R.id.dialog_reward_detail)
    public TextView rewardDetail;
    @BindView(R.id.dialog_reward_expiration_date)
    public TextView rewardExpirationDate;
    @BindView(R.id.dialog_reward_merchant_address)
    public TextView rewardMerchantAddress;
    @BindView(R.id.dialog_reward_merchant_name)
    public TextView rewardMerchantName;

    public Context context;
    public RewardListItem item;

    public RewardInfoDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    public RewardInfoDialog(@NonNull Context context, RewardListItem item) {
        super(context);

        this.context = context;
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reward_info);
        ButterKnife.bind(this);

        rewardCode.setText(item.getBookingShortCode());
        rewardDetail.setText(item.getRewardDescription());
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        rewardExpirationDate.setText(dateFormat.format(item.getExpiresDate()));
        rewardMerchantAddress.setText(item.getMerchantAddress());
        rewardMerchantName.setText(item.getMerchantName());

        rewardGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reward_dialog_go:
                Toast.makeText(context, "Go was pressed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
