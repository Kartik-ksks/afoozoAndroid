package in.kpis.afoozo.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class LoginActi extends AppCompatActivity {
    private static final int RESOLVE_HINT = 112;
    private Toolbar toolbar;
    private Context mContext;
    private ActivityLoginBinding binding;
    private static final int CREDENTIAL_PICKER_REQUEST = 1;
    private String mobileNo;
    private String emailId;
    private String password;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = LoginActi.this;
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
        binding.toolbar.activityTitle.setText(getString(R.string.login));


        requestHint();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    public void loginProcess(View view) {
        mobileNo = binding.etMobile.getText().toString();
//        if (Utils.isValidMobileNumber(mContext, mobileNo)) {
            callLoginWithMobileApi();
//        }
    }





//    private void requestHint() {
//        HintRequest hintRequest = new HintRequest.Builder()
//                .setPhoneNumberIdentifierSupported(true)
//                .build();
//        PendingIntent mPendingIntent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
//        IntentSenderRequest.Builder intentSenderRequest = new IntentSenderRequest.Builder(mPendingIntent.getIntentSender());
//        hintLauncher.launch(intentSenderRequest.build());
//    }
private void requestHint() {
    HintRequest hintRequest = new HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build();

    try {
        PendingIntent pendingIntent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
        hintLauncher.launch(intentSenderRequest);
    } catch (Exception e) {
        Log.e("HintRequestError", "Failed to request hint picker intent", e);
    }
}

    ActivityResultLauncher<IntentSenderRequest> hintLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result != null && result.getData() != null) {
                    String unformattedPhone;
                    Intent data = result.getData();
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    if (credential != null) {
                        unformattedPhone = credential.getId();
                        if (unformattedPhone.contains("+"))
                            binding.etMobile.setText(credential.getId().substring(3, (credential.getId()).length()));
                        else
                            binding.etMobile.setText(credential.getId());
                    }
                }
            });

//    private void requestHint() {
//        HintRequest hintRequest = new HintRequest.Builder()
//                .setPhoneNumberIdentifierSupported(true)
//                .build();
//        PendingIntent pendingIntent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
//        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender()).build();
//        hintLauncher.launch(intentSenderRequest);
//    }
//
//
//    ActivityResultLauncher<IntentSenderRequest> hintLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartIntentSenderForResult(), result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Intent data = result.getData();
//                    if (data != null) {
//                        Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
//                        if (credential != null) {
//                            String unformattedPhone = credential.getId();
//                            if (unformattedPhone != null) {
//                                if (unformattedPhone.contains("+")) {
//                                    binding.etMobile.setText(unformattedPhone.substring(3));
//                                } else {
//                                    binding.etMobile.setText(unformattedPhone);
//                                }
//                            }
//                        }
//                    }
//                }
//            });


    // Obtain the phone number from the result

/*    private void getTokenId() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(refreshedToken)) {

            // Saving reg id to shared preferences
            SharedPref.setDeviceToken(mContext, refreshedToken);

            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
            registrationComplete.putExtra("token", refreshedToken);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } else {
            new MyFirebaseInstanceIDService().onTokenRefresh();
        }
    }*/


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        String unformattedPhone;
//        if (requestCode == RESOLVE_HINT) {
//            if (resultCode == RESULT_OK) {
//                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
//                if (credential != null) {
//                    unformattedPhone = credential.getId();
//                    if (unformattedPhone.contains("+"))
//                        binding.etMobile.setText(credential.getId().substring(3, (credential.getId()).length()));
//                    else
//                        binding.etMobile.setText(credential.getId());
//                }
//                // credential.getId();  <-- will need to process phone number string
//            }
//        }
//    }

    private void callLoginWithMobileApi() {
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
                    if (responseClass.isSuccess()) {
                        Utils.dismissProgressDialog();
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        customCleverLoginEvent();
                        goToOtpScreen();

                    } else {
                        Toast.makeText(mContext, "" + responseClass.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Toast.makeText(mContext, "" + getResources().getString(R.string.internet_connection_not_found), Toast.LENGTH_SHORT).show();
//                    Utils.showCenterToast(mContext, getResources().getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GENERATE_OTP_FOR_USER, json.toString(), "Loading...", true, AppUrls.REQUESTTAG_GENERATEOTPFORUSER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * this method is used to go to Otp screen and also send mobile no
     */
    private void goToOtpScreen() {
        Intent intent = new Intent(mContext, OtpActi.class);
        intent.putExtra(Constants.IS_FROM_REGISTER, false);
        intent.putExtra(Constants.MOBILE_NO, mobileNo);
        startActivity(intent);
    }

    private void customCleverLoginEvent() {
        HashMap<String, Object> loginAction = new HashMap<String, Object>();
        loginAction.put("Mobile", mobileNo);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Login", loginAction);
    }

}
