package org.apache.activemq.book.ch13;

public class OrderAction {
    // 0--删除, 1--增加
    private int action;
    private long orderId;
    private String deliverToAddress;
    private String phone;


    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getDeliverToAddress() {
        return deliverToAddress;
    }

    public void setDeliverToAddress(String deliverToAddress) {
        this.deliverToAddress = deliverToAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
