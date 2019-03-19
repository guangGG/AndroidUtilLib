package gapp.season.util;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import gapp.season.util.log.LogUtil;
import gapp.season.util.net.NetworkUtil;
import gapp.season.util.tips.ToastUtil;

public class SeasonUtil {
    /**
     * init when application onCreate
     */
    public static void init(Application application, boolean isDebug) {
        ToastUtil.init(application);
        NetworkUtil.init(application);
        initLogUtil(application, isDebug);
    }

    private static void initLogUtil(Application application, boolean isDebug) {
        LogUtil.init(application);
        LogUtil.setTAG("SeasonLog");
        if (isDebug) {
            LogUtil.setShowLogGrade(LogUtil.LOG_ALL);
        } else {
            LogUtil.setShowLogGrade(LogUtil.LOG_D);
            List<String> hideWords = new ArrayList<>();
            hideWords.add("password");
            hideWords.add("sessionid");
            LogUtil.setHideWords(hideWords);
        }
    }
}
