package in.kpis.afoozo.bean;

import java.io.Serializable;

public class BannerListBean implements Serializable {


    /**
     * recordId : 3
     * active : true
     * adImageUrlLarge : https://s3.ap-south-1.amazonaws.com/crypto-pro-bucket/primary-image/1572500981549_download.png
     * sliderAction : OfferList
     */

    private long recordId;
    private boolean active;
    private String adImageUrlLarge;
    private String sliderAction;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAdImageUrlLarge() {
        return adImageUrlLarge;
    }

    public void setAdImageUrlLarge(String adImageUrlLarge) {
        this.adImageUrlLarge = adImageUrlLarge;
    }

    public String getSliderAction() {
        return sliderAction;
    }

    public void setSliderAction(String sliderAction) {
        this.sliderAction = sliderAction;
    }
}
