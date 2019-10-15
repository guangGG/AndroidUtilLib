package gapp.season.util.sys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.PermissionChecker;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gapp.season.encryptlib.code.ByteUtil;
import gapp.season.encryptlib.code.HexUtil;

@SuppressLint({"HardwareIds", "MissingPermission"})
public class DeviceUtil {
    /**
     * 获取设备型号信息
     */
    public static String getOsModel() {
        return Build.BRAND + "/" + Build.MODEL;
    }

    /**
     * 获取设备版本信息
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")";
    }

    /**
     * 获取设备AndroidId
     */
    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备IMEI (需要权限： android.permission.READ_PHONE_STATE)
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PermissionChecker.PERMISSION_GRANTED && telephonyManager != null) {
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
     * 获取设备IMSI (需要权限： android.permission.READ_PHONE_STATE)
     * IMSI号前面3位460是国家:中国
     * "中国移动":(IMSI.startsWith("46000") || IMSI.startsWith("46002"))
     * "中国联通":(IMSI.startsWith("46001"))
     * "中国电信":(IMSI.startsWith("46003"))
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PermissionChecker.PERMISSION_GRANTED && telephonyManager != null) {
            return telephonyManager.getSubscriberId();
        } else {
            return "";
        }
    }

    /**
     * 判断当前设备是否root过
     */
    public static boolean isRoot() {
        File f;
        String[] kSuSearchPaths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (String kSuSearchPath : kSuSearchPaths) {
                f = new File(kSuSearchPath + "su");
                if (f.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取WiFi的ip地址
     */
    public static String getWifiIp(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return ByteUtil.intToIp(info.getIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取WiFi的mac地址
     */
    public static String getWifiMac(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress(); //XX:XX:XX:XX:XX:XX
    }

    /**
     * 获取网络IP地址列表
     */
    public static List<String> getLocalIps(boolean onlyIpv4) {
        List<String> list = new ArrayList<>();
        try {
            ArrayList<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : networkInterfaces) {
                ArrayList<InetAddress> inetAddresses = Collections.list(ni.getInetAddresses());
                for (InetAddress address : inetAddresses) {
                    if (!address.isLoopbackAddress() && (!onlyIpv4 || address instanceof Inet4Address)) {
                        list.add(address.getHostAddress());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 获取网络IP地址对应的mac地址
     */
    public static List<String> getLocalMacs(boolean onlyIpv4) {
        List<String> list = new ArrayList<>();
        try {
            ArrayList<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : networkInterfaces) {
                boolean isIpv4 = false;
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        isIpv4 = true;
                        break;
                    }
                }
                if (!onlyIpv4 || isIpv4) {
                    String mac = HexUtil.toHexStr(ni.getHardwareAddress());
                    if (mac != null) list.add(mac);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 获取设备的cpu型号("armeabi-v7a"等)
     *
     * @return cpu型号1&cpu型号2
     */
    public static String getCpuAbi() {
        String cpuAbi = Build.CPU_ABI;
        String cpuAbi2 = Build.CPU_ABI2;
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(cpuAbi)) {
            sb.append(cpuAbi);
        }
        if (!TextUtils.isEmpty(cpuAbi2)) {
            if (!TextUtils.isEmpty(sb)) {
                sb.append("&");
            }
            sb.append(cpuAbi2);
        }
        if (!TextUtils.isEmpty(sb)) {
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 判断GPS是否打开(需要权限： android.permission.ACCESS_FINE_LOCATION)
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        if (locationManager != null)
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        else
            return false;
    }

    /**
     * 判断蓝牙是否打开(需要权限： android.permission.BLUETOOTH)
     */
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //bluetoothAdapter.getName() 蓝牙名称
        //bluetoothAdapter.getAddress() 蓝牙MAC地址
        //bluetoothAdapter.getState() 蓝牙状态(BluetoothAdapter.STATE_OFF、STATE_TURNING_ON、STATE_ON、STATE_TURNING_OFF)
        if (bluetoothAdapter != null)
            return bluetoothAdapter.isEnabled();
        else
            return false;
    }

    /**
     * 判断当前设备是手机还是平板(代码来自 Google I/O App for Android)
     *
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
