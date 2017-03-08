package com.tyb.xd.fastbean;

import java.io.Serializable;

/**
 * Created by wangpeiyu on 2016/8/4.
 */
public class GoOutDetailWithReceiveRoot implements Serializable {

    private String status;

    private GoOutDeliveryDetailWithReceiver delivery;

    public GoOutDetailWithReceiveRoot() {
    }

    public GoOutDetailWithReceiveRoot(GoOutDeliveryDetailWithReceiver delivery, String status) {
        this.delivery = delivery;
        this.status = status;
    }

    public GoOutDeliveryDetailWithReceiver getDelivery() {
        return delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setDelivery(GoOutDeliveryDetailWithReceiver delivery) {
        this.delivery = delivery;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
