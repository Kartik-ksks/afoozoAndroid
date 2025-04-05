package in.kpis.afoozo.bean;

import java.io.Serializable;

public class CouponListBean implements Serializable {


    /**
     * recordId : 1
     * active : true
     * title : OFFER
     * couponCode : OFFER20
     * startDate : 23-10-2019
     * endDate : 13-02-2020
     * minOrderValue : 700
     * discountType : PERCENTAGE
     * discountValue : 20.0
     * maxDiscountAmount : 100
     * description : GET UPTO RS.100
     * maxUsePerUser : 1
     * applicableOnAllCities : true
     * applicableOnAllRestaurants : true
     * termsAndCondition :
     */

    private String orderReferenceId;
    private long recordId;
    private boolean active;
    private String title;
    private String couponCode;
    private String startDate;
    private String endDate;
    private int minOrderValue;
    private String discountType;
    private double discountValue;
    private int maxDiscountAmount;
    private String description;
    private int maxUsePerUser;
    private boolean applicableOnAllCities;
    private boolean applicableOnAllRestaurants;
    private String termsAndCondition;
    private long createdAtTimeStamp;
    private double loyaltyPoint;

    public double getLoyaltyPoint() {
        return loyaltyPoint;
    }

    public void setLoyaltyPoint(double loyaltyPoint) {
        this.loyaltyPoint = loyaltyPoint;
    }

    public String getOrderReferenceId() {
        return orderReferenceId;
    }

    public void setOrderReferenceId(String orderReferenceId) {
        this.orderReferenceId = orderReferenceId;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(int minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public int getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(int maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxUsePerUser() {
        return maxUsePerUser;
    }

    public void setMaxUsePerUser(int maxUsePerUser) {
        this.maxUsePerUser = maxUsePerUser;
    }

    public boolean isApplicableOnAllCities() {
        return applicableOnAllCities;
    }

    public void setApplicableOnAllCities(boolean applicableOnAllCities) {
        this.applicableOnAllCities = applicableOnAllCities;
    }

    public boolean isApplicableOnAllRestaurants() {
        return applicableOnAllRestaurants;
    }

    public void setApplicableOnAllRestaurants(boolean applicableOnAllRestaurants) {
        this.applicableOnAllRestaurants = applicableOnAllRestaurants;
    }

    public String getTermsAndCondition() {
        return termsAndCondition;
    }

    public void setTermsAndCondition(String termsAndCondition) {
        this.termsAndCondition = termsAndCondition;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public void setCreatedAtTimeStamp(long createdAtTimeStamp) {
        this.createdAtTimeStamp = createdAtTimeStamp;
    }
}
