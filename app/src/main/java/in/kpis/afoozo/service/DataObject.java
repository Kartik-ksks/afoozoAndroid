package in.kpis.afoozo.service;

/**
 * Created by KHEMRAJ on 11/16/2018.
 */
public class DataObject {

    private String title;
    private String message;
    private String adminNotification;
    private String orderId;
    private String image;
    private String type;
    private String payloadJson;
    private String timeStamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdminNotification() {
        return adminNotification;
    }

    public void setAdminNotification(String adminNotification) {
        this.adminNotification = adminNotification;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
