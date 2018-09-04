package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

/**
 * Created by abisoyeoke-lawal on 11/13/17.
 */

public class NetworkErrorDialog extends Dialog {
    private Button closeButton;
    private ImageView errorImage;
    private TextView errorBody;
    private TextView errorHeader;

    int image;
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
        errorImage = (ImageView) findViewById(R.id.error_image);
        errorBody = (TextView) findViewById(R.id.error_body);
        errorHeader = (TextView) findViewById(R.id.error_header);

        errorBody.setText(body);
        errorImage.setImageResource(image);
        errorHeader.setText(title);

        closeButton.setOnClickListener(resolve);
    }

    /**
     * Set options for dialog
     * @param body
     * @param image
     * @param title
     * @param resolve
     */
    public void setOptions(int image, String title, String body, View.OnClickListener resolve) {
        this.body = body;
        this.image = image;
        this.resolve = resolve;
        this.title = title;
    }
}
