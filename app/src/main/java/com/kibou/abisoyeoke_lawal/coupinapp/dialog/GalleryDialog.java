package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kibou.abisoyeoke_lawal.coupinapp.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by abisoyeoke-lawal on 4/21/18.
 */

public class GalleryDialog extends Dialog implements View.OnClickListener {
    private ArrayList<String> thumbnails;
    private Context context;
    private String pictureUrl;
    private ImageView pictureView;
    private ImageView thumb1;
    private ImageView thumb2;
    private ImageView thumb3;
    private ImageView thumb4;
    private ImageView[] thumbViews;

    public GalleryDialog(@NonNull Context context) {
        super(context);
    }

    public GalleryDialog(@Nullable Context context, String pictureUrl, ArrayList<String> thumbnails) {
        super(context);
        this.context = context;
        this.pictureUrl = pictureUrl;
        this.thumbnails = thumbnails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_gallery);

        pictureView = (ImageView)findViewById(R.id.picture);
        thumb1 = (ImageView)findViewById(R.id.thumb1);
        thumb2 = (ImageView)findViewById(R.id.thumb2);
        thumb3 = (ImageView)findViewById(R.id.thumb3);
        thumb4 = (ImageView)findViewById(R.id.thumb4);
        thumbViews = new ImageView[]{thumb1, thumb2, thumb3, thumb4};

        Glide.with(getContext()).clear(pictureView);
        Glide.with(getContext()).load(pictureUrl).into(pictureView);
        for(int x = 0; x < thumbnails.size() && x < 4; x++) {
            thumbViews[x].setOnClickListener(this);
            Glide.with(getContext()).load(thumbnails.get(x)).into(thumbViews[x]);
        }
    }

    public void setImage(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumb1:
                Glide.with(getContext()).load(thumbnails.get(0)).into(pictureView);
                break;
            case R.id.thumb2:
                Glide.with(getContext()).load(thumbnails.get(1)).into(pictureView);
                break;
            case R.id.thumb3:
                Glide.with(getContext()).load(thumbnails.get(2)).into(pictureView);
                break;
            case R.id.thumb4:
                Glide.with(getContext()).load(thumbnails.get(3)).into(pictureView);
                break;
        }
    }
}
