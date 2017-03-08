package com.tyb.xd.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.tyb.xd.utils.FileUtil;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.PlacePicker;
import com.tyb.xd.view.TimePicker;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.ac_send_reward)
public class EditRewardActivity extends Activity {
    @ViewInject(R.id.id_top_back_tv)
    private TextView mtvTopBackContent;
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    private RelativeLayout mrlTop;
    @ViewInject(R.id.id_ac_send_reward_iv_express_img)
    private ImageView mivExpress;
    @ViewInject(R.id.id_ac_send_reward_iv_food_img)
    private ImageView mivFood;
    @ViewInject(R.id.id_ac_send_reward_iv_paper_img)
    private ImageView mivPaper;
    @ViewInject(R.id.id_ac_send_reward_iv_other_img)
    private ImageView mivOther;
    @ViewInject(R.id.id_ac_send_reward_iv_take_photo_img)
    private ImageView mivTakePhoto;
    @ViewInject(R.id.id_ac_send_reward_rb_light)
    private RadioButton mrbLight;
    @ViewInject(R.id.id_ac_send_reward_rb_medium)
    private RadioButton mrbMedium;
    @ViewInject(R.id.id_ac_send_reward_rb_heavy)
    private RadioButton mrbHeavy;
    @ViewInject(R.id.id_ac_reward_tv_limited_time)
    private TextView mtvLimitedTime;
    @ViewInject(R.id.id_ac_send_reward_et_start_place)
    private TextView mtvStartPlace;
    @ViewInject(R.id.id_ac_send_reward_et_arrive_place)
    private TextView mtvEndPlace;
    @ViewInject(R.id.id_ac_send_reward_et_xd_grade)
    private EditText metSmilePoint;
    @ViewInject(R.id.id_ac_send_reward_et_phone)
    private EditText metPhone;
    @ViewInject(R.id.id_ac_send_reward_et_text_des)
    private EditText metDescribe;
    @ViewInject(R.id.id_ac_send_reward_cb_qzone_share)
    private CheckBox mcbQzon;
    @ViewInject(R.id.id_ac_send_reward_cb_wechat_share)
    private CheckBox mcbWeChat;
    @ViewInject(R.id.id_ac_send_reward_cb_weibo_share)
    private CheckBox mcbWeibo;
    @ViewInject(R.id.id_ac_send_reward_btn_release)
    private Button mbtnRelease;
    @ViewInject(R.id.id_ac_send_reward_rg_weight)
    private RadioGroup mrgWeight;
    private Context mContext;

    private int miWeight = -1;
    private String msStartPlace = "";
    private String msEndPlace = "";
    private String msSmilePoint = "";
    private String msPhone = "";
    private String msTime = "";
    private String msDes = "";
    private User mUser;
    private String mDelivery;
    private String mDeliveryId;
    /**
     * 选择的类型
     */
    private int miSelectGoodType = -1;

    private String msSelectGoodType = "";

    private LayoutInflater mLayoutInflater;

    private PopupWindow mPopSelectTime;

    private PopupWindow mPopSelectPhoto;

    private PopupWindow mPopSelectPlace;

    private ProgressDialog mProgressDialog;

    private int iPlaceSelect = -1;

    //图片保存的文件夹
    private static String PHOTOSAVEPATH = XDApplication.msSavePhth + "/crop_photo/";
    //以当前时间的毫秒数当做文件名，设置好的图片的路径
    private String photoname = System.currentTimeMillis() + ".png";


    private String mPath;  //要找的图片路径

    private final static int PHOTOBYGALLERY = 0;//从相册获取照片

    private final static int PHOTOTACK = 1;//拍照获取

    private final static int PHOTOCOMPLETEBYTAKE = 2;//完成
    private final static int PHOTOCOMPLETEBYGALLERY = 3;//完成

    private static int PHOTOCROP = 3;//图片裁剪

    private GeocodeSearch mGeocoderSearch;

    //开始的坐标点
    private LatLonPoint mllpStart;
    //结束的坐标点
    private LatLonPoint mllpEnd;


