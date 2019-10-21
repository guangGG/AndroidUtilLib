package gapp.season.util.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * 管理Activity(需与所有activity的创建、销毁生命周期关联)
 */
public class ActivityHolder {
    private static final String TAG = ActivityHolder.class.getSimpleName();
    private static ActivityHolder sActivityHolder;
    private static boolean sLogShow = false;
    private List<Activity> mActivityList;
    private int mForeActivityCount;
    private List<ForegroundStatusListener> mListeners;

    private ActivityHolder() {
        mActivityList = new ArrayList<>();
        mListeners = new ArrayList<>();
    }

    public static synchronized ActivityHolder getInstance() {
        if (sActivityHolder == null) {
            sActivityHolder = new ActivityHolder();
        }
        return sActivityHolder;
    }

    public static void init(Application application, boolean logShow) {
        //API-14以上Application类增加了registerActivityLifecycleCallbacks来全局管理Activity生命周期
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (getInstance().mForeActivityCount <= 0 && getInstance().mListeners != null) {
                    //App切换到前台
                    if (sLogShow)
                        Log.d(TAG, "onAppForegroundStatusChange to Foreground");
                    for (ForegroundStatusListener listener : getInstance().mListeners) {
                        listener.onAppForegroundStatusChange(true);
                    }
                }
                getInstance().mForeActivityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                getInstance().mForeActivityCount--;
                if (getInstance().mForeActivityCount <= 0 && getInstance().mListeners != null) {
                    //App切换到后台
                    if (sLogShow)
                        Log.d(TAG, "onAppForegroundStatusChange to Background");
                    for (ForegroundStatusListener listener : getInstance().mListeners) {
                        listener.onAppForegroundStatusChange(false);
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                getInstance().removeActivity(activity);
            }
        });
        setLogShow(logShow);
    }

    public static void setLogShow(boolean logShow) {
        sLogShow = logShow;
    }

    /**
     * 获得当前任务栈activity列表
     */
    public List<Activity> getActivityList() {
        return mActivityList;
    }

    /**
     * 添加activity (when activity onCreate)
     *
     * @deprecated 现改用ActivityLifecycleCallback全局注册回调，不需要手动添加删除Activity了
     */
    public void addActivity(Activity activity) {
        try {
            if (activity != null && mActivityList != null) {
                if (contains(activity)) {
                    removeActivity(activity);
                    mActivityList.add(mActivityList.size(), activity);
                } else {
                    mActivityList.add(activity);
                }

                if (sLogShow)
                    Log.d(TAG, "addActivity:" + activity + ";size=" + mActivityList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除activity (when activity onDestroy)
     *
     * @deprecated 现改用ActivityLifecycleCallback全局注册回调，不需要手动添加删除Activity了
     */
    public void removeActivity(Activity activity) {
        try {
            if (mActivityList != null) {
                boolean b = mActivityList.remove(activity);

                if (sLogShow)
                    Log.d(TAG, "removeActivity:" + activity + "-" + b + ";size=" + mActivityList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭所有的Activity
     */
    public void finishAllActivity() {
        // 关闭所有的Activity
        if (mActivityList != null) {
            for (int i = mActivityList.size() - 1; i >= 0; i--) {
                Activity activity = mActivityList.get(i);
                if (activity != null) {
                    activity.finish();
                }
            }

            if (sLogShow)
                Log.d(TAG, "finishAllActivity size=" + mActivityList.size());
        }
    }

    /**
     * 获得当前的activity
     */
    public Activity getCurrentActivity() {
        if (mActivityList != null && mActivityList.size() > 0) {
            return mActivityList.get(mActivityList.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * 判断应用是否有组件在运行
     */
    public boolean hasActivity() {
        if (mActivityList != null && mActivityList.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断activity是否已经添加的列表中
     */
    public boolean contains(Activity activity) {
        if (mActivityList != null && mActivityList.size() > 0) {
            return mActivityList.contains(activity);
        } else {
            return false;
        }
    }

    /**
     * 判断列表中是否含有该类型的activity
     */
    public boolean contains(Class clazz) {
        if (mActivityList != null && mActivityList.size() > 0) {
            for (int i = mActivityList.size() - 1; i >= 0; i--) {
                Activity activity = mActivityList.get(i);
                if (activity != null && activity.getClass().equals(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断应用是否在前台运行
     */
    public boolean isAppForeground() {
        return (mForeActivityCount > 0);
    }

    /**
     * 添加app前后台切换的监听器
     */
    public void registerForegroundStatusListener(ForegroundStatusListener listener) {
        if (mListeners == null) mListeners = new ArrayList<>();
        mListeners.add(listener);
    }

    /**
     * 移除app前后台切换的监听器
     */
    public void unregisterForegroundStatusListener(ForegroundStatusListener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    public interface ForegroundStatusListener {
        /**
         * 应用前后台切换时回调这个方法
         */
        void onAppForegroundStatusChange(boolean isAppForeground);
    }
}
