package in.kpis.afoozo.bean;

import android.text.TextUtils;

import in.kpis.afoozo.util.Utils;

import java.io.Serializable;

/**
 * Created by Hemant Jangid on 9/5/2018.
 */
public class UserBean implements Serializable {


    /**
     * fullName : Shashi Baheti
     * userImage : https://s3.ap-south-1.amazonaws.com/crypto-pro-bucket/primary-image/1571386953822_MushroomSupericon.png
     * mobile : 9460818386
     * mobileVerified : true
     * email : shashi@kpis.in
     * emailVerified : false
     * gender : MALE
     * dateOfBirth : 644437800000
     * secretKey : 934ec8ff161bc08095f929b3ff24f2f0
     */

    private String uuid;
    private String fullName;
    private String userImage;
    private String facebookUserId;
    private String facebookUserAccessToken;
    private String mobile;
    private boolean mobileVerified;
    private String email;
    private boolean emailVerified;
    private String gender;
    private long dateOfBirth;
    private long anniversaryDate;
    private String dobString;
    private String anniversaryString;
    private String secretKey;
    private String razorPayCustomerId;
    private boolean createCustomerOnPaymentGateway;
    private String recordId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRazorPayCustomerId() {
        return razorPayCustomerId;
    }

    public void setRazorPayCustomerId(String razorPayCustomerId) {
        this.razorPayCustomerId = razorPayCustomerId;
    }

    public boolean isCreateCustomerOnPaymentGateway() {
        return createCustomerOnPaymentGateway;
    }

    public void setCreateCustomerOnPaymentGateway(boolean createCustomerOnPaymentGateway) {
        this.createCustomerOnPaymentGateway = createCustomerOnPaymentGateway;
    }

    public String getFullName() {
        if (!TextUtils.isEmpty(fullName))
            return fullName;
        else
            return "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserImage() {
        if (!TextUtils.isEmpty(userImage))
            return userImage;
        else
            return "";
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public void setAnniversaryString(String anniversaryString) {
        this.anniversaryString = anniversaryString;
    }

    public String getFacebookUserAccessToken() {
        return facebookUserAccessToken;
    }

    public void setFacebookUserAccessToken(String facebookUserAccessToken) {
        this.facebookUserAccessToken = facebookUserAccessToken;
    }

    public String getMobile() {
        if (!TextUtils.isEmpty(mobile))
            return mobile;
        else
            return "";
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getEmail() {
        if (!TextUtils.isEmpty(email))
            return email;
        else
            return "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getGender() {
        if (!TextUtils.isEmpty(gender))
            return gender;
        else
            return "MALE";
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDobString(){
        if (dateOfBirth != 0)
            return Utils.getFormetedDate(dateOfBirth);
        else
            return "dd/mm/yyyy";

    }

    public String getAnniversaryString(){
        if (anniversaryDate != 0)
            return Utils.getFormetedDate(anniversaryDate);
        else
            return "dd/mm/yyyy";

    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public long getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(long anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }

    public void setDobString(String dobString) {
        this.dobString = dobString;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
