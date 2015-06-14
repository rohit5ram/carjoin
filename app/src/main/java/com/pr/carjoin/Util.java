package com.pr.carjoin;

import android.util.Log;

/**
 * Created by rohit on 31/5/15.
 */
public class Util {
    public static final String TAG = "com.letzpool";
    public static void logException(Exception e, String logLabel) {
        Log.e(TAG, logLabel + " [[[ " + e.getMessage() + " ]]]");
        for (StackTraceElement el : e.getStackTrace()) {
            Log.e(TAG, logLabel + " :: " + el.toString());
        }
    }
}
