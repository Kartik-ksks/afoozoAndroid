package in.kpis.afoozo.bean;

public class PlaceOrderBean {


    /**
     * orderReferenceId : ORD-00009
     * paymentType : COD
     */

    private boolean deliveryOfferApplicable ;
    private String deliveryOfferId ;
    private String orderReferenceId;
    private String paymentType;
    private String specialInstruction;
    private long paidOnDelivery;
    private long paidByWallet;
    private long paidByCoin;
    private long deliveryLaterTimeStamp;
    private long addressId;
    private boolean billToCompany;

    private String bookingReferenceId;

    public String getBookingReferenceId() {
        return bookingReferenceId;
    }

    public void setBookingReferenceId(String bookingReferenceId) {
        this.bookingReferenceId = bookingReferenceId;
    }

    public long getPaidByCoin() {
        return paidByCoin;
    }

    public void setPaidByCoin(long paidByCoin) {
        this.paidByCoin = paidByCoin;
    }

    public long getDeliveryLaterTimeStamp() {
        return deliveryLaterTimeStamp;
    }

    public void setDeliveryLaterTimeStamp(long deliveryLaterTimeStamp) {
        this.deliveryLaterTimeStamp = deliveryLaterTimeStamp;
    }

    public boolean isBillToCompany() {
        return billToCompany;
    }

    public void setBillToCompany(boolean billToCompany) {
        this.billToCompany = billToCompany;
    }

    public boolean isDeliveryOfferApplicable() {
        return deliveryOfferApplicable;
    }

    public void setDeliveryOfferApplicable(boolean deliveryOfferApplicable) {
        this.deliveryOfferApplicable = deliveryOfferApplicable;
    }

    public String getDeliveryOfferId() {
        return deliveryOfferId;
    }

    public void setDeliveryOfferId(String deliveryOfferId) {
        this.deliveryOfferId = deliveryOfferId;
    }

    public String getOrderReferenceId() {
        return orderReferenceId;
    }

    public void setOrderReferenceId(String orderReferenceId) {
        this.orderReferenceId = orderReferenceId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public long getPaidOnDelivery() {
        return paidOnDelivery;
    }

    public void setPaidOnDelivery(long paidOnDelivery) {
        this.paidOnDelivery = paidOnDelivery;
    }

    public long getPaidByWallet() {
        return paidByWallet;
    }

    public void setPaidByWallet(long paidByWallet) {
        this.paidByWallet = paidByWallet;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }
}
