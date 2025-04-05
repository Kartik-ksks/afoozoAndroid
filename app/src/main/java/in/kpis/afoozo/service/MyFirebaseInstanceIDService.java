package in.kpis.afoozo.service;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import in.kpis.afoozo.app.Config;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by KHEMRAJ on 11/13/2018.
 */
public class MyFirebaseInstanceIDService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

 /*   @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(refreshedToken)) {

            // Saving reg id to shared preferences
            storeRegIdInPref(refreshedToken);

            // sending reg id to your server
            sendRegistrationToServer(refreshedToken);

            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
            registrationComplete.putExtra("token", refreshedToken);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } else {
            new MyFirebaseInstanceIDService().onTokenRefresh();
        }

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);

        if (SharedPref.isLogin(this)){
            JSONObject json = new JSONObject();
            try {
                json.put(Constants.FLD_DEVICE_TYPE, "ANDROID");
                json.put(Constants.FLD_DEVICE_TOKEN, SharedPref.getDeviceToken(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                    @Override
                    public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                        if (responseClass.isSuccess()){

//                            if (TextUtils.isEmpty(userBean.getFullName())) {
//                                Intent intent = new Intent(mContext, UpdateProfileActi.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra(Constants.IS_FROM_REGISTER, true);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                SharedPref.setIsProfileUpdate(mContext, true);
//                                Utils.startActivityWithFinish(mContext, Dashboard.class);
//                            }

                        } else {
//                            Utils.showCenterToast(mContext, responseClass.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(boolean success, String response, int which) {
//                        Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                    }
                }).callAPIJson(this, Constants.VAL_POST, AppUrls.UPDATE_DEVICE_DETAIL, json.toString(), "Loading...", true, AppUrls.REQUESTTAG_UPDATEDEVICEDETAIL);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPref.setDeviceToken(this, token);
    }*/
}
