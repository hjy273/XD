package com.tyb.xd.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.throrinstudio.android.common.libs.validator.Form;
import com.throrinstudio.android.common.libs.validator.Validate;
import com.throrinstudio.android.common.libs.validator.validator.NotEmptyValidator;
import com.tyb.xd.R;
import com.tyb.xd.activity.AddActivity;
import com.tyb.xd.activity.ClipActivity;
import com.tyb.xd.activity.GoOutDetail;
import com.tyb.xd.activity.LBS_amapActivity;
import com.tyb.xd.activity.RewardDetail;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.adapter.CommonAdapter;
import com.tyb.xd.adapter.ViewHolder;
import com.tyb.xd.bean.GoOutBean;
import com.tyb.xd.bean.RewardBean;
import com.tyb.xd.fastbean.GoOutDeliveries;
import com.tyb.xd.fastbean.GoOutRoot;
import com.tyb.xd.fastbean.RewardDeliveries;
import com.tyb.xd.fastbean.RewardRoot;
import com.tyb.xd.interfacelistener.ServiecePoolDataLoadListener;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.service.LoadDataRunnable;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.FileUtil;
import com.tyb.xd.utils.Util;
import com.tyb.xd.view.refreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

@ContentView(R.layout.fg_hall)
public class FgHall extends Fragment implements ServiecePoolDataLoadListener, refreshListView.IRefreshListener {
    @ViewInject(R.id.id_top_tv_content)
    private TextView mtvTopContent;
    @ViewInject(R.id.id_top_right_iv_img)
    private ImageView mivTopRight;
    @ViewInject(R.id.id_fg_hall_tv_placematch)
    private TextView mtvPlaceMatch;
    @ViewInject(R.id.id_fg_hall_tv_nearby)
    private TextView mtvNearby;
    @ViewInject(R.id.id_fg_hall_tv_smilepoint)
    private TextView mtvSmilepoint;
    @ViewInject(R.id.id_fg_hall_lv_reward)
    private refreshListView mlvInfo;
    @ViewInject(R.id.id_top_iv_down)
    private ImageView mivTopDown;
    private Context mContext;
    private List<GoOutBean> mlGoOut = new ArrayList<GoOutBean>();
    private List<RewardBean> mlReward = new ArrayList<RewardBean>();

    //刷新的类型
    public final static int REFRESH_UP = 0x112;
    public final static int REFRESH_DOWN = 0x113;
    public final static int REFRESH_HIDE_TOP = 0x114;
    public final static int REFRESH_HIDE_DOWN = 0x115;

    //内容的类型
    public final static int CONTENT_REWARD = 0x116;
    public final static int CONTENT_GOOUT = 0x117;
    public int miContent = 0x116;

    //适配器
    private CommonAdapter<RewardBean> mAdapterReward;
    private CommonAdapter<GoOutBean> mAdapterGoOut;

    private final int REFRESH_REWARD = 0x110;
    private final int REFRESH_GOOUT = 0x111;

    private static int miCurrPageReward = 1;
    private static int miCurrPageGoOut = 1;

    public  static boolean isKeyWord = false;
    public static String msKeyWord = "";
    public static boolean isFromAmap=false ;

    private static String SMILEPOINTASC_WEIGHTASC = "0101";
    private static String SMILEPOINTASC_WEIGHTDESC = "0110";
    private static String SMILEPOINTDESC_WEIGHTASC = "1001";
    private static String SMILEPOINTDESC_WEIGHTDESC = "1010";
    private static String SMILEPOINTDESC_WEIGHTNULL = "1000";
    private static String SMILEPOINTNULL_WEIGHTNULL = "0000";
    private static String SMILEPOINTNULL_WEIGHTDES = "0010";
    private static String SMILEPOINTNULL_WEIGHTAESC = "0001";

    private static String mSort = SMILEPOINTNULL_WEIGHTNULL;


