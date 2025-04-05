package in.kpis.afoozo.bean;

public class NotiSettingBean {

    /**
     * restaurantId : 39c16c22-956b-4412-84e0-44659cac995a
     * restaurantName : Krishna Padam IT Solutions
     * on : false
     */

    private String restaurantId;
    private String restaurantName;
    private boolean onFlag;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public boolean isOnFlag() {
        return onFlag;
    }

    public void setOnFlag(boolean onFlag) {
        this.onFlag = onFlag;
    }
}
