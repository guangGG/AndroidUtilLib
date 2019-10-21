package gapp.season.util.sys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 获得屏幕相关的辅助类
 */
public class ScreenUtil {
    private ScreenUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取当前屏幕截图，包含状态栏(状态栏不显示状态值，只显示背景)
     */
    static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        // View是需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        // 获取屏幕长和高
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        // 获取去掉状态栏的图像
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 保存当前界面截屏到文件中(格式:文件名前缀yyyyMMddHHmmss.jpg)
     *
     * @param fileDir        保存到的文件目录位置
     * @param fileNamePrefix 文件名前缀
     */
    public static boolean saveScreenshot(Activity activity, File fileDir, String fileNamePrefix) {
        try {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            String fname = sdf.format(new Date()) + ".jpg";
            File file = new File(fileDir, fileNamePrefix + fname);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap Bmp = snapShotWithoutStatusBar(activity);
            Bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isScreenPortrait(Context context) {
        Configuration configuration = context.getResources().getConfiguration(); //获取设置的配置信息
        if (configuration != null)
            return configuration.orientation == configuration.ORIENTATION_PORTRAIT;
        return true;
    }

    public static void changeIconDrawableColor(Context context, Drawable drawable, @ColorRes int colorId) {
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static int dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static float pxToDp(float px) {
        float densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return px / (densityDpi / 160f);
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度(不包含底部虚拟软键盘)
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕实际的宽高，包括底部导航栏(若存在)
     *
     * @return Point.x为屏幕宽度，Point.y为屏幕高度
     */
    public static Point getScreenRealSize(Context context) {
        Point point = new Point();
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
        }
        point.x = outMetrics.widthPixels;
        point.y = outMetrics.heightPixels;
        return point;
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部导航栏的高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 判断是否存在底部导航栏
     */
    public static boolean hasNavigationBar(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (identifier != 0) {
            boolean hasNavigationBar = resources.getBoolean(identifier);

            //获取底部导航栏被重写的状态
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    @SuppressLint("PrivateApi") Class c = Class.forName("android.os.SystemProperties");
                    //noinspection unchecked
                    Method method = c.getDeclaredMethod("get", String.class);
                    method.setAccessible(true);
                    String overrideStatus = (String) method.invoke(null, "qemu.hw.mainkeys");
                    if ("1".equals(overrideStatus)) { //注意：不能写成“if...else...”判断形式
                        hasNavigationBar = false;
                    } else if ("0".equals(overrideStatus)) {
                        hasNavigationBar = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return hasNavigationBar;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 设置状态栏颜色(api-23 6.0系统以上支持)
     *
     * @deprecated Use {@link #setSysBarColor(Activity, int, boolean)} instead.
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int colorInt, boolean darkText) {
        Window window = activity.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //设置状态栏背景颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(colorInt);
        }
        //设置状态栏前景颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = window.getDecorView();
            int flags = view.getSystemUiVisibility();
            if (darkText) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            view.setSystemUiVisibility(flags);
        }
    }

    /**
     * 设置Activity状态栏/导航栏颜色(api-21 5.0系统以上支持)
     */
    public static void setSysBarColor(Activity activity, @ColorInt int colorInt, boolean darkText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置状态栏和NavigationBar导航栏的背景色
            activity.getWindow().setNavigationBarColor(colorInt);
            activity.getWindow().setStatusBarColor(colorInt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //隐藏NavigationBar和界面的分割线
                activity.getWindow().setNavigationBarDividerColor(colorInt);
            }
            //设置状态栏和导航栏前景色是否高亮颜色(偏白色)
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (darkText && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uiFlags = (uiFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            if (darkText && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                uiFlags = (uiFlags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
}
