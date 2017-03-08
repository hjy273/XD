package com.tyb.xd.fastbean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 主要实现fastjson的转换
 * 悬赏相关
 */
public class RewardDeliveries implements Serializable {

    private Thing thing;

    private int state;

    private int reward;

    private String deadline;

    private String time;

    private String source;

    private String destination;

    private String _id;

    public RewardDeliveries() {
    }

    public RewardDeliveries(String _id, String deadline, String destination,
                            int reward, String source, Thing thing, int state, String time) {
        this._id = _id;
        this.deadline = deadline;
        this.destination = destination;
        this.reward = reward;
        this.source = source;
        this.thing = thing;
        this.state = state;
        this.time = time;
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

    public String getSource() {
        return source;
    }

    public int getState() {
        return state;
    }

    public Thing getThing() {
        return thing;
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

    public void setState(int state) {
        this.state = state;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public void setTime(String time) {
        this.time = time;
    }
}