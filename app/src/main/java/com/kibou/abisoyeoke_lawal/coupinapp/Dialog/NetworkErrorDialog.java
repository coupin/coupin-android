package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 11/13/17.
 */

public class NetworkErrorDialog extends Dialog {
    private Button closeButton;
    private TextView errorBody;
    private TextView errorHeader;

    String body;
    String title;

    private View.OnClickListener resolve;

    public NetworkErrorDialog(@NonNull Context context) {
        super(context);
    }

    public NetworkErrorDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected NetworkErrorDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_error);

        closeButton = (Button) findViewById(R.id.exit);
        errorBody = (TextView) findViewById(R.id.error_body);
        errorHeader = (TextView) findViewById(R.id.error_header);

        errorHeader.setText(title);
        errorBody.setText(body);

        closeButton.setOnClickListener(resolve);
    }

    /**
     * Set options for dialog
     * @param title
     * @param body
     * @param resolve
     */
    public void setOptions(String title, String body, View.OnClickListener resolve) {
        this.body = body;
        this.title = title;
        this.resolve = resolve;
    }
}
