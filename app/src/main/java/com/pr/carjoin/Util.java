package com.pr.carjoin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static String getRequestBodyForLocationRequest(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo.isConnected()) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return getRequestBodyForLocationWithNetworkWifi(context);
                case ConnectivityManager.TYPE_MOBILE:
                    return getRequestBodyForLocationWithNetworkMobile(context);
                case ConnectivityManager.TYPE_ETHERNET:
                    return "";
            }
        }
        return "";
    }

    private static String getRequestBodyForLocationWithNetworkWifi(Context context) {
        JSONObject jsonObject = new JSONObject();
        JSONObject wifiJsonObject = new JSONObject();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            wifiJsonObject.put("macAddress", wifiManager.getConnectionInfo().getBSSID());
            jsonObject.put("wifiAccessPoints", wifiJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getRequestBodyForLocationWithNetworkMobile(Context context) {
        int mcc = -1;
        int mnc = -1;
        int cid = -1;
        int lac = -1;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject mobileJsonObject = new JSONObject();
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = manager.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
        GsmCellLocation cellLocation = (GsmCellLocation) manager.getCellLocation();
        if(cellLocation != null){
            cid = cellLocation.getCid() & 0xffff;  // GSM cell id
            lac = cellLocation.getLac() & 0xffff;  // GSM Location Area Code
        }
        try {
            mobileJsonObject.put("cellId", cid);
            mobileJsonObject.put("locationAreaCode", lac);
            mobileJsonObject.put("mobileCountryCode", mcc);
            mobileJsonObject.put("mobileNetworkCode", mnc);
            jsonArray.put(mobileJsonObject);
            jsonObject.put("cellTowers", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
