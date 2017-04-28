package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.Utils.VideoUtils;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends Activity {
    @BindView(R.id.back_video)
    public ScalableVideoView backVideo;
    @BindView(R.id.sign_up_button)
    public Button signUpButton;
    @BindView(R.id.sign_in_button)
    public Button signInButton;
    @BindView(R.id.carouselView)
    public CarouselView carouselView;

    String[] quotes = new String[]{"If one is to know himself, he must first discover freedom",
            "Tell a man what his dreams are and he will tell you your nightmares",
            "Balley was once in town for Reni, now Balley is no more"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

//        signUpButton.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_blue_bright), null);

        final String video_path = "android.resource://" + getPackageName() + "/" + R.raw.back;

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

        try {
            backVideo.setRawData(R.raw.back);
            backVideo.prepareAsync(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.seekTo(5000);
                    mp.setLooping(true);
                    mp.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        carouselView.setPageCount(quotes.length);
        carouselView.setViewListener(viewListener);
    }

    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.carousel_view, null);

            ((TextView)customView.findViewById(R.id.quote)).setText(quotes[position]);

            return customView;
        }
    };
}
