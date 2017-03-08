package com.tyb.xd.bean;


import com.tyb.xd.fastbean.RewardDeliveryDetailWithReceiver;

import java.io.Serializable;

/**
 * 消息列表
 */
public class MsgBean implements Serializable {

    String id;

    String toUser;

    String msLastTime;

    int miNotReadNum;

    String msLastMsg;

    public MsgBean() {
    }

    public MsgBean(String id, int miNotReadNum, String msLastMsg, String msLastTime, String toUser) {
        this.id = id;
        this.miNotReadNum = miNotReadNum;
        this.msLastMsg = msLastMsg;
        this.msLastTime = msLastTime;
        this.toUser = toUser;
    }

    public String getId() {
        return id;
    }

    public int getMiNotReadNum() {
        return miNotReadNum;
    }

    public String getMsLastMsg() {
        return msLastMsg;
    }

    public String getMsLastTime() {
        return msLastTime;
    }

    public String getToUser() {
        return toUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMiNotReadNum(int miNotReadNum) {
        this.miNotReadNum = miNotReadNum;
    }

    public void setMsLastMsg(String msLastMsg) {
        this.msLastMsg = msLastMsg;
    }

    public void setMsLastTime(String msLastTime) {
        this.msLastTime = msLastTime;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
