package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yqritc.scalablevideoview.ScalableVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {
    @BindView(R.id.back_video)
    public ScalableVideoView backVideo;
    @BindView(R.id.sign_up_button)
    public Button signUpButton;
    @BindView(R.id.sign_in_button)
    public Button signInButton;
    @BindView(R.id.carouselView)
    public CarouselView carouselView;

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

        play();

        carouselView.setPageCount(quotes.length);
        carouselView.setViewListener(viewListener);

        ((TextView)findViewById(R.id.forgot_password)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingActivity.this, SplashScreen.class));
            }
        });
    }

    public void play() {
        try {
            backVideo.setRawData(R.raw.back_small);
            backVideo.prepareAsync(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mediaPlayer.seekTo(5000);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResume() {
        play();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        super.onPause();
    }
}
