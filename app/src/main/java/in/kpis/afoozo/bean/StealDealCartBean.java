package in.kpis.afoozo.bean;

public class StealDealCartBean {

    public static final String TABLE_NAME = "StealDealItems";

    public static final String COLUMN_RESTAURANT_ID = "restaurantId";
    public static final String COLUMN_ITEM_ID = "itemId";
    public static final String COLUMN_ITEM_NAME = "itemName";
    public static final String COLUMN_QTY = "qty";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PRICE = "price";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_RESTAURANT_ID + " TEXT,"
                    + COLUMN_ITEM_ID + " LONG,"
                    + COLUMN_ITEM_NAME + " TEXT,"
                    + COLUMN_QTY + " INTEGER,"
                    + COLUMN_DATE + " INTEGER,"
                    + COLUMN_PRICE + " DOUBLE"
                    + ")";

    private String restaurantId;
    private String itemName;
    private long stealDealItemId;
    private int stealDealItemQty;
    private int date;
    private double price;

    public StealDealCartBean() {
    }

    public StealDealCartBean(String restaurantId, long stealDealItemId, String itemName, int stealDealItemQty, int date, double price) {
        this.restaurantId = restaurantId;
        this.stealDealItemId = stealDealItemId;
        this.itemName = itemName;
        this.stealDealItemQty = stealDealItemQty;
        this.date = date;
        this.price = price;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getStealDealItemId() {
        return stealDealItemId;
    }

    public void setStealDealItemId(long stealDealItemId) {
        this.stealDealItemId = stealDealItemId;
    }

    public int getStealDealItemQty() {
        return stealDealItemQty;
    }

    public void setStealDealItemQty(int stealDealItemQty) {
        this.stealDealItemQty = stealDealItemQty;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
