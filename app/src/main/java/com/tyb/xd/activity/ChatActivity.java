package com.tyb.xd.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.tyb.xd.R;
import com.tyb.xd.adapter.ChatMessageAdapter;
import com.tyb.xd.adapter.FaceAdapter;
import com.tyb.xd.adapter.FacePageAdeapter;
import com.tyb.xd.bean.ChatMessage;
import com.tyb.xd.bean.RewardBean;
import com.tyb.xd.fastbean.GoOutDeliveryDetailWithReceiver;
import com.tyb.xd.fastbean.GoOutDeliveryDetailWithoutReceiver;
import com.tyb.xd.fastbean.GoOutDetailRoot;
import com.tyb.xd.fastbean.GoOutDetailWithReceiveRoot;
import com.tyb.xd.fastbean.RewardDeliveries;
import com.tyb.xd.fastbean.RewardDeliveryDetailWithReceiver;
import com.tyb.xd.fastbean.RewardDeliveryDetailWithReceiverRoot;
import com.tyb.xd.fastbean.RewardDeliveryDetailWithoutReceiver;
import com.tyb.xd.fastbean.RewardDetailRoot;
import com.tyb.xd.fragment.FgCommunicate;
import com.tyb.xd.fragment.FgHall;
import com.tyb.xd.interfacelistener.ServiecePoolDataLoadListener;
import com.tyb.xd.interfacelistener.receiveMessageHandlerForChatTo;
import com.tyb.xd.interfacelistener.receiveMessageHandlerForNoChatTo;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.service.LoadDataRunnable;
import com.tyb.xd.utils.Errorutils;
import com.tyb.xd.utils.FaceUtil;
import com.tyb.xd.utils.NotificationUtil;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.view.ChatListView;
import com.tyb.xd.view.CircleIndicator;
import com.tyb.xd.view.RewordStatuView;
import com.tyb.xd.view.animation.DepthPageTransformer;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.OnClick;

