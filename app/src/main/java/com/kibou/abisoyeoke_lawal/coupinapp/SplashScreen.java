package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

import butterknife.ButterKnife;

public class SplashScreen extends Activity {
    int count = 0;

    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        PreferenceMngr.setContext(getApplicationContext());

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (PreferenceMngr.getInstance().isLoggedIn()) {
                    Log.v("VolleyOPref", "" + PreferenceMngr.getInstance().categorySelected());
                    if (PreferenceMngr.getInstance().categorySelected()) {
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashScreen.this, InterestsActivity.class));
                        finish();
                    }
                }else {
                    startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                    finish();
                }
            }
        }, 2000);

    }
}
