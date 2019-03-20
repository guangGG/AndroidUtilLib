package gapp.season.util.app;

import android.app.Activity;
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

    private ActivityHolder() {
        mActivityList = new ArrayList<>();
    }

    public static synchronized ActivityHolder getInstance() {
        if (sActivityHolder == null) {
            sActivityHolder = new ActivityHolder();
        }
        return sActivityHolder;
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
}
