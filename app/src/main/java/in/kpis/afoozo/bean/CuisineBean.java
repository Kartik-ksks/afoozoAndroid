package in.kpis.afoozo.bean;

import java.io.Serializable;

public class CuisineBean implements Serializable {

/*
"cuisineTitle": "North Indian",
        "cuisineImageUrl": "https://s3.ap-south-1.amazonaws.com/afoozo-pro-bucket/thumbnail-image/1591365796070_burger.jpg"

*/

    private boolean selected;
//    private int cuisineId;
    private String cuisineTitle;
    private String cuisineImageUrl;


    public String getCuisineTitle() {
        return cuisineTitle;
    }

    public void setCuisineTitle(String cuisineTitle) {
        this.cuisineTitle = cuisineTitle;
    }

    public String getCuisineImageUrl() {
        return cuisineImageUrl;
    }

    public void setCuisineImageUrl(String cuisineImageUrl) {
        this.cuisineImageUrl = cuisineImageUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
