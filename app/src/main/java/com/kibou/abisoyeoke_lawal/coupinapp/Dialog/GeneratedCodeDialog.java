package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.ButterKnife;

/**
 * Created by abisoyeoke-lawal on 7/8/17.
 */

public class GeneratedCodeDialog extends Dialog implements View.OnClickListener {
    public Activity activity;

//    @BindView(R.id.code_loadingview)
    public AVLoadingIndicatorView loadingView;
//    @BindView(R.id.btn_dialog_ok)
    public Button btnOk;
//    @BindView(R.id.btn_dialog_go)
    public Button btnGenCode;
//    @BindView(R.id.btn_dialog_verify)
    public Button btnVerifyNumber;
//    @BindView(R.id.mobile_number_edittextview)
    public EditText mobileNumberView;
//    @BindView(R.id.mobile_view)
    public LinearLayout mobileView;
//    @BindView(R.id.code_main)
    public RelativeLayout mainView;
//    @BindView(R.id.generated_code_textview)
    public TextView codeView;

    public Dialog dialog;
    public Fragment fragment;
    public MyOnClick myOnClick;
    public LatLng latLng;
    public String code;

    public GeneratedCodeDialog(Fragment fragment) {
        super(fragment.getActivity());
        this.activity = fragment.getActivity();
        this.fragment = fragment;
    }

    /**
     * Set the code the and the geolocation of the address
     * @param {String} code
     * @param {LatLng} latLng
     */
    public void setCodeAndDirection(String code, LatLng latLng) {
        codeView.setText(code);
        this.latLng = latLng;
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_full_reward_details);
        ButterKnife.bind(this);

        String mobile = PreferenceMngr.getMobileNumber();
        if (mobile != null) {
            mobileNumberView.setText(mobile);
        }

        btnOk.setOnClickListener(this);
        btnGenCode.setOnClickListener(this);
        btnVerifyNumber.setOnClickListener(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimateUtils.crossFadeViews(mobileView, loadingView);
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_dialog_go:
//                navigate();
//                break;
//            case R.id.btn_dialog_ok:
//                dismiss();
//            break;
//            case R.id.btn_dialog_verify:
//                verifyNumber();
//                break;
            default:
                dismiss();
        }
    }

    public void showCodeAndDirection() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimateUtils.crossFadeViews(mainView, loadingView);
            }
        }, 2000);
    }

    private void verifyNumber() {
        if (mobileNumberView.getEditableText().toString().length() < 11) {
            mobileNumberView.setError("Number invalid. Please enter 11 digit mobile number");
            mobileNumberView.requestFocus();
        } else {
            String number = mobileNumberView.getEditableText().toString();
            if (!(number == PreferenceMngr.getMobileNumber())) {
                PreferenceMngr.setMobileNumber(mobileNumberView.getEditableText().toString());
            }

            AnimateUtils.crossFadeViews(loadingView, mobileView);
            fragment.onActivityResult(fragment.getTargetRequestCode(), 1001, activity.getIntent());
        }
    }

    private void navigate() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + latLng.latitude + "," + latLng.longitude));
        activity.startActivity(intent);
    }
}
