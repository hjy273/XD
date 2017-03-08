package com.tyb.xd.service;

import com.tyb.xd.interfacelistener.ServiecePoolDataLoadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 数据加载的Runnable父线程
 */
public abstract class LoadDataRunnable implements Runnable {

    private ServiecePoolDataLoadListener mListener;

    private int mContentType = 0;

    private int mRefreshType = 0;

    private List<Object> mlistData = new ArrayList<Object>();

    private Semaphore mSemaphore = new Semaphore(0);

    public LoadDataRunnable(ServiecePoolDataLoadListener listener, int refreshType, int contentType) {
        mListener = listener;
        mContentType = contentType;
        mRefreshType = refreshType;
    }

    @Override
    public void run() {
        loadData(mlistData, mSemaphore);
        try {
            /**
             * 每次请求时，需要请求信号量
             * 当异步加载完成时可以释放信号量
             * 进而可以实现加载完数据后将数据通过接口传出去
             */
            mSemaphore.acquire();
            mListener.setData(mlistData, mRefreshType, mContentType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现抽象函数
     * 当加载数据完成时，将加载到的数据放到mListData中
     * 并且释放信号量
     * 只有释放信号量之后，线程才可以调用接口，更新数据
     * 在此方法中，执行数据更新操作成功时，将数据添加到mlistdata中
     *
     * @param mlistData
     * @param semaphore
     */
    abstract public void loadData(List<Object> mlistData, Semaphore semaphore);
}
