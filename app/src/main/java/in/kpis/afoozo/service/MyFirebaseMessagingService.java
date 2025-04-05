package in.kpis.afoozo.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clevertap.android.sdk.CleverTapAPI;

import com.clevertap.android.sdk.pushnotification.NotificationInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.activity.ATMBarActi;
import in.kpis.afoozo.activity.Dashboard;
import in.kpis.afoozo.activity.EventsActi;
import in.kpis.afoozo.activity.LiveOrderActi;
import in.kpis.afoozo.activity.NotificationActi;
import in.kpis.afoozo.activity.NotificationPopUpActi;
import in.kpis.afoozo.activity.RatingActi;
import in.kpis.afoozo.activity.RewardsNewActi;
import in.kpis.afoozo.activity.SplashActi;
import in.kpis.afoozo.activity.StealDealActi;
import in.kpis.afoozo.app.Config;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.NotificationUtils;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * Created by Hemant on 11/13/2018.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if (!TextUtils.isEmpty(s)) {
            storeRegIdInPref(s);
            sendRegistrationToServer(s);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            Bundle extras = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                extras.putString(entry.getKey(), entry.getValue());
            }

            NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);
            if (info.fromCleverTap) {
                CleverTapAPI.createNotification(getApplicationContext(), extras);
                CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);
            } else {
                try {
                    DataObject object = new DataObject();
                    object.setTitle(remoteMessage.getData().get("title"));
                    object.setMessage(remoteMessage.getData().get("message"));
                    object.setOrderId(remoteMessage.getData().get("typeId"));
                    object.setAdminNotification(remoteMessage.getData().get("adminNotification"));
                    object.setImage(remoteMessage.getData().get("imageUrl"));
//                    object.setImage("http://www.asianjunkie.com/wp-content/uploads/2019/12/2020Blocks.jpg");
                    object.setType(remoteMessage.getData().get("type"));
                    object.setPayloadJson(remoteMessage.getData().get("payloadJson"));
                    object.setTimeStamp(remoteMessage.getData().get("timeStamp"));
                    handleDataMessage(object);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        } else if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Normal: " + remoteMessage.getNotification().getBody());
//                Toast.makeText(getApplicationContext(), "WithoutDataNotification", Toast.LENGTH_SHORT).show();
            handleNotification(remoteMessage.getNotification().getBody());
        }
//        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(DataObject object) {

        if (object.getType().equals(Constants.TAKE_AWAY)) {
            // TODO show dialog activity to accept particular order, and show activity for 30 second for now.
            Intent resultIntent;
            if (object.getAdminNotification().equalsIgnoreCase("true")) {
                resultIntent = new Intent(getApplicationContext(), Dashboard.class);
                resultIntent.putExtra(Constants.FROM_WHICH, getApplicationContext().getString(R.string.take_away));
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);

                SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_TYPE, object.getType());
                    intent.putExtra(Constants.MESSAGE, object.getMessage());
                    intent.putExtra(Constants.IMAGE_URL, object.getImage());
                    getApplicationContext().startActivity(intent);
