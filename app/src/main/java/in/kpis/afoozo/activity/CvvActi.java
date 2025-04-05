package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.SavedCardBean;
import com.kpis.afoozo.databinding.ActivityCvvBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;

public class CvvActi extends AppCompatActivity {

    private static final int SAVED_CARD_PAYMENT_REQUEST_CODE = 2001;
    private Toolbar toolbar;

    private Context mContext;

    private ActivityCvvBinding binding;

    private int count;

    private String cvv;

    private String paymentType;
    private String paymentMode;
    private SavedCardBean savedCardBean;
    private double totalPrice;
    private String placeOrderJson;
    private String paymentRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cvv);

        mContext = CvvActi.this;
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.btnCvv.setEnabled(false);
        binding.btnCvv.setBackgroundResource(R.drawable.grey_rounded_btn_bg);

        if(getIntent().getStringExtra(Constants.PAYMENT_MODE) != null &&
                getIntent().getStringExtra(Constants.PAYMENT_TYPE) != null &&
//                getIntent().getStringExtra(Constants.PLACE_ORDER_JSON) != null &&
                getIntent().getSerializableExtra(Constants.SAVED_CARD_DATA) != null &&
                getIntent().getDoubleExtra(Constants.TOTAL_AMOUNT,0) != 0){

            paymentMode = getIntent().getStringExtra(Constants.PAYMENT_MODE);
            paymentType= getIntent().getStringExtra(Constants.PAYMENT_TYPE);
//            placeOrderJson= getIntent().getStringExtra(Constants.PLACE_ORDER_JSON);
            paymentRequestId= getIntent().getStringExtra(Constants.PAYMENT_REQUEST_ID);
            totalPrice= getIntent().getDoubleExtra(Constants.TOTAL_AMOUNT,0);
            savedCardBean = (SavedCardBean) getIntent().getSerializableExtra(Constants.SAVED_CARD_DATA);
        }

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.etCVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() >2){
                    binding.btnCvv.setEnabled(true);
                    binding.btnCvv.setBackgroundResource(R.drawable.yellow_rounded_btn_bg);
                }else{
                    binding.btnCvv.setEnabled(false);
                    binding.btnCvv.setBackgroundResource(R.drawable.grey_rounded_btn_bg);
                }
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.cvv));
    }

    public void cardSaveProcess(View view){
        cvv = binding.etCVV.getText().toString();
        if (Utils.isValidCVV(mContext, cvv)){
            savedCardBean.setCvv(cvv);
            goToPaymentScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SAVED_CARD_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK){

            if(data.getStringExtra(Constants.TRANSACTION_ID) != null){
                Intent intent = new Intent();
                intent.putExtra(Constants.TRANSACTION_ID, data.getStringExtra(Constants.TRANSACTION_ID));
                intent.putExtra(Constants.ORDER_ID, orderId);
                intent.putExtra(Constants.NICK_NAME, "NaN");
                if(data.getStringExtra(Constants.TRANSACTION_ID).equals("")){
                    setResult(RESULT_CANCELED, intent);
                }else{
                    setResult(RESULT_OK, intent);
                }
            }
            finish();
        }

    }

    String orderId;

    private void callPlaceOrderApi(String json) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        orderId = (String) responseClass.getResponsePacket();
                        savedCardBean.setCvv(cvv);
                        goToPaymentScreen();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PLACE_ORDER, json, "Loading...", true, AppUrls.REQUESTTAG_PLACEORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void goToPaymentScreen(){
        Intent intent = new Intent(mContext, PaymentActi.class);

        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.SAVED_CARD_DATA, savedCardBean);
        intent.putExtra(Constants.IS_SAVED_CARD, true);
        intent.putExtra(Constants.TOTAL_AMOUNT, totalPrice);
        intent.putExtra(Constants.PAYMENT_REQUEST_ID, paymentRequestId);
        startActivityForResult(intent, SAVED_CARD_PAYMENT_REQUEST_CODE);
    }
}
