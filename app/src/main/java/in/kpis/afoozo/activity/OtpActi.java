package in.kpis.afoozo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.custome.Pinview;
import com.kpis.afoozo.databinding.ActivityOtpBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.receiver.SmsReceiver;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class OtpActi extends AppCompatActivity implements Pinview.PinViewEventListener , SmsReceiver.OTPReceiveListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityOtpBinding binding;

    private String mobileNo;

    private String otp;
    private final int REQUEST_PERMISSION_CODE = 101;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private CountDownTimer countDownTimer;
    private int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);

        if (getIntent().getExtras() != null)
            mobileNo = getIntent().getStringExtra(Constants.MOBILE_NO);


        mContext = OtpActi.this;
        startSmsListener();
        initialize();
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
        binding.txtNumber.setText(mobileNo);
        binding.toolbar.activityTitle.setText(getString(R.string.otp));

        binding.pinView.setPinViewEventListener(this);

        customTextViewForResend();

        showTimer();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        ContextCompat.registerReceiver(mContext,smsReceiver, intentFilter,ContextCompat.RECEIVER_NOT_EXPORTED);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        this.unregisterReceiver(smsReceiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");

                binding.pinView.setValue(message);
            }
        }
    };

    private void showTimer(){
        seconds = 59;
        binding.setIsTimerRun(true);
        binding.txtVerifyViaCall.setTextColor(getResources().getColor(R.color.gray));
        countDownTimer = new CountDownTimer(1000*seconds, 1000) {

            public void onTick(long millisUntilFinished) {

                binding.txttime.setText("00:" + seconds);
                seconds -= 1;

            }

            public void onFinish() {
                binding.setIsTimerRun(false);
                binding.txtVerifyViaCall.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

        }.start();
    }

    private void customTextViewForResend() {
        SpannableString ss = new SpannableString(getResources().getString(R.string.did_not_receive_code));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
//                callResendApi();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
//                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 21, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 21, 31, 0);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 21, 31, 0);

//        txtTerms.setText(ss);
//        txtTerms.append(ss1);
        binding.txtResendNow.setText(ss);
        binding.txtResendNow.setMovementMethod(LinkMovementMethod.getInstance());
        binding.txtResendNow.setHighlightColor(Color.TRANSPARENT);
    }
    private void getTokenId(UserBean userBean) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                SharedPref.setDeviceToken(mContext, token);
                callUpdateDeviceToken(userBean);
                Log.d("TAG", "retrieve token successful : " + token);
            } else{
                Log.w("TAG", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
            //handle e
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task -> Log.v("TAG", "This is the token : " + task.getResult()));

    /*    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( OtpActi.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                SharedPref.setDeviceToken(mContext, newToken);
                callUpdateDeviceToken(userBean);
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean readSms = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readSms) {
                    } else {
                        Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    /**
     * This Method is used to check and verify OTP process
     */
    private void verifyProcess(){
        otp = binding.pinView.getValue();
        if (!TextUtils.isEmpty(otp) && otp.length() == 4) {
            Utils.progressDialog(mContext, "");
            callVerifyOtpApi();
        } else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.enter_valid_otp), getString(R.string.Ok), "", null);
    }

    private void callVerifyOtpApi(){
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.FLD_MOBILE_NUMBER, mobileNo);
            json.put(Constants.FLD_ONETIME_PASSWORD, otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(UserBean.class, new NetworkManager.OnCallback<UserBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        UserBean userBean = ((UserBean) responseClass.getResponsePacket());
                        Gson gson = new Gson();
                        String json = gson.toJson(((UserBean) responseClass.getResponsePacket()));
                        SharedPref.setUserModelJSON(mContext, json);
                        SharedPref.setLogin(mContext, true);

                        final String basicAuth = "Basic " + Base64.encodeToString((userBean.getMobile() + ":" + userBean.getSecretKey()).getBytes(), Base64.NO_WRAP);
                        SharedPref.setBasicAuth(mContext, basicAuth);

                        customCleverLoginEvent();
                        sendDataToCleverTap(userBean);

                        if (!TextUtils.isEmpty(SharedPref.getDeviceToken(mContext))){
                            callUpdateDeviceToken(userBean);
                        }else{
                            getTokenId(userBean);
                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        Utils.dismissProgressDialog();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.LOGIN_USER_BY_OTP, json.toString(), "Loading...", false, AppUrls.REQUESTTAG_LOGINUSERBYOTP);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callResendApi(View view){
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.FLD_MOBILE_NUMBER, mobileNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        showTimer();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_OTP_FOR_USER, json.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATEOTPFORUSER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callVoiceOTPApi(View view){
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.FLD_MOBILE_NUMBER, mobileNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        showTimer();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SEND_VOICE_OTP_FOR_USER, json.toString(), "Loading...", true, AppUrls.REQUESTTAG_SENDVOICEOTPFORUSER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callUpdateDeviceToken(UserBean userBean){
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.FLD_DEVICE_TYPE, "ANDROID");
            json.put(Constants.FLD_DEVICE_TOKEN, SharedPref.getDeviceToken(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Utils.dismissProgressDialog();
                    if (responseClass.isSuccess()){

                        AppInitialization.getInstance().clevertapDefaultInstance.pushFcmRegistrationId(SharedPref.getDeviceToken(mContext),true);

                        if (TextUtils.isEmpty(userBean.getFullName())) {
                            Intent intent = new Intent(mContext, UpdateProfileActi.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra(Constants.IS_FROM_REGISTER, true);
//                            try {
//                                ZohoSalesIQ.registerVisitor(userBean.getSecretKey());
//                            } catch (InvalidVisitorIDException e) {
//                                e.printStackTrace();
//                            }
                            startActivity(intent);
                            finish();
                        } else {
//                            try {
//                                ZohoSalesIQ.registerVisitor(userBean.getSecretKey());
//                                ZohoSalesIQ.Visitor.setName(userBean.getFullName());
//                                ZohoSalesIQ.Visitor.setContactNumber(userBean.getMobile());
////                                ZohoSalesIQ.Visitor.setEmail("email id");
//                            } catch (InvalidVisitorIDException e) {
//                                e.printStackTrace();
//                            }
                            SharedPref.setIsProfileUpdate(mContext, true);
                            Utils.startActivityWithFinish(mContext, Dashboard.class);
                        }

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_DEVICE_DETAIL, json.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATEDEVICEDETAIL);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataEntered(Pinview pinview, boolean fromUser) {
        Utils.hideKeyboard(OtpActi.this);
        verifyProcess();
    }

    SmsReceiver smsReceiver;
    private void startSmsListener(){
        smsReceiver = new SmsReceiver();
        smsReceiver.setOTPListener(this);
        SmsRetrieverClient client = SmsRetriever.getClient(mContext);
//        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
        Task<Void> task = client.startSmsRetriever();
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
//                binding.pinView.setValue(message);
                Log.e("TASK STARTED-", "...");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TASK FAILED-", "...");
            }
        });
    }

    @Override
    public void onOTPReceived(String otp) {
        Log.e("SMS OTP- ", otp);
        binding.pinView.setValue(otp);
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }

    private void sendDataToCleverTap(UserBean user){
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", user.getFullName()); // String
        profileUpdate.put("Identity", user.getUuid()); // String or number
        profileUpdate.put("user_email", user.getEmail()); // Email address of the user
        profileUpdate.put("Email", user.getEmail()); // Email address of the user
        profileUpdate.put("user_mobile", user.getMobile()); // Phone (with the country code, starting with +)
        profileUpdate.put("Phone", "+91"+user.getMobile()); // Phone (with the country code, starting with +)
        profileUpdate.put("Gender", user.getGender().equalsIgnoreCase("Male")?"M":"F"); // Can be either M or F
        profileUpdate.put("Tz", "Asia/Kolkata"); //an abbreviation such as "PST", a full name such as "America/Los_Angeles",
//or a custom ID such as "GMT-8:00"
        profileUpdate.put("Photo", user.getUserImage()); // URL to the Image
        AppInitialization.getInstance().clevertapDefaultInstance.onUserLogin(profileUpdate);
        customCleverVerifyOtpEvent();
    }

    private void customCleverVerifyOtpEvent(){
        HashMap<String, Object> verigyOtpAction = new HashMap<String, Object>();
        verigyOtpAction.put("Mobile", mobileNo);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("OTP Verified", verigyOtpAction);
    }

    private void customCleverLoginEvent(){
        HashMap<String, Object> loginAction = new HashMap<String, Object>();
        loginAction.put("Mobile", mobileNo);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Login", loginAction);
    }

}
