package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnSelect;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

/**
 * Created by abisoyeoke-lawal on 4/7/18.
 */

public class UpdateDialog extends Dialog implements View.OnClickListener {
    public Button closeButton;
    public Button updateButton;

    private MyOnSelect myOnSelect;

    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateDialog(@NonNull Context context, MyOnSelect myOnSelect) {
        super(context);
        this.myOnSelect = myOnSelect;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update);

        closeButton = (Button) findViewById(R.id.cancel);
        updateButton = (Button) findViewById(R.id.update);

        closeButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                myOnSelect.onSelect(false, PreferenceManager.getLastAttempt());
                dismiss();
                break;
            case R.id.update:
                myOnSelect.onSelect(true, PreferenceManager.getLastAttempt());
                break;
        }
    }
}
