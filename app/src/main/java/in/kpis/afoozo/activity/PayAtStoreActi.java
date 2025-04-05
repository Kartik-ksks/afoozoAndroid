package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityPayAtStoreBinding;

import in.kpis.afoozo.bean.WalletBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

public class PayAtStoreActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityPayAtStoreBinding binding;
    private String restaurantName;

    private double totalBalance;
    private long payAmount;
    private long restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_at_store);

        if (getIntent().getExtras() != null) {
            restaurantName = getIntent().getStringExtra(Constants.RESTAURANT_NAME);
            restaurantId = getIntent().getLongExtra(Constants.RESTAURANT_ID, 0);
        }

        mContext = PayAtStoreActi.this;
        initialize();

        callGetBalanceApi();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbar.activityTitle.setVisibility(View.GONE);
        binding.toolbar.txtAfoozo.setVisibility(View.VISIBLE);

        binding.txtRestName.setText(getString(R.string.enter_amount_to_be_paid_at) + " " + restaurantName);

        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    payAmount = Long.parseLong(s.toString());
                    binding.txtPayAmount.setText("-" + getString(R.string.Rs) + payAmount);
                } else {
                    payAmount = 0;
                    binding.txtPayAmount.setText("-" + getString(R.string.Rs) + payAmount);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.txtPayAmount.setText("-" + getString(R.string.Rs) + 0);
    }

    public void payProcess(View view){
        if (!TextUtils.isEmpty(binding.etAmount.getText().toString()) && payAmount > 0) {
            if (payAmount <= totalBalance)
                callPayAtRestaurntApi();
            else
                Snackbar.make(binding.llMain, getString(R.string.low_balance_msg).replace("_", "" + totalBalance), Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(binding.llMain, getString(R.string.amount_error_msg), Snackbar.LENGTH_SHORT).show();
    }

    public void callGetBalanceApi(){

        try {
            new NetworkManager(WalletBean.class, new NetworkManager.OnCallback<WalletBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {

                    if (responseClass.isSuccess()) {
                        if (responseClass.isSuccess()) {
                            WalletBean    walletBean = (WalletBean) responseClass.getResponsePacket();
                            binding.txtWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(walletBean.getWalletBalance()));
                        }
                    }

                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_COIN_AND_WALLET_BALANCE, "", "Loading...", true, AppUrls.REQUESTTAG_GETCOINANDWALLETBALANCE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callPayAtRestaurntApi(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.RESTAURANT_ID, restaurantId);
            jsonObject.put(Constants.PAYMENT_AMOUNT, payAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {


                    if (responseClass.isSuccess()) {
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                Utils.startActivityWithFinish(mContext, Dashboard.class);
                            }
                        });
                    } else
                        Utils.showCenterToast(mContext,  responseClass.getMessage());
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PAY_AT_RESTAURANT, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_PAYATRESTAURANT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
