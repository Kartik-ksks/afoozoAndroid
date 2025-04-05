package in.kpis.afoozo.activity;

import java.io.Serializable;

public class AddressBean implements Serializable {


    /**
     * latitude : 26.912434
     * longitude : 75.78727
     * addressLine1 : B-135, Vaishali Nagar
     * addressLine2 :
     * addressLine3 :
     * addressType : Home
     */

    private long recordId;
    private double latitude;
    private double longitude;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressType;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
