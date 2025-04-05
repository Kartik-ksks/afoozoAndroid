package in.kpis.afoozo.bean;

public class NotificationListBean {


    /**
     * notificationTitle : Order Processed
     * notificationMessage : Dear Shashi Baheti, Your order with Afoozo Ambala has been delivered.
     Order ID = ORD-00000150
     * notificationType : HomeDelivery
     * notificationTypeId : ORD-00000150
     */

    private String notificationTitle;
    private String notificationMessage;
    private String notificationType;
    private String notificationTypeId;
    private long createdAt;

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(String notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
