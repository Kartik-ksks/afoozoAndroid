package in.kpis.afoozo.bean;

public class VersionBean {


    /**
     * confidential : false
     * version : 1
     * forceUpdate : false
     */

    private boolean confidential;
    private String version;
    private boolean forceUpdate;

    public boolean isConfidential() {
        return confidential;
    }

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
