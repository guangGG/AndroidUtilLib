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

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

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

    public static void closeApp() {
        closeApp(0);
    }

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

    public static void restartApp() {
        ActivityHolder.getInstance().finishAllActivity();
        Intent intent = sApplication.getPackageManager().getLaunchIntentForPackage(sApplication.getPackageName());
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sApplication.startActivity(intent);
        }
    }

    public static void openInBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void openInAppStore(Context context) {
        final String appPackageName = context.getPackageName();
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appPackageName));
        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent);
            }
        }
    }

    /**
     * 判断应用是否在运行中
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前进程名
     */
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
}
