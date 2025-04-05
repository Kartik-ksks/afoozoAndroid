package in.kpis.afoozo.bean;

public class CheckListBean {


    /**
     * bookingReferenceId : BOOKING-00078
     * restaurantName : Masala Street
     * roomType : SingleOccupancy
     * bookingStatus : Ordered
     * bookingSubTotal : 0
     * couponDiscountAmount : 0
     * taxAmount : 0
     * bookingTotal : 6825
     * orderTotal : 0
     * paidByWallet : 0
     * paidOnDelivery : 0
     * checkInDateTimeStamp : 1624991400000
     * checkOutDateTimeStamp : 1626200999000
     */

    private String bookingReferenceId;
    private String restaurantName;
    private String roomType;
    private String bookingStatus;
    private String roomNumber;
    private int bookingSubTotal;
    private int couponDiscountAmount;
    private int taxAmount;
    private int bookingTotal;
    private int orderTotal;
    private int paidByWallet;
    private int paidOnDelivery;
    private long checkInDateTimeStamp;
    private long checkOutDateTimeStamp;

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBookingReferenceId() {
        return bookingReferenceId;
    }

    public void setBookingReferenceId(String bookingReferenceId) {
        this.bookingReferenceId = bookingReferenceId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public int getBookingSubTotal() {
        return bookingSubTotal;
    }

    public void setBookingSubTotal(int bookingSubTotal) {
        this.bookingSubTotal = bookingSubTotal;
    }

    public int getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(int couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getBookingTotal() {
        return bookingTotal;
    }

    public void setBookingTotal(int bookingTotal) {
        this.bookingTotal = bookingTotal;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(int orderTotal) {
        this.orderTotal = orderTotal;
    }

    public int getPaidByWallet() {
        return paidByWallet;
    }

    public void setPaidByWallet(int paidByWallet) {
        this.paidByWallet = paidByWallet;
    }

    public int getPaidOnDelivery() {
        return paidOnDelivery;
    }

    public void setPaidOnDelivery(int paidOnDelivery) {
        this.paidOnDelivery = paidOnDelivery;
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
}
