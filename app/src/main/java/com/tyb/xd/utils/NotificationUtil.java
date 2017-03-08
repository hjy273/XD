package com.tyb.xd.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;

import com.tyb.xd.R;

/**
 * Created by wangpeiyu on 2016/7/5.
 */
public class NotificationUtil {

    /**
     * 获取NoticicationManager对象
     *
     * @param context
     * @return
     */
    public static NotificationManager getNotificationManager(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    /**
     * 接收到消息后。
     * 发出通知声音
     *
     * @param context
     */
    public static void noticeInMessage(Context context) {
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notifymusic1);
        mediaPlayer.start();
                    /* 当MediaPlayer.OnCompletionLister会运行的Listener */
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    // @Override
                                /*覆盖文件播出完毕事件*/
                    public void onCompletion(MediaPlayer arg0) {
                        try {
                                /*解除资源与MediaPlayer的赋值关系
                                 * 让资源可以为其它程序利用*/
                            mediaPlayer.release();
                        } catch (Exception e) {
                        }
                    }
                });
    }

    public static void noticeInShake(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {//手机具有震动的硬件支持
            /**
             * 以pattern方式重复repeat次启动vibrator。（pattern的形式为new long[]{arg1,arg2,arg3,arg4......},
             * 其中以两个一组的如arg1 和arg2为一组、arg3和arg4为一组，每一组的前一个代表等待多少毫 秒启动vibrator，
             * 后一个代表vibrator持续多少毫秒停止，之后往复即 可
             * 。Repeat表示重复次数，当其为-1时，表示不重复只以pattern的方 式运行一次）。
             */
            long[] pattern = new long[]{500, 500, 500, 500};
            vibrator.vibrate(pattern, -1);
        } else {//手机没有震动的硬件

        }
    }
}
