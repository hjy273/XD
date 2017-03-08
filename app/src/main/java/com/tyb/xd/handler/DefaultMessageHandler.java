package com.tyb.xd.handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.tyb.xd.R;
import com.tyb.xd.activity.ChatActivity;
import com.tyb.xd.activity.HomeActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.NotificationUtil;
import com.tyb.xd.utils.PermissionUtil;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.utils.Util;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * 系统默认的处理消息
 * 主要是通过Notification进行通知
 */
public class DefaultMessageHandler extends AVIMMessageHandler {

    public static int number = 1;

    public Context mContext;

    public String mYou;

    public DefaultMessageHandler(Context mContext, String mYou) {
        this.mContext = mContext;
        this.mYou = mYou;
    }

    @Override
    public void onMessage(final AVIMMessage message, final AVIMConversation conversation, final AVIMClient client) {
        super.onMessage(message, conversation, client);
        if (client.getClientId().equals(mYou)) {
            /**
             * 处理文本笑递的
             */
            if (message instanceof AVIMMessage) {
                final String ta = message.getFrom();
                final Intent intentHome = new Intent(mContext, HomeActivity.class);
                final Intent intent = new Intent(mContext, ChatActivity.class);
                final Bundle bundle = new Bundle();
                conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        String id = conversation.getName();
                        bundle.putString("id", id);
                        bundle.putString("touser", message.getFrom());
                        bundle.putString("username", message.getFrom());
                        bundle.putString("from", "");
                        intent.putExtras(bundle);
                        Intent intentlist[] = new Intent[]{intentHome, intent};
                        final PendingIntent pendingIntent = PendingIntent.getActivities(mContext,
                                1, intentlist, PendingIntent.FLAG_UPDATE_CURRENT, null);
                        String url = null;
                        try {
                            url = XDApplication.dbUrl + "/user/person/" + URLEncoder.encode(ta, "utf-8");
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }
                        RequestParams requestParams = new RequestParams(url);
                        requestParams.addParameter("token", XDApplication.getmUser().getmToken());
                        requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
                        x.http().get(requestParams, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                JSONObject jsonObject = JSON.parseObject(result);
                                if (jsonObject.getString("status").equals("success")) {
                                    final String imgUrl = jsonObject.getJSONObject("user").getString("headimg");
                                    new AsyncTask<String, String, Bitmap>() {

                                        @Override
                                        protected Bitmap doInBackground(String... params) {
                                            Bitmap bitmap = null;
                                            try {
                                                URL urlImg = new URL(imgUrl);
                                                HttpURLConnection httpURLConnection = (HttpURLConnection) urlImg.openConnection();
                                                bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                                            } catch (MalformedURLException e1) {
                                                e1.printStackTrace();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            return bitmap;
                                        }

                                        @Override
                                        protected void onPostExecute(Bitmap bitmap) {
                                            if (bitmap == null) {
                                                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.good_type_express);
                                            }
                                            int width = bitmap.getWidth();
                                            int height = bitmap.getHeight();
                                            int desireWidth = Util.dpToPx(mContext.getResources(), 60);
                                            int desireHeight = Util.dpToPx(mContext.getResources(), 60);
                                            float scaleX = ((float) desireWidth) / width;
                                            float scaleY = ((float) desireHeight) / height;
                                            Matrix matrix = new Matrix();
                                            matrix.setScale(scaleX, scaleY);
                                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                                            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                            Notification notification = new Notification.Builder(mContext)
                                                    .setTicker("新消息...")
                                                    .setLargeIcon(bitmap)
                                                    .setSmallIcon(R.drawable.login_logo)
                                                    .setContentTitle(message.getFrom())
                                                    .setContentText(((AVIMTextMessage) message).getText().toString())
                                                    .setNumber(number++)
                                                    .setContentIntent(pendingIntent)
                                                    .setWhen(System.currentTimeMillis())
                                                    .setAutoCancel(true)
                                                    .build();
                                            if (SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SOUND)) {
                                                notification.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.notifymusic1);
                                            }
                                            if (SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SHAKE))
                                                NotificationUtil.noticeInShake(mContext);
                                            manager.notify(0, notification);
                                        }
                                    }.execute();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                Errorutils.showError(mContext, ex, null, null, null);
                                Errorutils.showXutilError(mContext, ex);
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {
                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                });
            }
        }
    }
}