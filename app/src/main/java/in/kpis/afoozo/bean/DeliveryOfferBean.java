package in.kpis.afoozo.bean;

import java.io.Serializable;

public class DeliveryOfferBean implements Serializable {


    /**
     * offerId : f38c70d4-3de9-4412-a6b8-4eee4eadc474
     * offerMessage : Add ₹125 more to your existing order & Get 1 Bacardi Free worth ₹135.0.
     */

    private String orderType;
    private long lastRequestTime;
    private String offerId;
    private String offerMessage;
    private String freeItemTitle;
    private String freeItemQty;
    private double minimumOrderValue;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferMessage() {
        return offerMessage;
    }

    public void setOfferMessage(String offerMessage) {
        this.offerMessage = offerMessage;
    }

    public String getFreeItemTitle() {
        return freeItemTitle;
    }

    public void setFreeItemTitle(String freeItemTitle) {
        this.freeItemTitle = freeItemTitle;
    }

    public String getFreeItemQty() {
        return freeItemQty;
    }

    public void setFreeItemQty(String freeItemQty) {
        this.freeItemQty = freeItemQty;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }
}
