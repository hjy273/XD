package com.tyb.xd.fastbean;

import java.io.Serializable;
import java.util.List;

/**
 * 主要用户解析fastjson
 * 出行的根节点
 */
public class GoOutRoot implements Serializable{

    private String status;

    private int max_page;

    private List<GoOutDeliveries> deliveries;

    public GoOutRoot() {
    }

    public GoOutRoot(List<GoOutDeliveries> deliveries, int max_page, String status) {
        this.deliveries = deliveries;
        this.max_page = max_page;
        this.status = status;
    }

    public List<GoOutDeliveries> getDeliveries() {
        return deliveries;
    }

    public int getMax_page() {
        return max_page;
    }

    public String getStatus() {
        return status;
    }

    public void setDeliveries(List<GoOutDeliveries> deliveries) {
        this.deliveries = deliveries;
    }

    public void setMax_page(int max_page) {
        this.max_page = max_page;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
