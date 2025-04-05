package in.kpis.afoozo.bean;

import java.io.Serializable;

public class ATMBarHistoryBean implements Serializable {


    /**
     * restaurantName : Ambala
     * tapNo : 0
     * drinkName : Calsberg
     * drinkAmount : 60.0
     * drinkQuantity : 60.0
     * createdAtTimeStamp : 1583228666000
     */

    private String restaurantName;
    private int tapNo;
    private String drinkName;
    private double drinkAmount;
    private double drinkQuantity;
    private long createdAtTimeStamp;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getTapNo() {
        return tapNo;
    }

    public void setTapNo(int tapNo) {
        this.tapNo = tapNo;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public double getDrinkAmount() {
        return drinkAmount;
    }

    public void setDrinkAmount(double drinkAmount) {
        this.drinkAmount = drinkAmount;
    }

    public double getDrinkQuantity() {
        return drinkQuantity;
    }

    public void setDrinkQuantity(double drinkQuantity) {
        this.drinkQuantity = drinkQuantity;
    }

    public long getCreatedAtTimeStamp() {
        return createdAtTimeStamp;
    }

    public void setCreatedAtTimeStamp(long createdAtTimeStamp) {
        this.createdAtTimeStamp = createdAtTimeStamp;
    }
}
