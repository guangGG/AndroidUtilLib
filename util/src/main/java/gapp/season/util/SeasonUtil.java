package gapp.season.util;

import android.app.Application;

import gapp.season.util.tips.ToastUtil;

public class SeasonUtil {
    /**
     * init when application onCreate
     */
    public static void init(Application application) {
        ToastUtil.init(application);
    }
}
