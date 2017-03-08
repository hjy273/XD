package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.tyb.xd.R;
import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.bean.User;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.FileUtil;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class WelcomeActivity extends Activity {

    public User mUser;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.ac_welcome);
        mContext = WelcomeActivity.this;
        /**
         * 表示首次进入
         */
        if (SharePreferenceUtils.isFirstInApp(this)) {
            BgServicePool.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {

                    }
                }
            });
        } else {//表示不是首次进入
            /**
             * 直接进入主界面
             * 如果有用户的历史登录状态
             * 则重新登录，获取用户的信息
             */
            if (SharePreferenceUtils.getLoginStatus(this)) {
                mUser = SharePreferenceUtils.getCurrLoginUser(this);
                XDApplication.setmUser(mUser);
                if (mUser instanceof ThirdPartyUser) {//如果是第三方登录
                    String url = XDApplication.dbUrl + "/user/uniqueid/" + ((ThirdPartyUser) mUser).getmThirdPartyId();
                    RequestParams params = new RequestParams(url);
                    /**
                     * save
                     */
                    params.addBodyParameter("username", "admin1314");
                    params.addBodyParameter("token", "$1$Ewpc17O/$hdkoVxGTQRzRMoKeDMMaF1");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject json = JSON.parseObject(result);
                            String status = json.getString("status");
                            if (status.equals("success")) {
                                User user = new User();
                                user.setmToken(json.getString("token"));
                                user.setmUsername(json.getJSONObject("user").getString("username"));
                                complete_Info_Third(mContext);
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onFinished() {
                        }
                    });

                } else {//如果是传统的登录
                    SharePreferenceUtils.getCurrUser(mContext, XDApplication.getmUser());
                    XDApplication.getmUser().setmUserPhone(mUser.getmUserPhone());
                    XDApplication.getmUser().setmUserPass(mUser.getmUserPass());
                    SharePreferenceUtils.getCurrUser(mContext, XDApplication.getmUser());
                    BgServicePool.getInstance().addRunnable(new Runnable() {
                        @Override
                        public void run() {
                            String url = XDApplication.dbUrl + "/user/login";
                            RequestParams login = new RequestParams(url);
                            login.addBodyParameter("phone", XDApplication.getmUser().getmUserPhone());
                            login.addBodyParameter("password", XDApplication.getmUser().getmUserPass());
                            x.http().post(login, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    final JSONObject json = JSON.parseObject(result);
                                    String status = json.getString("status");
                                    if (status.equals("success")) {
                                        XDApplication.getmUser().setmToken(json.getString("token"));
                                        XDApplication.getmUser().setmUsername(json.getString("username"));
                                        complete_Info(mContext);
                                    }
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
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
            toNext();
        }
    }

    private void toNext() {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {

                }
            }
        });
    }
    public static void complete_Info(final Context context) {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                final String user_url = XDApplication.dbUrl + "/user/self";
                RequestParams complete_user_info = new RequestParams(user_url);
                complete_user_info.addBodyParameter("token", XDApplication.getmUser().getmToken());
                complete_user_info.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                x.http().get(complete_user_info, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject userInfo = jsonObject.getJSONObject("user");
                            XDApplication.getmUser().setmGoldMoney(Float.parseFloat(userInfo.getString("bright_point")));
                            XDApplication.getmUser().setmUserPhone(userInfo.getString("phone"));
//                            XDApplication.getmUser().setmUserPass(userInfo.getString("password"));
                            XDApplication.getmUser().setmSilverMoney(Float.parseFloat(userInfo.getString("original_point")));
                            XDApplication.getmUser().setmRole(userInfo.getString("role"));
                            XDApplication.getmUser().setmNickname(userInfo.getString("nickname"));
                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" + XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                            loginConversation(context);
                            //存储图片到本地
                            RequestParams imgPar = new RequestParams(XDApplication.getmUser().getmUserUrl());
                            x.http().get(imgPar, new CommonCallback<File>() {
                                @Override
                                public void onSuccess(File result) {
                                    FileUtil.writeFile(context, XDApplication.getmUser().getmUserPhone(), result, XDApplication.getmUser().getmUserPhone(), FileUtil.FILE_IMAGE);
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    Errorutils.showXutilError(context, ex);
                                    Errorutils.showError(context, ex, null, null, null);
                                }

                                @Override
                                public void onCancelled(CancelledException cex) {
                                }

                                @Override
                                public void onFinished() {
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(context, ex);
                        Errorutils.showError(context, ex, null, null, null);
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


    public static void complete_Info_without_download(final Context context) {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                String user_url = XDApplication.dbUrl + "/user/self";
                RequestParams complete_user_info = new RequestParams(user_url);
                complete_user_info.addBodyParameter("token", XDApplication.getmUser().getmToken());
                complete_user_info.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                x.http().get(complete_user_info, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject userInfo = jsonObject.getJSONObject("user");
                            XDApplication.getmUser().setmGoldMoney(Float.parseFloat(userInfo.getString("bright_point")));
                            XDApplication.getmUser().setmSilverMoney(Float.parseFloat(userInfo.getString("original_point")));
                            XDApplication.getmUser().setmRole(userInfo.getString("role"));
                            XDApplication.getmUser().setmNickname(userInfo.getString("nickname"));
                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" +
                                    XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                            loginConversation(context);
                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(context, ex);
                        Errorutils.showError(context, ex, null, null, null);
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


    public static void complete_Info_Third(final Context context) {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                String user_url = XDApplication.dbUrl + "/user/self";
                RequestParams complete_user_info = new RequestParams(user_url);
                complete_user_info.addBodyParameter("token", XDApplication.getmUser().getmToken());
                complete_user_info.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                x.http().get(complete_user_info, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject userInfo = jsonObject.getJSONObject("user");
                            XDApplication.getmUser().setmGoldMoney(Float.parseFloat(userInfo.getString("bright_point")));
                            XDApplication.getmUser().setmUserPhone(userInfo.getString("phone"));
                            XDApplication.getmUser().setmSilverMoney(Float.parseFloat(userInfo.getString("original_point")));
                            XDApplication.getmUser().setmRole(userInfo.getString("role"));
                            XDApplication.getmUser().setmNickname(userInfo.getString("nickname"));
                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" + XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                            loginConversation(context);
                            //存储图片到本地
                            RequestParams imgPar = new RequestParams(XDApplication.getmUser().getmUserUrl());
                            x.http().get(imgPar, new CommonCallback<File>() {
                                @Override
                                public void onSuccess(File result) {
                                    FileUtil.writeFile(context, XDApplication.getmUser().getmUserPhone(), result, XDApplication.getmUser().getmUserPhone(), FileUtil.FILE_IMAGE);
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    Errorutils.showXutilError(context, ex);
                                    Errorutils.showError(context, ex, null, null, null);
                                }

                                @Override
                                public void onCancelled(CancelledException cex) {
                                }

                                @Override
                                public void onFinished() {
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(context, ex);
                        Errorutils.showError(context, ex, null, null, null);
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

    public static void complete_Info_Third_without_download(final Context context) {
        BgServicePool.getInstance().addRunnable(new Runnable() {
            @Override
            public void run() {
                String user_url = XDApplication.dbUrl + "/user/self";
                RequestParams complete_user_info = new RequestParams(user_url);
                complete_user_info.addBodyParameter("token", XDApplication.getmUser().getmToken());
                complete_user_info.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                x.http().get(complete_user_info, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject userInfo = jsonObject.getJSONObject("user");
                            XDApplication.getmUser().setmGoldMoney(Float.parseFloat(userInfo.getString("bright_point")));
                            XDApplication.getmUser().setmUserPhone(userInfo.getString("phone"));
                            XDApplication.getmUser().setmSilverMoney(Float.parseFloat(userInfo.getString("original_point")));
                            XDApplication.getmUser().setmRole(userInfo.getString("role"));
                            XDApplication.getmUser().setmNickname(userInfo.getString("nickname"));
                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" + XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                            loginConversation(context);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showXutilError(context, ex);
                        Errorutils.showError(context, ex, null, null, null);
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


    public static void loginConversation(final Context context) {
        XDApplication.avimClient = AVIMClient.getInstance(XDApplication.getmUser().getmUsername());
        XDApplication.avimClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    /**
                     * 登录成功之后执行的操作
                     */
                    XDApplication.startDefaultNotification(context);
                }
            }
        });
    }
}
