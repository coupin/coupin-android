package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.saeid.fabloading.LoadingView;

public class SplashScreen extends Activity {
    @BindView(R.id.loading_view)
    public LoadingView loadingView;
    @BindView(R.id.body)
    public RelativeLayout body;

    private int colors[] = new int[]{R.color.colorAccent, R.color.facebookBlue, R.color.googleRed, R.color.colorPrimary,
    R.color.gradientEnd};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        loadingView.addAnimation(R.color.facebookBlue, R.drawable.slide1, LoadingView.FROM_LEFT);
        loadingView.addAnimation(R.color.googleRed, R.drawable.slide2, LoadingView.FROM_TOP);
        loadingView.addAnimation(R.color.facebookBlue, R.drawable.slide4, LoadingView.FROM_RIGHT);
        loadingView.addAnimation(R.color.colorAccent, R.drawable.slide5, LoadingView.FROM_BOTTOM);
        loadingView.addAnimation(R.color.googleRed, R.drawable.slide3, LoadingView.FROM_LEFT);

        loadingView.startAnimation();
    }
}
