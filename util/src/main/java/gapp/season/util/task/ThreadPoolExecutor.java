package gapp.season.util.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExecutor {
    private ExecutorService mExecutorService;
    private long mExecutedCount;

    private static class SingleHolder {
        private static final ThreadPoolExecutor instance = new ThreadPoolExecutor();
    }

    public static ThreadPoolExecutor getInstance() {
        return SingleHolder.instance;
    }

    private ThreadPoolExecutor() {
    }

    public ExecutorService getExecutorService() {
        if (mExecutorService == null)
            mExecutorService = Executors.newCachedThreadPool();
        return mExecutorService;
    }

    public void execute(Runnable command) {
        if (command != null) {
            mExecutedCount++;
            getExecutorService().execute(command);
        }
    }

    public long getExecutedCount() {
        return mExecutedCount;
    }

    public int getActiveCount() {
        try {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) getExecutorService();
            return tpe.getActiveCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getTaskCount() {
        try {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) getExecutorService();
            return tpe.getTaskCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getCompletedTaskCount() {
        try {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) getExecutorService();
            return tpe.getCompletedTaskCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
