package in.kpis.afoozo.bean;

public class RoomDetailBean {


    /**
     * recordId : 10
     * bookingReferenceId : BOOKING-00010
     * userName : madan yadav
     * userMobile : 7877047085
     * restaurantId : 5
     * restaurantName : Masala Street
     * roomId : 1
     * roomNumber : 1
     * roomType : SingleOccupancy
     * bookingStatus : Saved
     * bookingSubTotal : 500.0
     * couponDiscountAmount : 0.0
     * taxAmount : 25.0
     * bookingTotal : 525.0
     * paymentStatus : Pending
     * paidByWallet : 0.0
     * paidOnDelivery : 0.0
     * guestList : []
     */


        private int recordId;
        private String bookingReferenceId;
        private String userName;
        private String userMobile;
        private int restaurantId;
        private String restaurantName;
        private int roomId;
        private String roomNumber;
        private String roomType;
        private String bookingStatus;
        private double bookingSubTotal;
        private double couponDiscountAmount;
        private double taxAmount;
        private double orderTotal;
        private String paymentStatus;
        private double paidByWallet;
        private double paidOnDelivery;
        private long checkInDateTimeStamp;
        private long checkOutDateTimeStamp;

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
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

    public int getRecordId() {
            return recordId;
        }

        public void setRecordId(int recordId) {
            this.recordId = recordId;
        }

        public String getBookingReferenceId() {
            return bookingReferenceId;
        }

        public void setBookingReferenceId(String bookingReferenceId) {
            this.bookingReferenceId = bookingReferenceId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public int getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(int restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
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

        public double getBookingSubTotal() {
            return bookingSubTotal;
        }

        public void setBookingSubTotal(double bookingSubTotal) {
            this.bookingSubTotal = bookingSubTotal;
        }

        public double getCouponDiscountAmount() {
            return couponDiscountAmount;
        }

        public void setCouponDiscountAmount(double couponDiscountAmount) {
            this.couponDiscountAmount = couponDiscountAmount;
        }

        public double getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(double taxAmount) {
            this.taxAmount = taxAmount;
        }


        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
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

}
