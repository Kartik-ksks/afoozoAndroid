package in.kpis.afoozo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.kpis.afoozo.R;


/**
 * Created by Vishal on 28/07/2016.
 */

public class SharedPref {

    private static SharedPreferences mPref;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void setLogin(Context context, boolean login) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putBoolean(Constants.FLD_IS_LOGIN, login).commit();
    }

    public static void setDeviceTokenUpdated(Context context, boolean login) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putBoolean(Constants.FLD_IS_TOKEN_UPDATED, login).commit();
    }

    public static boolean isLogin(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean isLogin = mPref.getBoolean(Constants.FLD_IS_LOGIN, false);
        return isLogin;
    }

    public static boolean isDeviceTokenUpdated(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean isLogin = mPref.getBoolean(Constants.FLD_IS_TOKEN_UPDATED, false);
        return isLogin;
    }

    public static void setIsProfileUpdate(Context context, boolean login) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putBoolean(Constants.FLD_IS_PROFILE_UPDATE, login).commit();
    }

    public static boolean isProfileUpdate(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean isLogin = mPref.getBoolean(Constants.FLD_IS_PROFILE_UPDATE, false);
        return isLogin;
    }

    public static void setIsSkipped(Context context, boolean skipped) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putBoolean(Constants.FLD_IS_SKIPPED, skipped).commit();
    }

    public static void setUserId(Context context, String userId) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_USER_ID, userId).commit();
    }

    public static void setSavedDataForWhich(Context context, String forWhich) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_DATA_FOR_WHICH, forWhich).commit();
    }


    public static void setCartCount(Context context, int count) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putInt(Constants.FLD_CART_COUNT, count).commit();
    }


    public static boolean isSkipped(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        boolean isLogin = mPref.getBoolean(Constants.FLD_IS_SKIPPED, false);
        return isLogin;
    }

    public static String getUserId(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userId = mPref.getString(Constants.FLD_USER_ID, "");
        return userId;
    }

    public static String getSavedDataForWhich(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String forWhich = mPref.getString(Constants.FLD_DATA_FOR_WHICH, "");
        return forWhich;
    }

    public static int getCartCount(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        int role = mPref.getInt(Constants.FLD_CART_COUNT, 0);
        return role;
    }

    public static String getUserModelJSON(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userModelJson = mPref.getString(Constants.FLD_USER_MODEL_JSON, "");
        return userModelJson;
    }

    public static String getNotificationJSON(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String notificationJson = mPref.getString(Constants.FLD_NOTIFICATION_JSON, "");
        return notificationJson;
    }

    public static String getScanBeanJson(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userModelJson = mPref.getString(Constants.FLD_SCAN_BEAN_JSON, "");
        return userModelJson;
    }

    public static String getScanCafeBeanJson(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userModelJson = mPref.getString(Constants.FLD_CAFE_SCAN_BEAN_JSON, "");
        return userModelJson;
    }

    public static String getAddressModelJSON(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String addressModelJson = mPref.getString(Constants.FLD_ADDRESS_MODEL_JSON, "");
        return addressModelJson;
    }

    public static void setUserModelJSON(Context context, String userModelJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_USER_MODEL_JSON, userModelJson).commit();
    }

    public static void setNotificationJSON(Context context, String notificationJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_NOTIFICATION_JSON, notificationJson).commit();
    }

    public static void setScanBeanJson(Context context, String userModelJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_SCAN_BEAN_JSON, userModelJson).commit();
    }

    public static void setScanCafeBeanJson(Context context, String userModelJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_CAFE_SCAN_BEAN_JSON, userModelJson).commit();
    }

    public static void setAddressModelJSON(Context context, String addressModelJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_ADDRESS_MODEL_JSON, addressModelJson).commit();
    }

    public static void setLoginUserToken(Context context, String token) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_USER_TOKEN, token).commit();
    }

    public static String getLoginUserToken() {
        mPref = (SharedPreferences) mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userToken = mPref.getString(Constants.FLD_USER_TOKEN, "");
        return userToken;
    }

    public static void setScreenW(Context context, int screenW) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putInt(Constants.FLD_SCREEN_WIDTH, screenW).commit();
    }

    public static int getScreenW(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        return mPref.getInt(Constants.FLD_SCREEN_WIDTH, 0);
    }

    public static void setDeviceToken(Context context, String deviceToken) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_DEVICE_TOKEN, deviceToken).commit();
    }

    public static String getDeviceToken(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String deviceToken = mPref.getString(Constants.FLD_DEVICE_TOKEN, "");
        return deviceToken;
    }


    public static void setBuildVersion(Context context, String deviceToken) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_BUILD_VERSION, deviceToken).commit();
    }

    public static String getBuildVersion(Context mContext) {
        mPref = (SharedPreferences) mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String deviceToken = mPref.getString(Constants.FLD_BUILD_VERSION, "");
        return deviceToken;
    }

    public static void setCouponCode(Context context, String couponCode) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_COUPON_CODE, couponCode).commit();
    }

    public static String getCouponCode(Context mContext) {
        mPref = (SharedPreferences) mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String couponCode = mPref.getString(Constants.FLD_COUPON_CODE, "");
        return couponCode;
    }

    public static void setBasicAuth(Context mContext, String basicAuth) {
        mPref = (SharedPreferences) mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_BASIC_AUTH, basicAuth).commit();
    }

    public static String getBasicAuth(Context mContext) {
        mPref = (SharedPreferences) mContext.getSharedPreferences(mContext.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String basicAuth = mPref.getString(Constants.FLD_BASIC_AUTH, "");
        return basicAuth;
    }

    public static String getDeliveryOfferJson(Context context) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String userModelJson = mPref.getString(Constants.FLD_DELIVERY_OFFER_JSON, "");
        return userModelJson;
    }

    public static void setDeliveryOfferJson(Context context, String userModelJson) {
        mPref = (SharedPreferences) context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        mPref.edit().putString(Constants.FLD_DELIVERY_OFFER_JSON, userModelJson).commit();
    }
}
