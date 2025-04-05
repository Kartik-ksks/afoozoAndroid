package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class RestaurantBean implements Serializable {


    /**
     * recordId : 1
     * title : Krishna Padam IT Solutions
     * restaurantAddress : 11/1490, Malviya Nagar, Jaipur
     * latitude : 26.9124
     * longitude : 75.7873
     * mobileNumbers : 9460818386,9782144150
     * emails : shashi@kpis.in,shashibaheti04@gmail.com
     * restaurantBannerUrl : https://s3.ap-south-1.amazonaws.com/crypto-pro-bucket/primary-image/1571386958715_MushroomSupericon.png
     * open : true
     * distanceInMeter : 4301
     */


//            "latitude": 22.9825,
//            "longitude": 72.3786,
//            "mobileNumbers": "8160602092",
//            "emails": "um.gingersanand@afoozo.com",
//            "restaurantBannerUrl": "https://s3.ap-south-1.amazonaws.com/afoozo-pro-bucket/primary-image/1588676061803_WhatsAppImageatPM.png",
//            "open": true,
//            "distanceInMeter": 553891,
//            "cityName": "Sanand",
//            "takeAwayMinimumOrderAmount": 0,
//            "deliveryMinimumOrderAmount": 200,
//            "cuisineList": [
//            "South Indian"
//            ]

    private long recordId;
    private String restaurantUuid;
    private String title;
    private String cityName;
    private String restaurantAddress;
    private double latitude;
    private double estimatedTimeArrival;
    private double longitude;
    private double rating;
    private int takeAwayMinimumOrderAmount;
    private int deliveryMinimumOrderAmount;
    private String mobileNumbers;
    private String emails;
    private String restaurantBannerUrl;
    private boolean open;
    private long distanceInMeter;
    private ArrayList<String> cuisineList;


    public double getEstimatedTimeArrival() {
        return estimatedTimeArrival;
    }

    public void setEstimatedTimeArrival(double estimatedTimeArrival) {
        this.estimatedTimeArrival = estimatedTimeArrival;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getCuisineList() {
        return cuisineList;
    }

    public void setCuisineList(ArrayList<String> cuisineList) {
        this.cuisineList = cuisineList;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String getRestaurantUuid() {
        return restaurantUuid;
    }

    public void setRestaurantUuid(String restaurantUuid) {
        this.restaurantUuid = restaurantUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMobileNumbers() {
        return mobileNumbers;
    }

    public void setMobileNumbers(String mobileNumbers) {
        this.mobileNumbers = mobileNumbers;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getRestaurantBannerUrl() {
        return restaurantBannerUrl;
    }

    public void setRestaurantBannerUrl(String restaurantBannerUrl) {
        this.restaurantBannerUrl = restaurantBannerUrl;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getDistanceInMeter() {
        return distanceInMeter;
    }

    public void setDistanceInMeter(long distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
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