//                    Utils.showNotificationPopUp(getApplicationContext(), object.getMessage(), object.getImage(), object.getType());
                } else {
                    resultIntent.putExtra(Constants.FROM_WHICH, "Notification");
                }
            } else {
                if (object.getTitle().equals("Order Delivered") || object.getTitle().equals("Order Picked Up")) {
                    resultIntent = new Intent(getApplicationContext(), RatingActi.class);
                    resultIntent.putExtra("message", object.getMessage());
                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
                    if (!TextUtils.isEmpty(object.getOrderId()))
                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        resultIntent.putExtra(Constants.FROM_WHICH, "History");
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(resultIntent);
                    } else
                        resultIntent.putExtra(Constants.FROM_WHICH, "Notification");
                } else
                    resultIntent = new Intent(getApplicationContext(), LiveOrderActi.class);
            }
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            if (!TextUtils.isEmpty(object.getOrderId())) {
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
            }

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);


        } else if (object.getType().equals(Constants.HOME_DELIVERY)) {
            // TODO show dialog activity to accept particular order, and show activity for 30 second for now.
            Intent resultIntent;
            if (object.getAdminNotification().equalsIgnoreCase("true")) {
                resultIntent = new Intent(getApplicationContext(), Dashboard.class);
                resultIntent.putExtra(Constants.IS_FROM_CART, true);
                resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.putExtra(Constants.FROM_WHICH, getApplicationContext().getString(R.string.delivery));

                SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_TYPE, object.getType());
                    intent.putExtra(Constants.MESSAGE, object.getMessage());
                    intent.putExtra(Constants.IMAGE_URL, object.getImage());
                    getApplicationContext().startActivity(intent);
                }
            } else {
                if (object.getTitle().equals("Order Delivered") || object.getTitle().equals("Order Picked Up")) {
                    resultIntent = new Intent(getApplicationContext(), RatingActi.class);
                    resultIntent.putExtra("message", object.getMessage());
                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
                    if (!TextUtils.isEmpty(object.getOrderId()))
                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        resultIntent.putExtra(Constants.FROM_WHICH, "History");
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(resultIntent);
                    } else
                        resultIntent.putExtra(Constants.FROM_WHICH, "Notification");
                } else
                    resultIntent = new Intent(getApplicationContext(), LiveOrderActi.class);
            }
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            if (!TextUtils.isEmpty(object.getOrderId())) {
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
            }

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);

        }
        else if (object.getType().equals(Constants.DINE_IN)) {

            Intent resultIntent;
            if (object.getAdminNotification().equalsIgnoreCase("true")) {
                resultIntent = new Intent(getApplicationContext(), Dashboard.class);
                resultIntent.putExtra(Constants.FROM_WHICH, getApplicationContext().getString(R.string.dine_in));
                resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);

                SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_TYPE, object.getType());
                    intent.putExtra(Constants.MESSAGE, object.getMessage());
                    intent.putExtra(Constants.IMAGE_URL, object.getImage());
                    getApplicationContext().startActivity(intent);
                }

            } else {
                if (object.getTitle().equals("Order Served")) {
                    Utils.sendLocalBroadCast1(getApplicationContext(), Constants.LOCAL_INTENT_STATUS_CHANGE);

//                    resultIntent = new Intent(getApplicationContext(), LiveOrderActi.class);
//                    resultIntent = new Intent(getApplicationContext(), RatingActi.class);
//                    resultIntent.putExtra("message", object.getMessage());
//                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
//                    if (!TextUtils.isEmpty(object.getOrderId()))
//                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
//                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                        resultIntent.putExtra(Constants.FROM_WHICH, "History");
//                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        getApplicationContext().startActivity(resultIntent);
//                    } else
//                        resultIntent.putExtra(Constants.FROM_WHICH, "Notification");
//                } else {
//                    resultIntent = new Intent(getApplicationContext(), LiveOrderActi.class);
//                    resultIntent.putExtra("message", object.getMessage());
//                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
//                    if (!TextUtils.isEmpty(object.getOrderId()))
//                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
                }
            }

//            resultIntent.putExtra("message", object.getMessage());
//            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
//            if (!TextUtils.isEmpty(object.getOrderId())) {
//                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
//            }

