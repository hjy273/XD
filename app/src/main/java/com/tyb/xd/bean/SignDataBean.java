package com.tyb.xd.bean;

import java.io.Serializable;

/**
 * 签到的
 * 详情：type参数0表示星期标题，1表示本月日期，2表示非本月日期
 */
public class SignDataBean implements Serializable {
    private int type;
    private String date;

    public boolean isSigned() {
        return Signed;
    }

    public void setSigned(boolean signed) {
        Signed = signed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private boolean Signed;

    public SignDataBean() {

    }

    public SignDataBean(int type, String date, boolean Signed) {
        this.type = type;
        this.date = date;
        this.Signed = Signed;
    }
}