    /**
     * 搜索选择的城市，主要是为了定位的准确性
     */
    private String mSearchCity = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initView();
        initData();
        initBundle();
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        final CarryRecordBean bean = (CarryRecordBean) bundle.getSerializable("task");
        mDeliveryId = bean.getSendId();
        mDelivery = bean.getTaskOrOuting();
        String url = XDApplication.dbUrl + "/delivery/" + mDelivery + "/" + mDeliveryId;
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("token", mUser.getmToken());
        params.addBodyParameter("username", mUser.getmUsername());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject json = JSON.parseObject(result);
                String status = json.getString("status");
                if (status.equals("success")) {
                    JSONObject delivery = json.getJSONObject("delivery");
                    initBundleData(delivery);
                } else {
                    Toast.makeText(mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "onCreate", "EditRewardActivity", EditRewardActivity.this);
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
     *
     * @param json
     */
    private void initBundleData(JSONObject json) {
        msSelectGoodType = json.getJSONObject("thing").getString("type");
        switch (msSelectGoodType) {
            case RewardBean.GOODTYPE_EXPRESS:
                ImageOptions options_express = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.drawable.good_type_express)
                        .setFailureDrawableId(R.drawable.good_type_express)
                        .setCircular(true)
                        .build();
                x.image().bind(mivExpress, json.getString("thumbnail"), options_express);
                mivExpress.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 0;
                break;
            case RewardBean.GOODTYPE_FOOG:
                ImageOptions options_food = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.drawable.good_type_food)
                        .setFailureDrawableId(R.drawable.good_type_food)
                        .setCircular(true)
                        .build();
                x.image().bind(mivFood, json.getString("thumbnail") , options_food);
                mivFood.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 1;
                break;
            case RewardBean.GOODTYPE_PAPER:
                ImageOptions options_paper = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.drawable.good_type_paper)
                        .setFailureDrawableId(R.drawable.good_type_paper)
                        .setCircular(true)
                        .build();
                x.image().bind(mivPaper, json.getString("thumbnail") , options_paper);
                mivPaper.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 2;
                break;
            case RewardBean.GOODTYPE_OTHER:
                ImageOptions options_other = new ImageOptions.Builder()
                        .setLoadingDrawableId(R.drawable.good_type_other)
                        .setFailureDrawableId(R.drawable.good_type_other)
                        .setCircular(true)
                        .build();
                x.image().bind(mivOther, json.getString("thumbnail") , options_other);
                mivOther.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 3;
                break;
        }
        miWeight = Integer.valueOf(json.getJSONObject("thing").getString("weight"));
        RadioButton button = (RadioButton) mrgWeight.getChildAt(miWeight);
        button.setChecked(true);
        String startPlace = json.getString("source");
        mtvStartPlace.setText(startPlace);
        String arrivePlace = json.getString("destination");
        mtvEndPlace.setText(arrivePlace);
        String contact = json.getString("contact");
        metPhone.setText(contact);
        String describe = json.getString("describe");
        metDescribe.setText(describe);
        String reward = json.getString("reward");
        metSmilePoint.setText(reward);
    }

    public void initView() {
        mContext = EditRewardActivity.this;
        mUser = XDApplication.getmUser();
        mLayoutInflater = LayoutInflater.from(mContext);
        mtvTopContent.setText(mContext.getResources().getString(R.string.send_reward));
        mtvTopBackContent.setText(mContext.getResources().getString(R.string.cancel));
        mtvTopBackContent.setTextColor(mContext.getResources().getColor(R.color.text_color_white));
        metPhone.setText(XDApplication.getmUser().getmUserPhone());
        mtvLimitedTime.setText(TimeUtils.getTime());
        resetImgBg();
        initRadioGroup();
    }

    private void initData() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mContext.getResources().getString(R.string.is_publishing));
        mGeocoderSearch = new GeocodeSearch(mContext);
        mGeocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                switch (iPlaceSelect) {
                    case R.id.id_ac_send_reward_et_start_place:
                        List<GeocodeAddress> geocodeAddressList_start = geocodeResult.getGeocodeAddressList();
                        GeocodeAddress geocodeAddress_start = geocodeAddressList_start.get(0);
                        mllpStart = geocodeAddress_start.getLatLonPoint();
                        break;
                    case R.id.id_ac_send_reward_et_arrive_place:
                        List<GeocodeAddress> geocodeAddressList_end = geocodeResult.getGeocodeAddressList();
                        GeocodeAddress geocodeAddress_end = geocodeAddressList_end.get(0);
                        mllpEnd = geocodeAddress_end.getLatLonPoint();
                        break;
                }
                AutoInitSmilePoint();
            }
        });
    }

    private void AutoInitSmilePoint() {
        switch (miWeight) {
            case 0:
                if (mllpStart != null && mllpEnd != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(mllpStart.getLatitude(), mllpStart.getLongitude(),
                            mllpEnd.getLatitude(), mllpEnd.getLongitude(), results);
                    if (results[0] < 1000) {
                        msSmilePoint = 50 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else if (results[0] < 5000) {
                        msSmilePoint = 80 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else {
                        msSmilePoint = 120 + "";
                        metSmilePoint.setText(msSmilePoint);
                    }
                }
                break;
            case 1:
                if (mllpStart != null && mllpEnd != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(mllpStart.getLatitude(), mllpStart.getLongitude(),
                            mllpEnd.getLatitude(), mllpEnd.getLongitude(), results);
                    if (results[0] < 1000) {
                        msSmilePoint = 70 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else if (results[0] < 5000) {
                        msSmilePoint = 100 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else {
                        msSmilePoint = 150 + "";
                        metSmilePoint.setText(msSmilePoint);
                    }
                }
                break;
            case 2:
                if (mllpStart != null && mllpEnd != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(mllpStart.getLatitude(), mllpStart.getLongitude(),
                            mllpEnd.getLatitude(), mllpEnd.getLongitude(), results);
                    if (results[0] < 1000) {
                        msSmilePoint = 100 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else if (results[0] < 5000) {
                        msSmilePoint = 130 + "";
                        metSmilePoint.setText(msSmilePoint);
                    } else {
                        msSmilePoint = 200 + "";
                        metSmilePoint.setText(msSmilePoint);
                    }
                }
                break;
            case -1:
                break;
        }
    }


    private void initRadioGroup() {
        mrgWeight.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_ac_send_reward_rb_light:
                        miWeight = RewardBean.IWEIGHT_LIGHT_INT;
                        break;
                    case R.id.id_ac_send_reward_rb_medium:
                        miWeight = RewardBean.IWEIGHT_MEDIUM_INT;
                        break;
                    case R.id.id_ac_send_reward_rb_heavy:
                        miWeight = RewardBean.IWEIGHT_HEAVY_INT;
                        break;
                }
                AutoInitSmilePoint();
            }
        });
    }

    private void initPopSelectTime(View view) {
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.id_pop_select_time_tp_time);
        timePicker.setDate(new Date().getTime());
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_time_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_time_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectTime.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msTime = timePicker.toString();
                mtvLimitedTime.setText(timePicker.toString());
                mPopSelectTime.dismiss();
            }
        });
        mPopSelectTime.setOutsideTouchable(true);
        mPopSelectTime.setAnimationStyle(android.R.style.Animation_InputMethod);
    }

    private void showPopSelectTime() {
        if (mPopSelectTime == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_time, null);
            mPopSelectTime = new PopupWindow(view, Util.dpToPx(getResources(), 320),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectTime(view);
        }
        mPopSelectTime.showAtLocation(mtvLimitedTime.getRootView().getRootView().getRootView(), Gravity.CENTER, 0, 0);
    }

    @Event(value = {R.id.id_top_back_tv, R.id.id_ac_send_reward_iv_express_img,
            R.id.id_ac_send_reward_iv_food_img, R.id.id_ac_send_reward_iv_paper_img,
            R.id.id_ac_send_reward_iv_other_img, R.id.id_ac_reward_tv_limited_time,
            R.id.id_ac_send_reward_btn_release, R.id.id_ac_send_reward_iv_take_photo_img,
            R.id.id_ac_send_reward_et_start_place, R.id.id_ac_send_reward_et_arrive_place})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_tv:
                finish();
                break;
            case R.id.id_ac_send_reward_iv_express_img:
                resetImgBg();
                mivExpress.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 0;
                msSelectGoodType = RewardBean.GOODTYPE_EXPRESS;
                break;
            case R.id.id_ac_send_reward_iv_food_img:
                resetImgBg();
                mivFood.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 1;
                msSelectGoodType = RewardBean.GOODTYPE_FOOG;
                break;
            case R.id.id_ac_send_reward_iv_paper_img:
                resetImgBg();
                mivPaper.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 2;
                msSelectGoodType = RewardBean.GOODTYPE_PAPER;
                break;
            case R.id.id_ac_send_reward_iv_other_img:
                resetImgBg();
                mivOther.setBackgroundResource(R.drawable.circle_stroke_green_60);
                miSelectGoodType = 3;
                msSelectGoodType = RewardBean.GOODTYPE_OTHER;
                break;
            case R.id.id_ac_reward_tv_limited_time:
                showPopSelectTime();
                break;
            case R.id.id_ac_send_reward_et_start_place:
                iPlaceSelect = R.id.id_ac_send_reward_et_start_place;
                showPopSelectPlace();
                break;
            case R.id.id_ac_send_reward_et_arrive_place:
                iPlaceSelect = R.id.id_ac_send_reward_et_arrive_place;
                showPopSelectPlace();
                break;
            case R.id.id_ac_send_reward_iv_take_photo_img:
                if (miSelectGoodType == -1) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.please_select_good_type), Toast.LENGTH_SHORT).show();
                    break;
                }
                showPopSelectPhoto();
                break;
            case R.id.id_ac_send_reward_btn_release:
                confirm();
                break;
        }
    }

    private void confirm() {
        //选择物品的类型
        if (miSelectGoodType == -1) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_select_good_type), Toast.LENGTH_SHORT).show();
            return;
        }
        //选择物品的重量类型
        if (miWeight == -1) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_select_good_weight_type), Toast.LENGTH_SHORT).show();
            return;
        }
        //判断出发地和目的地是否为空
        msStartPlace = mtvStartPlace.getText().toString();
        msEndPlace = mtvEndPlace.getText().toString();
        if ((TextUtils.isEmpty(msStartPlace))
                || (TextUtils.isEmpty(msEndPlace))) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.good_start_or_end_place_null), Toast.LENGTH_SHORT).show();
            return;
        }
        //选择物品笑点
        msSmilePoint = metSmilePoint.getText().toString();
        if ((TextUtils.isEmpty(msSmilePoint))) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_give_smile_point), Toast.LENGTH_SHORT).show();
            return;
        }
        //手机号判断
        msPhone = metPhone.getText().toString();
        if ((TextUtils.isEmpty(msPhone))) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.phone_no_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mllpStart == null || mllpEnd == null) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.net_work_is_fail), Toast.LENGTH_SHORT).show();
            return;
        }
        //获取时间
        msTime = mtvLimitedTime.getText().toString();
        if (!TimeUtils.TimeOverNow(msTime)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.time_over_now), Toast.LENGTH_SHORT).show();
            return;
        }
        if (XDApplication.jurisdiction(mContext)) {
            //具有权限操作了，将物品进行发布
            publicGood();
        }
    }

    private void publicGood() {
        mProgressDialog.show();
        String url = XDApplication.dbUrl + "/delivery/" + mDelivery + "/" + mDeliveryId;
        RequestParams requestParams = new RequestParams(url);
        requestParams.addBodyParameter("type", msSelectGoodType);
        requestParams.addBodyParameter("weight", miWeight + "");
        requestParams.addBodyParameter("source", msStartPlace);
        requestParams.addBodyParameter("destination", msEndPlace);
        requestParams.addBodyParameter("reward", msSmilePoint);
        requestParams.addBodyParameter("contact", msPhone);
        requestParams.addBodyParameter("describe", metDescribe.getText().toString() + "");
        requestParams.addBodyParameter("deadline", msTime);
        requestParams.addBodyParameter("lng", mllpStart.getLongitude() + "");
        requestParams.addBodyParameter("lat", mllpStart.getLatitude() + "");
        requestParams.addBodyParameter("token", XDApplication.getmUser().getmToken());
        requestParams.addBodyParameter("username", XDApplication.getmUser().getmUsername());
        if (mPath != null) {
            requestParams.addBodyParameter("thumbnail", new File(mPath));
        }
        x.http().request(HttpMethod.PUT, requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mProgressDialog.dismiss();
                JSONObject jsonObjec = JSON.parseObject(result);
                String status = jsonObjec.getString("status");
                if (status.equals("success")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.public_success), Toast.LENGTH_SHORT).show();
                    WelcomeActivity.complete_Info_without_download(mContext);
                    finish();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.public_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mProgressDialog.dismiss();
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "onCreate", "EditRewardActivity", EditRewardActivity.this);
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
        if (mPopSelectPlace == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_place, null);
            mPopSelectPlace = new PopupWindow(view, Util.dpToPx(getResources(), 320),
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectPlace(view);
        }
        mPopSelectPlace.showAtLocation(mtvStartPlace.getRootView(), Gravity.CENTER, 0, 0);
    }


    private void initPopSelectPlace(View view) {
        final PlacePicker placePicker = (PlacePicker) view.findViewById(R.id.id_pop_select_place_tp_place);
        Button btnCancel = (Button) view.findViewById(R.id.id_pop_select_place_cancel);
        Button btnConfirm = (Button) view.findViewById(R.id.id_pop_select_place_confirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectPlace.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (iPlaceSelect) {
                    case R.id.id_ac_send_reward_et_start_place:
                        msStartPlace = placePicker.toString();
                        mtvStartPlace.setText(msStartPlace);
                        GeocodeQuery query_start = new GeocodeQuery(XDApplication.getmUser().getmSchool() + placePicker.toEntireString(), mSearchCity);
                        mGeocoderSearch.getFromLocationNameAsyn(query_start);
                        mPopSelectPlace.dismiss();
                        break;
                    case R.id.id_ac_send_reward_et_arrive_place:
                        msEndPlace = placePicker.toString();
                        mtvEndPlace.setText(msEndPlace);
                        GeocodeQuery query_end = new GeocodeQuery(XDApplication.getmUser().getmSchool() + placePicker.toEntireString(), mSearchCity);
                        mGeocoderSearch.getFromLocationNameAsyn(query_end);
                        mPopSelectPlace.dismiss();
                        break;
                }

            }
        });
        mPopSelectPlace.setOutsideTouchable(true);
        mPopSelectPlace.setAnimationStyle(android.R.style.Animation_InputMethod);
    }

    private void showPopSelectPhoto() {
        if (mPopSelectPhoto == null) {
            View view = mLayoutInflater.inflate(R.layout.pop_select_photo, null);
            mPopSelectPhoto = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            initPopSelectPhoto(view);
        }
        mPopSelectPhoto.showAtLocation(mivOther.getRootView(), Gravity.CENTER, 0, 0);
    }

    private void initPopSelectPhoto(View v) {
        //获取控件
        TextView tvGallery = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_from_gallery);
        TextView tvTack = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_take_photo);
        TextView tvCancel = (TextView) v.findViewById(R.id.id_pop_select_photo_tv_cancel);
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectPhoto.dismiss();
                startToGetPhotoByGallery();
            }
        });
        tvTack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSelectPhoto.dismiss();
                startToGetPhotoByTack();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopSelectPhoto.isShowing())
                    mPopSelectPhoto.dismiss();
            }
        });
        //设置动画
        mPopSelectPhoto.setAnimationStyle(android.R.style.Animation_InputMethod);
        //设置可以点击外面
        mPopSelectPhoto.setOutsideTouchable(true);
        //设置popupwindow为透明的，这样背景就是主界面的内容
        mPopSelectPhoto.setBackgroundDrawable(new BitmapDrawable());
        mPopSelectPhoto.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void resetImgBg() {
        mivExpress.setBackground(new BitmapDrawable());
        mivFood.setBackground(new BitmapDrawable());
        mivPaper.setBackground(new BitmapDrawable());
        mivOther.setBackground(new BitmapDrawable());
    }

    private void resetImg() {
        mivExpress.setImageResource(R.drawable.good_type_express);
        mivFood.setImageResource(R.drawable.good_type_express);
        mivPaper.setImageResource(R.drawable.good_type_express);
        mivOther.setImageResource(R.drawable.good_type_express);
    }


    private void startToGetPhotoByGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openGalleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openGalleryIntent, PHOTOBYGALLERY);
    }

    private void startToGetPhotoByTack() {
        photoname = String.valueOf(System.currentTimeMillis()) + ".png";
        Uri imageUri = null;
        /***********************************************************************************/
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(PHOTOSAVEPATH, photoname));
        openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PHOTOTACK);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case PHOTOBYGALLERY:
                uri = data.getData();
                if (uri != null) {
                    if (Build.VERSION.SDK_INT > 18) {
                        if (DocumentsContract.isDocumentUri(mContext, uri)) {
                            String wholeID = DocumentsContract.getDocumentId(uri);
                            String id = wholeID.split(":")[1];
                            String[] column = {MediaStore.Images.Media.DATA};
                            String sel = MediaStore.Images.Media._ID + "=?";
                            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                                    sel, new String[]{id}, null);
                            int columnIndex = cursor.getColumnIndex(column[0]);
                            if (cursor.moveToFirst()) {
                                mPath = cursor.getString(columnIndex);
                            }
                            cursor.close();
                        } else {
                            String[] projection = {MediaStore.Images.Media.DATA};
                            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            mPath = cursor.getString(column_index);
                        }
                    } else {
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        mPath = cursor.getString(column_index);
                    }
                }
                /**
                 * 获取到照片之后调用裁剪acticity
                 */
                Intent intentGalley = new Intent(mContext, ClipActivity.class);
                intentGalley.putExtra("path", mPath);
                startActivityForResult(intentGalley, PHOTOCOMPLETEBYGALLERY);
                break;
            case PHOTOTACK:
                mPath = PHOTOSAVEPATH + photoname;
                //   uri = Uri.fromFile(new File(path));
                /**
                 * 拿到uri后进行裁剪处理
                 */
                Intent intentTake = new Intent(mContext, ClipActivity.class);
                intentTake.putExtra("path", mPath);
                startActivityForResult(intentTake, PHOTOCOMPLETEBYTAKE);
                break;
            case PHOTOCOMPLETEBYTAKE:
                final String temppath = data.getStringExtra("path");
                resetImg();
                switch (miSelectGoodType) {
                    case 0:
                        mivExpress.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                        break;
                    case 1:
                        mivFood.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                        break;
                    case 2:
                        mivPaper.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                        break;
                    case 3:
                        mivOther.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppath));
                        break;
                }
                /**
                 * 删除旧文件
                 */
                File file = new File(mPath);
                file.delete();
                mPath = temppath;
                break;
            case PHOTOCOMPLETEBYGALLERY:
                final String temppathgallery = data.getStringExtra("path");
                resetImg();
                switch (miSelectGoodType) {
                    case 0:
                        mivExpress.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                        break;
                    case 1:
                        mivFood.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                        break;
                    case 2:
                        mivPaper.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                        break;
                    case 3:
                        mivOther.setImageBitmap(FileUtil.getBitmapFormPath(mContext, temppathgallery));
                        break;
                }
                mPath = temppathgallery;
                break;
        }
    }

    /**
     * 根据返回的重量级返回指定的字符串
     *
     * @param context
     * @param index
     * @return
     */
    public String getGoodWeightType(Context context, int index) {
        String sWeightType = "";
        switch (index) {
            case 1:
                sWeightType = context.getResources().getString(R.string.weight_light);
                break;
            case 2:
                sWeightType = context.getResources().getString(R.string.weight_middle);
                break;
            case 3:
                sWeightType = context.getResources().getString(R.string.weight_heavy);
                break;
            case -1:
                break;
        }
        return sWeightType;
    }
}
