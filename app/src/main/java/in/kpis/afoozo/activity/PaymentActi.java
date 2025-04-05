package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.BankBean;
import in.kpis.afoozo.bean.CardBean;
import in.kpis.afoozo.bean.SavedCardBean;
import in.kpis.afoozo.bean.UserBean;
import com.kpis.afoozo.databinding.ActivityPaymentBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import com.razorpay.ApplicationDetails;
import com.razorpay.PaymentResultListener;
import com.razorpay.Razorpay;
import com.razorpay.RzpUpiSupportedAppsCallback;

import org.json.JSONObject;

import java.util.List;

public class PaymentActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityPaymentBinding binding;

    Razorpay razorpay;

    private BankBean bankBean;
    private CardBean cardData;
    private SavedCardBean savedCardBean;

    private double price;

    private String paymentType;
    private String paymentMode;
    private boolean isSavedCard;

    private String nickName = "";
    private String paymentRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        if (getIntent().getExtras() != null) {
            paymentType = getIntent().getStringExtra(Constants.PAYMENT_TYPE);
            paymentMode = getIntent().getStringExtra(Constants.PAYMENT_MODE);
            paymentRequestId = getIntent().getStringExtra(Constants.PAYMENT_REQUEST_ID);
            price = getIntent().getDoubleExtra(Constants.TOTAL_AMOUNT, 0);
            if (paymentType.equals(getString(R.string.netbanking)))
                bankBean = (BankBean) getIntent().getSerializableExtra(Constants.BANK_BEAN);
            else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                isSavedCard = getIntent().getBooleanExtra(Constants.IS_SAVED_CARD, false);
                if (isSavedCard) {
                    savedCardBean = (SavedCardBean) getIntent().getSerializableExtra(Constants.SAVED_CARD_DATA);
                    if (savedCardBean != null && savedCardBean.getNickName() != null)
                        nickName = savedCardBean.getNickName() != null ? savedCardBean.getNickName() : "";
                } else {
                    cardData = (CardBean) getIntent().getSerializableExtra(Constants.CARD_DATA);
                    if (savedCardBean != null && savedCardBean.getNickName() != null)
                        nickName = cardData.getNickName() != null ? cardData.getNickName() : "";
                }
            }

        }

//        if (paymentType.equals(getString(R.string.credit_debit_atm_card)))

        mContext = PaymentActi.this;

        razorpay = new Razorpay(this);
        binding.webView.setVisibility(View.GONE);
        razorpay.setWebView(binding.webView);
//        razorpay.callNativeIntent("");

        razorpay.getAppsWhichSupportUpi(this, new RzpUpiSupportedAppsCallback() {
            @Override
            public void onReceiveUpiSupportedApps(List<ApplicationDetails> list) {
                // List of upi supported app
            }
        });

//        razorpay = new Razorpay(this,"rzp_test_98xs2S8EA3rYLc");
//        razorpay.submit();

        walletPaytm();
    }

    private void walletPaytm() {
        UserBean user = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);
        try {
            JSONObject data = new JSONObject();
            data.put("amount", (int) (price * 100));
            data.put("currency", "INR");
            data.put("email", user.getEmail());
            data.put("contact", user.getMobile());
            JSONObject notes = new JSONObject();
            notes.put("shopping_order_id", paymentRequestId);
            data.put("notes", notes);

//            JSONArray prefAppsJArray = new JSONArray();
//            prefAppsJArray.put("in.org.npci.upiapp");
//
//            JSONArray otherAppsJArray = new JSONArray();
//            otherAppsJArray.put("com.google.android.apps.nbu.paisa.user");
////        otherAppsJArray.put("com.google.android.apps.nbu.paisa.user&hl=en_IN");
//
//            data.put("method", "upi");
//            data.put("_[flow]", "intent");
//            data.put("preferred_apps_order", prefAppsJArray);
//            data.put("other_apps_order", otherAppsJArray);

            if (paymentMode.equals(getString(R.string.wallet))) {
                data.put("method", "wallet");
                if (paymentType.equals(getString(R.string.paytm)))
                    data.put("wallet", "paytm");
                else if (paymentType.equals(getString(R.string.ola_money)))
                    data.put("wallet", "olamoney");
                else if (paymentType.equals(getString(R.string.pay_z_app)))
                    data.put("wallet", "payzapp");
                else if (paymentType.equals(getString(R.string.phone_pe)))
                    data.put("wallet", "phonepe");
                else if (paymentType.equals(getString(R.string.freecharge)))
                    data.put("wallet", "freecharge");
            } else if (paymentMode.equals(getString(R.string.online))) {
                if (paymentType.equals(getString(R.string.netbanking))) {
                    data.put("method", "netbanking");
                    data.put("bank", bankBean.getKey());
                } else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                    if (isSavedCard) {
                        data.put("card[cvv]", savedCardBean.getCvv());
                        data.put("token", savedCardBean.getTokenId());
                        data.put("customer_id", user.getRazorPayCustomerId());
                        data.put("save", 1);
                    } else {
                        data.put("method", "card");
                        data.put("card[name]", cardData.getName());
                        data.put("card[number]", cardData.getCardNo());
                        data.put("card[expiry_month]", cardData.getExpiryMonth());
                        data.put("card[expiry_year]", cardData.getExpiryYear());
                        data.put("card[cvv]", cardData.getCvv());
                        data.put("customer_id", user.getRazorPayCustomerId());
                        data.put("save", 1);
                    }
                }
            }


            binding.webView.setVisibility(View.VISIBLE);

            razorpay.submit(data, new PaymentResultListener() {
                @Override
                public void onPaymentSuccess(String razorpayPaymentId) {
                    // Razorpay payment ID is passed here after a successful payment
                    Log.e("RESPONSE---", razorpayPaymentId);

                    goToPreviousScreen(razorpayPaymentId, true);
                }

                @Override
                public void onPaymentError(int code, String description) {
                    Log.e("ERROR---", description);
                    // Error code and description is passed here
                    goToPreviousScreen("", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToPreviousScreen(String id, boolean success) {
        Intent intent = new Intent();
        intent.putExtra(Constants.TRANSACTION_ID, id);
        if (nickName.equals("")) {
            intent.putExtra(Constants.NICK_NAME, "NaN");
        } else {
            intent.putExtra(Constants.NICK_NAME, nickName);
        }
        if (success)
            setResult(RESULT_OK, intent);
        else
            setResult(RESULT_CANCELED, intent);
        finish();
    }
}