@ContentView(R.layout.ac_chat)
public class ChatActivity extends CheckPermissionsActivity implements ChatListView.IRefreshListener,
        receiveMessageHandlerForChatTo, receiveMessageHandlerForNoChatTo, ServiecePoolDataLoadListener {
    @ViewInject(R.id.id_top_back_iv_img)
    ImageView mivTopBack;
    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_top_rl)
    RelativeLayout mtlTop;
    @ViewInject(R.id.id_ac_chat_rsv_statu)
    RewordStatuView mrsState;
    @ViewInject(R.id.id_ac_chat_iv_pull)
    ImageView mivDetailPullDown;
    @ViewInject(R.id.id_ac_chat_iv_good_img)
    ImageView mivInfoImg;
    @ViewInject(R.id.id_ac_chat_tv_smilepoint)
    TextView mtvInfoSmilePoint;
    @ViewInject(R.id.id_ac_chat_iv_weight)
    TextView mtvInfoWeight;
    @ViewInject(R.id.id_ac_chat_iv_complete)
    Button mbtnComplete;
    @ViewInject(R.id.id_ac_chat_tv_startplace)
    TextView mtvInfoStartPlace;
    @ViewInject(R.id.id_ac_chat_tv_endplace)
    TextView mtvInfoEndPlace;
    @ViewInject(R.id.id_ac_chat_tv_end_time)
    TextView mtvInfoLimitTime;
    @ViewInject(R.id.id_ac_chat_iv_showface)
    ImageView mivShowFace;
    @ViewInject(R.id.id_ac_chat_et_content)
    EditText metSendContent;
    @ViewInject(R.id.id_ac_chat_tv_send)
    Button mbtnSend;
    @ViewInject(R.id.id_ac_chat_iv_more)
    ImageView mivMore;
    @ViewInject(R.id.id_ac_chat_iv_call)
    ImageView mivCall;
    @ViewInject(R.id.id_ac_chat_vp_face)
    ViewPager mvpFace;
    @ViewInject(R.id.id_ac_chat_indicator_face_indicator)
    CircleIndicator mFaceIndicator;
    @ViewInject(R.id.id_ac_chat_iv_phone)
    ImageView mivPhone;
    @ViewInject(R.id.id_ac_chat_iv_takephoto)
    ImageView mivTakePhone;
    @ViewInject(R.id.id_ac_chat_tv_album)
    ImageView mivGallery;
    @ViewInject(R.id.id_ac_chat_clv_msg)
    ChatListView mlvChatMsg;
    @ViewInject(R.id.id_ac_chat_rl_goodinfo)
    RelativeLayout mrlInfo;
    @ViewInject(R.id.id_ac_chat_ll_face_layout)
    LinearLayout mllFace;
    @ViewInject(R.id.id_ac_chat_rl_more)
    RelativeLayout mrlMore;
    //表情list，主要存表情的名字，Key
    private List<String> facelist;
    //保存聊天记录
    private List<ChatMessage> mChatMessageList;
    //聊天记录的adapter
    private ChatMessageAdapter mChatMessageAdapter;
    //当前会话的用户
    private AVIMClient mCurrentUser;
    //当前的对话
    private AVIMConversation mCurrentConversation;
    private CustomMessageHandler mMessageHandler;
    public static int FLAG_NOTIFICATION_INTENT = 0;
    public static int FLAG_CURRENT_INTENT = 1;
    private Context mContext;
    private int miState = 0;
    private final static int DELETE_AUTO = 1;//程序自动删除了
    private final static int PUBLISH = 2;//已经发布了
    private final static int HAVEGET = 3;//已经领取了
    private final static int HAVESEND = 4;//已经发出了
    private final static int ARRIVE = 5;//已经送达了
    private final static int COMPLETE = 6;//已经完成了
    private String mImgUrl = "";
    /**
     * 获取信息
     */
    private RewardDeliveryDetailWithReceiver mRewardDeliveryDetailWithReceiver;
    private RewardDeliveryDetailWithoutReceiver mRewardDeliveryDetailWithoutReceiver;
    private GoOutDeliveryDetailWithoutReceiver mGoOutDeliveryDetailWithoutReceiver;
    private GoOutDeliveryDetailWithReceiver mGoOutDeliveryDetailWithReceiver;
    String msRewardId;
    String msAllId;
    String msContentType;
    private static final int REWARD = 0x110;
    private static final int GOOUT = 0x111;
    private boolean isFromDetail = false;
    private String msToUser = "";
    private String msToUsername = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REWARD:
                    if (miState < 3) {
                        if (mRewardDeliveryDetailWithoutReceiver != null) {
                            //表示发布人是自己
                            if (mRewardDeliveryDetailWithoutReceiver.getPublisher()
                                    .getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和receive
                                 */
                                switch (miState) {
                                    case DELETE_AUTO:
                                        /**
                                         * 程序自动删除了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case PUBLISH:
                                        /**
                                         * 发布了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                }
                                /**
                                 * 执行显示界面信息
                                 * ******************************************************************
                                 */
                                RewardBean.setImg(mRewardDeliveryDetailWithoutReceiver.getThing().getThumbnail(),
                                        mivInfoImg, mRewardDeliveryDetailWithoutReceiver.getThing().getType());
                                mtvInfoStartPlace.setText(mRewardDeliveryDetailWithoutReceiver.getSource());
                                mtvInfoEndPlace.setText(mRewardDeliveryDetailWithoutReceiver.getDestination());
                                mtvInfoLimitTime.setText(mRewardDeliveryDetailWithoutReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText(RewardBean.getWeightByIndex(mContext, mRewardDeliveryDetailWithoutReceiver.getThing().getWeight()));
                                mtvInfoSmilePoint.setText(mRewardDeliveryDetailWithoutReceiver.getReward() + "");
                            } else {//表明自己是笑递员
                                switch (miState) {
                                    case DELETE_AUTO:
                                        /**
                                         * 程序自动删除了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case PUBLISH:
                                        /**
                                         * 发布了
                                         */
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        break;
                                }
                                /**
                                 * 将信息显示在界面上
                                 */
                                RewardBean.setImg(mRewardDeliveryDetailWithoutReceiver.getThing().getThumbnail(),
                                        mivInfoImg, mRewardDeliveryDetailWithoutReceiver.getThing().getType());
                                mtvInfoStartPlace.setText(mRewardDeliveryDetailWithoutReceiver.getSource());
                                mtvInfoEndPlace.setText(mRewardDeliveryDetailWithoutReceiver.getDestination());
                                mtvInfoLimitTime.setText(mRewardDeliveryDetailWithoutReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText(RewardBean.getWeightByIndex(mContext, mRewardDeliveryDetailWithoutReceiver.getThing().getWeight()));
                                mtvInfoSmilePoint.setText(mRewardDeliveryDetailWithoutReceiver.getReward() + "");
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和publisher
                                 */
                            }
                        }
                    } else {//表示state>=3,也就是物品已经领取了
                        if (mRewardDeliveryDetailWithReceiver != null) {
                            //表示发布人是自己
                            if (mRewardDeliveryDetailWithReceiver.getPublisher().getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和receive
                                 */
                                switch (miState) {
                                    case HAVEGET:
                                        /**
                                         * 领取之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                        break;
                                    case HAVESEND:
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                        break;
                                    case ARRIVE:
                                        /**
                                         * 已经送达之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                    case COMPLETE:
                                        mrsState.setmStatu(RewordStatuView.IFINISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                }

                                /**
                                 * 显示信息
                                 */
                                mtvTopContent.setText(mRewardDeliveryDetailWithReceiver.getReceiver().getNickname());
                                RewardBean.setImg(mRewardDeliveryDetailWithReceiver.getThing().getThumbnail(),
                                        mivInfoImg, mRewardDeliveryDetailWithReceiver.getThing().getType());
                                mtvInfoStartPlace.setText(mRewardDeliveryDetailWithReceiver.getSource());
                                mtvInfoEndPlace.setText(mRewardDeliveryDetailWithReceiver.getDestination());
                                mtvInfoLimitTime.setText(mRewardDeliveryDetailWithReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText(RewardBean.getWeightByIndex(mContext, mRewardDeliveryDetailWithReceiver.getThing().getWeight()));
                                mtvInfoSmilePoint.setText(mRewardDeliveryDetailWithReceiver.getReward() + "");
                            } else {//表明自己是笑递员
                                switch (miState) {
                                    case HAVEGET:
                                        /**
                                         * 领取之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                        break;
                                    case HAVESEND:
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                        break;
                                    case ARRIVE:
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                    case COMPLETE:
                                        mrsState.setmStatu(RewordStatuView.IFINISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                }
                                /**
                                 * 显示界面信息
                                 */
                                mtvTopContent.setText(mRewardDeliveryDetailWithReceiver.getPublisher().getNickname());
                                RewardBean.setImg(mRewardDeliveryDetailWithReceiver.getThing().getThumbnail(),
                                        mivInfoImg, mRewardDeliveryDetailWithReceiver.getThing().getType());
                                mtvInfoStartPlace.setText(mRewardDeliveryDetailWithReceiver.getSource());
                                mtvInfoEndPlace.setText(mRewardDeliveryDetailWithReceiver.getDestination());
                                mtvInfoLimitTime.setText(mRewardDeliveryDetailWithReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText(RewardBean.getWeightByIndex(mContext, mRewardDeliveryDetailWithReceiver.getThing().getWeight()));
                                mtvInfoSmilePoint.setText(mRewardDeliveryDetailWithReceiver.getReward() + "");
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和publisher
                                 */
                            }
                        }
                    }
                    break;
                case GOOUT:
                    if (miState < 3) {
                        if (mGoOutDeliveryDetailWithoutReceiver != null) {
                            //表示发布人是自己
                            if (mGoOutDeliveryDetailWithoutReceiver.getPublisher()
                                    .getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和receive
                                 */
                                switch (miState) {
                                    case DELETE_AUTO:
                                        /**
                                         * 程序自动删除了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case PUBLISH:
                                        /**
                                         * 发布了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                }
                                /**
                                 * 执行显示界面信息
                                 * ******************************************************************
                                 */
                                ImageOptions options = new ImageOptions.Builder()
                                        .setLoadingDrawableId(R.drawable.good_type_express)
                                        .setFailureDrawableId(R.drawable.good_type_express)
                                        .setCircular(true)
                                        .build();
                                x.image().bind(mivInfoImg, mGoOutDeliveryDetailWithoutReceiver.getPublisher().getHeadimg(), options);
                                mtvInfoStartPlace.setText(mGoOutDeliveryDetailWithoutReceiver.getSource());
                                mtvInfoEndPlace.setText(mGoOutDeliveryDetailWithoutReceiver.getDestination());
                                mtvInfoLimitTime.setText(mGoOutDeliveryDetailWithoutReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText("无");
                                mtvInfoSmilePoint.setText(mGoOutDeliveryDetailWithoutReceiver.getReward() + "");
                            } else {//表明自己是笑递员
                                switch (miState) {
                                    case DELETE_AUTO:
                                        /**
                                         * 程序自动删除了
                                         */
                                        mrsState.setmStatu(RewordStatuView.IPUBLISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case PUBLISH:
                                        /**
                                         * 发布了
                                         */
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        break;
                                }
                                /**
                                 * 将信息显示在界面上
                                 */
                                ImageOptions options = new ImageOptions.Builder()
                                        .setLoadingDrawableId(R.drawable.good_type_express)
                                        .setFailureDrawableId(R.drawable.good_type_express)
                                        .setCircular(true)
                                        .build();
                                x.image().bind(mivInfoImg, mGoOutDeliveryDetailWithoutReceiver.getPublisher().getHeadimg(), options);
                                mtvInfoStartPlace.setText(mGoOutDeliveryDetailWithoutReceiver.getSource());
                                mtvInfoEndPlace.setText(mGoOutDeliveryDetailWithoutReceiver.getDestination());
                                mtvInfoLimitTime.setText(mGoOutDeliveryDetailWithoutReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText("无");
                                mtvInfoSmilePoint.setText(mGoOutDeliveryDetailWithoutReceiver.getReward() + "");
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和publisher
                                 */
                            }
                        }
                    } else {//表示state>=3,也就是物品已经领取了
                        if (mGoOutDeliveryDetailWithReceiver != null) {
                            //表示发布人是自己
                            if (mGoOutDeliveryDetailWithReceiver.getPublisher().getNickname().equals(XDApplication.getmUser().getmUsername())) {
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和receive
                                 */
                                switch (miState) {
                                    case HAVEGET:
                                        /**
                                         * 领取之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case HAVESEND:
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                        break;
                                    case ARRIVE:
                                        /**
                                         * 已经送达之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                    case COMPLETE:
                                        mrsState.setmStatu(RewordStatuView.IFINISH);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                }

                                /**
                                 * 显示信息
                                 */
                                ImageOptions options = new ImageOptions.Builder()
                                        .setLoadingDrawableId(R.drawable.good_type_express)
                                        .setFailureDrawableId(R.drawable.good_type_express)
                                        .setCircular(true)
                                        .build();
                                x.image().bind(mivInfoImg, mGoOutDeliveryDetailWithReceiver.getPublisher().getHeadimg(), options);
                                mtvInfoStartPlace.setText(mGoOutDeliveryDetailWithReceiver.getSource());
                                mtvInfoEndPlace.setText(mGoOutDeliveryDetailWithReceiver.getDestination());
                                mtvInfoLimitTime.setText(mGoOutDeliveryDetailWithReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText("无");
                                mtvInfoSmilePoint.setText(mGoOutDeliveryDetailWithReceiver.getReward() + "");
                            } else {//表明自己是笑递员
                                switch (miState) {

                                    case HAVEGET:
                                        /**
                                         * 领取之后
                                         */
                                        mrsState.setmStatu(RewordStatuView.IGET);

                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                        break;
                                    case HAVESEND:
                                        mrsState.setmStatu(RewordStatuView.IGET);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        break;
                                    case ARRIVE:
                                        mrsState.setmStatu(RewordStatuView.ICOMPLETE);
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                    case COMPLETE:
                                        mrsState.setmStatu(RewordStatuView.IFINISH);
                                        mbtnComplete.setBackgroundResource(R.drawable.complete_circle_bg_pressed);
                                        mbtnComplete.setClickable(false);//设置不能点击
                                        mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                        break;
                                }
                                /**
                                 * 显示界面信息
                                 */
                                ImageOptions options = new ImageOptions.Builder()
                                        .setLoadingDrawableId(R.drawable.good_type_express)
                                        .setFailureDrawableId(R.drawable.good_type_express)
                                        .setCircular(true)
                                        .build();
                                x.image().bind(mivInfoImg, mGoOutDeliveryDetailWithReceiver.getPublisher().getHeadimg(), options);
                                mtvInfoStartPlace.setText(mGoOutDeliveryDetailWithReceiver.getSource());
                                mtvInfoEndPlace.setText(mGoOutDeliveryDetailWithReceiver.getDestination());
                                mtvInfoLimitTime.setText(mGoOutDeliveryDetailWithReceiver.getDeadline().substring(5));
                                mtvInfoWeight.setText("无");
                                mtvInfoSmilePoint.setText(mGoOutDeliveryDetailWithReceiver.getReward() + "");
                                /**
                                 * 用自己的username创建对话
                                 * 和谁通话就是和publisher
                                 */
                            }
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        x.view().inject(this);
        mContext = ChatActivity.this;
        initFace();
        initData();
        initEvent();
        getState();
    }


    /**
     * Activity重新获取焦点的时候调用
     * 注册MessageHandler
     */
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 取消所有消息的状态栏通知
         */
        NotificationUtil.getNotificationManager(this).cancelAll();
        mMessageHandler = new CustomMessageHandler(this, XDApplication.getmUser().getmUsername(), msToUsername, this, this);
        //注册消息处理逻辑
        AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, mMessageHandler);
    }

    /**
     * Activity失去焦点的时候调用
     * 注销MessageHandler
     */
    @Override
    protected void onPause() {
        super.onPause();
        AVIMMessageManager.unregisterMessageHandler(AVIMTextMessage.class, mMessageHandler);
        /**
         * 让垃圾回收站清理
         */
        mMessageHandler = null;
    }


    private void initEvent() {
        metSendContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    //    mivMore.setVisibility(View.GONE);
                    mivCall.setVisibility(View.GONE);
                    mbtnSend.setVisibility(View.VISIBLE);
                } else {
                    //   mivMore.setVisibility(View.VISIBLE);
                    mivCall.setVisibility(View.VISIBLE);
                    mbtnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        metSendContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        metSendContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mllFace.getVisibility() == View.VISIBLE) {
                    mllFace.setVisibility(View.GONE);
                }
                if (mrlMore.getVisibility() == View.VISIBLE) {
                    mrlMore.setVisibility(View.GONE);
                }
            }
        });
        mbtnComplete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (msContentType.equals(FgCommunicate.REWARD)) {
                    if (mRewardDeliveryDetailWithReceiver != null) {
                        switch (mRewardDeliveryDetailWithReceiver.getState()) {
                            case HAVEGET:
                                String urlSend = XDApplication.dbUrl + "/delivery/task/" + msRewardId + "/sending";
                                RequestParams requestParams_send = new RequestParams(urlSend);
                                requestParams_send.addParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_send.addParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().request(HttpMethod.PUT, requestParams_send, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_send = JSON.parseObject(result);
                                        if (jsonObject_send.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_send), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(HAVESEND);
                                            miState = HAVESEND;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_send));
                                            mHandler.sendEmptyMessage(REWARD);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {
                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case HAVESEND:
                                String url_arrive = XDApplication.dbUrl + "/delivery/task/" + msRewardId + "/arrival";
                                RequestParams requestParams_arrive = new RequestParams(url_arrive);
                                requestParams_arrive.addBodyParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_arrive.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().post(requestParams_arrive, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_arrive = JSON.parseObject(result);
                                        if (jsonObject_arrive.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_arrive), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(ARRIVE);
                                            miState = ARRIVE;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_arrive));
                                            mHandler.sendEmptyMessage(REWARD);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case ARRIVE:
                                String url_over = XDApplication.dbUrl + "/delivery/task/" + msRewardId + "/over";
                                RequestParams requestParams_over = new RequestParams(url_over);
                                requestParams_over.addBodyParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_over.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().post(requestParams_over, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_arrive = JSON.parseObject(result);
                                        if (jsonObject_arrive.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_complete), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(COMPLETE);
                                            miState = COMPLETE;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_complete));
                                            mHandler.sendEmptyMessage(REWARD);
                                            /**
                                             * 应该直接跳转到评分界面
                                             */
                                            toGrade();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case COMPLETE:
                                break;
                        }
                    }

                } else {
                    if (mGoOutDeliveryDetailWithReceiver != null) {
                        switch (mGoOutDeliveryDetailWithReceiver.getState()) {
                            case HAVEGET:
                                String urlSend = XDApplication.dbUrl + "/delivery/outing/" + msRewardId + "/sending";
                                RequestParams requestParams_send = new RequestParams(urlSend);
                                requestParams_send.addParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_send.addParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().request(HttpMethod.PUT, requestParams_send, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_send = JSON.parseObject(result);
                                        if (jsonObject_send.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_send), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(HAVESEND);
                                            miState = HAVESEND;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_send));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_send));
                                            mHandler.sendEmptyMessage(GOOUT);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {
                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case HAVESEND:
                                String url_arrive = XDApplication.dbUrl + "/delivery/outing/" + msRewardId + "/arrival";
                                RequestParams requestParams_arrive = new RequestParams(url_arrive);
                                requestParams_arrive.addBodyParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_arrive.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().post(requestParams_arrive, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_arrive = JSON.parseObject(result);
                                        if (jsonObject_arrive.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_arrive), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(ARRIVE);
                                            miState = ARRIVE;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_arrive));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_arrive));
                                            mHandler.sendEmptyMessage(GOOUT);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case ARRIVE:
                                String url_over = XDApplication.dbUrl + "/delivery/outing/" + msRewardId + "/over";
                                RequestParams requestParams_over = new RequestParams(url_over);
                                requestParams_over.addBodyParameter("token", XDApplication.getmUser().getmToken());
                                requestParams_over.addBodyParameter("username", XDApplication.getmUser().getmUsername());
                                x.http().post(requestParams_over, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JSONObject jsonObject_arrive = JSON.parseObject(result);
                                        if (jsonObject_arrive.getString("status").equals("success")) {
                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_complete), Toast.LENGTH_SHORT).show();
                                            mRewardDeliveryDetailWithReceiver.setState(COMPLETE);
                                            miState = COMPLETE;
                                            mbtnComplete.setText(mContext.getResources().getString(R.string.reward_complete));
                                            sendMessageTo(mContext.getResources().getString(R.string.have_complete));
                                            mHandler.sendEmptyMessage(GOOUT);
                                            /**
                                             * 应该直接跳转到评分界面
                                             */
                                            toGrade();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                        Errorutils.showXutilError(mContext, ex);
                                        Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                                break;
                            case COMPLETE:
                                break;
                        }
                    }

                }
                return true;
            }
        });
    }

    private void toGrade() {
        Intent intent = new Intent(mContext, EvaluateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("toUser", msToUser);
        if (msContentType.equals(FgCommunicate.REWARD)) {
            bundle.putString("reward", mRewardDeliveryDetailWithReceiver.getReward() + "");
        } else {
            bundle.putString("reward", mGoOutDeliveryDetailWithReceiver.getReward() + "");
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadData() {
        if (msContentType.equals(FgCommunicate.REWARD)) {
            BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, 0, FgHall.CONTENT_REWARD) {
                @Override
                public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                    String url = XDApplication.dbUrl + "/delivery/task/" + msRewardId;
                    RequestParams requestParams = new RequestParams(url);
                    requestParams.addParameter("token", XDApplication.getmUser().getmToken());
                    requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                if (miState < 3) {
                                    RewardDetailRoot root = JSON.parseObject(result, RewardDetailRoot.class);
                                    mlistData.add(root.getDelivery());
                                } else {
                                    RewardDeliveryDetailWithReceiverRoot rewardDeliveryDetailWithReceiverRoot = JSON.parseObject(result, RewardDeliveryDetailWithReceiverRoot.class);
                                    mlistData.add(rewardDeliveryDetailWithReceiverRoot.getDelivery());
                                }
                            }
                            semaphore.release();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showXutilError(mContext, ex);
                            Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
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
        } else {//表示是加载出行的信息
            BgServicePool.getInstance().addRunnable(new LoadDataRunnable(this, 0, FgHall.CONTENT_GOOUT) {
                @Override
                public void loadData(final List<Object> mlistData, final Semaphore semaphore) {
                    String url = XDApplication.dbUrl + "/delivery/outing/" + msRewardId;
                    RequestParams requestParams = new RequestParams(url);
                    requestParams.addParameter("token", XDApplication.getmUser().getmToken());
                    requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                if (miState < 3) {
                                    GoOutDetailRoot root = JSON.parseObject(result, GoOutDetailRoot.class);
                                    mlistData.add(root.getDelivery());
                                } else {
                                    GoOutDetailWithReceiveRoot goOutDetailWithReceiveRoot = JSON.parseObject(result, GoOutDetailWithReceiveRoot.class);
                                    mlistData.add(goOutDetailWithReceiveRoot.getDelivery());
                                }
                            }
                            semaphore.release();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showXutilError(mContext, ex);
                            Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
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
    }

    private void initData() {
        mrlInfo.setVisibility(View.GONE);
        closeInputMothod();
        mChatMessageList = new ArrayList<ChatMessage>();
        mChatMessageAdapter = new ChatMessageAdapter(mChatMessageList, this);
        mlvChatMsg.setAdapter(mChatMessageAdapter);
        mlvChatMsg.setInterface(this);
        /**
         * 获取这次聊天的id
         */
        msAllId = getIntent().getExtras().getString("id");
        msToUser = getIntent().getExtras().getString("touser");
        msToUsername = getIntent().getExtras().getString("username");
        if (getIntent().getExtras().getString("from").equals(FgCommunicate.ADDMSG)) {
            isFromDetail = true;
        }
        mtvTopContent.setText("");
        String url = null;
        try {
            url = XDApplication.dbUrl + "/user/person/" + URLEncoder.encode(msToUsername, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("token", XDApplication.getmUser().getmToken());
        requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getString("status").equals("success")) {
                    /**
                     * 更新用户的名字
                     */
                    String strName = jsonObject.getJSONObject("user").getString("nickname");
                    mtvTopContent.setText(strName);
                    mImgUrl = jsonObject.getJSONObject("user").getString("headimg");
                    mMessageHandler.setmImgUrl(mImgUrl);
                    createConversation(msToUsername);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Errorutils.showXutilError(mContext, ex);
                Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
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
     * 初始化表情
     */
    private void initFace() {
        Set<String> keySet = FaceUtil.getmFaceMap().keySet();
        facelist = new ArrayList<String>();
        facelist.addAll(keySet);
        final List<View> listFace = new ArrayList<View>();
        for (int i = 0; i < 4; i++) {
            GridView gv = new GridView(this);
            gv.setNumColumns(7);
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
            gv.setBackgroundColor(Color.TRANSPARENT);
            gv.setCacheColorHint(Color.TRANSPARENT);
            gv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            gv.setGravity(Gravity.NO_GRAVITY);
            gv.setPadding(30, 0, 30, 0);
            gv.setHorizontalSpacing(30);
            gv.setVerticalSpacing(10);
            gv.setVerticalScrollBarEnabled(false);
            gv.setAdapter(new FaceAdapter(this, i));
            gv.setOnItemClickListener(new faceOnClickListener(i));
            listFace.add(gv);
        }
        new FacePageAdeapter(listFace, mvpFace);
        //设置动画
        mvpFace.setPageTransformer(true, new DepthPageTransformer());
        /**
         * 设置指示器
         */
        mFaceIndicator.setViewPager(mvpFace);
    }

    /*******************************************/
    @Event(value = {R.id.id_top_back_iv_img, R.id.id_ac_chat_iv_pull,
            R.id.id_ac_chat_iv_showface, R.id.id_ac_chat_tv_send, R.id.id_ac_chat_iv_more, R.id.id_ac_chat_iv_phone,
            R.id.id_ac_chat_iv_takephoto, R.id.id_ac_chat_tv_album, R.id.id_ac_chat_iv_call})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_top_back_iv_img:
                finish();
                break;
            case R.id.id_ac_chat_iv_pull:
                if (mrlInfo.getVisibility() == View.VISIBLE) {
                    RotateAnimation rotateAnimation = new RotateAnimation(180, 360,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(300);
                    rotateAnimation.setFillAfter(true);
                    mivDetailPullDown.clearAnimation();
                    mivDetailPullDown.setAnimation(rotateAnimation);
                    rotateAnimation.startNow();
                    mrlInfo.setVisibility(View.GONE);
                } else {
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF,
                            0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(300);
                    rotateAnimation.setFillAfter(true);
                    mivDetailPullDown.clearAnimation();
                    mivDetailPullDown.setAnimation(rotateAnimation);
                    rotateAnimation.startNow();
                    mrlInfo.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.id_ac_chat_iv_showface:
                /**隐藏软键盘**/
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(metSendContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mrlMore.setVisibility(View.GONE);
                /**
                 * 使用下面的方法，主要是为了解决在
                 * 有软输入时，在点击表情，此时软输入还没有隐藏，
                 * 但是表情已经显示了，在软输入的上面，此时就显得很难看。
                 */
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mllFace.getVisibility() == View.VISIBLE) {
                            mllFace.setVisibility(View.GONE);
                        } else {
                            mllFace.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
                break;
            case R.id.id_ac_chat_tv_send:
                /**
                 * 发送消息
                 */
                if (TextUtils.isEmpty(metSendContent.getText().toString()))//the content to send mustn't be empty
                {

                } else {//内容不为空，则可以进行发送
                    //将表情框进行隐藏
                    mllFace.setVisibility(View.GONE);
                    String s = metSendContent.getText().toString();
                    ChatMessage chatMessage = new ChatMessage(FaceUtil.convertNormalStringToSpannableString(s, this),
                            ChatMessage.SEND, TimeUtils.getTime(), mImgUrl);
                    mChatMessageList.add(chatMessage);
                    mChatMessageAdapter.notifyDataSetChanged();
                    mlvChatMsg.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                    metSendContent.setText("");
                    sendMessageTo(s);
                }
                break;
            /**
             * 目前没有作用
             * 因为该加号控件已经隐藏
             */
            case R.id.id_ac_chat_iv_more:
                /**隐藏软键盘**/
                InputMethodManager inputmanger_more = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger_more.hideSoftInputFromWindow(metSendContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mllFace.setVisibility(View.GONE);
                /**
                 * 使用下面的方法，主要是为了解决在
                 * 有软输入时，在点击更多，此时软输入还没有隐藏，
                 * 但是更多已经显示了，在软输入的上面，此时就显得很难看。
                 */
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mrlMore.getVisibility() == View.VISIBLE) {
                            mrlMore.setVisibility(View.GONE);
                        } else {
                            mrlMore.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
                break;
            case R.id.id_ac_chat_iv_call:
                toCallPhone();
                break;
            /**
             * 暂时废弃
             *
             case R.id.id_ac_chat_iv_phone:
             toCallPhone();
             break;
             case R.id.id_ac_chat_iv_takephoto:
             break;
             case R.id.id_ac_chat_tv_album:
             break;
             */
        }
    }

    private void toCallPhone() {
        if (msContentType.equals(FgCommunicate.REWARD))//表示当前的聊天的代送类型为悬赏代送
        {
            if (mRewardDeliveryDetailWithReceiver != null) {

                if (XDApplication.getmUser().getmNickname().equals(mRewardDeliveryDetailWithReceiver.getPublisher().getNickname())) {
                    //表示当前的登录账户是发布者，则打电话给接收者
                    tocall(mRewardDeliveryDetailWithReceiver.getReceiver().getPhone());
                } else {//表示当前登录的账户是接受者，则打电话给发布者
                    tocall(mRewardDeliveryDetailWithReceiver.getPublisher().getPhone());
                }
            }

        } else {//表示当前的聊天的代送类型为出行代送
            if (XDApplication.getmUser().getmNickname().equals(mGoOutDeliveryDetailWithReceiver.getPublisher().getNickname())) {
                //表示当前的登录账户是出行代送的发布者，即笑递员
                tocall(mGoOutDeliveryDetailWithReceiver.getReceiver().getPhone());
            } else {//表示当前的登录账户是出行的接受者,即物主
                tocall(mGoOutDeliveryDetailWithReceiver.getPublisher().getPhone());
            }
        }
    }

    private void tocall(String phone) {
        Intent intent_to_phone = new Intent();
        intent_to_phone.setAction(Intent.ACTION_CALL);
        intent_to_phone.setData(Uri.parse("tel:" + phone));
        startActivity(intent_to_phone);
    }


    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMessageTo(final String msg) {
        mCurrentUser.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    avimClient.createConversation(Arrays.asList(msToUsername), msAllId,
                            null, false, true, new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation avimConversation, AVIMException e) {
                                    if (e == null) {
                                        AVIMTextMessage avimMsg = new AVIMTextMessage();
                                        avimMsg.setText(msg);
                                        // 发送消息
                                        avimConversation.sendMessage(avimMsg, AVIMConversation.NONTRANSIENT_MESSAGE_FLAG, new AVIMConversationCallback() {
                                            @Override
                                            public void done(AVIMException e) {
                                                if (e == null) {
                                                    /**
                                                     * 发送成功执行的相关操作
                                                     */
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void setData(List<Object> data, int refreshType, int contentType) {
        switch (contentType) {
            case FgHall.CONTENT_GOOUT:
                if (miState < 3) {
                    for (Object item : data) {
                        mGoOutDeliveryDetailWithoutReceiver = (GoOutDeliveryDetailWithoutReceiver) item;
                        mHandler.sendEmptyMessage(GOOUT);
                    }
                } else {
                    for (Object item : data) {
                        mGoOutDeliveryDetailWithReceiver = (GoOutDeliveryDetailWithReceiver) item;
                        mHandler.sendEmptyMessage(GOOUT);
                    }
                }

                break;
            case FgHall.CONTENT_REWARD:
                if (miState < 3) {
                    for (Object item : data) {
                        mRewardDeliveryDetailWithoutReceiver = (RewardDeliveryDetailWithoutReceiver) item;
                        mHandler.sendEmptyMessage(REWARD);
                    }
                } else {
                    for (Object item : data) {
                        mRewardDeliveryDetailWithReceiver = (RewardDeliveryDetailWithReceiver) item;
                        mHandler.sendEmptyMessage(REWARD);
                    }
                }
                break;
        }
    }


    /**
     * 表情GridView的点击事件
     */
    private class faceOnClickListener implements AdapterView.OnItemClickListener {

        private int mCurrentPage = 0;

        public faceOnClickListener(int mCurrentPage) {
            this.mCurrentPage = mCurrentPage;
        }

        /**
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != 20) {
                int count = mCurrentPage * 20 + position;
                // 下面这部分，在EditText中显示表情
                SpannableString spannableString = FaceUtil.convertStringToSpanableString(facelist.get(count),
                        (int) (FaceUtil.getmFaceMap().values().toArray())[count], mContext);
                //在文本原有内容的基础上增加
                metSendContent.append(spannableString);
            } else {//当按删除键的时候进行删除操作
                int selection = metSendContent.getSelectionStart();
                String text = metSendContent.getText().toString();
                if (selection > 0) {
                    String text2 = text.substring(selection - 1);
                    if ("]".equals(text2)) {
                        int start = text.lastIndexOf("[");
                        int end = selection;
                        metSendContent.getText().delete(start, end);
                        return;
                    }
                    metSendContent.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * 当前消息记录ListView刷新数据的接口
     *
     * @return
     */
    @Override
    public Boolean onRefreshOnHeader() {
        try {
            mlvChatMsg.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            if (mChatMessageList.size() > 0) {
                try {
                    mCurrentConversation.queryMessages(mChatMessageList.get(0).getMessageId(),
                            TimeUtils.getLongTime(mChatMessageList.get(0).getmSendTime()), 5, new AVIMMessagesQueryCallback() {
                                @Override
                                public void done(List<AVIMMessage> list, AVIMException e) {
                                    if (e == null) {
                                        int listsize = list.size() - 1;
                                        for (int i = listsize; i >= 0; i--) {
                                            {
                                                AVIMMessage msg = list.get(i);
                                                ChatMessage chatMessage = new ChatMessage(
                                                        FaceUtil.convertNormalStringToSpannableString(((AVIMTextMessage) msg).getText().toString(), mContext),
                                                        msg.getMessageId(),
                                                        msg.getFrom().equals(XDApplication.getmUser().getmUsername()) ? ChatMessage.SEND : ChatMessage.RECEIVER,
                                                        TimeUtils.getTime(msg.getTimestamp()), mImgUrl);
                                                mChatMessageAdapter.addChatRecord(chatMessage);
                                            }
                                        }
                                    }
                                }
                            });
                    return true;
                } catch (Exception e) {
                    Toast.makeText(mContext, "没有更多记录了", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 处理当前的正在聊天对象的消息
     */
    @Override
    public void receiveMessageHandlerForChatTo(ChatMessage chatMessage) {
        String s = chatMessage.getmContent();
        if (s.equals(mContext.getResources().getString(R.string.have_send))
                || s.equals(mContext.getResources().getString(R.string.have_arrive))
                || s.equals(mContext.getResources().getString(R.string.have_complete))) {
            getState();
        }
        mChatMessageList.add(chatMessage);
        mChatMessageAdapter.notifyDataSetChanged();
        mlvChatMsg.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    @Override
    public void receiveMessageHandlerForNoChatTo(String id, String form, String content) {

    }

    private void getState() {
        msContentType = msAllId.substring(0, 6);
        msRewardId = msAllId.substring(6);
        if (msContentType.equals(FgCommunicate.REWARD)) {
            String url = XDApplication.dbUrl + "/delivery/task/" + msRewardId;
            final RequestParams requestParams = new RequestParams(url);
            requestParams.addParameter("token", XDApplication.getmUser().getmToken());
            requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
            BgServicePool.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                JSONObject json = jsonObject.getJSONObject("delivery");
                                miState = json.getInteger("state");
                                loadData();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showXutilError(mContext, ex);
                            Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
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
        } else {//表示是出行的时候
            String url = XDApplication.dbUrl + "/delivery/outing/" + msRewardId;
            final RequestParams requestParams = new RequestParams(url);
            requestParams.addParameter("token", XDApplication.getmUser().getmToken());
            requestParams.addParameter("username", XDApplication.getmUser().getmUsername());
            BgServicePool.getInstance().addRunnable(new Runnable() {
                @Override
                public void run() {
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            JSONObject jsonObject = JSON.parseObject(result);
                            if (jsonObject.getString("status").equals("success")) {
                                JSONObject json = jsonObject.getJSONObject("delivery");
                                miState = json.getInteger("state");
                                loadData();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Errorutils.showXutilError(mContext, ex);
                            Errorutils.showError(mContext, ex, "onCreate", "ChatActivity", ChatActivity.this);
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


    /**
     * 当前activity的MessageHandler消息处理
     */
    public static class CustomMessageHandler extends AVIMMessageHandler {

        private Context mContext;


        private String mUserName;

        private String mChatToName;

        private receiveMessageHandlerForChatTo mChatToListener;

        private receiveMessageHandlerForNoChatTo mNoChatToListener;

        private String mImgUrl = "";


        public CustomMessageHandler(Context context,
                                    String UserName, String chatToName,
                                    receiveMessageHandlerForNoChatTo noChatToListener,
                                    receiveMessageHandlerForChatTo chatToListener) {
            mContext = context;
            mUserName = UserName;
            mChatToName = chatToName;
            mChatToListener = chatToListener;
            mNoChatToListener = noChatToListener;
        }

        public void setmImgUrl(String mImgUrl) {
            this.mImgUrl = mImgUrl;
        }

        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            /**
             * 只接受自己的，而且对方是我当前聊天对象的消息
             */
            if (client.getClientId().equals(mUserName) && message.getFrom().equals(mChatToName)) {
                if (message instanceof AVIMTextMessage) {
                    /**
                     * 判断是否将消息设置为不弄放声音
                     * 播放提示音
                     */
                    if (SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SOUND))
                        NotificationUtil.noticeInMessage(mContext);
                    if (SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SHAKE))
                        NotificationUtil.noticeInShake(mContext);
                    /**
                     * message.getForm()方法是获取消息发送者的名称
                     * 因为message是文本类的消息
                     * AVIMMessage类是基类
                     * 他的子类有
                     * AVMIImageMessage   文本
                     * AVIMAudioMessage  音频
                     * AVIMImageMessage 照片
                     * AVIMConversation  视频
                     *
                     */
                    String s = ((AVIMTextMessage) message).getText();
                    ChatMessage chatMessage = new ChatMessage(
                            FaceUtil.convertNormalStringToSpannableString(s, mContext),
                            message.getMessageId(),
                            message.getFrom().equals(XDApplication.getmUser().getmUsername()) ? ChatMessage.SEND : ChatMessage.RECEIVER,
                            TimeUtils.getTime(message.getTimestamp()), mImgUrl);
                    mChatToListener.receiveMessageHandlerForChatTo(chatMessage);
                }
            } else if (client.getClientId().equals(mUserName)) {
                /**
                 * 此处是收到是我的，但是是其他朋友的发送的消息
                 * 更新消息列表的内容，特别是红色的提示（表示有多少未读）
                 */
                if (SharePreferenceUtils.getUserSetting(mContext, XDApplication.getmUser().getmUserPhone(), SharePreferenceUtils.SOUND))
                    NotificationUtil.noticeInMessage(mContext);
                /**
                 * 获取消息的主要内容
                 */
                String formName = message.getFrom();
                String content = ((AVIMTextMessage) message).getText().toString();
                String id = conversation.getName();
                mNoChatToListener.receiveMessageHandlerForNoChatTo(id, formName, content);
            }
        }

        @Override
        public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        }
    }

    /**
     * 创建对话
     * 包括检索当前对话的聊天记录
     * 在获取了id之后才创建对话框
     *
     * @param toUser
     */
    private void createConversation(final String toUser) {
        /**清空
         *
         */
        mChatMessageList.clear();
        mChatMessageAdapter.notifyDataSetChanged();
        mCurrentUser = AVIMClient.getInstance(XDApplication.getmUser().getmUsername());
        mCurrentUser.open(new AVIMClientCallback() {
            @Override
            public void done(final AVIMClient client, AVIMException e) {
                if (e == null) {
                    //登录成功后的逻辑
                    /**
                     *  * @param members 对话的成员
                     * @param name 对话的名字
                     * @param attributes 对话的额外属性
                     * @param isTransient 是否是暂态对话
                     * @param isUnique 如果已经存在符合条件的对话，是否返回已有对话
                     *                 为 false 时，则一直为创建新的对话
                     *                 为 true 时，则先查询，如果已有符合条件的对话，则返回已有的，否则，创建新的并返回
                     *                 为 true 时，仅 members 为有效查询条件
                     * @param callback
                     */
                    client.createConversation(Arrays.asList(toUser), msAllId, null, false, true,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation avimConversation, AVIMException e) {
                                    if (e == null) {
                                        mCurrentConversation = avimConversation;
                                        if (isFromDetail) {//表示接了一个新的任务
                                            mCurrentConversation.setName(msAllId);
                                            mCurrentConversation.updateInfoInBackground(new AVIMConversationCallback() {
                                                @Override
                                                public void done(AVIMException e) {
                                                    /**
                                                     * 创建对话框成功之后进行查询
                                                     */
                                                    if (e == null) {
                                                        mCurrentConversation.queryMessages(5, new AVIMMessagesQueryCallback() {
                                                            @Override
                                                            public void done(List<AVIMMessage> list, AVIMException e) {
                                                                if (e == null) {
                                                                    for (AVIMMessage msg : list) {
                                                                        ChatMessage chatMessage = new ChatMessage(
                                                                                FaceUtil.convertNormalStringToSpannableString((
                                                                                                (AVIMTextMessage) msg).getText().toString(),
                                                                                        mContext),
                                                                                msg.getMessageId(),
                                                                                msg.getFrom().equals(XDApplication.getmUser().getmUsername()) ? ChatMessage.SEND : ChatMessage.RECEIVER,
                                                                                TimeUtils.getTime(msg.getTimestamp()), mImgUrl);
                                                                        mChatMessageList.add(chatMessage);
                                                                    }
                                                                    mChatMessageAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                    }
                                                }
                                            });
                                        } else {//表示还是之前的任务，现在只是进入聊天
                                            mCurrentConversation.queryMessages(5, new AVIMMessagesQueryCallback() {
                                                @Override
                                                public void done(List<AVIMMessage> list, AVIMException e) {
                                                    if (e == null) {
                                                        for (AVIMMessage msg : list) {
                                                            ChatMessage chatMessage = new ChatMessage(
                                                                    FaceUtil.convertNormalStringToSpannableString((
                                                                                    (AVIMTextMessage) msg).getText().toString(),
                                                                            mContext),
                                                                    msg.getMessageId(),
                                                                    msg.getFrom().equals(XDApplication.getmUser().getmUsername()) ? ChatMessage.SEND : ChatMessage.RECEIVER,
                                                                    TimeUtils.getTime(msg.getTimestamp()), mImgUrl);
                                                            mChatMessageList.add(chatMessage);
                                                        }
                                                        mChatMessageAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }

                                    }
                                }
                            }
                    );
                }
            }
        });
    }

    private void closeInputMothod() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = inputMethodManager.isActive();
        if (isOpen) {
            inputMethodManager.hideSoftInputFromWindow(metSendContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
