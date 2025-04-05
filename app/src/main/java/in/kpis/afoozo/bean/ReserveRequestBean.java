package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class ReserveRequestBean {


    /**
     * restaurantId : a2bd6fe2-e374-42b0-b449-90cbc60efb63
     * stealDealItemIdQtyList : [{"stealDealItemId":68,"stealDealItemQty":1},{"stealDealItemId":69,"stealDealItemQty":2},{"stealDealItemId":70,"stealDealItemQty":3}]
     */

    private String restaurantId;
    private ArrayList<StealDealCartBean> stealDealItemIdQtyList;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public ArrayList<StealDealCartBean> getStealDealItemIdQtyList() {
        return stealDealItemIdQtyList;
    }

    public void setStealDealItemIdQtyList(ArrayList<StealDealCartBean> stealDealItemIdQtyList) {
        this.stealDealItemIdQtyList = stealDealItemIdQtyList;
    }

}
