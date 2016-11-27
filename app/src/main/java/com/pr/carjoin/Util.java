package com.pr.carjoin;

import android.location.Address;
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

    public static boolean isUserAlreadySignedIn() {
        return true;
    }

    public static String getAddressAsString(Address address) {
        String addressAsString = "";
        if (address != null && address.getMaxAddressLineIndex() > 0) {
            addressAsString = addressAsString.concat(address.getAddressLine(0));
            for (int i = 1; i <=address.getMaxAddressLineIndex(); i++) {
                addressAsString = addressAsString.concat(" " + address.getAddressLine(i));
            }
        }
        return addressAsString;
    }
}
