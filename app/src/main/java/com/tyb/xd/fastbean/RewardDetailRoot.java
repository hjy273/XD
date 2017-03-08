package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * 代送详情的根
 */
public class RewardDetailRoot implements Serializable{
    private String status;

    private RewardDeliveryDetailWithoutReceiver delivery;

    public RewardDetailRoot() {
    }

    public RewardDetailRoot(RewardDeliveryDetailWithoutReceiver delivery, String status) {
        this.delivery = delivery;
        this.status = status;
    }

    public RewardDeliveryDetailWithoutReceiver getDelivery() {
        return delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setDelivery(RewardDeliveryDetailWithoutReceiver delivery) {
        this.delivery = delivery;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
