package com.tyb.xd.fastbean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangpeiyu on 2016/8/11.
 */
public class AmapDeliveries {
    private String lat;

    private String source;

    private String lng;

    private String _id;

    public AmapDeliveries() {
    }

    public AmapDeliveries(String _id, String lat, String lng, String source) {
        this._id = _id;
        this.lat = lat;
        this.lng = lng;
        this.source = source;
    }

    @JSONField(name = "_id")
    public String get_id() {
        return _id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getSource() {
        return source;
    }
    @JSONField(name = "_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
