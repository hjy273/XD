package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * Created by wangpeiyu on 2016/8/4.
 */
public class GoOutDetailRoot implements Serializable {

    private String status;

    private GoOutDeliveryDetailWithoutReceiver delivery;

    public GoOutDetailRoot() {
    }

    public GoOutDetailRoot(GoOutDeliveryDetailWithoutReceiver delivery, String status) {
        this.delivery = delivery;
        this.status = status;
    }

    public GoOutDeliveryDetailWithoutReceiver getDelivery() {
        return delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setDelivery(GoOutDeliveryDetailWithoutReceiver delivery) {
        this.delivery = delivery;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
