package in.kpis.afoozo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment;
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme;
import com.google.gson.Gson;
//import com.paytm.pgsdk.Log;
//import com.paytm.pgsdk.PaytmOrder;
//import com.paytm.pgsdk.PaytmPGService;
//import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.activity.CartActi;
import in.kpis.afoozo.activity.CvvActi;
import in.kpis.afoozo.activity.PaymentActi;
import in.kpis.afoozo.activity.PaymentTypeActi;
import in.kpis.afoozo.activity.TermsActi;
import in.kpis.afoozo.activity.WalletActi;

import in.kpis.afoozo.bean.BankBean;
import in.kpis.afoozo.bean.BeanPaytmTxnResponse;
import in.kpis.afoozo.bean.CardBean;
import in.kpis.afoozo.bean.CashFreeBean;
import in.kpis.afoozo.bean.CheckSumBean;
import in.kpis.afoozo.bean.CreateOrderBean;
import in.kpis.afoozo.bean.LastPaymentModeBean;
import in.kpis.afoozo.bean.MerchantKeyBean;
import in.kpis.afoozo.bean.OrderDetailsBean;
import in.kpis.afoozo.bean.PayOrderBean;
import in.kpis.afoozo.bean.SavedCardBean;
import in.kpis.afoozo.bean.SodexoBean;
import in.kpis.afoozo.bean.UserBean;

import com.kpis.afoozo.databinding.FragmentAddBinding;
//import com.payumoney.core.PayUmoneySdkInitializer;
//import com.payumoney.core.entity.TransactionResponse;
//import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
//import com.payumoney.sdkui.ui.utils.ResultModel;

import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;

