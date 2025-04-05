package in.kpis.afoozo.bean;

import java.io.Serializable;

public class RewardsBean implements Serializable {


    /**
     * recordId : 4dbc42c9-a4cc-4617-ac0b-1790b4074c23
     * scratchAfter : 2020-03-23
     * couponCode : ZYVNWZIUZB
     * couponDiscountAmount : 123.0
     * applicableOnDelivery : false
     * applicableOnTakeAway : false
     * applicableOnStealDeal : false
     * applicableOnDineIn : true
     */

    private String recordId;
    private String title;
    private String scratchAfter;
    private String couponCode;
    private String description;
    private double couponDiscountAmount;
    private boolean applicableOnDelivery;
    private boolean applicableOnTakeAway;
    private boolean applicableOnStealDeal;
    private boolean applicableOnDineIn;
    private boolean used;
    private long createdAtTimeStamp;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScratchAfter() {
        return scratchAfter;
    }

    public void setScratchAfter(String scratchAfter) {
        this.scratchAfter = scratchAfter;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public double getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(double couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public boolean isApplicableOnDelivery() {
        return applicableOnDelivery;
    }

    public void setApplicableOnDelivery(boolean applicableOnDelivery) {
        this.applicableOnDelivery = applicableOnDelivery;
    }

    public boolean isApplicableOnTakeAway() {
        return applicableOnTakeAway;
    }

    public void setApplicableOnTakeAway(boolean applicableOnTakeAway) {
        this.applicableOnTakeAway = applicableOnTakeAway;
    }

    public boolean isApplicableOnStealDeal() {
        return applicableOnStealDeal;
    }

    public void setApplicableOnStealDeal(boolean applicableOnStealDeal) {
        this.applicableOnStealDeal = applicableOnStealDeal;
    }

    public boolean isApplicableOnDineIn() {
        return applicableOnDineIn;
    }

    public void setApplicableOnDineIn(boolean applicableOnDineIn) {
        this.applicableOnDineIn = applicableOnDineIn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public void setCreatedAtTimeStamp(long createdAtTimeStamp) {
        this.createdAtTimeStamp = createdAtTimeStamp;
    }
}
