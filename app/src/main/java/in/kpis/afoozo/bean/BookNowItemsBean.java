package in.kpis.afoozo.bean;

public class BookNowItemsBean {


    /**
     * recordId : 1
     * active : false
     * title : Jack Daniel's
     * categoryTitle : Drinks
     * price : 6000.0
     * imageUrl : https://s3.ap-south-1.amazonaws.com/afoozo-pro-bucket/thumbnail-image/1575966304654_Gx.jpg
     */

    private long recordId;
    private boolean active;
    private String title;
    private String sticker ;
    private String categoryTitle;
    private double price;
    private String imageUrl;
    private String backgroundImageUrl;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }
}
