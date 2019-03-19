package gapp.season.demo;

import android.app.Application;

import gapp.season.util.SeasonUtil;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SeasonUtil.init(this, BuildConfig.DEBUG);
    }
}
