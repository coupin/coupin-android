package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.FacebookSdk;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {
    @BindView(R.id.sign_up_button)
    public Button signUpButton;
    @BindView(R.id.sign_in_button)
    public Button signInButton;
    @BindView(R.id.carouselView)
    public CarouselView carouselView;
    @BindView(R.id.back_gif)
    public ImageView backGif;

    private MediaPlayer mediaPlayer;

    String[] titles = new String[]{"Explore Nearby Rewards",
            "Browse Promotions",
            "Redeem Coupins"};
    String[] quotes = new String[]{"Discover promotions and discounts near you.",
            "Browse and select desired promotion.",
            "Generate and use your Coupin (promotion code)."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SignUpActivity.class));
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
            }
        });

        Glide.with(this)
                .load(R.raw.coupin_back)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(backGif);

        carouselView.setPageCount(quotes.length);
        carouselView.setViewListener(viewListener);

        ((TextView)findViewById(R.id.forgot_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SplashScreenActivity.class));
            }
        });
    }


    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.carousel_view, null);

            ((TextView)customView.findViewById(R.id.quote_title)).setText(titles[position]);
            ((TextView)customView.findViewById(R.id.quote)).setText(quotes[position]);

            return customView;
        }
    };
}
