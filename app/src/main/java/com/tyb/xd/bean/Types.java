package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 学校内标准地点的分类
 * 有四个分类
 * 宿舍楼、教学楼、运动场、学院楼
 */
public class Types implements Serializable {
    private String type;
    private List<String> specific_place;

    public Types() {
    }

    public Types(List<String> specific_place, String type) {
        this.specific_place = specific_place;
        this.type = type;
    }


    public List<String> getSpecific_place() {
        return specific_place;
    }

    public String getType() {
        return type;
    }

    public void setSpecific_place(List<String> specific_place) {
        this.specific_place = specific_place;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(String types) {
        return type.equals(types);
    }
}
