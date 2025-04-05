package in.kpis.afoozo.bean;

import java.io.Serializable;

public class OrdersBean implements Serializable {



    /**
     * orderReferenceId : ORD-00017
     * restaurantName : Afoozo Ambala
     * deliveryAddress : B/136, Shivraj Niketan Colony, Vaishali Nagar, Jaipur, Rajasthan 302021, India,
     * orderTotal : 10582.5
     * orderItemText : Chicken Steak With BBQ x 2
     * orderDateTime : 1573653508000
     * orderStatus : Preparing
     */

    private String orderReferenceId;
    private String restaurantName;
    private String deliveryAddress;
    private double orderTotal;
    private String orderItemText;
    private String itemName;
    private int buyingQty;
    private String consumableUnit;
    private String consumableUnitPostFix;
    private String buyingUnit;
    private String tableNumber;
    private int tipAmount;
    private long orderDateTime;
    private String orderStatus;
    private boolean driverRated;
    private String orderType;


    public String getOrderReferenceId() {
        return orderReferenceId;
    }

    public void setOrderReferenceId(String orderReferenceId) {
        this.orderReferenceId = orderReferenceId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderItemText() {
        return orderItemText;
    }

    public void setOrderItemText(String orderItemText) {
        this.orderItemText = orderItemText;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getBuyingQty() {
        return buyingQty;
    }

    public void setBuyingQty(int buyingQty) {
        this.buyingQty = buyingQty;
    }

    public String getConsumableUnit() {
        return consumableUnit;
    }

    public void setConsumableUnit(String consumableUnit) {
        this.consumableUnit = consumableUnit;
    }

    public String getConsumableUnitPostFix() {
        return consumableUnitPostFix;
    }

    public void setConsumableUnitPostFix(String consumableUnitPostFix) {
        this.consumableUnitPostFix = consumableUnitPostFix;
    }

    public String getBuyingUnit() {
        return buyingUnit;
    }

    public void setBuyingUnit(String buyingUnit) {
        this.buyingUnit = buyingUnit;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public long getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(long orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isDriverRated() {
        return driverRated;
    }

    public void setDriverRated(boolean driverRated) {
        this.driverRated = driverRated;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
