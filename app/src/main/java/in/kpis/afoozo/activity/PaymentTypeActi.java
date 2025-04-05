package in.kpis.afoozo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;

import in.kpis.afoozo.adapter.BankListAdapter;
import in.kpis.afoozo.adapter.PaymentGetWayAdapter;
import in.kpis.afoozo.adapter.SavedCardAdapter;
import in.kpis.afoozo.bean.BankBean;
import in.kpis.afoozo.bean.CardBean;
import in.kpis.afoozo.bean.SavedCardBean;

import com.kpis.afoozo.databinding.ActivityPaymentTypeBinding;

import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.DividerItemDecorationGray;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import com.razorpay.PaymentResultListener;
import com.razorpay.Razorpay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;


import static android.view.View.GONE;

public class PaymentTypeActi extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityPaymentTypeBinding binding;

//    Razorpay razorpay;

    private ArrayList<BankBean> bankList;
    private ArrayList<SavedCardBean> savedCardList;
    private ArrayList<String> paymentTypeList = new ArrayList<>();
    private ArrayList<String> paymentallTypeList;

    private int CARD_REQUEST_CODE = 1007;

    private CardBean cardDetailsBean;
    private SavedCardAdapter mSaveAdapter;
    private String fromWhich = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PaymentTypeActi.this, R.layout.activity_payment_type);

        mContext = PaymentTypeActi.this;


//        razorpay = new Razorpay(this);
        binding.webView.setVisibility(View.GONE);
//        razorpay.setWebView(binding.webView);
        if (getIntent().getDoubleExtra(Constants.TOTAL_AMOUNT, 0) != 0) {
            binding.txtTotalAmount.setText("Pay " + getString(R.string.Rs) + " " + getIntent().getDoubleExtra(Constants.TOTAL_AMOUNT, 0));
        }
//        if (getIntent().getStringExtra(Constants.FROM_WHICH) != null && getIntent().getStringExtra(Constants.FROM_WHICH).equals(getString(R.string.wallet))) {
        if (getIntent().getStringExtra(Constants.FROM_WHICH) != null ) {
            String which = getIntent().getStringExtra(Constants.FROM_WHICH);
            if (which.equals(Constants.DELIVERY))
                fromWhich = Constants.HOME_DELIVERY;
            else if (which.equals("Dine In")) {
                fromWhich = "DineIn";
            } else if (which.equals("Take Away")) {
                fromWhich = "TakeAway";
            } else if (which.equals("Wallet")) {
                fromWhich = "Wallet";
            }else if (which.equals(getString(R.string.cafe))) {
                fromWhich = Constants.CAFE;
            } else {
            }

            binding.llCash.setVisibility(GONE);
        } else
            binding.llCash.setVisibility(View.VISIBLE);

        initialize();
        Utils.progressDialog(mContext, "");

        /*razorpay.getPaymentMethods(new Razorpay.PaymentMethodsCallback() {
            @Override
            public void onPaymentMethodsReceived(String result) {
                try {
                    JSONObject paymentMethods = new JSONObject(result);
                    Log.e("PAYMENT METHODS--", paymentMethods.toString());
                    JSONObject obj = paymentMethods.getJSONObject("netbanking");

                    bankList = new ArrayList<>();

                    Iterator<String> iter = obj.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = obj.get(key);
                            BankBean bankBean = new BankBean(key, (String) obj.get(key));
                            bankList.add(bankBean);
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Utils.dismissProgressDialog();
            }

            @Override
            public void onError(String error) {

                if (error != null)
                    Log.e("ERROR---", error);
                Utils.dismissProgressDialog();
            }
        });*/

        setLogo();

