package com.tyb.xd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.bean.User;

import java.io.File;

/**
 * 账户的保存类
 */
public class SharePreferenceUtils {
    public static int SOUND = 0;
    public static int SHAKE = 1;

    /**
     * 获取当前的登录的账户
     *
     * @param context
     * @return
     */
    public static User getCurrLoginUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        String sUserPhone = sharedPreferences.getString("userphone", "");
        String sUserPass = sharedPreferences.getString("userpass", "");
        if (!TextUtils.isEmpty(sUserPass)) {
            sUserPass = SecurityUtils.decode(sUserPass);
        }
        String sUserThirdPartyId = sharedPreferences.getString("thirdpartyid", "");
        if ((!TextUtils.isEmpty(sUserPass)) && (!TextUtils.isEmpty(sUserPhone))) {
            User user = new User();
            user.setmUserPhone(sUserPhone);
            user.setmUserPass(sUserPass);
            String sToken = sharedPreferences.getString("token", "");
            user.setmToken(SecurityUtils.decode(sToken));
            return user;
        } else if (!TextUtils.isEmpty(sUserThirdPartyId)) {
            ThirdPartyUser user = new ThirdPartyUser();
            user.setmThirdPartyId(sUserThirdPartyId);
            String sToken = sharedPreferences.getString("token", "");
            user.setmToken(sToken);
            return user;
        } else {
            return null;
        }
    }

    /**
     * 判断账户的登录状态
     *
     * @param context
     * @return
     */
    public static boolean getLoginStatus(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        boolean isLogin = sharedPreferences.getBoolean("islogin", false);
        return isLogin;
    }

    /**
     * 设置账户的登录状态
     *
     * @param context
     * @return
     */
    public static void setLoginStatu(Context context, boolean statu) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("islogin", statu);
        editor.commit();
    }

    /**
     * 设置当前的登录账户
     *
     * @param context
     * @param user
     */
    public static void setCurrLoginUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user instanceof ThirdPartyUser) {
            editor.putString("thirdpartyid", ((ThirdPartyUser) user).getmThirdPartyId());
            editor.commit();
        } else {
            editor.putString("userphone", user.getmUserPhone());
            editor.putString("userpass", SecurityUtils.encode(user.getmUserPass()));
            editor.putString("thirdpartyid", "");
            editor.putString("token", SecurityUtils.encode(user.getmToken()));
            editor.putBoolean("islogin", true);//将登陆状态设置为true
            editor.commit();
        }
    }


    /**
     * 设置当前的个人账户
     *
     * @param context
     * @param user
     */
    public static void setCurrUser(Context context, User user) {
        if (user instanceof ThirdPartyUser) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(((ThirdPartyUser) user).getmThirdPartyId(), Context.MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", user.getmUsername());
            editor.putString("token", user.getmToken());
            editor.putString("thirdpartyid", ((ThirdPartyUser) user).getmThirdPartyId());
            editor.putString("school", user.getmSchool());
            editor.putString("userpath", user.getmUserPath());
            editor.putString("userurl", user.getmUserUrl());
            editor.putString("role", user.getmRole());
            editor.putFloat("bright_point", user.getmGoldMoney());
            editor.putFloat("original_point", user.getmSilverMoney());
            editor.putString("introduction", user.getmSign());
            editor.putFloat("credibility", user.getmCreditibility());
            editor.putBoolean("identified", user.ismIsIdentificate());
            editor.commit();
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(user.getmUserPhone(), Context.MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userphone", user.getmUserPhone());
            editor.putString("userpass", SecurityUtils.encode(user.getmUserPass()));
            editor.putString("token", SecurityUtils.encode(user.getmToken()));
            editor.putString("username", user.getmUsername());
            editor.putString("school", user.getmSchool());
            editor.putString("userpath", user.getmUserPath());
            editor.putString("userurl", user.getmUserUrl());
            editor.putString("role", user.getmRole());
            editor.putFloat("bright_point", user.getmGoldMoney());
            editor.putFloat("original_point", user.getmSilverMoney());
            editor.putString("introduction", user.getmSign());
            editor.putFloat("credibility", user.getmCreditibility());
            editor.putBoolean("identified", user.ismIsIdentificate());
            editor.commit();
        }
    }

    /**
     * 获取当前的个人账户
     * 传入user，但是内容只有手机号或者第三方的id
     *
     * @param context
     * @param user
     */
    public static void getCurrUser(Context context, User user) {
        if (user instanceof ThirdPartyUser) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(((ThirdPartyUser) user).getmThirdPartyId(), Context.MODE_APPEND);
            if (!TextUtils.isEmpty(sharedPreferences.getString("token", ""))) {
                user.setmToken(sharedPreferences.getString("token", ""));
            }
            String path = context.getExternalFilesDir(null).getAbsolutePath() + "/" + ((ThirdPartyUser) user).getmThirdPartyId() + "/" + ((ThirdPartyUser) user).getmThirdPartyId() + ".png";
            File file = new File(path);
            if (file.exists()) {
                user.setmUserPath(file.getAbsolutePath());
            } else {
                user.setmUserPath("");
            }
            ((ThirdPartyUser) user).setmThirdPartyId(sharedPreferences.getString("thirdpartyid", ""));
            user.setmUsername(sharedPreferences.getString("username", ""));
            user.setmUserUrl(sharedPreferences.getString("userurl", ""));
            user.setmUserPath(sharedPreferences.getString("userpath", ""));
            user.setmSilverMoney(sharedPreferences.getFloat("original_point", 0));
            user.setmGoldMoney(sharedPreferences.getFloat("bright_point", 0));
            user.setmCreditibility(sharedPreferences.getFloat("credibility", 0));
            user.setmIsIdentificate(sharedPreferences.getBoolean("identified", false));
            user.setmSign(sharedPreferences.getString("introduction", ""));
            user.setmSchool(sharedPreferences.getString("school", ""));
            user.setmRole(sharedPreferences.getString("role", ""));
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(user.getmUserPhone(), Context.MODE_APPEND);
            if (!TextUtils.isEmpty(sharedPreferences.getString("userpass", ""))) {
                user.setmUserPass(SecurityUtils.decode(sharedPreferences.getString("userpass", "")));
            }
            if (!TextUtils.isEmpty(sharedPreferences.getString("token", ""))) {
                user.setmToken(sharedPreferences.getString("token", ""));
            }
            user.setmUsername(sharedPreferences.getString("username", ""));
            user.setmUserUrl(sharedPreferences.getString("userurl", ""));
            user.setmUserPath(sharedPreferences.getString("userpath", ""));
            user.setmSilverMoney(sharedPreferences.getFloat("original_point", 0));
            user.setmGoldMoney(sharedPreferences.getFloat("bright_point", 0));
            user.setmCreditibility(sharedPreferences.getFloat("credibility", 0));
            user.setmIsIdentificate(sharedPreferences.getBoolean("identified", false));
            user.setmSign(sharedPreferences.getString("introduction", ""));
            user.setmSchool(sharedPreferences.getString("school", ""));
            user.setmRole(sharedPreferences.getString("role", ""));
        }
    }

    public static boolean isFirstInApp(Context context) {
        boolean isFirst = true;
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        isFirst = sharedPreferences.getBoolean("isfirstin", true);
        return isFirst;
    }

    public static void setNotFirstInApp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isfirstin", false);
        editor.commit();
    }

    public static void setUserSetting(Context context, String phone, boolean statu, int which) {
        SharedPreferences sharePreference = context.getSharedPreferences(phone, Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharePreference.edit();
        switch (which) {
            case 0:
                editor.putBoolean("soundsetting", statu);
                break;
            case 1:
                editor.putBoolean("shakesetting", statu);
                break;
        }
        editor.commit();
    }

    public static boolean getUserSetting(Context context, String phone, int which) {
        SharedPreferences sharePreference = context.getSharedPreferences(phone, Context.MODE_APPEND);
        Boolean statu = true;
        switch (which) {
            case 0:
                statu = sharePreference.getBoolean("soundsetting", true);
                break;
            case 1:
                statu = sharePreference.getBoolean("shakesetting", true);
                break;
            default:
                statu = false;
                break;
        }
        return statu;
    }

    public static void initUserSetting(Context context, String phone) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(phone, Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("soundsetting", true);
        editor.putBoolean("shakesetting", true);
    }
}
