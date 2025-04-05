package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class LocalCartBean implements Serializable {

    public static final String TABLE_NAME = "CartItems";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RESTAURANT_ID = "restaurantId";
    public static final String COLUMN_CATEGORY_ID = "categoryId";
    public static final String COLUMN_ITEM_ID = "itemId";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_INSTRUCTION = "instruction";
    public static final String COLUMN_CUSTOMIZATION = "customization";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_RESTAURANT_ID + " TEXT,"
                    + COLUMN_CATEGORY_ID + " TEXT,"
                    + COLUMN_ITEM_ID + " LONG,"
                    + COLUMN_QTY + " INTEGER,"
                    + COLUMN_PRICE + " DOUBLE,"
                    + COLUMN_INSTRUCTION + " TEXT,"
                    + COLUMN_CUSTOMIZATION + " TEXT"
                    + ")";

    private int id;
    private String restaurantId;
    private String categoryId;
    private long itemId;
    private int qty;
    private double price;
    private String specialInstruction;
    private ArrayList<CustomizationBean> customization;

    public LocalCartBean() {
    }

    public LocalCartBean(String restaurantId, long itemId, int qty, ArrayList<CustomizationBean> customization) {
        this.restaurantId = restaurantId;
        this.itemId = itemId;
        this.qty = qty;
        this.customization = customization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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
