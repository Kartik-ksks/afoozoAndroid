package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class OrderDetailsBean {


    /**
     * recordId : 2
     * orderDateTime : 1573305489000
     * orderRefId : ORD-00002
     * specialInstruction : Special Instruction
     * pickUpCompleteAddress : 11/1490, Malviya Nagar, Jaipur, Ambala, Haryana, India, (302006)
     * deliveryCompleteAddress : 11/1490, Malviya Nagar, Jaipur, Jaipur, Rajasthan, India, (302006)
     * orderType : HomeDelivery
     * orderStatus : Preparing
     * orderSubTotal : 175.0
     * orderTotal : 175.0
     * couponDiscountAmount : 0.0
     * deliveryFee : 0.0
     * taxAmount : 0.0
     * taxJson : []
     * paymentStatus : Pending
     * driverName: Hemant,
     * driverMobile: 9509787937,
     * itemList : [{"orderItemId":2,"itemId":1,"active":true,"title":"Sandwich","vegNonVeg":"Veg","dineIn":false,"dineInPrice":0,"dineInFinalPrice":0,"deliveryAndTakeAway":false,"deliveryAndTakeAwayPrice":0,"deliveryAndTakeAwayFinalPrice":0,"price":12,"finalPrice":175,"itemTotal":175,"texAmount":0,"productCustomizationOptionJson":"[{\"name\":\"dfg\",\"price\":45.0},{\"name\":\"fgsdfg\",\"price\":45.0}]","totalTaxAmount":0,"imageSize":"SMALL","itemImageUrl":"1571398925953_download.png","recommended":false,"availableDineIn":false,"availableDeliveryAndTakeAway":false,"quantity":1}]
     */

    private long recordId;
    private long orderDateTime;
    private String orderRefId;
    private String restaurantContactNumber;
    private String specialInstruction;
    private String pickUpCompleteAddress;
    private String deliveryCompleteAddress;
    private String orderType;
    private String couponCode;
    private String orderStatus;
    private String restaurantName;
    private double orderSubTotal;
    private double orderTotal;
    private double couponDiscountAmount;
    private double deliveryFee;
    private double packingCharges;
    private double taxAmount;
    private double taxDiscountAmount;
    private double pickUpLatitude;
    private double pickUpLongitude;
    private double deliveryLatitude;
    private double deliveryLongitude;
    private ArrayList<TaxesBean> taxJson;
    private String paymentStatus;
    private String driverName;
    private String driverMobile;
    private int tipAmount;
    private long acceptRejectAtTime;
    private long orderReadyIn;
    private long estimatedTimeArrival;
    private String driverImageUrl;
    private double paidByWallet;
    private double paidOnDelivery;

    private float driverRating;
    private double loyaltyAmount;

    private String roomNumber;
    private String roomType;
    private long checkInDateTimeStamp;
    private long checkOutDateTimeStamp;
    private double bookingSubTotal;

    private String orderId;
    private long totalAmount;

    private String bookingReferenceId;
    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getBookingSubTotal() {
        return bookingSubTotal;
    }

    public void setBookingSubTotal(double bookingSubTotal) {
        this.bookingSubTotal = bookingSubTotal;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public long getCheckInDateTimeStamp() {
        return checkInDateTimeStamp;
    }

    public void setCheckInDateTimeStamp(long checkInDateTimeStamp) {
        this.checkInDateTimeStamp = checkInDateTimeStamp;
    }

    public long getCheckOutDateTimeStamp() {
        return checkOutDateTimeStamp;
    }

    public void setCheckOutDateTimeStamp(long checkOutDateTimeStamp) {
        this.checkOutDateTimeStamp = checkOutDateTimeStamp;
    }

    public double getLoyaltyAmount() {
        return loyaltyAmount;
    }

    public void setLoyaltyAmount(double loyaltyAmount) {
        this.loyaltyAmount = loyaltyAmount;
    }

    public float getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(float driverRating) {
        this.driverRating = driverRating;
    }

    public double getPaidByWallet() {
        return paidByWallet;
    }

    public void setPaidByWallet(double paidByWallet) {
        this.paidByWallet = paidByWallet;
    }

    public double getPaidOnDelivery() {
        return paidOnDelivery;
    }

    public void setPaidOnDelivery(double paidOnDelivery) {
        this.paidOnDelivery = paidOnDelivery;
    }

    public String getRestaurantContactNumber() {
        return restaurantContactNumber;
    }

    public void setRestaurantContactNumber(String restaurantContactNumber) {
        this.restaurantContactNumber = restaurantContactNumber;
    }

    public String getDriverImageUrl() {
        return driverImageUrl;
    }

    public void setDriverImageUrl(String driverImageUrl) {
        this.driverImageUrl = driverImageUrl;
    }

    public long getAcceptRejectAtTime() {
        return acceptRejectAtTime;
    }

    public long getEstimatedTimeArrival() {
        return estimatedTimeArrival;
    }

    public void setEstimatedTimeArrival(long estimatedTimeArrival) {
        this.estimatedTimeArrival = estimatedTimeArrival;
    }

    public void setAcceptRejectAtTime(long acceptRejectAtTime) {
        this.acceptRejectAtTime = acceptRejectAtTime;
    }

    public long getOrderReadyIn() {
        return orderReadyIn;
    }

    public void setOrderReadyIn(long orderReadyIn) {
        this.orderReadyIn = orderReadyIn;
    }

    private ArrayList<OrderDetailsItemBean> itemList;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(long orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderRefId() {
        return orderRefId;
    }

    public void setOrderRefId(String orderRefId) {
        this.orderRefId = orderRefId;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public String getPickUpCompleteAddress() {
        return pickUpCompleteAddress;
    }

    public void setPickUpCompleteAddress(String pickUpCompleteAddress) {
        this.pickUpCompleteAddress = pickUpCompleteAddress;
    }

    public String getDeliveryCompleteAddress() {
        return deliveryCompleteAddress;
    }

    public void setDeliveryCompleteAddress(String deliveryCompleteAddress) {
        this.deliveryCompleteAddress = deliveryCompleteAddress;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public double getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(double orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public double getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(double couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getPackingCharges() {
        return packingCharges;
    }

    public void setPackingCharges(double packingCharges) {
        this.packingCharges = packingCharges;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTaxDiscountAmount() {
        return taxDiscountAmount;
    }

    public void setTaxDiscountAmount(double taxDiscountAmount) {
        this.taxDiscountAmount = taxDiscountAmount;
    }

    public double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public double getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(double deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public double getDeliveryLongitude() {
        return deliveryLongitude;
    }

    public void setDeliveryLongitude(double deliveryLongitude) {
        this.deliveryLongitude = deliveryLongitude;
    }

    public ArrayList<TaxesBean> getTaxJson() {
        return taxJson;
    }

    public void setTaxJson(ArrayList<TaxesBean> taxJson) {
        this.taxJson = taxJson;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public ArrayList<OrderDetailsItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<OrderDetailsItemBean> itemList) {
        this.itemList = itemList;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }


    public String getBookingReferenceId() {
        return bookingReferenceId;
    }

    public void setBookingReferenceId(String bookingReferenceId) {
        this.bookingReferenceId = bookingReferenceId;
    }
}
