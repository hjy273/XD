package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * Created by wangpeiyu on 2016/8/1.
 */
public class Receiver implements Serializable{

    private String nickname;
    private String username;
    private String phone;
    private String role;
    private String headimg;
    private String credibility;

    public Receiver() {
    }

    public Receiver(String credibility, String headimg, String nickname, String phone, String role, String username) {
        this.credibility = credibility;
        this.headimg = headimg;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCredibility() {
        return credibility;
    }

    public String getHeadimg() {
        return headimg;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public void setCredibility(String credibility) {
        this.credibility = credibility;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
