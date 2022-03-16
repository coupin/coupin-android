package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.lang.ref.WeakReference;

import dagger.hilt.android.HiltAndroidApp;

import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.isDarkModePref;

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

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setProjectId(BuildConfig.FIREBASE_P_ID)
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .build();

        FirebaseApp.initializeApp(this, options, BuildConfig.FIREBASE_NAME);

        mContext = new WeakReference<>(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_PLACES_API);
        }

        PreferenceManager.setContext(this);
        Boolean isDarkMode = PreferenceManager.getBoolean(isDarkModePref);
        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static Context getContext(){ return mContext.get(); }
}
