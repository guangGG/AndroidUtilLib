package gapp.season.util.net;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 向CookieManager同步Cookie，常用于向WebView设置Cookie (HTTP请求头："Cookie" , HTTP响应头："Set-Cookie")
 */
public class CookieUtil {
    public static void syncCookie(Context context, String domain, List<String> cookies) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true); // 允许接受 Cookie
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookie(); // 移除
        } else {
            cookieManager.removeSessionCookies(null); // 移除
        }
        if (cookies != null) {
            for (String cookie : cookies) {
                cookieManager.setCookie(domain, cookie); //分开传不同的cookie
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }

    public static void setCookies(Context context, String url, String cookies) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true); // 允许接受 Cookie
        cookieManager.setCookie(url, cookies);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }

    public static String getCookies(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true); // 允许接受 Cookie
        String cookies = cookieManager.getCookie(url);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
        return cookies;
    }

    public static void clearCookies(Context context, @Nullable ValueCallback<Boolean> callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true); // 允许接受 Cookie
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //cookieManager.removeSessionCookie(); // 移除
            cookieManager.removeAllCookie();
            if (callback != null) {
                callback.onReceiveValue(true);
            }
        } else {
            //cookieManager.removeSessionCookies(callback); // 移除
            cookieManager.removeAllCookies(callback);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }
}
