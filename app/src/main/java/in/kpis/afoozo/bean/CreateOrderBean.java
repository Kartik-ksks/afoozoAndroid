package in.kpis.afoozo.bean;

import android.text.TextUtils;

import java.io.Serializable;

import in.kpis.afoozo.util.Utils;

/**
 * Created by Hemant Jangid on 9/5/2018.
 */
public class CreateOrderBean implements Serializable {
    private String payment_session_id;
    private String order_id;
    private String transactionId;

    public String getPayment_session_id() {
        return payment_session_id;
    }

    public void setPayment_session_id(String payment_session_id) {
        this.payment_session_id = payment_session_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
