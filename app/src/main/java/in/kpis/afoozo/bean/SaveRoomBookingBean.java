package in.kpis.afoozo.bean;

public class SaveRoomBookingBean {

    public long roomId;
    public long restaurantId;
    public String roomType;
    public long checkInDate;
    public long checkOutDate;
    public long checkInDateTimeStamp;
    public long checkOutDateTimeStamp;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public long getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(long checkInDate) {
        this.checkInDate = checkInDate;
    }

    public long getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(long checkOutDate) {
        this.checkOutDate = checkOutDate;
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
