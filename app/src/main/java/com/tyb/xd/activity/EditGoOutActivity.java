package com.tyb.xd.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.tyb.xd.R;
import com.tyb.xd.bean.CarryRecordBean;
import com.tyb.xd.bean.RewardBean;
import com.tyb.xd.bean.User;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.PlacePicker;
import com.tyb.xd.view.TimePicker;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;
import java.util.List;

@ContentView(R.layout.ac_show_go_out)
public class EditGoOutActivity extends Activity {
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mrlTop;
    @ViewInject(R.id.id_show_go_our_et_start_place)
    TextView mtvStartPlace;
    @ViewInject(R.id.id_show_go_our_et_arrive_place)
    TextView mtvEndPlace;
    @ViewInject(R.id.id_show_go_our_et_xd_grade)
    EditText metSmilePoint;
    @ViewInject(R.id.id_show_go_our_et_phone)
    EditText metPhone;
    @ViewInject(R.id.id_show_go_our_et_text_des)
    EditText metDes;
    @ViewInject(R.id.id_show_go_our_cb_qzone_share)
    CheckBox mcbQzon;
    @ViewInject(R.id.id_show_go_our_cb_frinds_share)
    CheckBox mcbWechat;
    @ViewInject(R.id.id_show_go_our_cb_weibo_share)
    CheckBox mcbWeibo;
    @ViewInject(R.id.id_show_go_our_btn_release)
    Button mbtnPublic;
    @ViewInject(R.id.id_top_back_tv)
    TextView mtvTopBack;
    @ViewInject(R.id.id_ac_show_go_out_tp_time)
    private TimePicker mtpTimePicker;

    private PopupWindow mPopWindowSelectPlace;

    //用于保存点击的是哪个textview（选择place的）
    private int miSelectPlace = -1;

    private Context mContext;
    private User mUser;
    private String mDelivery;
    private String mDeliveryId;
    private String msStartPlace;
    private String msEndPlace;
    private String msLimiteTIme;
    private String msSmilePoint;
    private String msPhone;
    private String msDes;
    private LatLonPoint mllpStartPlace;

