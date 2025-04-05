package in.kpis.afoozo.bean;

import java.util.List;

public class RateDriverBean {


    /**
     * driverRating : 5
     * driverDeliveryTime : true
     * driverFoodHandling : true
     * driverAttitude : true
     * restaurantRating : 5
     * restaurantTaste : true
     * restaurantPortionSize : true
     * restaurantFoodPacking : true
     * itemRatingList : [{"orderItemId":1,"itemRating":5},{"orderItemId":2,"itemRating":5}]
     * suggestion : Suggestion here.............................
     */

    private int driverRating;
    private boolean driverDeliveryTime;
    private boolean driverFoodHandling;
    private boolean driverAttitude;
    private int restaurantRating;
    private int itemRating;
    private boolean restaurantTaste;
    private boolean restaurantPortionSize;
    private boolean restaurantFoodPacking;
    private String suggestion;
    private List<ItemRatingListBean> itemRatingList;

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public boolean isDriverDeliveryTime() {
        return driverDeliveryTime;
    }

    public void setDriverDeliveryTime(boolean driverDeliveryTime) {
        this.driverDeliveryTime = driverDeliveryTime;
    }

    public boolean isDriverFoodHandling() {
        return driverFoodHandling;
    }

    public void setDriverFoodHandling(boolean driverFoodHandling) {
        this.driverFoodHandling = driverFoodHandling;
    }

    public boolean isDriverAttitude() {
        return driverAttitude;
    }

    public void setDriverAttitude(boolean driverAttitude) {
        this.driverAttitude = driverAttitude;
    }

    public int getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(int restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public int getItemRating() {
        return itemRating;
    }

    public void setItemRating(int itemRating) {
        this.itemRating = itemRating;
    }

    public boolean isRestaurantTaste() {
        return restaurantTaste;
    }

    public void setRestaurantTaste(boolean restaurantTaste) {
        this.restaurantTaste = restaurantTaste;
    }

    public boolean isRestaurantPortionSize() {
        return restaurantPortionSize;
    }

    public void setRestaurantPortionSize(boolean restaurantPortionSize) {
        this.restaurantPortionSize = restaurantPortionSize;
    }

    public boolean isRestaurantFoodPacking() {
        return restaurantFoodPacking;
    }

    public void setRestaurantFoodPacking(boolean restaurantFoodPacking) {
        this.restaurantFoodPacking = restaurantFoodPacking;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public List<ItemRatingListBean> getItemRatingList() {
        return itemRatingList;
    }

    public void setItemRatingList(List<ItemRatingListBean> itemRatingList) {
        this.itemRatingList = itemRatingList;
    }

    public static class ItemRatingListBean {
        /**
         * orderItemId : 1
         * itemRating : 5
         */

        private long orderItemId;
        private int itemRating;

        public long getOrderItemId() {
            return orderItemId;
        }

        public void setOrderItemId(long orderItemId) {
            this.orderItemId = orderItemId;
        }

        public int getItemRating() {
            return itemRating;
        }

        public void setItemRating(int itemRating) {
            this.itemRating = itemRating;
        }
    }
}
