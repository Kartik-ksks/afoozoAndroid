package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class OrderDetailsItemBean {


    /**
     * orderItemId : 1
     * itemId : 70
     * active : true
     * title : Burger
     * subCategoryId : 25
     * vegNonVeg : Veg
     * dineIn : false
     * dineInPrice : 0.0
     * dineInFinalPrice : 0.0
     * availableInNight : false
     * deliveryAndTakeAway : false
     * deliveryAndTakeAwayPrice : 0.0
     * deliveryAndTakeAwayFinalPrice : 0.0
     * price : 0.0
     * finalPrice : 105.0
     * itemTotal : 105.0
     * texAmount : 5.25
     * taxJson : [{"active":true,"taxTitle":"GST","taxAmount":5.25}]
     * totalTaxAmount : 5.25
     * imageSize : SMALL
     * itemImageUrl : 1573472090776_MushroomSupericon.png
     * recommended : false
     * availableDineIn : false
     * availableDeliveryAndTakeAway : false
     * customization : [{"recordId":5,"active":true,"title":"CUST1","customizationType":"MultiSelection","customizationOptions":[{"name":"001","price":10},{"name":"002","price":15}]},{"recordId":6,"active":true,"title":"CUST2","customizationType":"MultiSelection","customizationOptions":[{"name":"003","price":20}]},{"recordId":7,"active":true,"title":"CUST3","customizationType":"MultiSelection","customizationOptions":[{"name":"005","price":30}]}]
     * quantity : 1
     */

    private long orderItemId;
    private long itemId;
    private boolean active;
    private String title;
    private String specialInstruction;
    private long subCategoryId;
    private String vegNonVeg;
    private boolean dineIn;
    private double dineInPrice;
    private double dineInFinalPrice;
    private boolean availableInNight;
    private boolean deliveryAndTakeAway;
    private double deliveryAndTakeAwayPrice;
    private double deliveryAndTakeAwayFinalPrice;
    private double price;
    private double finalPrice;
    private double itemTotal;
    private double taxAmount;
    private String taxJson;
    private double totalTaxAmount;
    private String imageSize;
    private String itemImageUrl;
    private boolean recommended;
    private boolean availableDineIn;
    private boolean availableDeliveryAndTakeAway;
    private int quantity;
    private ArrayList<CustomizationBean> customization;
    private int ratingUserToDish = 5;

    public int getRatingUserToDish() {
        return ratingUserToDish;
    }

    public void setRatingUserToDish(int ratingUserToDish) {
        this.ratingUserToDish = ratingUserToDish;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
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

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getVegNonVeg() {
        return vegNonVeg;
    }

    public void setVegNonVeg(String vegNonVeg) {
        this.vegNonVeg = vegNonVeg;
    }

    public boolean isDineIn() {
        return dineIn;
    }

    public void setDineIn(boolean dineIn) {
        this.dineIn = dineIn;
    }

    public double getDineInPrice() {
        return dineInPrice;
    }

    public void setDineInPrice(double dineInPrice) {
        this.dineInPrice = dineInPrice;
    }

    public double getDineInFinalPrice() {
        return dineInFinalPrice;
    }

    public void setDineInFinalPrice(double dineInFinalPrice) {
        this.dineInFinalPrice = dineInFinalPrice;
    }

    public boolean isAvailableInNight() {
        return availableInNight;
    }

    public void setAvailableInNight(boolean availableInNight) {
        this.availableInNight = availableInNight;
    }

    public boolean isDeliveryAndTakeAway() {
        return deliveryAndTakeAway;
    }

    public void setDeliveryAndTakeAway(boolean deliveryAndTakeAway) {
        this.deliveryAndTakeAway = deliveryAndTakeAway;
    }

    public double getDeliveryAndTakeAwayPrice() {
        return deliveryAndTakeAwayPrice;
    }

    public void setDeliveryAndTakeAwayPrice(double deliveryAndTakeAwayPrice) {
        this.deliveryAndTakeAwayPrice = deliveryAndTakeAwayPrice;
    }

    public double getDeliveryAndTakeAwayFinalPrice() {
        return deliveryAndTakeAwayFinalPrice;
    }

    public void setDeliveryAndTakeAwayFinalPrice(double deliveryAndTakeAwayFinalPrice) {
        this.deliveryAndTakeAwayFinalPrice = deliveryAndTakeAwayFinalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(double itemTotal) {
        this.itemTotal = itemTotal;
    }

//    public double getTexAmount() {
//        return texAmount;
//    }
//
//    public void setTexAmount(double texAmount) {
//        this.texAmount = texAmount;
//    }


    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTaxJson() {
        return taxJson;
    }

    public void setTaxJson(String taxJson) {
        this.taxJson = taxJson;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isAvailableDineIn() {
        return availableDineIn;
    }

    public void setAvailableDineIn(boolean availableDineIn) {
        this.availableDineIn = availableDineIn;
    }

    public boolean isAvailableDeliveryAndTakeAway() {
        return availableDeliveryAndTakeAway;
    }

    public void setAvailableDeliveryAndTakeAway(boolean availableDeliveryAndTakeAway) {
        this.availableDeliveryAndTakeAway = availableDeliveryAndTakeAway;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<CustomizationBean> getCustomization() {
        return customization;
    }

    public void setCustomization(ArrayList<CustomizationBean> customization) {
        this.customization = customization;
    }
}
