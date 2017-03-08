package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * Created by wangpeiyu on 2016/8/2.
 */
public class RewardDeliveryDetailWithReceiverRoot implements Serializable {
    private String status;

    private RewardDeliveryDetailWithReceiver delivery;

    public RewardDeliveryDetailWithReceiverRoot() {
    }

    public RewardDeliveryDetailWithReceiverRoot(RewardDeliveryDetailWithReceiver delivery, String status) {
        this.delivery = delivery;
        this.status = status;
    }

    public RewardDeliveryDetailWithReceiver getDelivery() {
        return delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setDelivery(RewardDeliveryDetailWithReceiver delivery) {
        this.delivery = delivery;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
