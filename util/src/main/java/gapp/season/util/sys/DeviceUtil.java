package gapp.season.util.sys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.content.PermissionChecker;

public class DeviceUtil {

    public static String getOsModel() {
        return Build.BRAND + "/" + Build.MODEL;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")";
    }

    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED && telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return telephonyManager.getImei();
            } else {
                return telephonyManager.getDeviceId();
            }
        } else {
            return "";
        }
    }

    /**
     * 获取设备IMSI
     * IMSI号前面3位460是国家:中国
     * "中国移动":(IMSI.startsWith("46000") || IMSI.startsWith("46002"))
     * "中国联通":(IMSI.startsWith("46001"))
     * "中国电信":(IMSI.startsWith("46003"))
     */
    @SuppressLint("HardwareIds")
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED && telephonyManager != null) {
            return telephonyManager.getSubscriberId();
        } else {
            return "";
        }
    }
}
