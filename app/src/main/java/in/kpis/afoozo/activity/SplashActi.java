package in.kpis.afoozo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivitySplashBinding;
import com.kpis.afoozo.databinding.VersionCodeLayoutBinding;
import com.soundcloud.android.crop.BuildConfig;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.AppSignatureHashHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class SplashActi extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private ActivitySplashBinding binding;
    private long versionCode;

    private GoogleApiClient mGoogleApiClient;

    public static final int PERMISSION_ALL = 1;
    final static int REQUEST_ = 199;

    private String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS};
    private Dialog alert;

    private boolean isFromNotification;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        mContext = SplashActi.this;
        if (mContext.getPackageName().equalsIgnoreCase("in.kpis.afoozo"))
            FirebaseMessaging.getInstance().subscribeToTopic("allA");
        else
            FirebaseMessaging.getInstance().subscribeToTopic("testA");

//        Log.d("package", mContext.getPackageName());

        if (Utils.isDeviceOnline(mContext))
            initialize();
        else
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.no_internet_connection_available), "", getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.dismissRetryAlert();
                    finish();
                }
            });
    }

    private void initialize() {
        if (android.os.Build.VERSION.SDK_INT > 27) {
            try {
                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                versionCode = pInfo.getLongVersionCode();
                Log.e("VERSION CODE ---", versionCode + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
                versionCode = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
//            versionCode = BuildConfig.VERSION_CODE;
            Log.e("VERSION CODE ---", versionCode + "");
        }

//        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
//        Toast.makeText(mContext, "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0), Toast.LENGTH_LONG).show();
//        Log.e("HASH SMS KEY:- ", "HashKey: " + appSignatureHashHelper.getAppSignatures().get(0));

//        Utils.checkAndOnTouchVibrationSetting(mContext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions(SplashActi.this, PERMISSIONS, PERMISSION_ALL);
            } else {
                enableGps();
            }
        } else {
            enableGps();
        }

//        printKeyHash();

    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "in.kpis.afoozo",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void enableGps() {
        Utils.checkIsGPSEnabled(mContext, new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashActi.this, REQUEST_);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    default: {
                        Log.d("Location", "Default in");
//                            callGetVersionApi();
                        callGetVersionApi();
                        break;
                    }

                }
            }
        });
    }

