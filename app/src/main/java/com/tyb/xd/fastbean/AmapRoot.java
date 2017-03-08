package com.tyb.xd.fastbean;

import java.util.List;

/**
 * Created by wangpeiyu on 2016/8/11.
 */
public class AmapRoot {

    private String status;

    private List<AmapDeliveries> deliveries;

    public AmapRoot() {
    }

    public AmapRoot(List<AmapDeliveries> deliveries, String status) {
        this.deliveries = deliveries;
        this.status = status;
    }

    public List<AmapDeliveries> getDeliveries() {
        return deliveries;
    }

    public String getStatus() {
        return status;
    }

    public void setDeliveries(List<AmapDeliveries> deliveries) {
        this.deliveries = deliveries;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
