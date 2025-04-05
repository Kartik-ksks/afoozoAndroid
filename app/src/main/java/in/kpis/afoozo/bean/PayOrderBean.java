package in.kpis.afoozo.bean;

public class PayOrderBean {
    private String orderId;
    private long userId;
    private double waiterTipAmount;

    private BeanPaytmTxnResponse txnResponse;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getWaiterTipAmount() {
        return waiterTipAmount;
    }

    public void setWaiterTipAmount(double waiterTipAmount) {
        this.waiterTipAmount = waiterTipAmount;
    }

    public BeanPaytmTxnResponse getTxnResponse() {
        return txnResponse;
    }

    public void setTxnResponse(BeanPaytmTxnResponse txnResponse) {
        this.txnResponse = txnResponse;
    }
}
