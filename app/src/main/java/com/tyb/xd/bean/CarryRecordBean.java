package com.tyb.xd.bean;

import java.io.Serializable;

/**
 * 代送记录
 */
public class CarryRecordBean implements Serializable {
    private String typeOrName;
    private String sendStartPlace;
    private String sendId;

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    private String reward;

    public String getTaskOrOuting() {
        return taskOrOuting;
    }

    public void setTaskOrOuting(String taskOrOuting) {
        this.taskOrOuting = taskOrOuting;
    }

    private String taskOrOuting;
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private String img;
    private String sendArrivePlace;
    private String sendRecordTime;
    private String sendRecordStatus;

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }


    public CarryRecordBean() {

    }

    public String getTypeOrName() {
        return typeOrName;
    }

    public void setTypeOrName(String typeOrName) {
        this.typeOrName = typeOrName;
    }

    public String getSendStartPlace() {
        return sendStartPlace;
    }

    public void setSendStartPlace(String sendStartPlace) {
        this.sendStartPlace = sendStartPlace;
    }

    public String getSendArrivePlace() {
        return sendArrivePlace;
    }

    public void setSendArrivePlace(String sendArrivePlace) {
        this.sendArrivePlace = sendArrivePlace;
    }

    public String getSendRecordTime() {
        return sendRecordTime;
    }

    public void setSendRecordTime(String sendRecordTime) {
        this.sendRecordTime = sendRecordTime;
    }

    public String getSendRecordStatus() {
        return sendRecordStatus;
    }

    public void setSendRecordStatus(String sendRecordStatus) {
        this.sendRecordStatus = sendRecordStatus;
    }
}
