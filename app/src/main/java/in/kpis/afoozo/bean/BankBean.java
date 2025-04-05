package in.kpis.afoozo.bean;

import java.io.Serializable;

public class BankBean implements Serializable{

    private String key;
    private String bankName;

    public BankBean(String key, String bankName) {
        this.key = key;
        this.bankName = bankName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
