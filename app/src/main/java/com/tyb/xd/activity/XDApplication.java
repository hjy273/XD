package com.tyb.xd.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.tyb.xd.bean.User;
import com.tyb.xd.handler.DefaultMessageHandler;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.x;

import java.util.HashMap;
import java.util.List;

/**
 * 应用的入口
 */
public class XDApplication extends Application {

    public static User mUser = new User();
    public static String dbUrl = "http://api.xiaodi16.com";
    public static String msSavePhth;
    public static AVIMClient avimClient;
    public static String ROLE_FULLTIME="fulltime";
    public static String ROLE_COMMON="common";

    //自身的纬度
    public static Double mLat = 0d;
    //自身的经度
    public static Double mLng = 0d;
    //未读的消息数
    public static HashMap<String, Integer> mHaveNotRead = new HashMap<String, Integer>();

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化xutils
         */
        x.Ext.init(this);
        initData();
    }

    private void initData() {
        msSavePhth = getExternalFilesDir(null).getAbsolutePath();
        AVOSCloud.initialize(this,
                "qic6qOW7S3M8O2hzt9uX2ur3-9Nh9j0Va", "scm598yxrsNnC8QF1oXGmfVh");
        AVIMClient.setOfflineMessagePush(true);
        AVIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());
    }

    public static User getmUser() {
        return mUser;
    }

    public static void setmUser(User mUser) {
        XDApplication.mUser = mUser;
    }

    /**
     * 判断用户是否具有权限。
     * 如果没有权限，则跳转到相应的界面进行
     * 权限的完善
     *
     * @param context
     */
    public static boolean jurisdiction(Context context) {
        boolean jurisdiction = true;
        //如果是未登录状态，则会进入登录界面
        if (!SharePreferenceUtils.getLoginStatus(context)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            jurisdiction = false;
            return jurisdiction;
        }
        //如果没有实名认证，则进入实名认证界面
        if (!XDApplication.getmUser().ismIsIdentificate()) {
            Intent intent = new Intent(context, RealNameVertifyActivity.class);
            context.startActivity(intent);
            jurisdiction = false;
            return jurisdiction;
        }
        return jurisdiction;
    }

    /**
     * 在每个activity中进行重新注册新的默认通知
     *
     * @param context
     */
    public static void startDefaultNotification(Context context) {
        if (SharePreferenceUtils.getLoginStatus(context)) {
            AVIMMessageManager.registerDefaultMessageHandler(
                    new DefaultMessageHandler(context, getmUser().getmUsername()));
        }
    }

    public class CustomConversationEventHandler extends AVIMConversationEventHandler {
        @Override
        public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members,
                                 String kickedBy) {
            // 有其他成员离开时，执行此处逻辑
        }

        @Override
        public void onMemberJoined(AVIMClient client, AVIMConversation conversation,
                                   List<String> members, String invitedBy) {
            // 手机屏幕上会显示一小段文字：Tom 加入到 551260efe4b01608686c3e0f ；操作者为：Tom
        }

        @Override
        public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
            // 当前 ClientId(Bob) 被踢出对话，执行此处逻辑
        }

        @Override
        public void onInvited(AVIMClient client, AVIMConversation conversation, String invitedBy) {
            // 当前 ClientId(Bob) 被邀请到对话，执行此处逻辑
        }

        @Override
        public void onOfflineMessagesUnread(AVIMClient client, AVIMConversation conversation, int unreadCount) {
            if (conversation != null) {
                mHaveNotRead.put(conversation.getConversationId(), unreadCount);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