//        callPaytmApi();
    }

    private void setLogo() {
       /* Utils.setImage(mContext, binding.imvPaytmW, razorpay.getWalletSqLogoUrl("paytm"));
        Utils.setImage(mContext, binding.imvPayZApp, razorpay.getWalletSqLogoUrl("payzapp"));
        Utils.setImage(mContext, binding.imvOlaMoney, razorpay.getWalletSqLogoUrl("olamoney"));
        Utils.setImage(mContext, binding.imvPhonepeW, razorpay.getWalletSqLogoUrl("phonepe"));
        Utils.setImage(mContext, binding.imvFreechargeW, razorpay.getWalletSqLogoUrl("freecharge"));*/
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
                if (binding.getShowBankList())
                    binding.setShowBankList(false);
                else
                    finish();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.choose_payment_mode));

        binding.rvSavedCards.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvSavedCards.setItemAnimator(new DefaultItemAnimator());
        binding.rvSavedCards.addItemDecoration(new DividerItemDecorationGray(mContext));
        binding.rvSavedCards.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goToPreviousScreen(getString(R.string.online), getString(R.string.credit_debit_atm_card), savedCardList.get(position));

            }
        }));


        binding.rvBank.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvBank.setItemAnimator(new DefaultItemAnimator());
        binding.rvBank.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goToPreviousScreen(getString(R.string.online), getString(R.string.netbanking), bankList.get(position));
            }
        }));

        binding.rvPaymentType.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvPaymentType.setItemAnimator(new DefaultItemAnimator());
        binding.rvPaymentType.addItemDecoration(new DividerItemDecorationGray(mContext));
        binding.rvPaymentType.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goToPreviousScreen(getString(R.string.cash), paymentTypeList.get(position));
            }
        }));

        callSavedCardApi();
        callPaymentTypeApi();
        binding.llCOD.setOnClickListener(this);
    }

    private void walletPaytm() {
        try {
            JSONObject data = new JSONObject();
            data.put("amount", 100);
            data.put("currency", "INR");
            data.put("email", "gaurav.kumar@example.com");
            data.put("contact", "9123456789");
//            data.put("method", "netbanking");
            data.put("method", "wallet");
//            data.put("bank", "BBKM");
            data.put("wallet", "freecharge");

            // Make webview visible before submitting payment details
            binding.webView.setVisibility(View.VISIBLE);
            binding.nsvMain.setVisibility(GONE);

       /*     razorpay.submit(data, new PaymentResultListener() {
                @Override
                public void onPaymentSuccess(String razorpayPaymentId) {
                    // Razorpay payment ID is passed here after a successful payment
                    Log.e("RESPONSE---", razorpayPaymentId);
                    binding.webView.setVisibility(View.GONE);
                    binding.nsvMain.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPaymentError(int code, String description) {
                    Log.e("ERROR---", description);
                    // Error code and description is passed here
                    binding.webView.setVisibility(View.GONE);
                    binding.nsvMain.setVisibility(View.VISIBLE);
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPaytm:
                goToPreviousScreen(getString(R.string.wallet), getString(R.string.paytm));
                break;
            case R.id.llOlaMoney:
                goToPreviousScreen(getString(R.string.wallet), getString(R.string.ola_money));
                break;
            case R.id.llPayZApp:
                goToPreviousScreen(getString(R.string.wallet), getString(R.string.pay_z_app));
                break;
            case R.id.llPhonePe:
                goToPreviousScreen(getString(R.string.wallet), getString(R.string.phone_pe));
                break;
            case R.id.llFreeCharge:
                goToPreviousScreen(getString(R.string.wallet), getString(R.string.freecharge));
                break;
            case R.id.llCreditDebit:
                goToCardScreen();
                break;
            case R.id.llNetBanking:
                showBankList();
                break;
            case R.id.llCOD:
                goToPreviousScreen(getString(R.string.cash), getString(R.string.cash_on_delivery));
                break;
            case R.id.txtCaseONDelivery:
                goToPreviousScreen(getString(R.string.cash), getString(R.string.cash_on_delivery));
                break;
            case R.id.llBillToCompany:
                goToPreviousScreen(getString(R.string.cash), Constants.BILL_TO_COMPANY);
                break;

            case R.id.txtBilltoCompany:
                goToPreviousScreen(getString(R.string.cash), getString(R.string.bill_to_company));
                break;
//            case R.id.llPayUmoney:
//                goToPreviousScreen(getString(R.string.cash), getString(R.string.pay_u_money));
//                break;
        }
    }

    private void goToCardScreen() {
        Intent intent = new Intent(mContext, CardActi.class);
        startActivityForResult(intent, CARD_REQUEST_CODE);
    }

    private void showBankList() {
     /*   BankListAdapter mAdapter = new BankListAdapter(mContext, bankList, razorpay);
        binding.rvBank.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        binding.setShowBankList(true);*/
    }

    private void showSavedCards() {
        mSaveAdapter = new SavedCardAdapter(mContext, savedCardList);
        binding.rvSavedCards.setAdapter(mSaveAdapter);
        mSaveAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                cardDetailsBean = (CardBean) data.getSerializableExtra(Constants.CARD_DATA);
                goToPreviousScreen(getString(R.string.online), getString(R.string.credit_debit_atm_card), cardDetailsBean);
            }
        }
    }

    /**
     * Go To Cart Screen With data
     *
     * @param paymentMode = payment mode like wallet, online or cash
     * @param paymentType = payment type like paytm, phonepe, credit card
     */
    private void goToPreviousScreen(String paymentMode, String paymentType) {
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.IS_SAVED_CARD, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void goToPreviousScreen(String paymentMode, String paymentType, BankBean bankBean) {
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.BANK_BEAN, bankBean);
        intent.putExtra(Constants.IS_SAVED_CARD, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void goToPreviousScreen(String paymentMode, String paymentType, CardBean cardBean) {
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.CARD_DATA, cardBean);
        intent.putExtra(Constants.IS_SAVED_CARD, false);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void goToPreviousScreen(String paymentMode, String paymentType, SavedCardBean savedCardBean) {
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.SAVED_CARD_DATA, savedCardBean);
        intent.putExtra(Constants.IS_SAVED_CARD, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void callSavedCardApi() {
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<SavedCardBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<SavedCardBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        savedCardList = responseClass1.getResponsePacket();

                        if (savedCardList != null && savedCardList.size() > 0) {
                            binding.llSavedCard.setVisibility(View.VISIBLE);
                            showSavedCards();
                        } else
                            binding.llSavedCard.setVisibility(GONE);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETSAVEDCARDS, "", "Loading...", true, AppUrls.REQUESTTAG_GETSAVEDCARDS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* this api use for th payment type list */

    private void callPaymentTypeApi() {
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Type objType = new TypeToken<ResponseClass<ArrayList<SavedCardBean>>>() {
//                        }.getType();
//                        ResponseClass<ArrayList<SavedCardBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        paymentallTypeList = (ArrayList<String>) responseClass.getResponsePacket();

                        if (paymentallTypeList != null && paymentallTypeList.size() > 0) {
//                            binding.llSavedCard.setVisibility(View.VISIBLE);
                            setDatapaymentType();
                        } else
                            binding.llSavedCard.setVisibility(GONE);
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_PAYMENTGATEWAYLIST_V1 + fromWhich, "", "Loading...", true, AppUrls.REQUESTTAG__GETPAYMENTGATEWAYLIST_V1);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setDatapaymentType() {
        for (int i = 0; i <paymentallTypeList.size() ; i++) {
            if(!paymentallTypeList.get(i).equalsIgnoreCase("Wallet")){
                paymentTypeList.add(paymentallTypeList.get(i));
            }
        }

        PaymentGetWayAdapter paymentTypeAdapter = new PaymentGetWayAdapter(mContext, paymentTypeList);
        binding.rvPaymentType.setAdapter(paymentTypeAdapter);
        paymentTypeAdapter.notifyDataSetChanged();

    }

//    private void callPaytmApi(){
//
//        try {
//            new NetworkManager(VersionBean.class, new NetworkManager.OnCallback<VersionBean>() {
//                @Override
//                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
//                    if (responseClass.isSuccess()){
//                        VersionBean versionBean = (VersionBean) responseClass.getResponsePacket();
//                        if (versionBean.isValues())
//                            binding.llPaytm.setVisibility(View.VISIBLE);
//                        else
//                            binding.llPaytm.setVisibility(GONE);
//
//                    } else {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(boolean success, String response, int which) {
//                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
//                }
//            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.PAYTM_VISIBLE, "", "Loading...", true, AppUrls.REQUESTTAG_PAYTMVISIBLE);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }


}
