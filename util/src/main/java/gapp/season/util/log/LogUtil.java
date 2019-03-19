package gapp.season.util.log;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class LogUtil {
    public static final int LOG_ALL = -1;
    public static final int LOG_V = Log.VERBOSE;
    public static final int LOG_D = Log.DEBUG;
    public static final int LOG_I = Log.INFO;
    public static final int LOG_W = Log.WARN;
    public static final int LOG_E = Log.ERROR;
    public static final int LOG_ASSERT = Log.ASSERT;
    public static final int LOG_NONE = 100;

    private static Application sApplication;
    /**
     * 需要打印哪种级别的bug，可以在application的onCreate函数里面初始化
     */
    private static int sShowLogGrade = LOG_ALL;
    private static String sTAG = "LogUtil"; //默认TAG
    private static List<String> sHideWords; //需屏蔽日志信息的词语列表

    private LogUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Application application) {
        sApplication = application;
    }

    public static void setShowLogGrade(int showLogGrade) {
        sShowLogGrade = showLogGrade;
    }

    public static void setTAG(String tag) {
        sTAG = tag;
    }

    public static void setHideWords(List<String> hideWords) {
        sHideWords = hideWords;
    }

    public static void addHideWord(@NonNull String hideWord) {
        if (sHideWords == null)
            sHideWords = new ArrayList<>();
        sHideWords.add(hideWord);
    }

    public static void v(@StringRes int resId) {
        v(null, resId);
    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void v(String tag, @StringRes int resId) {
        v(tag, sApplication.getString(resId));
    }

    /**
     * 打印VERBOSE日志级别的log信息
     *
     * @param tag tag标记，为空时tag使用默认值
     * @param msg 要显示的信息
     */
    public static void v(String tag, String msg) {
        log(tag, msg, null, LOG_V);
    }

    public static void d(@StringRes int resId) {
        d(null, resId);
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, @StringRes int resId) {
        d(tag, sApplication.getString(resId));
    }

    /**
     * 打印DEBUG日志级别的log信息
     *
     * @param tag tag标记，为空时tag使用默认值
     * @param msg 要显示的信息
     */
    public static void d(String tag, String msg) {
        log(tag, msg, null, LOG_D);
    }

    public static void i(@StringRes int resId) {
        i(null, resId);
    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void i(String tag, @StringRes int resId) {
        i(tag, sApplication.getString(resId));
    }

    /**
     * 打印INFO日志级别的log信息
     *
     * @param tag tag标记，为空时tag使用默认值
     * @param msg 要显示的信息
     */
    public static void i(String tag, String msg) {
        log(tag, msg, null, LOG_I);
    }

    public static void w(@StringRes int resId) {
        w(null, resId);
    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void w(String tag, @StringRes int resId) {
        w(tag, sApplication.getString(resId));
    }

    /**
     * 打印WARN日志级别的log信息
     *
     * @param tag tag标记，为空时tag使用默认值
     * @param msg 要显示的信息
     */
    public static void w(String tag, String msg) {
        log(tag, msg, null, LOG_W);
    }

    public static void w(Throwable tr) {
        w(null, null, tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        log(tag, msg, tr, LOG_W);
    }

    public static void e(@StringRes int resId) {
        e(null, resId);
    }

    public static void e(String msg) {
        e(null, msg);
    }

    public static void e(String tag, @StringRes int resId) {
        e(tag, sApplication.getString(resId));
    }

    /**
     * 打印ERROR日志级别的log信息
     *
     * @param tag tag标记，为空时tag使用默认值
     * @param msg 要显示的信息
     */
    public static void e(String tag, String msg) {
        log(tag, msg, null, LOG_E);
    }

    public static void e(Throwable tr) {
        e(null, null, tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        log(tag, msg, tr, LOG_E);
    }

    public static void a(String msg) {
        a(null, msg);
    }

    public static void a(String tag, String msg) {
        log(tag, msg, null, LOG_ASSERT);
    }

    public static void log(String tag, String message, Throwable tr, int logLevel) {
        if (sShowLogGrade <= logLevel && (!TextUtils.isEmpty(message) || tr != null)) {
            if (TextUtils.isEmpty(tag)) {
                tag = sTAG;
            }
            String format = format(message);
            switch (logLevel) {
                case LOG_V:
                    if (tr == null) {
                        Log.v(tag, format);
                    } else {
                        Log.v(tag, format, tr);
                    }
                    break;
                case LOG_D:
                    if (tr == null) {
                        Log.d(tag, format);
                    } else {
                        Log.d(tag, format, tr);
                    }
                    break;
                case LOG_I:
                    if (tr == null) {
                        Log.i(tag, format);
                    } else {
                        Log.i(tag, format, tr);
                    }
                    break;
                case LOG_W:
                    if (tr == null) {
                        Log.w(tag, format);
                    } else {
                        Log.w(tag, format, tr);
                    }
                    break;
                case LOG_E:
                case LOG_ASSERT:
                    if (tr == null) {
                        Log.e(tag, format);
                    } else {
                        Log.e(tag, format, tr);
                    }
                    break;
            }
        }
    }

    private static String format(String message) {
        if (message != null) {
            String msg = message;
            if (sHideWords != null) {
                try {
                    for (String word : sHideWords) {
                        if (!TextUtils.isEmpty(word)) {
                            String regex = "((?i)" + word + ")[^,;\\n]*";
                            String replacement = "**" + word + "**";
                            msg = msg.replaceAll(regex, replacement);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return msg;
        }
        return "";
    }
}
