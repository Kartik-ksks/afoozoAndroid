package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuListBean implements Serializable {

    /**
     * recordId : 1
     * uuid : f05a57a1-a0cc-4618-91e3-b468b2c3547c
     * active : true
     * title : Drinks
     * categoryUuid : c5b75eaf-d13b-4e72-be42-b78b3abdad5e
     * subCategoryTitle :
     * vegNonVeg : Veg
     * dineIn : false
     * dineInPrice : 0.0
     * dineInFinalPrice : 12.0
     * deliveryAndTakeAway : false
     * deliveryAndTakeAwayPrice : 0.0
     * deliveryAndTakeAwayFinalPrice : 12.0
     * price : 34.0
     * finalPrice : 0.0
     * imageSize : SMALL
     * recommended : false
     * availableDineIn : false
     * availableDeliveryAndTakeAway : false
     * customization : [{"recordId":2,"active":true,"customizationType":"SingleSelection","customizationOptions":[{"id":1,"name":"a","price":1},{"id":2,"name":"b","price":2}]},{"recordId":3,"active":true,"customizationType":"MultiSelection","customizationOptions":[{"id":1,"name":"dfg","price":12},{"id":2,"name":"fgsdfg","price":32}]}]
     * quantity : 0
     */

    private long recordId;
    private long itemId;
    private String uuid;
    private String restaurantUuid;
    private boolean active;
    private String title;
    private String description;
    private String categoryUuid;
    private String subCategoryTitle;
    private String vegNonVeg;
    private String sticker;
    private boolean dineIn;
    private double dineInPrice;
    private double dineInFinalPrice;
    private boolean deliveryAndTakeAway;
    private double deliveryAndTakeAwayPrice;
    private double deliveryAndTakeAwayFinalPrice;
    private double price;
    private double finalPrice;
    private int takeAwayMinimumOrderAmount;
    private int deliveryMinimumOrderAmount;
    private String imageSize;
    private String itemImageUrl;
    private String itemImageUrlThumbnail;
    private boolean recommended;
    private boolean availableDineIn;
    private boolean restaurantOpen;
    private boolean availableDeliveryAndTakeAway;
    private int quantity;
    private ArrayList<CustomizationBean> customization;
    private MenuBean subCategory;
    private double rating;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public MenuListBean() {
    }

    public MenuListBean(MenuBean subCategory) {
        this.subCategory = subCategory;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantUuid() {
        return restaurantUuid;
    }

    public void setRestaurantUuid(String restaurantUuid) {
        this.restaurantUuid = restaurantUuid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getSubCategoryTitle() {
        return subCategoryTitle;
    }

    public void setSubCategoryTitle(String subCategoryTitle) {
        this.subCategoryTitle = subCategoryTitle;
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

    public String getItemImageUrlThumbnail() {
        return itemImageUrlThumbnail;
    }

    public void setItemImageUrlThumbnail(String itemImageUrlThumbnail) {
        this.itemImageUrlThumbnail = itemImageUrlThumbnail;
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

    public boolean isRestaurantOpen() {
        return restaurantOpen;
    }

    public void setRestaurantOpen(boolean restaurantOpen) {
        this.restaurantOpen = restaurantOpen;
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

    public MenuBean getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(MenuBean subCategory) {
        this.subCategory = subCategory;
    }

    public int getTakeAwayMinimumOrderAmount() {
        return takeAwayMinimumOrderAmount;
    }

    public void setTakeAwayMinimumOrderAmount(int takeAwayMinimumOrderAmount) {
        this.takeAwayMinimumOrderAmount = takeAwayMinimumOrderAmount;
    }

    public int getDeliveryMinimumOrderAmount() {
        return deliveryMinimumOrderAmount;
    }

    public void setDeliveryMinimumOrderAmount(int deliveryMinimumOrderAmount) {
        this.deliveryMinimumOrderAmount = deliveryMinimumOrderAmount;
    }
}