import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
//import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFrag extends Fragment implements View.OnClickListener, CFCheckoutResponseCallback {

    private View view;

    private Context mContext;

    private FragmentAddBinding binding;

    private boolean isSavedCard;

    private int PAYMENT_TYPE_REQUEST_CODE = 101;
    private int PAYMENT_REQUEST_CODE = 102;

    private double remainingAmount;

    private String paymentMode;
    private String paymentType;
    private String paymentRequestId;
    private String transactionId;
    private String nickName = "";
    private String cashFreeSessionId = "";

    private BankBean bankBean;
    private CardBean cardBean;
    private SavedCardBean savedCardBean;

    private CheckSumBean checkSumBean;
    private BeanPaytmTxnResponse beanPaytmTxnResponse;

    private SodexoBean sodexoBean;
    private int SODEXO_PAYMENT_REQUEST_CODE = 106;
    //    private PayUmoneySdkInitializer.PaymentParam.Builder builder;
    private CashFreeBean cashFreeResponse = new CashFreeBean();

    CreateOrderBean createOrderBean = new CreateOrderBean();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false);
        view = binding.getRoot();

        try {
            // Set Callback for payment result
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException exception) {
            Log.d("exceptionDetails", "##" + exception);
            exception.printStackTrace();
        }
        initialize();
        return view;
    }

    private void initialize() {
        binding.btnApplyCode.setOnClickListener(this);
        binding.txt2.setOnClickListener(this);
        binding.txt5.setOnClickListener(this);
        binding.txt10.setOnClickListener(this);
        binding.btnAddAmount.setOnClickListener(this);

//        remainingAmount = ((WalletActi) getActivity()).remainingAmount;
//        if (remainingAmount > 0)
//            binding.etAmount.setText(new DecimalFormat("0").format(remainingAmount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnApplyCode:
                String voucherCode = binding.etVoucher.getText().toString().trim();
                if (!TextUtils.isEmpty(voucherCode))
                    callRedeemVoucherApi(voucherCode);
                break;
            case R.id.txt2:
                addAmount(2000);
                break;
            case R.id.txt5:
                addAmount(5000);
                break;
            case R.id.txt10:
                addAmount(10000);
                break;
            case R.id.btnAddAmount:
                if (!TextUtils.isEmpty(binding.etAmount.getText().toString())) {
                    remainingAmount = Long.parseLong(binding.etAmount.getText().toString());
                    if (remainingAmount > 0)
                        callGeneratePaymentRequestApi();
                }
                break;
        }
    }

    private void addAmount(long add) {
        binding.etAmount.setText(("" + add));
    }

    /**
     * this method is for go to payment type screen
     */
    public void goToPaymentTypeScreen() {
        Intent intent = new Intent(mContext, PaymentTypeActi.class);
        intent.putExtra(Constants.FROM_WHICH, getString(R.string.wallet));
        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        startActivityForResult(intent, PAYMENT_TYPE_REQUEST_CODE);
    }

    /**
     * Go To Pyament Screen for payment
     */
    public void goToPaymentScreen() {
        Intent intent = new Intent(mContext, PaymentActi.class);

        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.PAYMENT_REQUEST_ID, paymentRequestId);
        if (paymentType.equals(getString(R.string.netbanking)))
            intent.putExtra(Constants.BANK_BEAN, bankBean);
        else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
            intent.putExtra(Constants.CARD_DATA, cardBean);
            intent.putExtra(Constants.IS_SAVED_CARD, false);
        }

        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("abe", "ss");
        if (requestCode == PAYMENT_TYPE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            paymentMode = data.getStringExtra(Constants.PAYMENT_MODE);
            paymentType = data.getStringExtra(Constants.PAYMENT_TYPE);
            isSavedCard = data.getBooleanExtra(Constants.IS_SAVED_CARD, false);
            bankBean = null;
            savedCardBean = null;
            cardBean = null;
            if (paymentType.equals(getString(R.string.netbanking))) {
                bankBean = (BankBean) data.getSerializableExtra(Constants.BANK_BEAN);
            } else if (paymentType.equals(getString(R.string.credit_debit_atm_card))) {
                if (isSavedCard) {
                    savedCardBean = (SavedCardBean) data.getSerializableExtra(Constants.SAVED_CARD_DATA);
                } else {
                    cardBean = (CardBean) data.getSerializableExtra(Constants.CARD_DATA);
                }
            } else if (paymentType.equalsIgnoreCase("Paytm")) {
                callGenerateChecksumApi();
            } else if (isSavedCard) {
                goToCvvScreen();
            } else if (paymentType.equals("Sodexo")) {
                callGenerateTransactionLinkApi(paymentRequestId);
            } else {
                callGetTokenApi(paymentRequestId);
            }
        } /*else if (requestCode == CFPaymentService.REQ_CODE) {
            boolean isPayment = false;
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    for (String key : bundle.keySet()) {
                        if (bundle.getString(key) != null && bundle.getString("orderId") != null && bundle.getString("orderAmount") != null
                                && bundle.getString("referenceId") != null && bundle.getString("txStatus") != null) {
                            cashFreeResponse.setOrderId(bundle.getString("orderId"));
                            cashFreeResponse.setOrderAmount(Double.parseDouble(bundle.getString("orderAmount")));
                            cashFreeResponse.setReferenceId(bundle.getString("referenceId"));
                            cashFreeResponse.setTxStatus(bundle.getString("txStatus"));
                            cashFreeResponse.setTxMsg(bundle.getString("txMsg"));
                            android.util.Log.d("key", key + " : " + bundle.getString(key) + "  == " + cashFreeResponse);
                            isPayment = true;
                        }
                    }
                if (isPayment)
                    callUpdatePaymentRequestCashFreeApi();
            }
        } */ else if (requestCode == SODEXO_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            callUpdatePaymentRequestForSodexoApi("Sodexo");
        }
    }

    public void goToCvvScreen() {
        Intent intent = new Intent(mContext, CvvActi.class);
        intent.putExtra(Constants.PAYMENT_MODE, paymentMode);
        intent.putExtra(Constants.PAYMENT_TYPE, paymentType);
        intent.putExtra(Constants.SAVED_CARD_DATA, savedCardBean);
        intent.putExtra(Constants.TOTAL_AMOUNT, remainingAmount);
        intent.putExtra(Constants.IS_SAVED_CARD, true);
        intent.putExtra(Constants.PAYMENT_REQUEST_ID, paymentRequestId);
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    private void callRedeemVoucherApi(String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.VOUCHER_CODE, code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Utils.showCenterToast(mContext, responseClass.getMessage());
                    if (responseClass.isSuccess()) {
                        new Thread(() -> customCleverVoucherEvent(code)).start();

                        binding.etVoucher.setText("");
                        ((WalletActi) getActivity()).callGetBalanceApi();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.REDEEM_VOUCHER, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_REDEEMVOUCHER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGeneratePaymentRequestApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_AMOUNT, remainingAmount);
            jsonObject.put(Constants.GENERATEPAYMENTREQUESTFORORDER, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        paymentRequestId = (String) responseClass.getResponsePacket();
                        goToPaymentTypeScreen();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_PAYMENT_REQUEST, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATEPAYMENTREQUEST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void callUpdatePaymentResuestApi(String paymentGateway) {

        String successTransactionId = "";

//        if(paymentGateway.equalsIgnoreCase("Razorpay"))
        if (paymentGateway.equalsIgnoreCase("PayUMoney"))
            successTransactionId = transactionId;
        else
            successTransactionId = paymentRequestId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_GATEWAY, paymentGateway);
            jsonObject.put(Constants.NICKNAME, nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {

                    if (responseClass.isSuccess()) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                customCleverWalletEvent(binding.etAmount.getText().toString());
                            }
                        }).start();

                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        binding.etAmount.setText("");
                        ((WalletActi) getActivity()).callGetBalanceApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST + successTransactionId, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATEPAYMENTREQUEST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Paytm Code

    private void callGenerateChecksumApi() {
        UserBean userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        CheckSumBean bean = new CheckSumBean();
        bean.setMid(Constants.PAYTM_MID);
        bean.setCallbackUrl("https://securegw.paytm.in/order/process?ORDER_ID=" + paymentRequestId);
//        bean.setCallbackUrl("https://securegw-stage.paytm.in/order/process?ORDER_ID=" + paymentRequestId);
        bean.setOrderId(paymentRequestId);
        bean.setMercunqref("Afoozo" + " " + paymentRequestId);
        bean.setChannelId("WAP");
//        bean.setCustId(Long.parseLong(SharedPref.getUserId(mContext)));
        bean.setPhone(userBean.getMobile());
        bean.setEmail(userBean.getEmail());
        bean.setTxnAmount(new DecimalFormat("0.00").format(remainingAmount));
        bean.setWebsite("DEFAULT");
//        bean.setWebsite("WEBSTAGING");
//        bean.setIndustryTypeId("Retail");
        bean.setIndustryTypeId("Retail109");
//        bean.setType(type);

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(CheckSumBean.class, new NetworkManager.OnCallback<CheckSumBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        checkSumBean = ((CheckSumBean) responseClass.getResponsePacket());
//                        paytmPaymentProcess();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.PG_REDIRECT + paymentRequestId, json, "Loading...", true, AppUrls.REQUESTTAG_PGREDIRECT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callPayOrderByPaytm(PayOrderBean bean) {

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        goToTrackingActi(orderId);
//                        callPlaceOrderApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.VERIFY_TRANSACTION, json, "Loading...", true, AppUrls.REQUESTTAG_VERIFYTRANSACTION);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

//    private void paytmPaymentProcess() {
//        if (checkSumBean != null) {
//            Map<String, String> paramMap = new HashMap<String, String>();
//            paramMap.put("MID", checkSumBean.getMid());
//            // Key in your staging and production MID available in your dashboard
//            paramMap.put("ORDER_ID", paymentRequestId);
//            paramMap.put("CUST_ID", String.valueOf(checkSumBean.getCustId()).replace("-", ""));
//            paramMap.put("MOBILE_NO", checkSumBean.getPhone());
//            paramMap.put("EMAIL", checkSumBean.getEmail());
//            paramMap.put("CHANNEL_ID", checkSumBean.getChannelId());
//            paramMap.put("TXN_AMOUNT", checkSumBean.getTxnAmount());
//            paramMap.put("WEBSITE", checkSumBean.getWebsite());
//            paramMap.put("MERC_UNQ_REF", checkSumBean.getMercunqref());
//            // This is the staging value. Production value is available in your dashboard
//            paramMap.put("INDUSTRY_TYPE_ID", checkSumBean.getIndustryTypeId());
//            // This is the staging value. Production value is available in your dashboard
//            paramMap.put("CALLBACK_URL", checkSumBean.getCallbackUrl());
//            paramMap.put("CHECKSUMHASH", checkSumBean.getChecksumhash());
//
//            PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
//
////            PaytmPGService Service = PaytmPGService.getStagingService();
//            PaytmPGService Service = PaytmPGService.getProductionService();
//
//            Service.initialize(Order, null);
//
//            Service.startPaymentTransaction(mContext, true, true, new PaytmPaymentTransactionCallback() {
//                /*Call Backs*/
//                public void someUIErrorOccurred(String inErrorMessage) {
//                    Toast.makeText(getActivity(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onTransactionResponse(Bundle inResponse) {
//                    if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
//                        getDataFromPaytm(inResponse);
//                        Toast.makeText(getActivity(), "Payment Successful ", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Payment Failed ", Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                public void networkNotAvailable() {
//                    Toast.makeText(getActivity(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
//                }
//
//                public void clientAuthenticationFailed(String inErrorMessage) {
//                    Toast.makeText(getActivity(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
//                    Toast.makeText(getActivity(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
//                }
//
//                public void onBackPressedCancelTransaction() {
//                    Toast.makeText(getActivity(), "Transaction cancelled", Toast.LENGTH_LONG).show();
////                    callCancelTransactionApi();
//                }
//
//                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
//                    Toast.makeText(getActivity(), "Transaction Cancelled", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }

    private void getDataFromPaytm(Bundle inResponse) {
        beanPaytmTxnResponse = new BeanPaytmTxnResponse();

        beanPaytmTxnResponse.setSTATUS("STATUS");
        beanPaytmTxnResponse.setCHECKSUMHASH(inResponse.getString("CHECKSUMHASH"));
        beanPaytmTxnResponse.setBANKNAME(inResponse.getString("BANKNAME"));
        beanPaytmTxnResponse.setORDERID(inResponse.getString("ORDERID"));
        beanPaytmTxnResponse.setTXNAMOUNT(inResponse.getString("TXNAMOUNT"));
        beanPaytmTxnResponse.setTXNDATE(inResponse.getString("TXNDATE"));
        beanPaytmTxnResponse.setMID(inResponse.getString("MID"));
        beanPaytmTxnResponse.setTXNID(inResponse.getString("TXNID"));
        beanPaytmTxnResponse.setRESPCODE(inResponse.getString("RESPCODE"));
        beanPaytmTxnResponse.setPAYMENTMODE(inResponse.getString("PAYMENTMODE"));
        beanPaytmTxnResponse.setBANKTXNID(inResponse.getString("BANKTXNID"));
        beanPaytmTxnResponse.setCURRENCY(inResponse.getString("CURRENCY"));
        beanPaytmTxnResponse.setGATEWAYNAME(inResponse.getString("GATEWAYNAME"));
        beanPaytmTxnResponse.setRESPMSG(inResponse.getString("RESPMSG"));

        PayOrderBean bean = new PayOrderBean();
        bean.setOrderId(paymentRequestId);
//        bean.setUserId(Long.parseLong(SharedPref.getUserId(mContext)));
        bean.setTxnResponse(beanPaytmTxnResponse);

//        callPayOrderByPaytm(bean);
        callUpdatePaymentResuestApi("Paytm");

    }


    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */

//    private void generatePaymentId(String paymentRequestId) {
//        UserBean userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);
//
////        transactionId = System.currentTimeMillis() +"";
//        transactionId =paymentRequestId;
//
//        builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
//
//        builder.setAmount(new DecimalFormat("0.00").format(remainingAmount))                          // Payment amount
//                .setTxnId(paymentRequestId)                                             // Transaction ID
//                .setPhone(userBean.getMobile())                                           // User Phone number
//                .setProductName("Afoozo")                   // Product Name or description
//                .setFirstName(userBean.getFullName().replace(" ", ""))                              // User First name
//                .setEmail(userBean.getEmail())                                            // User Email ID
//                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")                    // Success URL (surl)
//                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")             //Failure URL (furl)
//                .setUdf1("a")
//                .setUdf2("b")
//                .setUdf3("c")
//                .setUdf4("d")
//                .setUdf5("e")
//                .setUdf6("")
//                .setUdf7("")
//                .setUdf8("")
//                .setUdf9("")
//                .setUdf10("")
//                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
//                .setKey(Constants.MERCHANT_KEY)                        // Merchant key
//                .setMerchantId(Constants.MERCHANT_ID);// Merchant ID
//
//        MerchantKeyBean bean = new MerchantKeyBean();
////        bean.setKey(Constants.MERCHANT_KEY);
//        bean.setAmount((float) remainingAmount);
//        bean.setEmail(userBean.getEmail());
//        bean.setFirstname(userBean.getFullName().replace(" ", ""));
//        bean.setProductinfo("Afoozo");
//        bean.setTxnid(paymentRequestId);
//        bean.setUdf1("a");
//        bean.setUdf2("b");
//        bean.setUdf3("c");
//        bean.setUdf4("d");
//        bean.setUdf5("e");
////        Log.d("trangsationId",transactionId);
//        callGetMerchantHashApi(new Gson().toJson(bean));
//
//    }
    private void callGetMerchantHashApi(String json) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        generatePaymentParams((String) responseClass.getResponsePacket());

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_HASH, json, "Loading...", true, AppUrls.REQUESTTAG_GENERATEHASH);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void generatePaymentParams(String hash) {
//        //declare paymentParam object
//        PayUmoneySdkInitializer.PaymentParam paymentParam = null;
//        try {
//            paymentParam = builder.build();
//            paymentParam.setMerchantHash(hash);
//
//            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, getActivity(), R.style.AppTheme_Grey, true);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//set the hash
    }


    private void customCleverWalletEvent(String amount) {
        HashMap<String, Object> walletAction = new HashMap<String, Object>();
        walletAction.put("Amount", amount);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Add Money in Wallet", walletAction);
    }

    private void customCleverVoucherEvent(String code) {
        HashMap<String, Object> voucherAction = new HashMap<String, Object>();
        voucherAction.put("Voucher Code Used", code);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Voucher Consumed", voucherAction);
    }

    private void customCleverPaymentFailedEvent(String paymentMode, String reason) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> paymentFailedAction = new HashMap<String, Object>();
                paymentFailedAction.put("Payment Mode", paymentMode);
                paymentFailedAction.put("Reason", reason);
                AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Wallet Payment Failed", paymentFailedAction);
            }
        }).start();

    }


    /* for add wallet amount  from cash free
     * 25 Dec 2024*/
    /* for cash free payment process*/

    private void callGetTokenApi(String orderId) {
        OrderDetailsBean bean = new OrderDetailsBean();
        bean.setOrderId(orderId);
        bean.setTotalAmount(Math.round(remainingAmount));
        String json = new Gson().toJson(bean);
        try {
            new NetworkManager(CreateOrderBean.class, new NetworkManager.OnCallback<CreateOrderBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        ProceedCashFree((String) responseClass.getResponsePacket());
                        createOrderBean = ((CreateOrderBean) responseClass.getResponsePacket());
                        doWebCheckoutPayment();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_TOKEN, json, "Loading...", true, AppUrls.REQUESTTAG_GENERATETOKEN);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void doWebCheckoutPayment() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
//                    .setEnvi ronment(CFSession.Environment.PRODUCTION)
                    .setEnvironment(CFSession.Environment.SANDBOX)
                    .setPaymentSessionID(createOrderBean.getPayment_session_id())
                    .setOrderId(paymentRequestId)
                    .build();
            // Replace with your application's theme colors
            CFWebCheckoutTheme cfTheme = new CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
                    .setNavigationBarBackgroundColor("#fc2678")
                    .setNavigationBarTextColor("#ffffff")
                    .build();
            CFWebCheckoutPayment cfWebCheckoutPayment = new CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFWebCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService.getInstance().doPayment(mContext, cfWebCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onPaymentVerify(String orderID) {
        callVerifyPaymentCashFree();
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String orderID) {
        callVerifyPaymentCashFree();
    }

    private void callVerifyPaymentCashFree() {
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.getResponsePacket().toString().equalsIgnoreCase("SUCCESS")) {
                        callUpdatePaymentRequestCashFreeApi("SUCCESS");
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getResponsePacket().toString());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.verifiedPaymentCashFree + createOrderBean.getOrder_id(), "", "Loading...", true, AppUrls.REQUESTTAG_verifiedPaymentCashFree);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callUpdatePaymentRequestCashFreeApi(String paymentStatus) {
        cashFreeResponse.setPaymentGateway("CashFree");
        cashFreeResponse.setOrderId(createOrderBean.getOrder_id());
        cashFreeResponse.setOrderAmount(remainingAmount);
        cashFreeResponse.setReferenceId(paymentRequestId);
        cashFreeResponse.setTxStatus(paymentStatus);
        String json = new Gson().toJson(cashFreeResponse);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        new Thread(() -> customCleverWalletEvent(binding.etAmount.getText().toString())).start();
                        binding.etAmount.setText("");
                        ((WalletActi) getActivity()).callGetBalanceApi();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST_FOR_CASH_FREE_V2, json, "Loading...", true, AppUrls.REQUESTTAG_UPDATEPAYMENTREQUESTFORCASHFREE_V2);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void callGenerateTransactionLinkApi(String orderRefId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderRefId);
            jsonObject.put("amount", remainingAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            new NetworkManager(SodexoBean.class, new NetworkManager.OnCallback<SodexoBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        sodexoBean = (SodexoBean) responseClass.getResponsePacket();
                        goToWebViewActivity(sodexoBean.getPaymentUrl());
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_TRANSACTION_LINK, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATE_TRANSACTION_LINK);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void goToWebViewActivity(String paymentUrl) {
        Intent intent = new Intent(mContext, TermsActi.class);
        intent.putExtra(Constants.PAYMENT_URL, paymentUrl);
        intent.putExtra("ForWhich", "payment");
        startActivityForResult(intent, SODEXO_PAYMENT_REQUEST_CODE);
    }

    private void callUpdatePaymentRequestForSodexoApi(String paymentGateway) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.PAYMENT_GATEWAY, paymentGateway);
            jsonObject.put(Constants.ORDER_ID, paymentRequestId);
            jsonObject.put(Constants.TRANSACTION_ID, sodexoBean.getTransactionId());
            jsonObject.put(Constants.NICKNAME, nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                       /* if (fromWhich.equals(Constants.ROOM_BOOKING)) {
                            callRoomPlaceApi();
                        } else
                            callPlaceOrderApi();*/

                    } else {
                      /*  if (responseClass.getErrorCode()==1){
                            onBackPressed();
                        }*/
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PAYMENT_REQUEST_FOR_SODEXO, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATE_PAYMENT_REQUEST_FOR_SODEXO);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
