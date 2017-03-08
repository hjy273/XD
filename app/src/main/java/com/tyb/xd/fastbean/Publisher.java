package com.tyb.xd.fastbean;
import java.io.Serializable;

/**
 * 代送发布者，结合代送的详情
 */
public class Publisher implements Serializable{

    private String nickname;

    private String username;

    private String role;

    private String headimg;

    public String phone;

    private int credibility;

    public Publisher() {
    }

    public Publisher(int credibility, String headimg, String nickname, String phone, String role, String username) {
        this.credibility = credibility;
        this.headimg = headimg;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

    public int getCredibility() {
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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCredibility(int credibility) {
        this.credibility = credibility;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
