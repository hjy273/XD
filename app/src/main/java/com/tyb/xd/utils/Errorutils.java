package com.tyb.xd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tyb.xd.R;
import com.tyb.xd.activity.LoginActivity;
import com.tyb.xd.activity.WelcomeActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.bean.User;
import com.tyb.xd.service.BgServicePool;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangpeiyu on 2016/7/25.
 */
public class Errorutils {
    public static List<String> xutilerror;
    public static List<String> xutilerror_tip;

    public static List<String> getXutilError(Context context) {
        if (xutilerror == null) {
            String[] sError = context.getResources().getStringArray(R.array.xutil_error);
            xutilerror = Arrays.asList(sError);
        }
        return xutilerror;
    }

    public static List<String> getXutilErrorTip(Context context) {
        if (xutilerror_tip == null) {
            String[] sErrorTip = context.getResources().getStringArray(R.array.xutil_error_tip);
            xutilerror_tip = Arrays.asList(sErrorTip);
        }
        return xutilerror_tip;
    }

    /**
     * 显示xutil的错误信息
     *
     * @param context
     * @param ex
     */
    public static void showXutilError(Context context, Throwable ex) {
        int index = 0;
        for (String s : getXutilError(context)) {
            if (s.equals(ex.toString())) {
                Toast.makeText(context, Errorutils.getXutilErrorTip(context).get(index), Toast.LENGTH_SHORT).show();
                return;
            }
            index++;
        }
    }

    /**
     * 根据后台返回的错误码给出相应的提示
     *
     * @param context
     * @param throwable
     */
    public static void showError(final Context context, Throwable throwable, final String functionName, final String ClassName, final Object instance) {
        if (throwable.toString().length() > 41) {
            try {
                String error = throwable.toString().substring(41);
                JSONObject json = JSON.parseObject(error);
                String code = json.getString("error_code");
                if (code != null && !TextUtils.isEmpty(code)) {
                    //需要重新登录的
                    if (code.equals("lc01") || code.equals("er02")) {
                        if (functionName != null && ClassName != null && instance != null) {
                            if (SharePreferenceUtils.getLoginStatus(context)) {
                                User mUser = SharePreferenceUtils.getCurrLoginUser(context);
                                XDApplication.setmUser(mUser);
                                SharePreferenceUtils.getCurrUser(context, XDApplication.getmUser());
                                if (XDApplication.getmUser() instanceof ThirdPartyUser) {
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
                                                XDApplication.getmUser().setmToken(json.getString("token"));
                                                XDApplication.getmUser().setmUsername(json.getJSONObject("user").getString("username"));
                                                /**
                                                 * 重新刷界面
                                                 */
                                                Class<? extends Activity> aClass = (Class<? extends Activity>) instance.getClass();
                                                try {
                                                    aClass.getDeclaredMethod(functionName).invoke(instance);
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                } catch (InvocationTargetException e) {
                                                    e.printStackTrace();
                                                } catch (NoSuchMethodException e) {
                                                    try {
                                                        aClass.getDeclaredMethod(functionName,Bundle.class).invoke(instance,new Bundle());
                                                    } catch (IllegalAccessException e1) {
                                                        e1.printStackTrace();
                                                    } catch (InvocationTargetException e1) {
                                                        e1.printStackTrace();
                                                    } catch (NoSuchMethodException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                                WelcomeActivity.complete_Info_Third(context);
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Errorutils.showXutilError(context, ex);
                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {
                                        }

                                        @Override
                                        public void onFinished() {
                                        }
                                    });
                                } else {
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
                                                        /**
                                                         * 重新刷界面
                                                         */
                                                        Class<? extends Activity> aClass = (Class<? extends Activity>) instance.getClass();
                                                        try {
                                                            aClass.getDeclaredMethod(functionName).invoke(instance);
                                                        } catch (IllegalAccessException e) {
                                                            e.printStackTrace();
                                                        } catch (InvocationTargetException e) {
                                                            e.printStackTrace();
                                                        } catch (NoSuchMethodException e) {
                                                            try {
                                                                aClass.getDeclaredMethod(functionName,Bundle.class).invoke(instance,new Bundle());
                                                            } catch (IllegalAccessException e1) {
                                                                e1.printStackTrace();
                                                            } catch (InvocationTargetException e1) {
                                                                e1.printStackTrace();
                                                            } catch (NoSuchMethodException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        }
                                                        WelcomeActivity.loginConversation(context);
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
                                                                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                                                                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                                                                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                                                                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                                                                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                                                                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" + XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                                                                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                                                                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable ex, boolean isOnCallback) {
                                                                        Errorutils.showXutilError(context, ex);
                                                                        Errorutils.showError(context, ex, functionName, ClassName, instance);
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

                                                @Override
                                                public void onError(Throwable ex, boolean isOnCallback) {
                                                    Errorutils.showError(context, ex, functionName, ClassName, instance);
                                                    Errorutils.showXutilError(context, ex);
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

                            } else {
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            }
                        } else {
                            if (SharePreferenceUtils.getLoginStatus(context)) {
                                User mUser = SharePreferenceUtils.getCurrLoginUser(context);
                                SharePreferenceUtils.getCurrUser(context, mUser);
                                XDApplication.setmUser(mUser);
                                if (XDApplication.getmUser() instanceof ThirdPartyUser) {


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
                                                XDApplication.getmUser().setmToken(json.getString("token"));
                                                XDApplication.getmUser().setmUsername(json.getJSONObject("user").getString("username"));
                                                WelcomeActivity.complete_Info_Third(context);
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Errorutils.showXutilError(context, ex);
                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {
                                        }

                                        @Override
                                        public void onFinished() {
                                        }
                                    });
                                } else {
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
                                                                            XDApplication.getmUser().setmSchool(userInfo.getString("school"));
                                                                            XDApplication.getmUser().setmSign(userInfo.getString("introduction"));
                                                                            XDApplication.getmUser().setmUserUrl(userInfo.getString("headimg"));
                                                                            XDApplication.getmUser().setmIsIdentificate(userInfo.getBoolean("identified"));
                                                                            XDApplication.getmUser().setmCreditibility(Float.parseFloat(userInfo.getString("credibility")));
                                                                            XDApplication.getmUser().setmUserPath(XDApplication.msSavePhth + "/" + XDApplication.getmUser().getmUserPhone() + "/" + XDApplication.getmUser().getmUserPhone() + ".png");
                                                                            SharePreferenceUtils.setCurrLoginUser(context, XDApplication.getmUser());
                                                                            SharePreferenceUtils.setCurrUser(context, XDApplication.getmUser());
                                                                            WelcomeActivity.loginConversation(context);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable ex, boolean isOnCallback) {
                                                                        Errorutils.showXutilError(context, ex);
                                                                        Errorutils.showError(context, ex, functionName, ClassName, instance);
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

                                                @Override
                                                public void onError(Throwable ex, boolean isOnCallback) {
                                                    Errorutils.showError(context, ex, functionName, ClassName, instance);
                                                    Errorutils.showXutilError(context, ex);
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
                            } else {
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                            }
                        }

                    } else {
                        Toast.makeText(context, getStringIdByName(context, code), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    /**
     * 反射获取id
     *
     * @param context
     * @param name
     * @return
     */
    public static int getStringIdByName(Context context, String name) {
        String packagename = context.getPackageName();
        int id = 0;
        try {
            Class desireClass = Class.forName(packagename + ".R$string");
            try {
                id = desireClass.getField(name).getInt(desireClass);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return id;
    }
}