    private PopupWindow mPopSearch;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_REWARD:
                    mAdapterReward.notifyDataSetChanged();
                    break;
                case REFRESH_GOOUT:
                    mAdapterGoOut.notifyDataSetChanged();
                    break;
                case REFRESH_HIDE_TOP:
                    mlvInfo.refreshCompleteOnHeader();
                    switch (miContent) {
                        case CONTENT_REWARD:
                            mlvInfo.setAdapter(mAdapterReward);
                            break;
                        case CONTENT_GOOUT:
                            mlvInfo.setAdapter(mAdapterGoOut);
                            break;
                    }
                    break;
                case REFRESH_HIDE_DOWN:
                    mlvInfo.refreshCompleteOnFooter();
                    break;
            }
        }
    };

    public FgHall() {
    }

    // TODO: Rename and change types and number of parameters
    public static FgHall newInstance() {
        FgHall fragment = new FgHall();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        initView();
        initData();
        loadData();
        return view;
    }


    private void loadData() {
        loadRewardData();
        loadGoOutData();
    }

    /**
     * 初次的加载出行数据
     */
    private void loadGoOutData() {
        loadTop(mSort, CONTENT_GOOUT);
    }

    /**
     * 初次的加载网络数据
     */
    private void loadRewardData() {
        loadTop(mSort, CONTENT_REWARD);
    }

    /**
     * 显示本地缓存的数据
     */
    private void initData() {
        mlvInfo.setInterface(this);
        mAdapterReward = new CommonAdapter<RewardBean>(mContext, mlReward, R.layout.reward_item) {
            @Override
            public void convert(ViewHolder helper, RewardBean item) {
                //设置图片
                ImageView img = helper.getView(R.id.id_reward_iv_img);
                RewardBean.setImg(item.getImgUrl(), img, item.getType());
                //设置文字
                helper.setText(R.id.id_reward_tv_name, item.getType());
                //设置轻重
                helper.setText(R.id.id_reward_tv_weight, item.getWeight());
                //设置开始地点
                helper.setText(R.id.id_reward_tv_start_place, item.getStartPlace().substring(item.getStartPlace()
                        .indexOf("校区") + 2));
                //设置结束地点
                helper.setText(R.id.id_reward_tv_end_place, item.getEndPlace().substring(item.getEndPlace().indexOf("校区") + 2));
                //设置截止时间
                helper.setText(R.id.id_reward_tv_limit_time, item.getLimitTime());
                //设置发布时间
                helper.setText(R.id.id_reward_tv_time, item.getPublicTime().substring(6));
                //设置悬赏笑点
                helper.setText(R.id.id_reward_tv_credit, item.getReward());
            }
        };
        mAdapterGoOut = new CommonAdapter<GoOutBean>(mContext, mlGoOut, R.layout.reward_item) {
            @Override
            public void convert(ViewHolder helper, GoOutBean item) {
                //设置图片
                ImageView img = helper.getView(R.id.id_reward_iv_img);
                GoOutBean.setImg(img, item.getHeadimgUrl());
                //设置文字
                helper.setText(R.id.id_reward_tv_name, item.getUserName());
                //隐藏重量的textview
                TextView textView = helper.getView(R.id.id_reward_tv_weight);
                textView.setVisibility(View.INVISIBLE);
                //设置开始地点
                helper.setText(R.id.id_reward_tv_start_place, item.getStartPlace().substring(item.getStartPlace()
                        .indexOf("校区") + 2));
                //设置结束地点
                helper.setText(R.id.id_reward_tv_end_place, item.getEndPlace().substring(item.getEndPlace().indexOf("校区") + 2));
                //设置截止时间
                helper.setText(R.id.id_reward_tv_limit_time, item.getLimitTime());
                //设置发布时间
                helper.setText(R.id.id_reward_tv_time, item.getPublicTime().substring(6));
                //设置悬赏笑点
                helper.setText(R.id.id_reward_tv_credit, item.getReward());
            }
        };
        mlvInfo.setAdapter(mAdapterReward);
        mlvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (miContent) {
                    case CONTENT_REWARD:
                        RewardBean rewardBean = mlReward.get(position - 1);
                        Intent intent = new Intent(mContext, RewardDetail.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", rewardBean.getId());
                        bundle.putString("img", rewardBean.getImgUrl());
                        bundle.putString("type", rewardBean.getType());
                        bundle.putString("weight", rewardBean.getWeight());
                        bundle.putString("reward", rewardBean.getReward() + "");
                        bundle.putString("startplace", rewardBean.getStartPlace());
                        bundle.putString("endplace", rewardBean.getEndPlace());
                        bundle.putString("limit_time", rewardBean.getLimitTime());
                        bundle.putInt("contenttype", miContent);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case CONTENT_GOOUT:
                        GoOutBean goOutBean = mAdapterGoOut.getItem(position - 1);
                        Intent intent_go_out = new Intent(mContext, GoOutDetail.class);
                        Bundle bundle_go_out = new Bundle();
                        bundle_go_out.putSerializable("goout", goOutBean);
                        intent_go_out.putExtras(bundle_go_out);
                        startActivity(intent_go_out);
                        break;
                }
            }
        });
        initRewardData();
        initGoOutData();
    }

    /**
     * 显示本地缓存的悬赏的数据
     */
    private void initRewardData() {
    }

    private void initGoOutData() {

    }

    public void initView() {
        mContext = getActivity();
        mtvTopContent.setText(getResources().getString(R.string.reward_hall));
        mivTopRight.setImageResource(R.drawable.hall_add);
        mivTopDown.setImageResource(R.drawable.hall_down_green);
        mivTopDown.setBackgroundResource(R.drawable.circle_white);
        mivTopDown.setVisibility(View.VISIBLE);
    }

    @Event(value = {R.id.id_top_right_iv_img, R.id.id_fg_hall_tv_placematch, R.id.id_fg_hall_tv_nearby, R.id.id_fg_hall_tv_smilepoint, R.id.id_top_iv_down}, type = View.OnClickListener.class)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_right_iv_img:
                toNext(AddActivity.class);
                mivTopRight.setVisibility(View.INVISIBLE);
                break;
            case R.id.id_fg_hall_tv_placematch:
