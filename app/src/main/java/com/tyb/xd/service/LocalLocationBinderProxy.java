package com.tyb.xd.service;

/**
 * 定位Service的动态代理
 */
import android.os.Binder;

/**
 * Service类代理,用于Activity与Service进行通信
 */
public class LocalLocationBinderProxy extends Binder {
    LocalLocationService mService;

    public LocalLocationBinderProxy(LocalLocationService mService) {
        this.mService = mService;
    }
    /**
     * 可以添加相应的方法，进行实现activity于service之间的交互
     */
}
