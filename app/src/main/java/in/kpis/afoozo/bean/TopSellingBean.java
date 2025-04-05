package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class TopSellingBean {


    private String homeDeliveryTitle;
    private String takeAwayTitle;
    private String dineInTitle;
    private ArrayList<MenuListBean> homeDeliveryTopSellingItem;
    private ArrayList<MenuListBean> takeAwayTopSellingItem;
    private ArrayList<MenuListBean> dineInTopSellingItem;

    public String getHomeDeliveryTitle() {
        return homeDeliveryTitle;
    }

    public void setHomeDeliveryTitle(String homeDeliveryTitle) {
        this.homeDeliveryTitle = homeDeliveryTitle;
    }

    public String getTakeAwayTitle() {
        return takeAwayTitle;
    }

    public void setTakeAwayTitle(String takeAwayTitle) {
        this.takeAwayTitle = takeAwayTitle;
    }

    public String getDineInTitle() {
        return dineInTitle;
    }

    public void setDineInTitle(String dineInTitle) {
        this.dineInTitle = dineInTitle;
    }

    public ArrayList<MenuListBean> getHomeDeliveryTopSellingItem() {
        return homeDeliveryTopSellingItem;
    }

    public void setHomeDeliveryTopSellingItem(ArrayList<MenuListBean> homeDeliveryTopSellingItem) {
        this.homeDeliveryTopSellingItem = homeDeliveryTopSellingItem;
    }

    public ArrayList<MenuListBean> getTakeAwayTopSellingItem() {
        return takeAwayTopSellingItem;
    }

    public void setTakeAwayTopSellingItem(ArrayList<MenuListBean> takeAwayTopSellingItem) {
        this.takeAwayTopSellingItem = takeAwayTopSellingItem;
    }

    public ArrayList<MenuListBean> getDineInTopSellingItem() {
        return dineInTopSellingItem;
    }

    public void setDineInTopSellingItem(ArrayList<MenuListBean> dineInTopSellingItem) {
        this.dineInTopSellingItem = dineInTopSellingItem;
    }
}
