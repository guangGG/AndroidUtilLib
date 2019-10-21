package gapp.season.util.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.ColorInt;

/**
 * AppTheme设置工具类
 */
public class ThemeUtil {
    /**
     * 设置Activity样式，themeResid传0表示使用默认样式
     */
    public static void setTheme(Activity activity, int themeResid) {
        if (themeResid != 0) {
            activity.setTheme(themeResid);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            } else {
                activity.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setNavigationBarColor(0xff757575); //Material样式默认状态栏颜色
            }
        }
    }

    /**
     * 设置Activity样式，themeResid传0表示使用默认样式
     */
    public static void setTheme(Activity activity, int themeResid, @ColorInt int navigationBarColor) {
        if (themeResid != 0) {
            activity.setTheme(themeResid);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.setTheme(android.R.style.Theme_Material_Light_NoActionBar);
            } else {
                activity.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(navigationBarColor);
        }
    }

    /**
     * 设置全屏Activity样式，themeResid传0表示使用默认样式
     */
    public static void setFullScreenTheme(Activity activity, int themeResid, @ColorInt int navigationBarColor) {
        if (themeResid != 0) {
            activity.setTheme(themeResid);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.setTheme(android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            } else {
                activity.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(navigationBarColor);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int uiFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
                Color color = Color.valueOf(navigationBarColor);
                double brightness = 0.2126 * color.red() + 0.7152 * color.green() + 0.0722 * color.blue();
                if (brightness < 0.8) { //导航栏颜色为暗色(brightness取值范围[0,1])
                    uiFlags = (uiFlags & (~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)); //移除亮导航栏标记
                } else { //状态栏颜色为亮色
                    uiFlags = (uiFlags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
                activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
            }
        }
    }
}
