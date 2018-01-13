package com.kibou.abisoyeoke_lawal.coupinapp;

import android.app.Application;

/**
 * Created by abisoyeoke-lawal on 4/22/17.
 */

class CoupinApp extends Application {
    private static final CoupinApp ourInstance = new CoupinApp();

    static CoupinApp getInstance() {
        return ourInstance;
    }

    private CoupinApp() {
    }
}