    private GeocodeSearch mLatLongSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
        initData();
        initBundle();
    }
    private void initBundle(){
        Bundle bundle = getIntent().getExtras();
        final CarryRecordBean bean = (CarryRecordBean) bundle.getSerializable("outing");
        mDeliveryId = bean.getSendId();
        mDelivery = bean.getTaskOrOuting();
        String url = XDApplication.dbUrl+ "/delivery/"+mDelivery+"/"+mDeliveryId;
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("token",mUser.getmToken());
        params.addBodyParameter("username",mUser.getmUsername());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = JSON.parseObject(result);
                String status = json.getString("status");
                if(status.equals("success")){
                    JSONObject delivery = json.getJSONObject("delivery");
                    initBundleData(delivery);
                }else{
                    Toast.makeText(mContext,"加载数据失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex,"onCreate","EditGoOutActivity",EditGoOutActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    /**
     * 设置已有的数据
     * @param json
     */
    private void initBundleData(JSONObject json){
        String startPlace = json.getString("source");
        mtvStartPlace.setText(startPlace);
        String arrivePlace= json.getString("destination");
        mtvEndPlace.setText(arrivePlace);
        String contact = json.getString("contact");
        metPhone.setText(contact);
        String describe = json.getString("describe");
        metDes.setText(describe);
        String reward = json.getString("reward");
        metSmilePoint.setText(reward);
    }
    private void initData() {
        mLatLongSearch = new GeocodeSearch(mContext);
        mLatLongSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
                GeocodeAddress geocodeAddress = geocodeAddressList.get(0);
                mllpStartPlace = geocodeAddress.getLatLonPoint();
            }
        });
    }

    private void initView() {
        mContext = EditGoOutActivity.this;
        mUser = XDApplication.getmUser();
        mtpTimePicker.setDate(new Date().getTime());
        mtvTopContent.setText(mContext.getResources().getString(R.string.send_travel));
        mtvTopBack.setText(mContext.getResources().getString(R.string.cancel));
        msPhone = XDApplication.getmUser().getmUserPhone();
        metPhone.setText(msPhone);
    }

    @Event(value = {R.id.id_top_back_tv, R.id.id_show_go_our_et_start_place, R.id.id_show_go_our_et_arrive_place, R.id.id_show_go_our_btn_release})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_show_go_our_et_start_place:
                showPopSelectPlace();
                miSelectPlace = R.id.id_show_go_our_et_start_place;
                break;
            case R.id.id_show_go_our_et_arrive_place:
                showPopSelectPlace();
                miSelectPlace = R.id.id_show_go_our_et_arrive_place;
                break;
            case R.id.id_show_go_our_btn_release:
                confirm();
                break;
        }
    }

    private void confirm() {
        msPhone = metPhone.getText().toString();
        msLimiteTIme = mtpTimePicker.toString();
        msStartPlace = mtvStartPlace.getText().toString();
        msEndPlace = mtvEndPlace.getText().toString();
        msSmilePoint = metSmilePoint.getText().toString();
        msDes = metDes.getText().toString() + "";
        if (TextUtils.isEmpty(msPhone)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.phone_no_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(msStartPlace) || TextUtils.isEmpty(msEndPlace)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.good_start_or_end_place_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(msLimiteTIme)) {
            Toast.makeText(mContext, "请选择截止时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(msSmilePoint)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_give_smile_point), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mllpStartPlace == null) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.net_work_is_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        if (XDApplication.jurisdiction(mContext)) {
            publicGoOut();
        }
    }

    private void publicGoOut() {
        String url = XDApplication.dbUrl + "/delivery/"+mDelivery+mDeliveryId;
        RequestParams public_go_out = new RequestParams(url);
        public_go_out.addBodyParameter("source", msStartPlace);
        public_go_out.addBodyParameter("destination", msEndPlace);
        public_go_out.addBodyParameter("reward", msSmilePoint);
        public_go_out.addBodyParameter("contact", msPhone);
        public_go_out.addBodyParameter("describe", msDes);
        public_go_out.addBodyParameter("deadline", msLimiteTIme);
        public_go_out.addBodyParameter("lng", mllpStartPlace.getLongitude() + "");
        public_go_out.addBodyParameter("lat", mllpStartPlace.getLatitude() + "");
        public_go_out.addBodyParameter("username", XDApplication.getmUser().getmUsername());
        public_go_out.addBodyParameter("token", XDApplication.getmUser().getmToken());
        x.http().request(HttpMethod.PUT,public_go_out, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    /**
                     * 发布成功之后执行的操作
                     */
                    Toast.makeText(mContext,"发布成功", Toast.LENGTH_SHORT).show();
                    WelcomeActivity.complete_Info_without_download(mContext);
                    finish();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.public_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex,"onCreate","EditGoOutActivity",EditGoOutActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void showPopSelectPlace() {
        if (mPopWindowSelectPlace == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pop_select_place, null);
            mPopWindowSelectPlace = new PopupWindow(view, Util.dpToPx(getResources(), 320),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectPlace(view);

        }
        mPopWindowSelectPlace.showAtLocation(mtvStartPlace.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void initPopSelectPlace(View view) {
        final PlacePicker placePicker = (PlacePicker) view.findViewById(R.id.id_pop_select_place_tp_place);
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_place_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_place_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindowSelectPlace.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (miSelectPlace) {
                    case R.id.id_show_go_our_et_start_place:
                        msStartPlace = placePicker.toString();
                        mtvStartPlace.setText(msStartPlace);
                        GeocodeQuery query = new GeocodeQuery(XDApplication.getmUser().getmSchool() + placePicker.toEntireString(), "");
                        mLatLongSearch.getFromLocationNameAsyn(query);
                        break;
                    case R.id.id_show_go_our_et_arrive_place:
                        msEndPlace = placePicker.toString();
                        mtvEndPlace.setText(msEndPlace);
                        break;
                }
                mPopWindowSelectPlace.dismiss();
            }
        });
        mPopWindowSelectPlace.setOutsideTouchable(true);
    }
}
