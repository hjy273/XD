package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * 物品类
 * 主要实现fastjson的转换
 * 悬赏相关
 */
public class Thing implements Serializable{

    private String type;

    private String thumbnail;

    private String weight;

    public Thing() {
    }

    public Thing(String thumbnail, String type, String weight) {
        this.thumbnail = thumbnail;
        this.type = type;
        this.weight = weight;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getType() {
        return type;
    }

    public String getWeight() {
        return weight;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}