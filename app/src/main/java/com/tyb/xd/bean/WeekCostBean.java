package com.tyb.xd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 一周消费记录
 * item的集合
 * 只要使用fastjson
 */
public class WeekCostBean implements Serializable {
    private String status;
    private String max_page;
    private List<WeekCostItemBean> records;
    public WeekCostBean() {

    }

    public WeekCostBean(String status, String max_page, List<WeekCostItemBean> records) {
        this.status = status;
        this.max_page = max_page;
        this.records = records;
    }
    public List<WeekCostItemBean> getRecords() {
        return records;
    }

    public void setRecords(List<WeekCostItemBean> records) {
        this.records = records;
    }

    public void setItemBean(List<WeekCostItemBean> records) {
        this.records = records;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMax_page() {
        return max_page;
    }

    public void setMax_page(String max_page) {
        this.max_page = max_page;
    }


}
