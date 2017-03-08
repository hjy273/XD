package com.tyb.xd.service;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 后台服务线程池
 * 主要用于处理耗时操作
 */
public class BgServicePoolScheduled {

    private static ScheduledExecutorService mExecutorServicePoolScheduled = Executors.newScheduledThreadPool(3);

    private static BgServicePoolScheduled mBgServicePoolScheduled;

    private static int onlyone = 1;

    public static BgServicePoolScheduled getInstance() {
        if (mBgServicePoolScheduled == null) {
            synchronized ((Object) onlyone) {
                if (mBgServicePoolScheduled == null) {
                    mBgServicePoolScheduled = new BgServicePoolScheduled();
                }
            }
        }
        return mBgServicePoolScheduled;
    }

    public void addRunnable(Runnable runnable) {
        if (mExecutorServicePoolScheduled == null) {
            mExecutorServicePoolScheduled = Executors.newScheduledThreadPool(3);
        }
        /**
         * 每隔30s自动执行后台刷新数据
         */
        mExecutorServicePoolScheduled.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    public void ShutDown()
    {
        mExecutorServicePoolScheduled.shutdownNow();
    }
}
