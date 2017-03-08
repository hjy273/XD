package com.tyb.xd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.tyb.xd.R;
import com.tyb.xd.activity.ChatActivity;
import com.tyb.xd.activity.LoginActivity;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.adapter.MsgAdapter;
import com.tyb.xd.bean.MsgBean;
import com.tyb.xd.service.BgServicePool;
import com.tyb.xd.utils.NetUtils;
import com.tyb.xd.utils.SharePreferenceUtils;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.view.refreshListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ContentView(R.layout.fg_communicate)
public class FgCommunicate extends Fragment implements refreshListView.IRefreshListener {

    @ViewInject(R.id.id_top_tv_content)
    TextView mtvTopContent;
    @ViewInject(R.id.id_fg_communicate_lv_msg)
    refreshListView mlvMsg;

    /**
     * 加在代送id前，用于标识该id所代表悬赏的内容类型
     * 标签加上id组合成conversation的id，用于唯一的标识
     * 该聊天
     */
    public static String REWARD = "REWARD";
    public static String SHOWGO = "SHOWGO";
    public static String ADDMSG = "ADDMESSAGEFROMFGHALL";

    private String toUsername;

    private List<MsgBean> mlMsg = new ArrayList<MsgBean>();

    private MsgAdapter msgAdapter;

    private HashMap<String, Integer> mHaveNotRead = XDApplication.mHaveNotRead;

