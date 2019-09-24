package gapp.season.util.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public final class AppUtil {
    private static Application sApplication;

    public static void init(Application application) {
        sApplication = application;
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static String getString(@StringRes int resId) {
        return sApplication.getString(resId);
    }

    @ColorInt
    public static int getColor(@ColorRes int id) {
        return sApplication.getResources().getColor(id);
    }

    public static float getDimension(@DimenRes int id) {
        return sApplication.getResources().getDimension(id);
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return sApplication.getResources().getDrawable(id);
    }

    /**
     * 退出应用
     */
    public static void closeApp() {
        closeApp(0);
    }

    /**
     * 延时退出应用
     */
    public static void closeApp(long delayMillis) {
        ActivityHolder.getInstance().finishAllActivity();
        if (delayMillis > 0) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, delayMillis);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 重启应用
     */
    public static void restartApp() {
        Intent intent = sApplication.getPackageManager().getLaunchIntentForPackage(sApplication.getPackageName());
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            sApplication.startActivity(intent);
        }
    }

    /**
     * 转到拨号界面-向指定电话拨号
     */
    public static boolean openTelDial(String phoneNum) {
        return openTelDial(Uri.parse("tel:" + phoneNum));
    }

    /**
     * 转到拨号界面-向指定电话拨号
     */
    public static boolean openTelDial(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(sApplication.getPackageManager()) != null) {
            sApplication.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 手机浏览器打开指定url
     */
    public static void openInBrowser(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(sApplication.getPackageManager()) != null) {
                sApplication.startActivity(intent);
            }
        }
    }

    /**
     * 应用市场中转到当前应用
     */
    public static void openInAppStore() {
        final String appPackageName = sApplication.getPackageName();
        final PackageManager packageManager = sApplication.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appPackageName));
        if (intent.resolveActivity(packageManager) != null) {
            sApplication.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            if (intent.resolveActivity(packageManager) != null) {
                sApplication.startActivity(intent);
            }
        }
    }

    /**
     * 判断应用是否在运行中
     */
    public static boolean isAppRunning(String packageName) {
        ActivityManager activityManager = (ActivityManager) sApplication.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前进程名(已过期，请使用getProcessName方法代替)
     */
    @Deprecated
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    private static String sProcessName = "";

    /**
     * 获取当前进程名
     */
    public static String getProcessName() {
        if (TextUtils.isEmpty(sProcessName)) {
            BufferedReader reader = null;
            try {
                File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
                reader = new BufferedReader(new FileReader(file));
                String processName = reader.readLine();
                if (!TextUtils.isEmpty(processName)) {
                    sProcessName = processName.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return sProcessName;
    }
}
