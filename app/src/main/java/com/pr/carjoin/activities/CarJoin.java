package com.pr.carjoin.activities;

import android.app.Application;

import com.pr.carjoin.Util;

/**
 * Created by vishnu on 12/11/16.
 */

public class CarJoin extends Application {
    private static final String LOG_LABEL = "CarJoin";

    @Override
    public void onCreate() {
        super.onCreate();

        if(Util.isUserAlreadySignedIn()){

        }
    }
}