    private Context mContext;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    msgAdapter.notifyDataSetChanged();
                    break;
                case 0x111:
                    refreshOnHeader();
                    break;
                case 0x112:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.net_work_is_fail), Toast.LENGTH_SHORT).show();
                    mlvMsg.refreshCompleteOnHeader();
                    break;
            }
        }
    };

    public FgCommunicate() {
    }

    // TODO: Rename and change types and number of parameters
    public static FgCommunicate newInstance() {
        FgCommunicate fragment = new FgCommunicate();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    private void loadData() {
        mlMsg = new ArrayList<MsgBean>();
        msgAdapter = new MsgAdapter(mlMsg, mContext);
        mlvMsg.setAdapter(msgAdapter);
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            if (XDApplication.avimClient == null) {
                XDApplication.avimClient = AVIMClient.getInstance(XDApplication.getmUser().getmUsername());
                XDApplication.avimClient.open(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        XDApplication.avimClient = avimClient;
                    }
                });
            }
            AVIMConversationQuery query = XDApplication.avimClient.getQuery();
            query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ONLY);
            query.findInBackground(new AVIMConversationQueryCallback() {
                @Override
                public void done(List<AVIMConversation> list, AVIMException e) {
                    if (e == null) {
                        ArrayList<List<String>> convsersaionUnique = new ArrayList<List<String>>();
                        int i=0;
                        for (AVIMConversation con : list) {
                            final AVIMConversation conversation = con;
                            if (conversation != null && conversation.getMembers().size() >= 2) {
                                if (!isHave(convsersaionUnique, conversation.getMembers())) {
                                    convsersaionUnique.add(conversation.getMembers());
                                    final List<String> members = conversation.getMembers();
                                    final String ta = members.get(0).equals(XDApplication.getmUser().getmUsername()) ?
                                            members.get(1) : members.get(0);
                                    conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                                        @Override
                                        public void done(AVIMException e) {
                                            conversation.queryMessages(1, new AVIMMessagesQueryCallback() {
                                                @Override
                                                public void done(List<AVIMMessage> list, AVIMException e) {
                                                    if (e == null &&(list!=null)&& !list.isEmpty()) {
                                                        AVIMMessage avimMessage = list.get(0);
                                                        if (avimMessage != null) {
                                                            MsgBean msgBean = new MsgBean();
                                                            msgBean.setId(conversation.getName());
                                                            Date lastMessageAt = conversation.getLastMessageAt();
                                                            msgBean.setMsLastTime(TimeUtils.getTimeOnlyHour(lastMessageAt.getTime()));
                                                            msgBean.setToUser(ta);
                                                            msgBean.setMsLastMsg(((AVIMTextMessage) avimMessage).getText().toString());
                                                            if (mHaveNotRead.containsKey(conversation.getConversationId())) {
                                                                msgBean.setMiNotReadNum(mHaveNotRead.get(conversation.getConversationId()));
                                                            } else {
                                                                msgBean.setMiNotReadNum(0);
                                                            }
                                                            mlMsg.add(msgBean);
                                                            mHandler.sendEmptyMessage(0x110);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            }
                        }
                    }
                }

            });
        }
    }

    public Boolean isHave(ArrayList<List<String>> all, List<String> members) {
        boolean isUnique = false;
        String nowUser1 = members.get(0);
        String NowUser2 = members.get(1);
        String NowUnique = createConversationName(nowUser1, NowUser2);
        for (List<String> item : all) {
            String user1 = item.get(0);
            String user2 = item.get(1);
            String unique = createConversationName(user1, user2);
            if (unique.equals(NowUnique)) {
                isUnique = true;
                break;
            }
        }
        return isUnique;
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

    private void initData() {
        mlvMsg.setInterface(this);
        mlvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MsgBean msg = mlMsg.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putString("id", msg.getId());
                bundle.putString("touser", msg.getToUser());
                bundle.putString("username", msg.getToUser());
                bundle.putString("from", "communicate");
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mtvTopContent.setText(getResources().getString(R.string.message_hall));
    }

    @Override
    public void onResume() {
        super.onResume();
  //      loadData();
    }

    @Override
    public void onRefreshOnHeader() {
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            if (NetUtils.isNetworkAvailable(mContext)) {
                BgServicePool.getInstance().addRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (NetUtils.ping()) {
                            mHandler.sendEmptyMessage(0x111);
                        } else {
                            mHandler.sendEmptyMessage(0x112);
                        }
                    }
                });
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.net_work_is_fail), Toast.LENGTH_SHORT).show();
                mlvMsg.refreshCompleteOnHeader();
            }
        } else {
            mlvMsg.refreshCompleteOnHeader();
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void refreshOnHeader() {
        mlMsg.clear();
        mlMsg = new ArrayList<MsgBean>();
        msgAdapter = new MsgAdapter(mlMsg, mContext);
        mlvMsg.setAdapter(msgAdapter);
        if (SharePreferenceUtils.getLoginStatus(mContext)) {
            if (XDApplication.avimClient == null) {
                XDApplication.avimClient = AVIMClient.getInstance(XDApplication.getmUser().getmUsername());
                XDApplication.avimClient.open(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            XDApplication.avimClient = avimClient;
                        }
                    }
                });
            }
            AVIMClient.setMessageQueryCacheEnable(false);
            AVIMConversationQuery query = XDApplication.avimClient.getQuery();
            query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ONLY);
            query.findInBackground(new AVIMConversationQueryCallback() {
                @Override
                public void done(List<AVIMConversation> list, AVIMException e) {
                    if (e == null) {
                        final ArrayList<List<String>> convsersaionUnique = new ArrayList<List<String>>();
                        for (final AVIMConversation conversation : list) {
                            if (conversation != null && conversation.getMembers().size() >= 2) {
                                conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVIMException e) {
                                        if (!isHave(convsersaionUnique, conversation.getMembers())) {
                                            convsersaionUnique.add(conversation.getMembers());
                                            final List<String> members = conversation.getMembers();
                                            final String ta = members.get(0).equals(XDApplication.getmUser().getmUsername()) ?
                                                    members.get(1) : members.get(0);
                                            conversation.queryMessages(1, new AVIMMessagesQueryCallback() {
                                                @Override
                                                public void done(List<AVIMMessage> list, AVIMException e) {
                                                    if (e == null && !list.isEmpty()) {
                                                        AVIMMessage avimMessage = list.get(0);
                                                        if (avimMessage != null) {
                                                            MsgBean msgBean = new MsgBean();
                                                            msgBean.setId(conversation.getName());
                                                            Date lastMessageAt = conversation.getLastMessageAt();
                                                            msgBean.setMsLastTime(TimeUtils.getTimeOnlyHour(lastMessageAt.getTime()));
                                                            msgBean.setToUser(ta);
                                                            msgBean.setMsLastMsg(((AVIMTextMessage) avimMessage).getText().toString());
                                                            if (mHaveNotRead.containsKey(conversation.getConversationId())) {
                                                                msgBean.setMiNotReadNum(mHaveNotRead.get(conversation.getConversationId()));
                                                            } else {
                                                                msgBean.setMiNotReadNum(0);
                                                            }
                                                            mlMsg.add(msgBean);
                                                            mHandler.sendEmptyMessage(0x110);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                        mlvMsg.refreshCompleteOnHeader();
                    }
                }
            });
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRefreshOnFooter() {
        mlvMsg.refreshCompleteOnFooter();
    }

    /**
     * 传入对话的用户
     * 返回对于这两个用户的唯一的对话名字
     *
     * @param user1
     * @param user2
     * @return
     */
    public static String createConversationName(String user1, String user2) {
        return user1.compareTo(user2) >= 0 ? user1 + " & " + user2 : user2 + " & " + user1;
    }
}
