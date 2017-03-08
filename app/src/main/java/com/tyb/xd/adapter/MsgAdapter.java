package com.tyb.xd.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jauker.widget.BadgeView;
import com.tyb.xd.R;
import com.tyb.xd.activity.WelcomeActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.bean.MsgBean;
import com.tyb.xd.bean.ThirdPartyUser;
import com.tyb.xd.bean.User;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.SharePreferenceUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by wangpeiyu on 2016/8/4.
 */
public class MsgAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<MsgBean> mlist;
    private Context mContext;

    public MsgAdapter(List<MsgBean> mlist, Context mContext) {
        this.mlist = mlist;
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.msg_item, null);
        final BadgeView number = (BadgeView) view.findViewById(R.id.id_msg_bv_number);
        final ImageView imageView = (ImageView) view.findViewById(R.id.id_msg_iv_img);
        final TextView toUser = (TextView) view.findViewById(R.id.id_msg_tv_name);
        final TextView content = (TextView) view.findViewById(R.id.id_msg_tv_last_msg);
        final TextView time = (TextView) view.findViewById(R.id.id_msg_tv_time);
        number.setBadgeCount(mlist.get(position).getMiNotReadNum());
        content.setText(mlist.get(position).getMsLastMsg());
        time.setText(mlist.get(position).getMsLastTime());
        String url = null;
        try {
            url = XDApplication.dbUrl + "/user/person/" + URLEncoder.encode(mlist.get(position).getToUser(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        imageView.setImageResource(R.drawable.default_headimg);
        RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("token", XDApplication.getmUser().getmToken());
        requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getString("status").equals("success")) {
                    String imgUrl = jsonObject.getJSONObject("user").getString("headimg");
                    ImageOptions options = new ImageOptions.Builder()
                            .setLoadingDrawableId(R.drawable.default_headimg)
                            .setFailureDrawableId(R.drawable.default_headimg)
                            .setCircular(true)
                            .build();
                    x.image().bind(imageView, imgUrl, options);
                    toUser.setText(jsonObject.getJSONObject("user").getString("nickname"));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                String error = ex.toString().substring(41);
                JSONObject json = JSON.parseObject(error);
                String code = json.getString("error_code");
                if (code.equals("er02")) {
                    if (SharePreferenceUtils.getLoginStatus(mContext)) {
                        User mUser = SharePreferenceUtils.getCurrLoginUser(mContext);
                        SharePreferenceUtils.getCurrUser(mContext, mUser);
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
                                        MsgAdapter.this.notifyDataSetChanged();
                                        WelcomeActivity.complete_Info_Third(mContext);
                                    }
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    Errorutils.showXutilError(mContext, ex);
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
                                                MsgAdapter.this.notifyDataSetChanged();
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
                                                                    SharePreferenceUtils.setCurrLoginUser(mContext, XDApplication.getmUser());
                                                                    SharePreferenceUtils.setCurrUser(mContext, XDApplication.getmUser());
                                                                    WelcomeActivity.loginConversation(mContext);
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Throwable ex, boolean isOnCallback) {
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

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
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

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
        return view;
    }


}
