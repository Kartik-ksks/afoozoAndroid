package in.kpis.afoozo;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;

import in.kpis.afoozo.util.SharedPref;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.SyncListener;

import org.json.JSONObject;


public class AppInitialization extends Application {

    private static Typeface fontRegular;
    private static Typeface fontMaker;
    private static Typeface fontBold;

    private static AppInitialization mInstance;

    public CleverTapAPI clevertapDefaultInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //include this method
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
//        MultiDex.install(this);
        super.onCreate();

        ActivityLifecycleCallback.register(this);

        SyncListener listener = new SyncListener() {
            @Override
            public void profileDataUpdated(JSONObject updates) {

            }

            @Override
            public void profileDidInitialize(String CleverTapID) {
                String attributionID = CleverTapAPI.getDefaultInstance(getApplicationContext()).getCleverTapAttributionIdentifier();
//                AppsFlyerLib.getInstance().setCustomerUserId(attributionID);
//
//                AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
//                AppsFlyerLib.getInstance().startTracking(AppInitialization.this);
            }
        };

        CleverTapAPI.getDefaultInstance(AppInitialization.this).setSyncListener(listener);

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        clevertapDefaultInstance.createNotificationChannel(getApplicationContext(),"Afoozo_Channel_02","Afoozo_Channel_02","Afoozo Channel",NotificationManager.IMPORTANCE_MAX,true,"notification.mp3");
//        clevertapDefaultInstance.createNotificationChannel(getApplicationContext(),"Afoozo_Channel_03","Afoozo_Channel_03","Afoozo Channel", NotificationManager.IMPORTANCE_MAX,true,"roster.mp3");

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG);
        mInstance = this;

// String attributionID = clevertapDefaultInstance.getCleverTapAttributionIdentifier();
// appsFlyerLib.setCustomerUserId(attributionID);

//        ZohoSalesIQ.init(this, "XhcaOn9Y6oLotZILgXlzfWXrpyfbj%2FhqypFtCoccqlMlraM1bLYJ1pdrWHf5z9Fj_in",
//                "80po6MOdePINr7EYUfvmsA%2Bug0d5uBXFlCTrktpb3NPkS0gb9SgppdJ2Udrm7VKqBi6frsGWZ4vkJMUrVOrA6nZmnOolqDexaqCaqgtsdYQxBQ6EYkcbGp1mYL3ueAe66QNMOmAlLFoTbXhJPO31S5UsTCQvc%2Fqr");


        fontMaker = Typeface.createFromAsset(getAssets(), "fonts/fontmaker-normal.otf");
        fontRegular = Typeface.createFromAsset(getAssets(), "fonts/circular-medium.ttf");
        fontBold = Typeface.createFromAsset(getAssets(), "fonts/circular-bold.ttf");

        try {
            PackageInfo packageinfo = null;
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageinfo.versionName.toString();
            SharedPref.setBuildVersion(this, version);
            Log.d("versionCode", version);

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static synchronized AppInitialization getInstance() {
        return mInstance;
    }

    public static Typeface getFontRegular() {
        return fontRegular;
    }

    public static Typeface getFontMaker() {
        return fontMaker;
    }

    public static Typeface getFontBold() {
        return fontBold;
    }
}
