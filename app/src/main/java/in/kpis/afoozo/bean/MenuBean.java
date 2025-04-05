package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuBean implements Serializable {


    /**
     * categoryUuid : c5b75eaf-d13b-4e72-be42-b78b3abdad5e
     * categoryName : Parent One
     * categoryViewType : Vertical
     * menuList : [{"recordId":1,"uuid":"f05a57a1-a0cc-4618-91e3-b468b2c3547c","active":true,"title":"Drinks","categoryUuid":"c5b75eaf-d13b-4e72-be42-b78b3abdad5e","subCategoryTitle":"","vegNonVeg":"Veg","dineIn":false,"dineInPrice":0,"dineInFinalPrice":12,"deliveryAndTakeAway":false,"deliveryAndTakeAwayPrice":0,"deliveryAndTakeAwayFinalPrice":12,"price":34,"finalPrice":0,"imageSize":"SMALL","recommended":false,"availableDineIn":false,"availableDeliveryAndTakeAway":false,"customization":[{"recordId":2,"active":true,"customizationType":"SingleSelection","customizationOptions":[{"id":1,"name":"a","price":1},{"id":2,"name":"b","price":2}]},{"recordId":3,"active":true,"customizationType":"MultiSelection","customizationOptions":[{"id":1,"name":"dfg","price":12},{"id":2,"name":"fgsdfg","price":32}]}],"quantity":0}]
     */

    private int subCatPosition;
    private boolean expended;
    private String categoryUuid;
    private String categoryName;
    private String categoryViewType;
    private ArrayList<MenuListBean> menuList;
    private ArrayList<MenuBean> subCategoryList;

    public int getSubCatPosition() {
        return subCatPosition;
    }

    public void setSubCatPosition(int subCatPosition) {
        this.subCatPosition = subCatPosition;
    }

    public boolean isExpended() {
        return expended;
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryViewType() {
        return categoryViewType;
    }

    public void setCategoryViewType(String categoryViewType) {
        this.categoryViewType = categoryViewType;
    }

    public ArrayList<MenuListBean> getMenuList() {
        return menuList;
    }

    public void setMenuList(ArrayList<MenuListBean> menuList) {
        this.menuList = menuList;
    }

    public ArrayList<MenuBean> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(ArrayList<MenuBean> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
}
