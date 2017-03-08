package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * 加载出行数据的用户类
 */
public class FastJsonUser implements Serializable{

    private String username;

    private String headimg;

    public FastJsonUser() {
    }

    public FastJsonUser(String headimg, String username) {
        this.headimg = headimg;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
