package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 7/22/17.
 */

public class RewardInfoDialog extends Dialog implements View.OnClickListener {
    public Button btnDiscard;
    public Button btnKeep;
    public Context context;

    public MyOnClick myOnClick;

    public RewardInfoDialog(@NonNull Context context, MyOnClick myOnClick) {
        super(context);

        this.context = context;
        this.myOnClick = myOnClick;
    }

    public RewardInfoDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_exiting_merchant);

        btnDiscard = (Button) findViewById(R.id.cancel);
        btnKeep = (Button) findViewById(R.id.btn_keep);

        btnDiscard.setOnClickListener(this);
        btnKeep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_keep:
                myOnClick.onItemClick(0);
                break;
            case R.id.cancel:
                myOnClick.onItemClick(1);
                break;
        }
    }
}
