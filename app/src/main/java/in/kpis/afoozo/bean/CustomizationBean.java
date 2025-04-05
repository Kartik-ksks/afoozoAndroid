package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomizationBean implements Serializable {

    /**
     * recordId : 2
     * active : true
     * customizationType : SingleSelection
     * customizationOptions : [{"id":1,"name":"a","price":1},{"id":2,"name":"b","price":2}]
     */

    private long recordId;
    private boolean active;
    private String customizationType;
    private String title;
    private long minSelection;
    private long maxSelection;

    public long getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(long minSelection) {
        this.minSelection = minSelection;
    }

    public long getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(long maxSelection) {
        this.maxSelection = maxSelection;
    }

    private ArrayList<CustomizationOptionsBean> customizationOptions;

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

    public String getCustomizationType() {
        return customizationType;
    }

    public void setCustomizationType(String customizationType) {
        this.customizationType = customizationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<CustomizationOptionsBean> getCustomizationOptions() {
        return customizationOptions;
    }

    public void setCustomizationOptions(ArrayList<CustomizationOptionsBean> customizationOptions) {
        this.customizationOptions = customizationOptions;
    }

}
