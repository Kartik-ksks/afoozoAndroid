package in.kpis.afoozo.bean;

public class CashFreeBean {
 /*   orderId : BOOKING-00049
    referenceId : 584438
    type : CashFreeResponse
    txMsg : Transaction Successful
    orderAmount : 525.0
    txStatus : SUCCESS*/

    private String orderId;
    private double orderAmount;
    private String referenceId;
    private String txStatus;
    private String paymentMode;
    private String txMsg;
    private String signature;
    private String paymentGateway;

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTxMsg() {
        return txMsg;
    }

    public void setTxMsg(String txMsg) {
        this.txMsg = txMsg;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
