package in.kpis.afoozo.bean;

public class LastPaymentModeBean {

   private String lastPaymentMode;
    private String lastPaymentType;
    private BankBean bankBean;
    private CardBean CardBean;
    private SavedCardBean savedCard;
    private String key;

    public String getLastPaymentMode() {
        return lastPaymentMode;
    }

    public void setLastPaymentMode(String lastPaymentMode) {
        this.lastPaymentMode = lastPaymentMode;
    }

    public String getLastPaymentType() {
        return lastPaymentType;
    }

    public void setLastPaymentType(String lastPaymentType) {
        this.lastPaymentType = lastPaymentType;
    }

    public SavedCardBean getSavedCard() {
        return savedCard;
    }

    public void setSavedCard(SavedCardBean savedCard) {
        this.savedCard = savedCard;
    }

    public BankBean getBankBean() {
        return bankBean;
    }

    public void setBankBean(BankBean bankBean) {
        this.bankBean = bankBean;
    }

    public CardBean getCardBean() {
        return CardBean;
    }

    public void setCardBean(CardBean cardBean) {
        CardBean = cardBean;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
