package com.tyb.xd.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyb.xd.R;
import com.tyb.xd.activity.XDApplication;
import com.tyb.xd.bean.ChatMessage;
import com.tyb.xd.bean.RewardBean;
import com.tyb.xd.utils.TimeUtils;
import com.tyb.xd.view.GifTextView;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 聊天消息适配
 */
public class ChatMessageAdapter extends BaseAdapter {

    private List<ChatMessage> mlMsg;

    private Context mContext;


    public ChatMessageAdapter() {
        // TODO Auto-generated constructor stub
    }

    public ChatMessageAdapter(List<ChatMessage> list, Context context) {
        mlMsg = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlMsg.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlMsg.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //if the content is received
        if (mlMsg.get(position).getFlag() == ChatMessage.RECEIVER) {
            convertView = (RelativeLayout) inflater.inflate(R.layout.chat_item_left, null);
        }
        if (mlMsg.get(position).getFlag() == ChatMessage.SEND) {
            convertView = (RelativeLayout) inflater.inflate(R.layout.chat_item_right, null);
        }
        viewHolder = new ViewHolder();
        x.view().inject(viewHolder, convertView);
        viewHolder.mGifTvContent.insertGif(mlMsg.get(position).getmContent());
        ImageOptions options = new ImageOptions.Builder()
                .setLoadingDrawableId(R.drawable.default_headimg)
                .setFailureDrawableId(R.drawable.default_headimg)
                .setCircular(true)
                .build();
        if (mlMsg.get(position).getFlag() == ChatMessage.SEND) {
            x.image().bind(viewHolder.mivHead, XDApplication.getmUser().getmUserUrl() , options);
        } else {
            x.image().bind(viewHolder.mivHead, mlMsg.get(position).getImgUrl() , options);
        }
        if (position == 0) {
            viewHolder.mtvTime.setText(mlMsg.get(position).getmSendTime());
        } else {
            if (mlMsg.size() > 1) {
                if (TimeUtils.TimeOverFiveMinute(mlMsg.get(position - 1).getmSendTime(),
                        mlMsg.get(position).getmSendTime())) {
                    viewHolder.mtvTime.setText(mlMsg.get(position).getmSendTime());
                } else {
                    viewHolder.mtvTime.setText("");
                }
            }
        }
        return convertView;
    }

    //增加消息记录
    public void addChatRecord(ChatMessage... list) {
        for (ChatMessage data : list) {
            mlMsg.add(0, data);
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        @ViewInject(R.id.id_tv_chat_time)
        TextView mtvTime;
        @ViewInject(R.id.id_gtv_chat_item_content)
        GifTextView mGifTvContent;
        @ViewInject(R.id.id_iv_chat_item_img)
        ImageView mivHead;
    }
}