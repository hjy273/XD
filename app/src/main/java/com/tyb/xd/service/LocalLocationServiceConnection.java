package com.tyb.xd.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 *定位Service的连接
 */
public class LocalLocationServiceConnection implements ServiceConnection {

    public LocalLocationBinderProxy mMyLocationBinderProxy;
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMyLocationBinderProxy = (LocalLocationBinderProxy)service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
    public LocalLocationBinderProxy getmMyLocationBinderProxy() {
        return mMyLocationBinderProxy;
    }
}
