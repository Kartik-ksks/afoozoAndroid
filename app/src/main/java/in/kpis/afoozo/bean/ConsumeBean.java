package in.kpis.afoozo.bean;

import java.io.Serializable;

public class ConsumeBean implements Serializable {


    /**
     * stealDealItemReservationId : 1
     * stealDealItemId : 1
     * stealDealItemTitle : 100 Pippers
     * stealDealItemImage :
     * consumableUnitPostfix : Peg
     * remainingQuantity : 25
     * consumedQuantity : 0
     * consumeAfter : 1576063103000
     * consumeExpiryDate : 1591593503000
     */

    private boolean gifted;
    private long stealDealItemReservationId;
    private long stealDealItemId;
    private String stealDealItemTitle;
    private String stealDealItemImage;
    private String backgroundImageUrl;
    private String consumableUnitPostfix;
    private String restaurantName;
    private int remainingQuantity;
    private int consumedQuantity;
    private long consumeAfter;
    private long consumeExpiryDate;

    public boolean isGifted() {
        return gifted;
    }

    public void setGifted(boolean gifted) {
        this.gifted = gifted;
    }

    public long getStealDealItemReservationId() {
        return stealDealItemReservationId;
    }

    public void setStealDealItemReservationId(long stealDealItemReservationId) {
        this.stealDealItemReservationId = stealDealItemReservationId;
    }

    public long getStealDealItemId() {
        return stealDealItemId;
    }

    public void setStealDealItemId(long stealDealItemId) {
        this.stealDealItemId = stealDealItemId;
    }

    public String getStealDealItemTitle() {
        return stealDealItemTitle;
    }

    public void setStealDealItemTitle(String stealDealItemTitle) {
        this.stealDealItemTitle = stealDealItemTitle;
    }

    public String getStealDealItemImage() {
        return stealDealItemImage;
    }

    public void setStealDealItemImage(String stealDealItemImage) {
        this.stealDealItemImage = stealDealItemImage;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getConsumableUnitPostfix() {
        return consumableUnitPostfix;
    }

    public void setConsumableUnitPostfix(String consumableUnitPostfix) {
        this.consumableUnitPostfix = consumableUnitPostfix;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public long getConsumeAfter() {
        return consumeAfter;
    }

    public void setConsumeAfter(long consumeAfter) {
        this.consumeAfter = consumeAfter;
    }

    public long getConsumeExpiryDate() {
        return consumeExpiryDate;
    }

    public void setConsumeExpiryDate(long consumeExpiryDate) {
        this.consumeExpiryDate = consumeExpiryDate;
    }
}
