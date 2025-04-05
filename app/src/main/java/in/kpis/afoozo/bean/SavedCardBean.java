package in.kpis.afoozo.bean;

import java.io.Serializable;

public class SavedCardBean implements Serializable {

//     "{\"lastPaymentType\":\"Credit Debit & ATM Cards\"," +
//             "\"lastPaymentMode\":\"Online\"," +
//             "\"savedCard\":" +
//             "\"{\\\"recordId\\\":7," +
//             "\\\"createdAt\\\":\\\"Sep 9, 2019 6:17:59 PM\\\"," +
//             "\\\"systemUserId\\\":60," +
//             "\\\"cardId\\\":\\\"card_DG6cPy6FwTRblr\\\",\\" +
//             "\"tokenId\\\":\\\"token_DG6cPyy2DKH8QC\\\"," +
//             "\\\"entity\\\":\\\"card\\\"," +
//             "\\\"name\\\":\\\"Tggdgg\\\"," +
//             "\\\"nickName\\\":\\\"Business\\\"," +
//             "\\\"last4\\\":\\\"1111\\\"," +
//             "\\\"network\\\":\\\"Visa\\\"," +
//             "\\\"type\\\":\\\"debit\\\"," +
//             "\\\"international\\\":false," +
//             "\\\"emi\\\":false}\"}"

    private String id;//": "card_DEVSTnAnB5lkHY",
    private String entity;//": "card",
    private String name;//": "Test",
    private String last4;//": "1111",
    private String network;//": "Visa",
    private String type;//": "debit",
    private boolean international;//": false,
    private boolean emi;//": false
    private String tokenId;
    private String cvv;
    private String nickName;


    private long recordId;
            private String createdAt;
    private long systemUserId;
            private String cardId;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getSystemUserId() {
        return systemUserId;
    }

    public void setSystemUserId(long systemUserId) {
        this.systemUserId = systemUserId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast4() {
        return last4 != null ? last4 : "";
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getNetwork() {
        return network != null ? network : "";
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    public boolean isEmi() {
        return emi;
    }

    public void setEmi(boolean emi) {
        this.emi = emi;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
