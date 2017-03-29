package com.pr.carjoin;

import android.location.Address;
import android.util.Log;

/**
 * Created by rohit on 31/5/15.
 */
public class Util {
    public static final String TAG = "com.letzpool";
    public static final String TRIPS = "trips";
    public static final String USERS = "users";

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
            for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
                addressAsString = addressAsString.concat(" " + address.getAddressLine(i));
            }
        }
        return addressAsString;
    }

    public static String getShortAdderss(String address) {
        String[] strings = address.split(" ");
        if (strings.length <= 0) {
            return "";
        } else if (strings.length == 1) {
            return strings[0];
        } else {
            return strings[0] + "," + strings[strings.length - 1];
        }
    }
}
