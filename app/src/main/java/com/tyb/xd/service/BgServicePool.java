package com.tyb.xd.service;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 后台服务线程池
 * 主要用于处理耗时操作
 */
public class BgServicePool {
    private static ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private static BgServicePool mBgService2;
    private static int onlyone = 1;
    public static BgServicePool getInstance() {
        if (mBgService2 == null) {
            synchronized ((Object) onlyone) {
                if (mBgService2 == null) {
                    mBgService2 = new BgServicePool();
                }
            }
        }
        return mBgService2;
    }
    public void addRunnable(Runnable runnable) {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
        mExecutorService.execute(runnable);
    }

    public void ShutDown()
    {
        mExecutorService.shutdownNow();
    }
}