//                mtvPlaceMatch.setTextColor(Color.parseColor("#00dec9"));
                showPopSearch();
                break;
            case R.id.id_fg_hall_tv_nearby:
                Bundle bundle = new Bundle();
                bundle.putInt("type", miContent);
                Intent intent = new Intent(mContext, LBS_amapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.id_fg_hall_tv_smilepoint:
                isKeyWord = false;
                switch (miContent) {
                    case CONTENT_GOOUT:
                        mlGoOut.clear();
                        mAdapterGoOut.notifyDataSetChanged();
                        mSort = SMILEPOINTNULL_WEIGHTNULL;
                        loadTop(mSort, CONTENT_GOOUT);
                        break;
                    case CONTENT_REWARD:
                        mlReward.clear();
                        mAdapterReward.notifyDataSetChanged();
                        mSort = SMILEPOINTDESC_WEIGHTNULL;
                        loadTop(mSort, CONTENT_REWARD);
                        break;
                }
                break;
            case R.id.id_top_iv_down:
                isKeyWord = false;
                String hall_which = mtvTopContent.getText().toString();
                if (hall_which.equals(mContext.getResources().getString(R.string.reward_hall))) {
                    mtvTopContent.setText(mContext.getResources().getString(R.string.go_out_hall));
                    mlvInfo.setAdapter(mAdapterGoOut);
                    miContent = CONTENT_GOOUT;
                } else {
                    mtvTopContent.setText(mContext.getResources().getString(R.string.reward_hall));
                    mlvInfo.setAdapter(mAdapterReward);
                    miContent = CONTENT_REWARD;
                }
                break;
        }
    }

    private void showPopSearch() {
        if (mPopSearch == null) {
            View popSearchView = LayoutInflater.from(mContext).inflate(R.layout.pop_search, null);
            mPopSearch = new PopupWindow(popSearchView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, true);
            initPopSearch(popSearchView);
        }
        mPopSearch.showAsDropDown(mtvPlaceMatch, Util.dpToPx(getResources(), 30), 0);
    }

    private void initPopSearch(View popSearchView) {
        final EditText etKeyWordSearch = (EditText) popSearchView.findViewById(R.id.id_pop_search_et_search);
        Button btnMattch = (Button) popSearchView.findViewById(R.id.id_pop_btn_search);
        ImageView ivClose = (ImageView) popSearchView.findViewById(R.id.id_pop_search_iv_close);
        btnMattch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sKeyword = etKeyWordSearch.getText().toString() + "";
                if (sKeyword.equals("")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.validator_empty), Toast.LENGTH_SHORT).show();
                } else {
                    SearchByPlace(sKeyword);
                    msKeyWord = sKeyword;
                    isKeyWord = true;
                    mPopSearch.dismiss();
                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopSearch.dismiss();
            }
        });
        mPopSearch.setOutsideTouchable(true);
        mPopSearch.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 根据输入的出发地，目的地的信息就行搜索
     */
    private void SearchByPlace(final String skeyword) {
        BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, REFRESH_UP, miContent) {
            @Override
            public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                String url = XDApplication.dbUrl + "/delivery/outing/search";
                switch (miContent) {
                    case CONTENT_REWARD:
                        url = XDApplication.dbUrl + "/delivery/task/search";
                        break;
                    case CONTENT_GOOUT:
                        url = XDApplication.dbUrl + "/delivery/outing/search";
                        break;
                }
                RequestParams requestParams = new RequestParams(url);
                requestParams.addParameter("school", XDApplication.getmUser().getmSchool().equals("") ?
                        "重庆大学" : XDApplication.getmUser().getmSchool());
                requestParams.addParameter("keyword", skeyword);
                requestParams.addParameter("page_num", "1");
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            switch (miContent) {
                                case CONTENT_REWARD:
                                    RewardRoot rewardRoot = JSON.parseObject(result, RewardRoot.class);
                                    for (RewardDeliveries item : rewardRoot.getDeliveries()) {
                                        RewardBean rewardBean = RewardBean.getInstance(mContext, item, 1);
                                        mlistData.add(rewardBean);
                                        miCurrPageReward = 2;
                                    }
                                    break;
                                case CONTENT_GOOUT:
                                    GoOutRoot goOutRoot = JSON.parseObject(result, GoOutRoot.class);
                                    for (GoOutDeliveries item : goOutRoot.getDeliveries()) {
                                        mlistData.add(GoOutBean.getInstance(item, 1));
                                        miCurrPageGoOut = 2;
                                    }
                                    break;
                            }
                        }
                        semaphore.release();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showError(mContext, ex, null, null, null);
                        Errorutils.showXutilError(mContext, ex);
                        semaphore.release();
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

    private void toNext(Class<?> next) {
        Intent intent = new Intent(mContext, next);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mivTopRight.setVisibility(View.VISIBLE);
        if(isFromAmap)
        {
            if(!TextUtils.isEmpty(msKeyWord))
            {
                isKeyWord = true;
                SearchByPlace(msKeyWord);
            }
        }
        isFromAmap = false;
    }

    //根据数据的类型进行判断
    @Override
    public void setData(List<Object> data, int refreshType, int contentType) {
        switch (contentType) {
            case CONTENT_REWARD:
                switch (refreshType) {
                    case REFRESH_DOWN://是上拉刷新
                        for (Object item : data) {
                            RewardBean rewardBean = (RewardBean) item;
                            mlReward.add(rewardBean);
                        }
                        /*****************************************************/
                        //要在主线程进行调用
                        mHandler.sendEmptyMessage(REFRESH_REWARD);
                        break;
                    case REFRESH_UP://是下拉刷新
                        mlReward = new ArrayList<RewardBean>();
                        for (Object item : data) {
                            RewardBean rewardBean = (RewardBean) item;
                            mlReward.add(rewardBean);
                        }
                        mAdapterReward = new CommonAdapter<RewardBean>(mContext, mlReward, R.layout.reward_item) {
                            @Override
                            public void convert(ViewHolder helper, RewardBean item) {
                                //设置图片
                                ImageView img = helper.getView(R.id.id_reward_iv_img);
                                RewardBean.setImg(item.getImgUrl(), img, item.getType());
                                //设置文字
                                helper.setText(R.id.id_reward_tv_name, item.getType());
                                //设置轻重
                                helper.setText(R.id.id_reward_tv_weight, item.getWeight());
                                //设置开始地点
                                helper.setText(R.id.id_reward_tv_start_place, item.getStartPlace().substring(item.getStartPlace()
                                        .indexOf("校区") + 2));
                                //设置结束地点
                                helper.setText(R.id.id_reward_tv_end_place, item.getEndPlace().substring(item.getEndPlace().indexOf("校区") + 2));
                                //设置截止时间
                                helper.setText(R.id.id_reward_tv_limit_time, item.getLimitTime());
                                //设置发布时间
                                helper.setText(R.id.id_reward_tv_time, item.getPublicTime().substring(6));
                                //设置悬赏笑点
                                helper.setText(R.id.id_reward_tv_credit, item.getReward());
                            }
                        };
                        mHandler.sendEmptyMessage(REFRESH_HIDE_TOP);
                        break;
                }
                break;
            case CONTENT_GOOUT:
                switch (refreshType) {
                    case REFRESH_DOWN:
                        for (Object item : data) {
                            GoOutBean goOutBean = (GoOutBean) item;
                            mlGoOut.add(goOutBean);
                        }
                        mHandler.sendEmptyMessage(REFRESH_GOOUT);
                        break;
                    case REFRESH_UP:
                        mlGoOut = new ArrayList<GoOutBean>();
                        for (Object item : data) {
                            GoOutBean goOutBean = (GoOutBean) item;
                            mlGoOut.add(goOutBean);
                        }
                        mAdapterGoOut = new CommonAdapter<GoOutBean>(mContext, mlGoOut, R.layout.reward_item) {
                            @Override
                            public void convert(ViewHolder helper, GoOutBean item) {
                                //设置图片
                                ImageView img = helper.getView(R.id.id_reward_iv_img);
                                GoOutBean.setImg(img, item.getHeadimgUrl());
                                //设置文字
                                helper.setText(R.id.id_reward_tv_name, item.getUserName());
                                //隐藏重量的textview
                                TextView textView = helper.getView(R.id.id_reward_tv_weight);
                                textView.setVisibility(View.INVISIBLE);
                                //设置开始地点
                                helper.setText(R.id.id_reward_tv_start_place, item.getStartPlace().substring(item.getStartPlace()
                                        .indexOf("校区") + 2));
                                //设置结束地点
                                helper.setText(R.id.id_reward_tv_end_place, item.getEndPlace().substring(item.getEndPlace().indexOf("校区") + 2));
                                //设置截止时间
                                helper.setText(R.id.id_reward_tv_limit_time, item.getLimitTime());
                                //设置发布时间
                                helper.setText(R.id.id_reward_tv_time, item.getPublicTime().substring(6));
                                //设置悬赏笑点
                                helper.setText(R.id.id_reward_tv_credit, item.getReward());
                            }
                        };
                        mHandler.sendEmptyMessage(REFRESH_HIDE_TOP);
                        break;
                }
                break;
        }
    }

    /**
     * 首先判断是什么内容的类型先
     */
    @Override
    public void onRefreshOnHeader() {
        isKeyWord = false;
        loadTopRefresh(miContent);
    }

    /**
     * 首先判断是什么内容
     */
    @Override
    public void onRefreshOnFooter() {
        loadDownRefresh(miContent);
    }

    private void loadTop(final String sorttype, final int contentType) {
        BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, REFRESH_UP, contentType) {
            @Override
            public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                String url = XDApplication.dbUrl + "/delivery/outing";
                switch (contentType) {
                    case CONTENT_REWARD:
                        url = XDApplication.dbUrl + "/delivery/task";
                        break;
                    case CONTENT_GOOUT:
                        url = XDApplication.dbUrl + "/delivery/outing";
                        break;
                }
                RequestParams requestParams = new RequestParams(url);
                requestParams.addParameter("school", XDApplication.getmUser().getmSchool().equals("") ?
                        "重庆大学" : XDApplication.getmUser().getmSchool());
                requestParams.addParameter("sort_type", sorttype);
                requestParams.addParameter("page_num", "1");
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            switch (contentType) {
                                case CONTENT_REWARD:
                                    RewardRoot rewardRoot = JSON.parseObject(result, RewardRoot.class);
                                    for (RewardDeliveries item : rewardRoot.getDeliveries()) {
                                        RewardBean rewardBean = RewardBean.getInstance(mContext, item, 1);
                                        mlistData.add(rewardBean);
                                        miCurrPageReward = 2;
                                    }
                                    break;
                                case CONTENT_GOOUT:
                                    GoOutRoot goOutRoot = JSON.parseObject(result, GoOutRoot.class);
                                    for (GoOutDeliveries item : goOutRoot.getDeliveries()) {
                                        mlistData.add(GoOutBean.getInstance(item, 1));
                                        miCurrPageGoOut = 2;
                                    }
                                    break;
                            }
                        }
                        semaphore.release();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showError(mContext, ex, null, null, null);
                        Errorutils.showXutilError(mContext, ex);
                        semaphore.release();
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

    private void loadTopRefresh(final int contentType) {
        BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, REFRESH_UP, contentType) {
            @Override
            public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                String url = XDApplication.dbUrl + "/delivery/outing";
                switch (contentType) {
                    case CONTENT_REWARD:
                        url = XDApplication.dbUrl + "/delivery/task";
                        break;
                    case CONTENT_GOOUT:
                        url = XDApplication.dbUrl + "/delivery/outing";
                        break;
                }
                RequestParams requestParams = new RequestParams(url);
                requestParams.addParameter("school", XDApplication.getmUser().getmSchool().equals("") ?
                        "重庆大学" : XDApplication.getmUser().getmSchool());
                switch (contentType) {
                    case CONTENT_REWARD:
                        requestParams.addParameter("sort_type", SMILEPOINTNULL_WEIGHTNULL);
                        mSort = SMILEPOINTNULL_WEIGHTNULL;
                        break;
                    case CONTENT_GOOUT:
                        requestParams.addParameter("sort_type", SMILEPOINTNULL_WEIGHTNULL);
                        mSort = SMILEPOINTNULL_WEIGHTNULL;
                        break;
                }
                requestParams.addParameter("page_num", "1");
                x.http().get(requestParams, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getString("status").equals("success")) {
                            switch (contentType) {
                                case CONTENT_GOOUT:
                                    GoOutRoot goOutRoot = JSON.parseObject(result, GoOutRoot.class);
                                    for (GoOutDeliveries item : goOutRoot.getDeliveries()) {
                                        mlistData.add(GoOutBean.getInstance(item, 1));
                                    }
                                    semaphore.release();
                                    miCurrPageGoOut = 2;
                                    break;
                                case CONTENT_REWARD:
                                    RewardRoot rewardRoot = JSON.parseObject(result, RewardRoot.class);
                                    for (RewardDeliveries item : rewardRoot.getDeliveries()) {
                                        RewardBean rewardBean = RewardBean.getInstance(mContext, item, 1);
                                        mlistData.add(rewardBean);
                                    }
                                    semaphore.release();
                                    miCurrPageReward = 2;
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Errorutils.showError(mContext, ex, null, null, null);
                        Errorutils.showXutilError(mContext, ex);
                        semaphore.release();
                        mHandler.sendEmptyMessage(REFRESH_HIDE_TOP);
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


    private void loadDownRefresh(final int contentType) {
        BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, REFRESH_DOWN, contentType) {
            @Override
            public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                if (!isKeyWord) {
                    String url = XDApplication.dbUrl + "/delivery/outing";
                    switch (contentType) {
                        case CONTENT_REWARD:
                            url = XDApplication.dbUrl + "/delivery/task";
                            break;
                        case CONTENT_GOOUT:
                            url = XDApplication.dbUrl + "/delivery/outing";
                            break;
                    }
                    RequestParams requestParams = new RequestParams(url);
                    requestParams.addParameter("school", XDApplication.getmUser().getmSchool().equals("") ?
                            "重庆大学" : XDApplication.getmUser().getmSchool());
                    switch (contentType) {
                        case CONTENT_REWARD:
                            requestParams.addParameter("sort_type", mSort);
                            requestParams.addParameter("page_num", miCurrPageReward + "");
                            break;
                        case CONTENT_GOOUT:
                            requestParams.addParameter("sort_type", mSort);
                            requestParams.addParameter("page_num", miCurrPageGoOut + "");
                            break;
                    }
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                switch (contentType) {
                                    case CONTENT_GOOUT:
                                        GoOutRoot goOutRoot = JSON.parseObject(result, GoOutRoot.class);
                                        for (GoOutDeliveries item : goOutRoot.getDeliveries()) {
                                            mlistData.add(GoOutBean.getInstance(item, miCurrPageGoOut));
                                        }
                                        semaphore.release();
                                        miCurrPageGoOut++;
                                        mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
                                        break;
                                    case CONTENT_REWARD:
                                        RewardRoot rewardRoot = JSON.parseObject(result, RewardRoot.class);
                                        for (RewardDeliveries item : rewardRoot.getDeliveries()) {
                                            RewardBean rewardBean = RewardBean.getInstance(mContext, item, miCurrPageReward);
                                            mlistData.add(rewardBean);
                                        }
                                        semaphore.release();
                                        miCurrPageReward++;
                                        mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showError(mContext, ex, null, null, null);
                            Errorutils.showXutilError(mContext, ex);
                            semaphore.release();
                            mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onFinished() {
                        }
                    });
                } else {
                    String url = XDApplication.dbUrl + "/delivery/outing/search";
                    switch (contentType) {
                        case CONTENT_REWARD:
                            url = XDApplication.dbUrl + "/delivery/task/search";
                            break;
                        case CONTENT_GOOUT:
                            url = XDApplication.dbUrl + "/delivery/outing/search";
                            break;
                    }
                    RequestParams requestParams = new RequestParams(url);
                    requestParams.addParameter("school", XDApplication.getmUser().getmSchool().equals("") ?
                            "重庆大学" : XDApplication.getmUser().getmSchool());
                    requestParams.addParameter("keyword", msKeyWord);
                    switch (contentType) {
                        case CONTENT_REWARD:
                            requestParams.addParameter("page_num", miCurrPageReward + "");
                            break;
                        case CONTENT_GOOUT:
                            requestParams.addParameter("page_num", miCurrPageGoOut + "");
                            break;
                    }
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                switch (contentType) {
                                    case CONTENT_GOOUT:
                                        GoOutRoot goOutRoot = JSON.parseObject(result, GoOutRoot.class);
                                        for (GoOutDeliveries item : goOutRoot.getDeliveries()) {
                                            mlistData.add(GoOutBean.getInstance(item, miCurrPageGoOut));
                                        }
                                        semaphore.release();
                                        miCurrPageGoOut++;
                                        mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
                                        break;
                                    case CONTENT_REWARD:
                                        RewardRoot rewardRoot = JSON.parseObject(result, RewardRoot.class);
                                        for (RewardDeliveries item : rewardRoot.getDeliveries()) {
                                            RewardBean rewardBean = RewardBean.getInstance(mContext, item, miCurrPageReward);
                                            mlistData.add(rewardBean);
                                        }
                                        semaphore.release();
                                        miCurrPageReward++;
                                        mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showError(mContext, ex, null, null, null);
                            Errorutils.showXutilError(mContext, ex);
                            semaphore.release();
                            mHandler.sendEmptyMessage(REFRESH_HIDE_DOWN);
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
        });
    }
}
