package in.kpis.afoozo.bean;

public class ReservationBean {

    /**
     * restaurantId : 0
     * eventId : 2
     * reservedByName : Sunveer
     * reservedByMobile : 8209414432
     * reservedByEmail : kpits.testing@gmail.com
     * reservationDateTime : 1573768800000
     * occasion : Bday
     * numberOfPeople : 10
     * specialInstruction : As a share of incremental deposits, private sector banks cornered more than 60 percent, while public sector banks took in 29 percent of fresh deposits in 2018-19 One of India’s top restaurateurs, Riyaaz Amlani, on over-regulation, GST and replacing the kitchen.
     */

    private long restaurantId;
    private long eventId;
    private String reservedByName;
    private String reservedByMobile;
    private String reservedByEmail;
    private long reservationDateTime;
    private String occasion;
    private int numberOfPeople;
    private String specialInstruction;



    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getReservedByName() {
        return reservedByName;
    }

    public void setReservedByName(String reservedByName) {
        this.reservedByName = reservedByName;
    }

    public String getReservedByMobile() {
        return reservedByMobile;
    }

    public void setReservedByMobile(String reservedByMobile) {
        this.reservedByMobile = reservedByMobile;
    }

    public String getReservedByEmail() {
        return reservedByEmail;
    }

    public void setReservedByEmail(String reservedByEmail) {
        this.reservedByEmail = reservedByEmail;
    }

    public long getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(long reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }
}
