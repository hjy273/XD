package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * 代送详情
 */
public class RewardDeliveryDetailWithReceiver implements Serializable {

    private String describe;

    private String deadline;

    private Publisher publisher;

    private String school;

    private Thing thing;

    private String destination;

    private String source;

    private int state;

    private String contact;

    private String time;

    private Receiver receiver;

    private int reward;

    public RewardDeliveryDetailWithReceiver() {
    }

    public RewardDeliveryDetailWithReceiver(String contact, String deadline, String describe,
                                            Publisher publisher, String destination,
                                            Receiver receiver, int reward, String school,
                                            String source, int state, Thing thing, String time) {
        this.contact = contact;
        this.deadline = deadline;
        this.describe = describe;
        this.publisher = publisher;
        this.destination = destination;
        this.receiver = receiver;
        this.reward = reward;
        this.school = school;
        this.source = source;
        this.state = state;
        this.thing = thing;
        this.time = time;
    }

    public String getContact() {
        return contact;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getDescribe() {
        return describe;
    }

    public String getDestination() {
        return destination;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public int getReward() {
        return reward;
    }

    public String getSchool() {
        return school;
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

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSchool(String school) {
        this.school = school;
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
