package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveItemsBean implements Serializable {

    /**
     * recordId : 1
     * quantity : 0
     * customization : [{"recordId":2,"customizationOptions":[{"id":1,"name":"a","price":1},{"id":2,"name":"b","price":2}]},{"recordId":3,"customizationOptions":[{"id":1,"name":"dfg","price":12},{"id":2,"name":"fgsdfg","price":32}]}]
     */

    private long itemId;
    private int quantity;
    private String specialInstruction;
    private ArrayList<CustomizationBean> customization;

    public SaveItemsBean(long itemId, int quantity, String specialInstruction, ArrayList<CustomizationBean> customization) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.specialInstruction = specialInstruction;
        this.customization = customization;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public ArrayList<CustomizationBean> getCustomization() {
        return customization;
    }

    public void setCustomization(ArrayList<CustomizationBean> customization) {
        this.customization = customization;
    }

}
