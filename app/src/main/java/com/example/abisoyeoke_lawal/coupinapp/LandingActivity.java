package com.example.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.abisoyeoke_lawal.coupinapp.Utils.VideoUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends Activity {
   @BindView(R.id.back_video)
    public TextureView backVideo;
    @BindView(R.id.sign_up_button)
    public Button signUpButton;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        signUpButton.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_blue_bright), null);

        final String video_path = "android.resource://" + getPackageName() + "/" + R.raw.back;

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LandingActivity.this, "Testing button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        backVideo.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface surface1 = new Surface(surface);

                try {
                    AssetFileDescriptor assetFileDescriptor = LandingActivity.this.getContentResolver().openAssetFileDescriptor(Uri.parse(video_path), "r");
                    VideoUtils.calculateVideoSize(assetFileDescriptor);
//                    VideoUtils.updateVideoSize(width, height, backVideo);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    mediaPlayer.setSurface(surface1);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(5000);
                            mp.start();
                        }
                    });
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                } catch (IllegalStateException isE) {
                    isE.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        // Put the video in a loop
//        backVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
