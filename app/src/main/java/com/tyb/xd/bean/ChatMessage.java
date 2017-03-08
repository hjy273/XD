package com.tyb.xd.bean;

/**
 * Created by wangpeiyu on 2016/7/3.
 */
public class ChatMessage {

    private String mContent;

    /**
     * 消息的两种状态
     */
    public static final int SEND = 1;

    public static final int RECEIVER = 0;

    //保存消息的状态
    public int mFlag = -1;

    private String mSendTime = "";

    //获取当前显示的最新消息的id，进而根据id获取新纪录
    private String MessageId;

    private String imgUrl;


    public ChatMessage() {
        // TODO Auto-generated constructor stub
    }

    public ChatMessage(String content) {
        // TODO Auto-generated constructor stub
        mContent = content;
    }

    public ChatMessage(String mContent, int mFlag, String mSendTime, String imgUrl) {
        this.mContent = mContent;
        this.mFlag = mFlag;
        this.mSendTime = mSendTime;
        this.imgUrl = imgUrl;
    }

    public ChatMessage(String mContent, String messageId, int mFlag, String mSendTime,String imgUrl) {
        this.mContent = mContent;
        MessageId = messageId;
        this.mFlag = mFlag;
        this.mSendTime = mSendTime;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setmSendTime(String mSendTime) {
        this.mSendTime = mSendTime;
    }

    public String getmSendTime() {
        return mSendTime;
    }

    public String getMessageId() {
        return MessageId;
    }

}
