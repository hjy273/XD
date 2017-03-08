package com.tyb.xd.fastbean;

/**
 * 主要是实现fastjson
 * 返回悬赏信息的根类
 */
import java.io.Serializable;
import java.util.List;
public class RewardRoot implements Serializable {

    private String status;

    private int max_page;

    private List<RewardDeliveries> deliveries ;

    public RewardRoot() {
    }

    public RewardRoot(List<RewardDeliveries> deliveries, int max_page, String status) {
        this.deliveries = deliveries;
        this.max_page = max_page;
        this.status = status;
    }

    public List<RewardDeliveries> getDeliveries() {
        return deliveries;
    }

    public int getMax_page() {
        return max_page;
    }

    public String getStatus() {
        return status;
    }

    public void setDeliveries(List<RewardDeliveries> deliveries) {
        this.deliveries = deliveries;
    }

    public void setMax_page(int max_page) {
        this.max_page = max_page;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}