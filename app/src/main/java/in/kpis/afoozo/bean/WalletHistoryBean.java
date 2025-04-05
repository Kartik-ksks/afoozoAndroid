package in.kpis.afoozo.bean;

public class WalletHistoryBean {


    /**
     * createdAt : 2019-12-20T09:30:37.000+0000
     * transactionReferenceId : TXN-0000000081
     * transactionType : AddMoney
     * addLess : ADD
     * remark : Add Money By Payment using Razorpay
     * transactionAmount : 702.0
     * previousWalletBalance : 0.0
     */

    private String createdAt;
    private Long createdAtTimeStamp;
    private String transactionReferenceId;
    private String transactionType;
    private String addLess;
    private String remark;
    private double transactionAmount;
    private double previousWalletBalance;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public void setCreatedAtTimeStamp(Long createdAtTimeStamp) {
        this.createdAtTimeStamp = createdAtTimeStamp;
    }

    public String getTransactionReferenceId() {
        return transactionReferenceId;
    }

    public void setTransactionReferenceId(String transactionReferenceId) {
        this.transactionReferenceId = transactionReferenceId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAddLess() {
        return addLess;
    }

    public void setAddLess(String addLess) {
        this.addLess = addLess;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public double getPreviousWalletBalance() {
        return previousWalletBalance;
    }

    public void setPreviousWalletBalance(double previousWalletBalance) {
        this.previousWalletBalance = previousWalletBalance;
    }
}