//    private void enableGps() {
//        mGoogleApiClient = null;
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
//                    .addApi(LocationServices.API)
//                    .build();
//            mGoogleApiClient.connect();
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(1 * 1000);
//            locationRequest.setFastestInterval(1 * 1000);
//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(locationRequest);
//
//            builder.setAlwaysShow(true);
//
//            PendingResult<LocationSettingsResult> result =
//                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//                @Override
//                public void onResult(LocationSettingsResult result) {
//                    final Status status = result.getStatus();
//                    switch (status.getStatusCode()) {
//                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            try {
//                                // Show the dialog by calling startResolutionForResult(),
//                                // and check the result in onActivityResult().
//                                status.startResolutionForResult(SplashActi.this, REQUEST_);
//
//                            } catch (IntentSender.SendIntentException e) {
//                                // Ignore the error.
//                            }
//                            break;
//                        default: {
//                            Log.d("Location", "Default in");
////                            callGetVersionApi();
//                            callGetVersionApi();
//                            break;
//                        }
//
//                    }
//                }
//            });
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0) {

                    boolean isAllGranted = true;
//                    for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED)
                        isAllGranted = false;
//                            notAllowedQty++;
//                        }
//                    }

                    if (isAllGranted) {
                        // write your logic here
                        enableGps();
                    } else {
                        String message = "";
//                        if (notAllowedQty > 1)
//                            message = "Please grant camera, location and media access permissions to continue.";
//                        else {
//                            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED)
                        message = "Please grant permission to access device’s location.";
//                        }
                        Snackbar.make(findViewById(android.R.id.content),
                                message,
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    PERMISSIONS,
                                                    PERMISSION_ALL);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    /**
     * We get Response from another activity when we call startforresult intent
     *
     * @param requestCode for which we request
     *                    location request response comes under this
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_) {
            switch (resultCode) {
                case AppCompatActivity.RESULT_CANCELED: {
                    finish();
                    break;
                }
                default: {
//                    callGetVersionApi();
                    callGetVersionApi();
                    break;
                }
            }
        }

    }

    private void thread() {
//        setImageAndTon();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Utils.setDeviceSreenH(this);
                if (SharedPref.isLogin(this)) {
                    if (SharedPref.isLogin(this)) {
                        runOnUiThread(() -> {
//                                    enableGps();
                            if (SharedPref.isProfileUpdate(mContext)) {
                                UserBean user = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);
                                clevertapPushProfile(user);
                                Intent intent = new Intent(mContext, Dashboard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constants.IS_FROM_SPLASH, true);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(mContext, UpdateProfileActi.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(Constants.IS_FROM_REGISTER, true);
                                startActivity(intent);
                                finish();
                            }

                        });
                    }
                } else {
                    Utils.startActivityWithFinish(SplashActi.this, LoginActi.class);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void setImageAndTon() {
        Glide.with(this).asGif().load(R.raw.gif_logo).into(binding.imvSplash);

        AssetFileDescriptor afd = null;

        int resID = getResources().getIdentifier("splash_tone", "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resID);
        mediaPlayer.start();
    }


    private void showPopUp(boolean isForceUpdate) {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        VersionCodeLayoutBinding popBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.version_code_layout, null, false);
        alert.setContentView(popBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);


        if (isForceUpdate) {
            popBinding.txtPopCancel.setVisibility(View.GONE);
        } else {
            popBinding.txtPopCancel.setVisibility(View.VISIBLE);
        }

        popBinding.txtPopCancel.setOnClickListener(this);
        popBinding.txtPopOk.setOnClickListener(this);


        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(false);
        alert.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtPopCancel:
                alert.cancel();
                thread();
                break;
            case R.id.txtPopOk:
                alert.cancel();
                final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                finish();
                break;
        }
    }

    private void callGetVersionApi() {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        String newVersion = (String) responseClass.getResponsePacket();
                        if (!newVersion.equals("") && Long.parseLong(newVersion) > versionCode) {
                            showPopUp(true);
                        } else {
                            thread();
                        }

                    } else {

                        if (responseClass.getErrorCode() == 1000) {
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), responseClass.getMessage(), "", getString(R.string.Ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.dismissRetryAlert();
                                    finish();
                                }
                            });
                        } else
                            thread();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    thread();
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_VERSION + "CustomerAndroidAppVersion", "", "Loading...", true, AppUrls.REQUESTTAG_GETVERSION);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void clevertapPushProfile(UserBean user) {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", user.getFullName());                  // String
        profileUpdate.put("Identity", user.getUuid());                    // String or number
        profileUpdate.put("user_email", user.getEmail());               // Email address of the user
        profileUpdate.put("Email", user.getEmail());               // Email address of the user
        profileUpdate.put("user_mobile", user.getMobile());                 // Phone (with the country code, starting with +)
        profileUpdate.put("Phone", "+91" + user.getMobile());                    // Phone (with the country code, starting with +)
        profileUpdate.put("Gender", user.getGender().equalsIgnoreCase("Male") ? "M" : "F");                        // Can be either M or F
        profileUpdate.put("Tz", "Asia/Kolkata");                    //an abbreviation such as "PST", a full name such as "America/Los_Angeles",
        profileUpdate.put("Photo", user.getUserImage());    // URL to the Image

        profileUpdate.put("MSG-dndPhone", false); // Disable SMS notifications

        AppInitialization.getInstance().clevertapDefaultInstance.pushProfile(profileUpdate);
//        Utils.SingleClickCleverTapEvent("Profile Updated");
    }
}
