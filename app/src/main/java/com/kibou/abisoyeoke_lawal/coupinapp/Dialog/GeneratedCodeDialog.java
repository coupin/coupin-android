package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abisoyeoke-lawal on 7/8/17.
 */

public class GeneratedCodeDialog extends Dialog implements View.OnClickListener {
    public Activity activity;

    @BindView(R.id.code_loadingview)
    public AVLoadingIndicatorView loadingView;
    @BindView(R.id.btn_dialog_ok)
    public Button btnOk;
    @BindView(R.id.btn_dialog_go)
    public Button btnGenCode;
    @BindView(R.id.code_main)
    public RelativeLayout mainView;
    @BindView(R.id.generated_code_textview)
    public TextView codeView;

    public Dialog dialog;
    public LatLng latLng;
    public String code;

    public GeneratedCodeDialog(Activity activity, String code, LatLng latLng) {
        super(activity);

        this.activity = activity;
        this.code = code;
        this.latLng = latLng;
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_generated_code);
        ButterKnife.bind(this);

        codeView.setText(code);

        btnOk.setOnClickListener(this);
        btnGenCode.setOnClickListener(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimateUtils.crossFadeViews(mainView, loadingView);
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_ok:
                dismiss();
                break;
            case R.id.btn_dialog_go:
                navigate();
                break;
            default:
                dismiss();
        }
    }

    private void navigate() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + latLng.latitude + "," + latLng.longitude));
        activity.startActivity(intent);
    }
}
