package in.kpis.afoozo.bean;

import java.io.Serializable;

public class ATMBarTransactionBean implements Serializable {


    /**
     * rfidTransactionType : Recharge
     * amount : 100.0
     * createdAtTimeStamp : 1583216771000
     * paymentType : Cash
     * transactionId : TXN-0000000016
     * message : Card Recharged by payment using Cash
     */

    private String rfidTransactionType;
    private double amount;
    private long createdAtTimeStamp;
    private String paymentType;
    private String transactionId;
    private String message;

    public String getRfidTransactionType() {
        return rfidTransactionType;
    }

    public void setRfidTransactionType(String rfidTransactionType) {
        this.rfidTransactionType = rfidTransactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public void setCreatedAtTimeStamp(long createdAtTimeStamp) {
        this.createdAtTimeStamp = createdAtTimeStamp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
