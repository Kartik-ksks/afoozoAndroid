package in.kpis.afoozo.bean;

import java.util.List;

public class EventsBean {


    /**
     * recordId : 1
     * uuid : null
     * active : true
     * occasionSecondaryId : null
     * restaurantId : null
     * occasionTitle : New Event
     * description : Event Description
     * startAt : 24-01-2019 20:09
     * endAt : 26-01-2019 20:10
     * contactNumber : 9685214745
     * occasionImage : null
     * occasionImageUrl : https://s3.ap-south-1.amazonaws.com/crypto-pro-bucket/primary-image/1571668872492_personflatx.png
     * paid : false
     * occasionTicket : []
     * occasionAddress : null
     * month : null
     */

    private boolean isHeading;
    private String heading;
    private String sharingContent;
    private int recordId;
    private String uuid;
    private boolean active;
    private Object occasionSecondaryId;
    private long restaurantId;
    private String occasionTitle;
    private String description;
    private long startDateTimeStamp;
    private long endDateTimeStamp;
    private String endAt;
    private String contactNumber;
    private Object occasionImage;
    private String occasionImageUrl;
    private boolean paid;
    private String occasionAddress;
    private String month;
    private double latitude;
    private double longitude;
    private List<?> occasionTicket;

    public EventsBean(boolean isHeading, String heading) {
        this.isHeading = isHeading;
        this.heading = heading;
    }

    public String getSharingContent() {
        return sharingContent;
    }

    public void setSharingContent(String sharingContent) {
        this.sharingContent = sharingContent;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isHeading() {
        return isHeading;
    }

    public void setHeading(boolean heading) {
        isHeading = heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Object getOccasionSecondaryId() {
        return occasionSecondaryId;
    }

    public void setOccasionSecondaryId(Object occasionSecondaryId) {
        this.occasionSecondaryId = occasionSecondaryId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOccasionTitle() {
        return occasionTitle;
    }

    public void setOccasionTitle(String occasionTitle) {
        this.occasionTitle = occasionTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartDateTimeStamp() {
        return startDateTimeStamp;
    }

    public void setStartDateTimeStamp(long startDateTimeStamp) {
        this.startDateTimeStamp = startDateTimeStamp;
    }

    public long getEndDateTimeStamp() {
        return endDateTimeStamp;
    }

    public void setEndDateTimeStamp(long endDateTimeStamp) {
        this.endDateTimeStamp = endDateTimeStamp;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Object getOccasionImage() {
        return occasionImage;
    }

    public void setOccasionImage(Object occasionImage) {
        this.occasionImage = occasionImage;
    }

    public String getOccasionImageUrl() {
        return occasionImageUrl;
    }

    public void setOccasionImageUrl(String occasionImageUrl) {
        this.occasionImageUrl = occasionImageUrl;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getOccasionAddress() {
        return occasionAddress;
    }

    public void setOccasionAddress(String occasionAddress) {
        this.occasionAddress = occasionAddress;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<?> getOccasionTicket() {
        return occasionTicket;
    }

    public void setOccasionTicket(List<?> occasionTicket) {
        this.occasionTicket = occasionTicket;
    }
}
