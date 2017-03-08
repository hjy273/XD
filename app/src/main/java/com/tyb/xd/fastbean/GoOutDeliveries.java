package com.tyb.xd.fastbean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 出行的信息
 */
public class GoOutDeliveries implements Serializable {

    private String source;

    private int state;

    private int reward;

    private String deadline;

    private FastJsonUser user;

    private String time;

    private String destination;

    private String _id;

    public GoOutDeliveries() {
    }

    public GoOutDeliveries(String _id, String deadline, String destination,
                           int reward, String source, int state, String time, FastJsonUser user) {
        this._id = _id;
        this.deadline = deadline;
        this.destination = destination;
        this.reward = reward;
        this.source = source;
        this.state = state;
        this.time = time;
        this.user = user;
    }

    @JSONField(name = "_id")
    public String get_id() {
        return _id;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDestination() {
        return destination;
    }

    public int getReward() {
        return reward;
    }

    public int getState() {
        return state;
    }

    public String getSource() {
        return source;
    }

    public FastJsonUser getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    @JSONField(name = "_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setUser(FastJsonUser user) {
        this.user = user;
    }
}
