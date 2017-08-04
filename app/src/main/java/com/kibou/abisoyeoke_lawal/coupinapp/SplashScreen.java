package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.victor.loading.book.BookLoading;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends Activity {
    @BindView(R.id.splash_loadingview)
    public BookLoading bookLoading;

    int count = 0;

//    public Handler handler = new Handler() {
//        public void handleLoading() {
//
//        }
//    };

    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        PreferenceMngr.setContext(getApplicationContext());

        bookLoading.start();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceMngr.getInstance().isLoggedIn()) {
                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                    finish();
                }
            }
        }, 2000);

    }

//    public void setWave() {
//        count++;
//        if (waveLoadingView.getProgressValue() != 100) {
//            waveLoadingView.setProgressValue(100);
//        } else {
//            waveLoadingView.setProgressValue(20);
//        }
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (count == 3) {
//                    if (PreferenceMngr.getInstance().isLoggedIn()) {
//                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
//                        finish();
//                    } else {
//                        startActivity(new Intent(SplashScreen.this, LandingActivity.class));
//                        finish();
//                    }
//                } else {
//                    setWave();
//                }
//            }
//        }, 3000);
//    }
}
