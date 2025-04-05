package in.kpis.afoozo.bean;

import com.google.gson.annotations.SerializedName;

public class StealDealRequestBean {


    /**
     * restaurantUuid : 39c16c22-956b-4412-84e0-44659cac995a
     * categoryId : 1
     * start : 0
     * length : 100
     * searchKey :
     */

    @SerializedName("restaurantId")
    private String restaurantId;
    private long categoryId;
    private int start;
    private int length;
    private String searchKey;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