//            if (!TextUtils.isEmpty(object.getImage()))
//                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
//            else
            resultIntent = new Intent();

            showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);

        } else if (object.getType().equals(Constants.STEAL_DEAL)) {

            Intent resultIntent;
            if (object.getAdminNotification().equalsIgnoreCase("true")) {
                resultIntent = new Intent(getApplicationContext(), Dashboard.class);
                resultIntent.putExtra(Constants.FROM_WHICH, getApplicationContext().getString(R.string.steal_deal));
                resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);

                SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_TYPE, object.getType());
                    intent.putExtra(Constants.MESSAGE, object.getMessage());
                    intent.putExtra(Constants.IMAGE_URL, object.getImage());
                    getApplicationContext().startActivity(intent);
                }

            } else {
                if (object.getTitle().equals("Order Served")) {
                    resultIntent = new Intent(getApplicationContext(), RatingActi.class);
                    resultIntent.putExtra("message", object.getMessage());
                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
                    if (!TextUtils.isEmpty(object.getOrderId()))
                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
                    if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        resultIntent.putExtra(Constants.FROM_WHICH, "History");
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(resultIntent);
                    } else
                        resultIntent.putExtra(Constants.FROM_WHICH, "Notification");
                } else {
                    resultIntent = new Intent(getApplicationContext(), LiveOrderActi.class);
                    resultIntent.putExtra("message", object.getMessage());
                    resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
                    if (!TextUtils.isEmpty(object.getOrderId()))
                        resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
                }
            }

            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            if (!TextUtils.isEmpty(object.getOrderId())) {
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
            }

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getType().equals("Event") || object.getType().equals("EventDetail")) {
            Intent resultIntent;
            if (object.getAdminNotification().equalsIgnoreCase("true")) {
                resultIntent = new Intent(getApplicationContext(), Dashboard.class);
                resultIntent.putExtra(Constants.FROM_WHICH, getApplicationContext().getString(R.string.events));
                resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);

                SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.ORDER_TYPE, object.getType());
                    intent.putExtra(Constants.MESSAGE, object.getMessage());
                    intent.putExtra(Constants.IMAGE_URL, object.getImage());
                    getApplicationContext().startActivity(intent);
                }

            } else {

                resultIntent = new Intent(getApplicationContext(), EventsActi.class);
                resultIntent.putExtra("message", object.getMessage());
                resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            }

            if (!TextUtils.isEmpty(object.getOrderId()))
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getType().equals("TableReservation")) {

            Intent resultIntent = new Intent(getApplicationContext(), NotificationActi.class);
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            if (!TextUtils.isEmpty(object.getOrderId()))
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getType().equals("ScratchCard") || object.getType().equals("CouponCode")) {

            Intent resultIntent = new Intent(getApplicationContext(), RewardsNewActi.class);
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.TITLE, object.getTitle());
            resultIntent.putExtra(Constants.MESSAGE, object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
            resultIntent.putExtra(Constants.IMAGE_URL, object.getImage());
//            if (!TextUtils.isEmpty(object.getOrderId()))
//                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getType().equals("StealDealGift") || object.getType().equalsIgnoreCase("StealDealExpire")) {

            Intent resultIntent = new Intent(getApplicationContext(), StealDealActi.class);
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.TITLE, object.getTitle());
            resultIntent.putExtra(Constants.MESSAGE, object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
            resultIntent.putExtra(Constants.IMAGE_URL, object.getImage());
//            if (!TextUtils.isEmpty(object.getOrderId()))
//                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getType().equals("RfidCardIssued") || object.getType().equalsIgnoreCase("RfidCardRecharged") || object.getType().equalsIgnoreCase("RfidCardRefunded") || object.getType().equalsIgnoreCase("RfidCardTransacted")) {

            Intent resultIntent = new Intent(getApplicationContext(), ATMBarActi.class);
            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.TITLE, object.getTitle());
            resultIntent.putExtra(Constants.MESSAGE, object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            resultIntent.putExtra(Constants.IS_FROM_NOTIFICATION, true);
            resultIntent.putExtra(Constants.IMAGE_URL, object.getImage());
//            if (!TextUtils.isEmpty(object.getOrderId()))
//                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        } else if (object.getTitle().equals("Welcome")) {
            Intent resultIntent;
            resultIntent = new Intent(getApplicationContext(), SplashActi.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            SharedPref.setNotificationJSON(getApplicationContext(), new Gson().toJson(object));

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Intent intent = new Intent(getApplicationContext(), NotificationPopUpActi.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.ORDER_TYPE, object.getType());
                intent.putExtra(Constants.MESSAGE, object.getMessage());
                intent.putExtra(Constants.IMAGE_URL, object.getImage());
                getApplicationContext().startActivity(intent);
            }

            resultIntent.putExtra("message", object.getMessage());
            resultIntent.putExtra(Constants.ORDER_TYPE, object.getType());
            if (!TextUtils.isEmpty(object.getOrderId())) {
                resultIntent.putExtra(Constants.ORDER_ID, object.getOrderId());
            }
            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);

        } else {
            Intent resultIntent = new Intent(getApplicationContext(), SplashActi.class);
            resultIntent.putExtra("message", object.getMessage());
//            resultIntent.putExtra(Constants.ORDER_ID, payload.getOrderNumber());

            if (!TextUtils.isEmpty(object.getImage()))
                showNotificationMessageWithBigImage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent, object.getImage());
            else
                showNotificationMessage(getApplicationContext(), object.getTitle(), object.getMessage(), object.getTimeStamp(), resultIntent);
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void sendRegistrationToServer(final String token) {

        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);

        if (SharedPref.isLogin(this)) {
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
                        if (responseClass.isSuccess()) {
                            AppInitialization.getInstance().clevertapDefaultInstance.pushFcmRegistrationId(token, true);
                            SharedPref.setDeviceTokenUpdated(getApplicationContext(), true);
                        } else
                            SharedPref.setDeviceTokenUpdated(getApplicationContext(), false);
                    }

                    @Override
                    public void onFailure(boolean success, String response, int which) {
                        SharedPref.setDeviceTokenUpdated(getApplicationContext(), false);
//                        Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                    }
                }).callAPIJson(this, Constants.VAL_POST, AppUrls.UPDATE_DEVICE_DETAIL, json.toString(), "Loading...", false, AppUrls.REQUESTTAG_UPDATEDEVICEDETAIL);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPref.setDeviceToken(this, token);
    }
}
