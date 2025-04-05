package in.kpis.afoozo.bean;

public class StealDealDetailsBean {


    /**
     * active : false
     * title : Jack Daniel's
     * rewardType : Bottle
     * categoryId : 26
     * quantity : 25
     * consumableUnit : 30 ML
     * price : 6000.0
     * buyingUnit : Bottle
     * consumableUnitPostfix : Peg
     * outletId : 3
     * shortDescription : 25 pegs X 30ml
     Offer Price : ₹6000
     Any Deal Purchased will be activated to Consume after 6 hours of Purchasing time.
     * detailedDescription : <p style="height: 100px; font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px;"><strong>Please Read Terms &amp; Condition Carefully before Any Purchase :</strong></p><p style="height: 100px; font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px;">&nbsp;</p><ol style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 11px;"><li data-mce-style="text-align: justify;" style="text-align: justify;">The&nbsp;Item&nbsp;Purchased must be consumed within 6 Months from the Date of Purchase else Deal will expire.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">The Age Limit to Consume Liquor must be 25 years or more.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">The Reserved&nbsp;Item/Offers can not be valid to consume on X-Mas Eve, New Year Eve &amp; any other Special Event Night or Occasions.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">Rights of Admission Reserved.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">If Any Entry Fees /Cover Charges are Applicable to enter the Lounge/Club, the Guest has to pay &amp; cannot deny for the same in any circumstances or even if his E-Wallet/Steal Deal has pending Offers.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">Management Decision will be the final Decision &amp; cannot be further challenged.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">The Liquor Must be Purchased when the guest is inside the premises &amp; should be consumed at the same location from where the offer was reserved.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">Reservations are Mandatory.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">Deal once purchased in not refundable or non adjustable.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">ZERO TOLERANCE POLICY :if any customer create any nonsense in the outlet, Hotel management has full right to cancel his deal without any refund and ban the entry.</li><li data-mce-style="text-align: justify;" style="text-align: justify;">In case the brand purchased in deal is not available due to any reason, customer can consume Substitute brand within same price range.</li></ol>
     * imageUrl : https://s3.ap-south-1.amazonaws.com/afoozo-pro-bucket/thumbnail-image/1575966304654_Gx.jpg
     */

    private boolean active;
    private String title;
    private String rewardType;
    private long categoryId;
    private int quantity;
    private String consumableUnit;
    private double price;
    private String buyingUnit;
    private String consumableUnitPostfix;
    private long outletId;
    private String shortDescription;
    private String detailedDescription;
    private String imageUrl;
    private String backgroundImageUrl;

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

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getConsumableUnit() {
        return consumableUnit;
    }

    public void setConsumableUnit(String consumableUnit) {
        this.consumableUnit = consumableUnit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBuyingUnit() {
        return buyingUnit;
    }

    public void setBuyingUnit(String buyingUnit) {
        this.buyingUnit = buyingUnit;
    }

    public String getConsumableUnitPostfix() {
        return consumableUnitPostfix;
    }

    public void setConsumableUnitPostfix(String consumableUnitPostfix) {
        this.consumableUnitPostfix = consumableUnitPostfix;
    }

    public long getOutletId() {
        return outletId;
    }

    public void setOutletId(long outletId) {
        this.outletId = outletId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
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
