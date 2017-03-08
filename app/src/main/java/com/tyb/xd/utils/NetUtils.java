package com.tyb.xd.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import java.io.IOException;

/**
 * Created by wangpeiyu on 2016/7/4.
 */
public class NetUtils {

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean wifi = false;
        boolean moblie = false;
        boolean connect = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            connect = manager.getActiveNetworkInfo().isAvailable();
        }
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
            wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        }
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            moblie = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        }
        return connect && (moblie || wifi);
    }

    /**
     * 有时候我们连接上一个没有外网连接的WiFi或者需要输入账号和密码才能链接外网的网络，
     * 就会出现虽然网络可用，但是外网却不可以访问。针对这种情况，
     * 一般的解决办法就是ping一个外网，如果能ping通就说明可以真正上网
     *
     * @return
     */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                return true;
            } else {
            }
        } catch (IOException e) {
        } catch (InterruptedException e) {
        } finally {
        }
        return false;
    }
}
