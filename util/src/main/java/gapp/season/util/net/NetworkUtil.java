package gapp.season.util.net;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresPermission;

public class NetworkUtil {
    private static Application sApplication;

    public static void init(Application application) {
        sApplication = application;
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connManager = (ConnectivityManager) sApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null)
            return connManager.getActiveNetworkInfo();
        return null;
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isNetworkConnect() {
        NetworkInfo activeNetwork = getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isWifi() {
        NetworkInfo networkInfo = getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && networkInfo.isAvailable();

    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isMobile() {
        NetworkInfo networkInfo = getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                && networkInfo.isAvailable();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean is3G() {
        if (!isMobile()) return false;

        TelephonyManager telManager = (TelephonyManager) sApplication.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) return false;

        int type = telManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_UMTS:   // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:  // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:   // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:  // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            default:
                return false;
        }
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean is2G() {
        if (!isMobile()) return false;

        TelephonyManager telManager = (TelephonyManager) sApplication.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) return false;

        int type = telManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:  // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:  // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:  // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:  // ~25 kbps
                return true;
            default:
                return false;
        }
    }
}
