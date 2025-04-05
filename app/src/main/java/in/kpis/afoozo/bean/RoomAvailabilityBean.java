package in.kpis.afoozo.bean;

public class RoomAvailabilityBean {

    private long restaurantRecordId;
    private String roomType;
    /**
     * recordId : 1
     * active : true
     * roomNumber : 1
     * roomPrice : 500.0
     * roomBlocked : false
     */

    private int recordId;
    private boolean active;
    private String roomNumber;
    private double roomPrice;
    private boolean roomBlocked;


    public long getRestaurantRecordId() {
        return restaurantRecordId;
    }

    public void setRestaurantRecordId(long restaurantRecordId) {
        this.restaurantRecordId = restaurantRecordId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public boolean isRoomBlocked() {
        return roomBlocked;
    }

    public void setRoomBlocked(boolean roomBlocked) {
        this.roomBlocked = roomBlocked;
    }
}
