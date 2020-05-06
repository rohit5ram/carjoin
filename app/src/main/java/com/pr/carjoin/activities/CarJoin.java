package com.pr.carjoin.activities;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.pr.carjoin.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by vishnu on 12/11/16.
 */

public class CarJoin extends Application {
    private static final String LOG_LABEL = "CarJoin";

    public static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3000, TimeUnit.SECONDS)
            .readTimeout(3000, TimeUnit.SECONDS)
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), getString(R.string.google_places_api_key), Locale.US);
    }
}
