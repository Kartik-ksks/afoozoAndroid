package in.kpis.afoozo.bean;

import java.io.Serializable;

public class ScanQrBean implements Serializable {


    /**
     * restaurantUuid : a2bd6fe2-e374-42b0-b449-90cbc60efb63
     * restaurantRecordId : 3
     * restaurantName : Afoozo Ambala
     * restaurantAddress : Railway Station Road, Ambala Cantt
     * tableNumber : 501
     */

    private String restaurantUuid;
    private long restaurantRecordId;
    private String restaurantName;
    private String restaurantAddress;
    private String tableNumber;
    private int date;

    public String getRestaurantUuid() {
        return restaurantUuid;
    }

    public void setRestaurantUuid(String restaurantUuid) {
        this.restaurantUuid = restaurantUuid;
    }

    public long getRestaurantRecordId() {
        return restaurantRecordId;
    }

    public void setRestaurantRecordId(long restaurantRecordId) {
        this.restaurantRecordId = restaurantRecordId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
