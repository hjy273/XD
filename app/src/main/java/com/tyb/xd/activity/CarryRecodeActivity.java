package com.tyb.xd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tyb.xd.R;
import com.tyb.xd.adapter.ExpandAdapter;
import com.tyb.xd.bean.CarryRecordBean;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.MyExpandListView;
import com.tyb.xd.view.MyExpandListView.IRefreshListener;
import com.tyb.xd.view.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.ac_carry_recode)
public class CarryRecodeActivity extends Activity {

    @ViewInject(R.id.id_top_back_iv_img)
    private ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    private TextView mivTopContent;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_carryRecord_epl_my_publish)
    private MyExpandListView mepListViewMyPublish;
    @ViewInject(R.id.id_ac_carryRecord_epl_my_receive)
    private MyExpandListView mepListViewMyReceive;

    private Context mContext;
    private User mUser;
    private List<List<CarryRecordBean>> mPublish, mReceive;
    private List<CarryRecordBean> mPtask, mPouting, mRtask, mRouting;
    private ExpandAdapter mAdapterMyPublish, mAdapterMyReceive;
    private final int MSG_REFRESH_COMPLETE = 0x01;
    private final int MSG_LOAD_PUBLISH_DATA = 0x02;
    private final int MSG_LOAD_RECEIVE_DATA = 0x03;
    private boolean isRefresh = false;
    private PopupWindow mPopWindow;
    private ProgressDialog mProgressDialog;
    private String mType = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_COMPLETE:
                    if (msg.obj.equals("publish")) {
                        mAdapterMyPublish.notifyDataSetChanged();
                        mepListViewMyPublish.refreshCompleteOnHeader();
                    } else {
                        mAdapterMyReceive.notifyDataSetChanged();
                        mepListViewMyReceive.refreshCompleteOnHeader();
                    }
                    Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                    isRefresh = false;
                    break;
                case MSG_LOAD_PUBLISH_DATA:
                    mAdapterMyPublish = new ExpandAdapter(mContext, mPublish);
                    mepListViewMyPublish.setAdapter(mAdapterMyPublish);
                    mType = "receive";
                    initData();
                    break;
                case MSG_LOAD_RECEIVE_DATA:
                    mAdapterMyReceive = new ExpandAdapter(mContext, mReceive);
                    mepListViewMyReceive.setAdapter(mAdapterMyReceive);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        x.view().inject(this);
        initView();
    }

    private void initView() {
        mContext = CarryRecodeActivity.this;
        mivTopBack.setImageResource(R.drawable.go_back_white);
        mivTopContent.setText(mContext.getResources().getString(R.string.carry_recode));

        mepListViewMyReceive.setInterface(new IRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                mType = "receive";
                initData();

            }
        });
        mepListViewMyPublish.setInterface(new IRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                mType = "publish";
                initData();
            }
        });

        mProgressDialog = new ProgressDialog(mContext);
        mUser = XDApplication.getmUser();
        mPublish = new ArrayList<>();
        mReceive = new ArrayList<>();
        mType = "publish";
        initData();
        /**
         * 设置子项点击事件
         */
        OnChildClickListener listener = new OnChildClickListener();
        mepListViewMyPublish.setOnChildClickListener(listener);
        mepListViewMyReceive.setOnChildClickListener(listener);
        /**
         * 设置子项长按事件
         */
        OnItemLongClickListener longClickListener = new OnItemLongClickListener();
        mepListViewMyPublish.setOnItemLongClickListener(longClickListener);
        mepListViewMyReceive.setOnItemLongClickListener(longClickListener);
    }

    class OnChildClickListener implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            CarryRecordBean bean = null;
            switch (parent.getId()) {
                case R.id.id_ac_carryRecord_epl_my_publish:
                    bean = mPublish.get(groupPosition).get(childPosition);
                    break;
                case R.id.id_ac_carryRecord_epl_my_receive:
                    bean = mReceive.get(groupPosition).get(childPosition);
                    break;
            }
            assert bean != null;
            String status = bean.getSendRecordStatus();
            if (status.equals("1") || status.equals("2")) {
                Bundle bundle = new Bundle();
                if (bean.getTaskOrOuting().equals("task")) {
                    Intent intent = new Intent(CarryRecodeActivity.this, EditRewardActivity.class);
                    bundle.putSerializable("task", bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CarryRecodeActivity.this, EditGoOutActivity.class);
                    bundle.putSerializable("outing", bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
            return true;
        }
    }

    class OnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final int w = parent.getId();
            final int groupPosition = (Integer) view.getTag(R.id.bottom);
            final int childPosition = (Integer) view.getTag(R.id.center);
            if (childPosition != -1 && groupPosition == 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle("确认删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCarryRecord(w, childPosition, groupPosition);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
            return true;
        }
    }

    private void deleteCarryRecord(final int which, final int childPosition, final int groupPosition) {
        CarryRecordBean bean = null;
        switch (which) {
            case R.id.id_ac_carryRecord_epl_my_publish:
                bean = mPublish.get(groupPosition).get(childPosition);
                break;
            case R.id.id_ac_carryRecord_epl_my_receive:
                bean = mReceive.get(groupPosition).get(childPosition);
                break;
        }
        assert bean != null;
        String delivery_id = bean.getSendId();
        String delivery_type = bean.getTaskOrOuting();
        String status = bean.getSendRecordStatus();
        if (!status.equals("1") && !status.equals("2") && !status.equals("3") && !status.equals("6")) {
            Toast.makeText(mContext, "不允许删除", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog.setTitle("正在删除...");
        mProgressDialog.show();
        String url = XDApplication.dbUrl + "/delivery/" + delivery_type + "/" + delivery_id;
        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("username", mUser.getmUsername());
        params.addQueryStringParameter("token", mUser.getmToken());
        x.http().request(HttpMethod.DELETE, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getString("status").equals("success")) {
                        Toast.makeText(mContext, getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case R.id.id_ac_carryRecord_epl_my_publish:
                                mPublish.get(groupPosition).remove(childPosition);
                                mAdapterMyPublish.notifyDataSetChanged();
                                break;
                            case R.id.id_ac_carryRecord_epl_my_receive:
                                mReceive.get(groupPosition).remove(childPosition);
                                mAdapterMyReceive.notifyDataSetChanged();
                                break;
                        }
                        mProgressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                Errorutils.showError(mContext, ex, "initView", "CarryRecodeActivity", this);
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

    private void showPopWindow(CarryRecordBean bean) {
        if (mPopWindow == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pop_select_time, null);
            mPopWindow = new PopupWindow(view, Util.dpToPx(getResources(), 320), LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectTime(view, bean);
        }
        mPopWindow.showAtLocation(null, Gravity.CENTER, 0, 0);
    }

    private void initPopSelectTime(View view, final CarryRecordBean bean) {
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.id_pop_select_time_tp_time);
        timePicker.setDate(new Date().getTime());
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_time_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_time_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msTime = timePicker.toString();
                String url = XDApplication.dbUrl + "/delivery/" + bean.getTaskOrOuting() + "/" + bean.getSendId() + "/overdue";
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("deadline", msTime);
                params.addBodyParameter("username", mUser.getmUsername());
                params.addBodyParameter("token", mUser.getmToken());
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            String status = json.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                                bean.setSendRecordStatus("2");
                                handler.sendEmptyMessage(MSG_REFRESH_COMPLETE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                        Errorutils.showError(mContext, ex, "initView", "CarryRecodeActivity", this);
                        Errorutils.showXutilError(mContext, ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
    }

    @Event(value = {R.id.id_top_back_iv_img})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    private void initData() {
        if (mType.equals("publish")) {
            mPtask = new ArrayList<>();
            mPouting = new ArrayList<>();
        } else {
            mRtask = new ArrayList<>();
            mRouting = new ArrayList<>();
        }
        String username = mUser.getmUsername();
        String token = mUser.getmToken();
        String url_task = XDApplication.dbUrl + "/delivery/task/self/record";
        RequestParams params_task = new RequestParams(url_task);
        params_task.addBodyParameter("username", username);
        params_task.addBodyParameter("token",token);
        params_task.addBodyParameter("type", mType);
        x.http().get(params_task, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                initBean(result, "task");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showError(mContext, ex, "initView", "CarryRecodeActivity", this);
                Errorutils.showXutilError(mContext, ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
        String url_outing = XDApplication.dbUrl + "/delivery/outing/self/record";
        RequestParams params_outing = new RequestParams(url_outing);
        params_outing.addBodyParameter("username", username);
        params_outing.addBodyParameter("token", token);
        params_outing.addBodyParameter("type", mType);
        x.http().get(params_outing, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                initBean(result, "outing");
                if (mType.equals("publish")) {
                    if (isRefresh) {
                        Message msg = new Message();
                        msg.what = MSG_REFRESH_COMPLETE;
                        msg.obj = "publish";
                        handler.sendMessage(msg);
                    } else {
                        mPublish.add(mPtask);
                        mPublish.add(mPouting);
                        handler.sendEmptyMessage(MSG_LOAD_PUBLISH_DATA);
                    }
                } else {
                    if (isRefresh) {
                        Message msg = new Message();
                        msg.what = MSG_REFRESH_COMPLETE;
                        msg.obj = "receive";
                        handler.sendMessage(msg);
                    } else {
                        mReceive.add(mRtask);
                        mReceive.add(mRouting);
                        handler.sendEmptyMessage(MSG_LOAD_RECEIVE_DATA);
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showError(mContext, ex, "initView", "CarryRecodeActivity", this);
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

    private void initBean(String result, String type) {
        try {
            JSONObject json = new JSONObject(result);
            String status = json.getString("status");
            if (status.equals("success")) {
                JSONArray deliveries = json.getJSONArray("deliveries");
                for (int j = 0; j < deliveries.length(); j++) {
                    JSONObject delivery = deliveries.getJSONObject(j);
                    CarryRecordBean bean = new CarryRecordBean();
                    bean.setReward(delivery.getString("reward"));
                    bean.setSendRecordStatus(delivery.getString("state"));
                    bean.setSendId(delivery.getString("_id"));
                    bean.setSendStartPlace(delivery.getString("source").substring(delivery.getString("source").indexOf("校区") + 2));
                    bean.setSendArrivePlace(delivery.getString("destination").substring(delivery.getString("source").indexOf("校区") + 2));
                    bean.setSendRecordTime(delivery.getString("time").substring(5, 16));
                    if (type.equals("task")) {
                        bean.setTypeOrName(delivery.getJSONObject("thing").getString("type"));
                        bean.setImg(delivery.getJSONObject("thing").getString("thumbnail"));
                        bean.setTaskOrOuting("task");
                        if (mType.equals("publish")) {
                            mPtask.add(bean);
                        } else {
                            mRtask.add(bean);
                        }
                    } else {
                        bean.setTypeOrName(delivery.getJSONObject("user").getString("nickname"));
                        bean.setImg(delivery.getJSONObject("user").getString("headimg"));
                        bean.setTaskOrOuting("outing");
                        if (mType.equals("publish")) {
                            mPouting.add(bean);
                        } else {
                            mRouting.add(bean);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return false;
    }
}
