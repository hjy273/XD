package com.tyb.xd.bean;

import java.io.Serializable;

/**
 * 一周消费记录的item
 */
public class WeekCostItemBean implements Serializable {


    private String time;
    private String username;
    private String number;
    private String type;
    private String balance;
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }


    public WeekCostItemBean() {

    }
    public WeekCostItemBean(String time, String username, String number, String type){
        this.time = time;
        this.username = username;
        this.number = number;
        this.type = type;

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
