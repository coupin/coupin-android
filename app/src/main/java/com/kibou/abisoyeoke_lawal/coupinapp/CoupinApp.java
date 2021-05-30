package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.google.android.libraries.places.api.Places;

import java.lang.ref.WeakReference;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Created by abisoyeoke-lawal on 4/22/17.
 */

@HiltAndroidApp
public class CoupinApp extends Application {
    private static final CoupinApp ourInstance = new CoupinApp();
    private static WeakReference<Context> mContext;

    static CoupinApp getInstance() {
        return ourInstance;
    }

    public CoupinApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = new WeakReference<>(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_PLACES_API);
        }
    }

    public static Context getContext(){ return mContext.get(); }
}
