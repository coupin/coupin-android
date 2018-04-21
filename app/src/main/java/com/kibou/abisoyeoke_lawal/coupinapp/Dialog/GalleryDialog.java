package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import org.jetbrains.annotations.Nullable;

/**
 * Created by abisoyeoke-lawal on 4/21/18.
 */

public class GalleryDialog extends Dialog {
    private Context context;
    private String pictureUrl;
    private ImageView pictureView;

    public GalleryDialog(@NonNull Context context) {
        super(context);
    }

    public GalleryDialog(@Nullable Context context, String pictureUrl) {
        super(context);
        this.context = context;
        this.pictureUrl = pictureUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gallery);

        pictureView = (ImageView)findViewById(R.id.picture);

        Glide.with(getContext()).clear(pictureView);
        Log.v("VolleyProfile", pictureUrl);
        Glide.with(getContext()).load(pictureUrl).into(pictureView);
    }

    public void setImage(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
