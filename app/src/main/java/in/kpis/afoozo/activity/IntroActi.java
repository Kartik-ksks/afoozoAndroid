//package in.kpis.afoozo.activity;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.databinding.DataBindingUtil;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
////import com.facebook.CallbackManager;
////import com.facebook.FacebookCallback;
////import com.facebook.FacebookException;
////import com.facebook.FacebookSdk;
////import com.facebook.GraphRequest;
////import com.facebook.GraphResponse;
////import com.facebook.login.LoginManager;
////import com.facebook.login.LoginResult;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.gson.Gson;
//import com.kpis.afoozo.R;
//import in.kpis.afoozo.app.Config;
//import in.kpis.afoozo.bean.UserBean;
//import com.kpis.afoozo.databinding.ActivityIntroBinding;
//import in.kpis.afoozo.okhttpServcice.AppUrls;
//import in.kpis.afoozo.okhttpServcice.NetworkManager;
//import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
//import in.kpis.afoozo.service.MyFirebaseInstanceIDService;
//import in.kpis.afoozo.util.Constants;
//import in.kpis.afoozo.util.NotificationUtils;
//import in.kpis.afoozo.util.SharedPref;
//import in.kpis.afoozo.util.Utils;
//import com.zoho.livechat.android.exception.InvalidVisitorIDException;
//import com.zoho.salesiqembed.ZohoSalesIQ;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Arrays;
//
//public class IntroActi extends AppCompatActivity {
//
//    private String TAG = LoginActi.class.getSimpleName();
//
//    private Toolbar toolbar;
//
//    private Context mContext;
//
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private CallbackManager callbackManager;
//
//    private ActivityIntroBinding binding;
//
//    private static final String EMAIL = "email";
//
//    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
//        @Override
//        public void onSuccess(LoginResult loginResult) {
//            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                @Override
//                public void onCompleted(JSONObject object, GraphResponse response) {
//                    Log.e(TAG, object.toString());
//                    Log.e(TAG, response.toString());
//
//                    try {
//                        userId = object.getString("id");
//                        accessToken = loginResult.getAccessToken().getToken();
//
//                        profilePicture = "https://graph.facebook.com/" + userId + "/picture?type=large";
//
//                        if (object.has("first_name"))
//                            name = object.getString("first_name");
//                        if (object.has("last_name"))
//                            name = name + " " + object.getString("last_name");
//                        if (object.has("email"))
//                            email = object.getString("email");
//
////                        if (!TextUtils.isEmpty(SharedPref.getDeviceToken(mContext)))
//                        LoginManager.getInstance().logOut();
//                        Utils.progressDialog(mContext, "");
//                        callSocialLoginApi();
////                        else {
////                            Utils.showCenterToast(mContext, getString(R.string.device_token_not_found));
////                            getTokenId();
////                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
////            //Here we put the requested fields to be returned from the JSONObject
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
//            request.setParameters(parameters);
//            request.executeAsync();
//        }
//
//        @Override
//        public void onCancel() {
//        }
//
//        @Override
//        public void onError(FacebookException e) {
//            e.printStackTrace();
//        }
//    };
//
//    private String userId;
//    private String profilePicture;
//    private String name;
//    private String email;
//    private String accessToken;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
//
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // checking for type intent filter
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    // gcm successfully registered
//                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic("allA");
//
////                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//
//                    String message = intent.getStringExtra("message");
//
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//
////                    txtMessage.setText(message);
//                }
//            }
//        };
//
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().logOut();
//
//        mContext = IntroActi.this;
//        initialize();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.REGISTRATION_COMPLETE));
//
//        // register new push message receiver
//        // by doing this, the activity will be notified each time a new message arrives
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
//
//        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void initialize() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        toolbar.setNavigationIcon(R.drawable.ic_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        binding.toolbar.activityTitle.setText(getString(R.string.login));
//
//        binding.loginButton.setReadPermissions(Arrays.asList(EMAIL));
//
//        binding.loginButton.registerCallback(callbackManager, callback);
//
//    }
//
//    public void loginWithMobile(View view){
//        Intent intent = new Intent(mContext, LoginActi.class);
//        intent.putExtra(Constants.IS_FOR_MOBILE_LOGIN, true);
//        startActivity(intent);
//    }
//
//    public void loginWithEmail(View view){
//        Intent intent = new Intent(mContext, LoginActi.class);
//        intent.putExtra(Constants.IS_FOR_MOBILE_LOGIN, false);
//        startActivity(intent);
//    }
//
//    public void facebookClick(View view){
////        binding.loginButton.performClick();
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//    }
//
//    private void getTokenId(UserBean userBean) {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        if (!TextUtils.isEmpty(refreshedToken)) {
//
//            // Saving reg id to shared preferences
//            SharedPref.setDeviceToken(mContext, refreshedToken);
//
//            // Notify UI that registration has completed, so the progress indicator can be hidden.
//            Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
//            registrationComplete.putExtra("token", refreshedToken);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
//
//            callUpdateDeviceToken(userBean);
//        } else {
//            new MyFirebaseInstanceIDService().onTokenRefresh();
//        }
//    }
//
//
//    private void goToLoginScreen() {
//        UserBean bean = new UserBean();
//        bean.setFacebookUserId(userId);
//        bean.setFacebookUserAccessToken(accessToken);
//        bean.setFullName(name);
//        if (!TextUtils.isEmpty(email))
//            bean.setEmail(email);
//        if (!TextUtils.isEmpty(profilePicture))
//            bean.setUserImage(profilePicture);
//
//        Intent intent = new Intent(mContext, LoginActi.class);
//        intent.putExtra(Constants.IS_FOR_MOBILE_LOGIN, false);
//        intent.putExtra("FacebookData", bean);
//        startActivity(intent);
//
//    }
//
//    private void callSocialLoginApi() {
//
//        UserBean bean = new UserBean();
//        bean.setFacebookUserId(userId);
//        bean.setFacebookUserAccessToken(accessToken);
//
//        String json = new Gson().toJson(bean);
//
//        try {
//            new NetworkManager(UserBean.class, new NetworkManager.OnCallback<UserBean>() {
//                @Override
//                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
//                    if (responseClass.isSuccess()){
//                        UserBean userBean = ((UserBean) responseClass.getResponsePacket());
//                        Gson gson = new Gson();
//                        String json = gson.toJson(((UserBean) responseClass.getResponsePacket()));
//                        SharedPref.setUserModelJSON(mContext, json);
//                        SharedPref.setLogin(mContext, true);
//
//                        final String basicAuth = "Basic " + Base64.encodeToString((userBean.getMobile() + ":" + userBean.getSecretKey()).getBytes(), Base64.NO_WRAP);
//                        SharedPref.setBasicAuth(mContext, basicAuth);
//
//                        if (!TextUtils.isEmpty(SharedPref.getDeviceToken(mContext)))
//                            callUpdateDeviceToken(userBean);
//                        else
//                            getTokenId(userBean);
//
//                    } else {
//                        Utils.dismissProgressDialog();
//                        if (responseClass.getErrorCode() == 100)
//                            goToLoginScreen();
//                        else
//                            Utils.showCenterToast(mContext, responseClass.getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(boolean success, String response, int which) {
//                    Utils.dismissProgressDialog();
//                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
//                }
//            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.LOGIN_USER_BY_FACEBOOK, json, "Loading...", false, AppUrls.REQUESTTAG_LOGINUSERBYFACEBOOK);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void callUpdateDeviceToken(UserBean userBean){
//        JSONObject json = new JSONObject();
//        try {
//            json.put(Constants.FLD_DEVICE_TYPE, "ANDROID");
//            json.put(Constants.FLD_DEVICE_TOKEN, SharedPref.getDeviceToken(mContext));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
//                @Override
//                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
//                    Utils.dismissProgressDialog();
//                    if (responseClass.isSuccess()){
//
//                        if (TextUtils.isEmpty(userBean.getFullName())) {
//                            try {
//                                ZohoSalesIQ.registerVisitor(userBean.getSecretKey());
//                            } catch (InvalidVisitorIDException e) {
//                                e.printStackTrace();
//                            }
//                            Intent intent = new Intent(mContext, UpdateProfileActi.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.putExtra(Constants.IS_FROM_REGISTER, true);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            try {
//                                ZohoSalesIQ.registerVisitor(userBean.getSecretKey());
//                                ZohoSalesIQ.Visitor.setName(userBean.getFullName());
//                                ZohoSalesIQ.Visitor.setContactNumber(userBean.getMobile());
////                                ZohoSalesIQ.Visitor.setEmail("email id");
//                            } catch (InvalidVisitorIDException e) {
//                                e.printStackTrace();
//                            }
//                            SharedPref.setIsProfileUpdate(mContext, true);
//                            Utils.startActivityWithFinish(mContext, Dashboard.class);
//                        }
//
//                    } else {
//                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                    }
//                }
//
//                @Override
//                public void onFailure(boolean success, String response, int which) {
//                    Utils.dismissProgressDialog();
//                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
//                }
//            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_DEVICE_DETAIL, json.toString(), "Loading...", false, AppUrls.REQUESTTAG_UPDATEDEVICEDETAIL);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//}
