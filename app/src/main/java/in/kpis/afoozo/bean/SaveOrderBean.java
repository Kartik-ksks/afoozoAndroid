package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveOrderBean implements Serializable {


    /**
     * restaurantId : 3
     * specialInstruction : Special Instruction
     * addressId : 1
     * itemList : [{"recordId":1,"quantity":0,"customization":[{"recordId":2,"customizationOptions":[{"id":1,"name":"a","price":1},{"id":2,"name":"b","price":2}]},{"recordId":3,"customizationOptions":[{"id":1,"name":"dfg","price":12},{"id":2,"name":"fgsdfg","price":32}]}]}]
     */

    private String restaurantId;
    private String orderType;
    private String tableNumber;
    private String specialInstruction;
    private long addressId;
    private ArrayList<SaveItemsBean> itemList;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public ArrayList<SaveItemsBean> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<SaveItemsBean> itemList) {
        this.itemList = itemList;
    }

}
