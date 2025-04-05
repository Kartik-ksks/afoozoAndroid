package in.kpis.afoozo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.activity.SplashActi;
import in.kpis.afoozo.adapter.TaxesAdapter;
import in.kpis.afoozo.bean.TaxesBean;
import com.kpis.afoozo.databinding.InstructionPopupLayoutBinding;
import com.kpis.afoozo.databinding.PopupDiscounOfferBinding;
import com.kpis.afoozo.databinding.RewardsNotificationLayoutBinding;
import com.kpis.afoozo.databinding.SuccessPopupLayoutBinding;
import com.kpis.afoozo.databinding.TaxInfoLayoutBinding;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Created by wingstud on 09-04-2018.
 */

public class Utils {
    private static AlertDialog retryCustomAlert;
    private static Dialog apiCallingProgressDialog;
    private static JSONObject jsonObject1;
    private static String message;
    public static boolean isShown = false;

    private static boolean isProgressDialogShowing;

    private static Dialog successAlert;
    private static Dialog notificationAlert;
    private static Dialog offerAlert;
    private static MediaPlayer mediaPlayer;

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),
                    null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }


    public static void setDeviceSreenH(Activity act) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        SharedPref.setScreenW(act, width);
    }

    public static Drawable setTintColorIcon(Context context, int drawableIcon) {
        final Drawable icons = context.getResources().getDrawable(drawableIcon);
        icons.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        return icons;
    }


    //to start any activity.
    public static void startActivityWithFinish(Context context, Class<?> class1) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(context, class1);
        ((Activity) context).startActivity(intent);
        ((Activity) context).finish();
    }

    public static void startActivity(Context context, Class<?> class1) {
        Intent intent = new Intent();
        intent.setClass(context, class1);
        ((Activity) context).startActivity(intent);
    }

    public static void logout(Context mContext) {
//        ZohoSalesIQ.unregisterVisitor();
        SharedPref.setLogin(mContext, false);
        SharedPref.setCartCount(mContext, 0);
        SharedPref.setIsProfileUpdate(mContext, false);
        SharedPref.setUserModelJSON(mContext, "");
        SharedPref.setUserId(mContext, "");
        SharedPref.setBasicAuth(mContext, "");
        startActivityWithFinish(mContext, SplashActi.class);
        isShown = false;
    }

    public static void setImage(Context mContext, ImageView imageView, String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .priority(Priority.IMMEDIATE)
//                .placeholder(R.drawable.ic_notfound_placeholder)
//                .error(R.drawable.ic_notfound_placeholder)
//                .fallback(R.drawable.ic_notfound_placeholder)
                .into(imageView);
    }

    public static void setImage(Context mContext, final ImageView imageView, final ProgressBar progressBar, String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        Glide.with(mContext)
                .load(imageUrl)
                .priority(Priority.IMMEDIATE)
//                .error(R.drawable.ic_notfound_placeholder)// TODO change with no image.
//                .placeholder(R.drawable.ic_notfound_placeholder)
//                .fallback(R.drawable.ic_notfound_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void setPostImage(Context mContext, ImageView imageView, String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .priority(Priority.IMMEDIATE)
//                .placeholder(R.drawable.ic_placeholder)
//                .error(R.drawable.ic_placeholder)
//                .fallback(R.drawable.ic_placeholder)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
                .into(imageView);
    }

    //will check whether device is connected to any internet connection or not.
    public static boolean isDeviceOnline(Context context) {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;

        if(context == null)  return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }

            else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status","Network is available : FALSE ");
        return false;
    }

    public static Bitmap createThumbnailFromPath(String filePath, int type){
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }

    public static String getMACAddress(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static String getUniqueId() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    public static void setRadioGroup(RadioGroup radioGroup, String value) {
        try {
            int getChild = radioGroup.getChildCount();
            //   Log.d("getchild", "" + getChild);
            for (int i = 0; i < getChild; i++) {
                int selectedId = radioGroup.getChildAt(i).getId();
                RadioButton radioSexButton = (RadioButton) radioGroup.findViewById(selectedId);
                // Log.d("getchild", "" + radioSexButton.getText().toString());
                if (radioSexButton.getText().toString().equals(value)) {
                    radioSexButton.setChecked(true);
                }                // Log.d("getchild", "" + getChild);
            }
        } catch (NullPointerException e) {
            Log.d("Null_Pointer", e.toString());
        }
    }

    public static String getTextfromRadioGroup(RadioGroup radioGroup) {
        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioSexButton = (RadioButton) radioGroup.findViewById(selectedId);
            return radioSexButton.getText().toString();
        } catch (NullPointerException e) {
            return "null";
        }
    }


    public static void shareWithFriends(Context mContext, String refferCode){
        try {
            final String appPackageName = mContext.getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + appPackageName + " \n\n";
            sAux = sAux + "My Referal code : " + refferCode;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            mContext.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public static void shareGiftCode(Context mContext, String message){
        try {
            final String appPackageName = mContext.getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
            String sAux = message;
            sAux = sAux + "\n\nGet Afoozo Android app now from:\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + appPackageName + " \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            mContext.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public static void shareEvents(Context mContext, String message){
        try {
            final String appPackageName = mContext.getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
            String sAux = message;
            sAux = sAux + "\n\nGet Afoozo Android app now from:\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + appPackageName;
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            mContext.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }


    public static AlertDialog retryAlertDialog(Context mContext, String title, String
            msg, String firstButton, String SecondButton, View.OnClickListener secondButtonClick) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.retry_alert, null);
        dialogBuilder.setView(dialogView);

        TextView txtRAMsg = (TextView) dialogView.findViewById(R.id.txtRAMsg);
        TextView txtRAFirst = (TextView) dialogView.findViewById(R.id.txtRAFirst);
        TextView txtRASecond = (TextView) dialogView.findViewById(R.id.txtRASecond);
        View deviderView = (View) dialogView.findViewById(R.id.deviderView);

        txtRAMsg.setText(msg);

        if (firstButton.length() == 0) {
            txtRAFirst.setVisibility(View.GONE);
            deviderView.setVisibility(View.GONE);
        } else {
            txtRAFirst.setVisibility(View.VISIBLE);
            txtRAFirst.setText(firstButton);
        }

        if (SecondButton.length() == 0) {
            txtRASecond.setVisibility(View.GONE);
            deviderView.setVisibility(View.GONE);
        } else {
            txtRASecond.setVisibility(View.VISIBLE);
            txtRASecond.setText(SecondButton);
        }

        txtRAFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryCustomAlert.dismiss();
                isShown = false;
            }
        });

        if (secondButtonClick == null) {
            secondButtonClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryCustomAlert.dismiss();
                    isShown = false;
                }
            };
        }
        txtRASecond.setOnClickListener(secondButtonClick);

        if (!isShown) {

            retryCustomAlert = dialogBuilder.create();
            retryCustomAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            retryCustomAlert.show();
            isShown = true;
        }
        return retryCustomAlert;
    }

    public static void dismissRetryAlert() {
        if (retryCustomAlert != null) {
            isShown = false;
            retryCustomAlert.dismiss();
        }
    }

    public static void showSuccessPopUp(Context context, String message, View.OnClickListener clickListener) {
        successAlert = new Dialog(context);
        successAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = successAlert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        SuccessPopupLayoutBinding successBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.success_popup_layout, null, false);
        successAlert.setContentView(successBinding.getRoot());

        successBinding.txtPopUpMsg.setText(message);

//        alert.setContentView(R.layout.buy_popup_layout);
        successBinding.btnOk.setOnClickListener(clickListener);

        successAlert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        successAlert.setCancelable(false);
        successAlert.show();
    }

    public static void dismissSuccessAlert(){
        if (successAlert != null)
            successAlert.dismiss();
    }

    public static void showNotificationPopUp(Context context, String message, String imageUrl, View.OnClickListener clickListener) {
        notificationAlert = new Dialog(context);
        notificationAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notificationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = notificationAlert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        RewardsNotificationLayoutBinding successBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rewards_notification_layout, null, false);
        notificationAlert.setContentView(successBinding.getRoot());

        if (!TextUtils.isEmpty(imageUrl))
            Utils.setImage(context, successBinding.imvNotification, imageUrl);
        successBinding.txtPopUpMsg.setText(message);

//        alert.setContentView(R.layout.buy_popup_layout);
        successBinding.btnView.setOnClickListener(clickListener);

        notificationAlert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        notificationAlert.setCancelable(false);
        notificationAlert.show();
    }

    public static void dismissNotificationAlert(){
        if (notificationAlert != null)
            notificationAlert.dismiss();
    }

    public static void showDefaultAlert(final Context mContext, String msg, final Class<?> cls) {
//        new android.support.v7.app.AlertDialog.Builder(mContext);
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (cls != null) {
                            startActivityWithFinish(mContext, cls);
                        }
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }


    public static boolean isValidName(Context act, String fName) {
        if (fName.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_name_blank), act.getResources().getString(R.string.Ok), "", null);
        }
//       else if (fName.trim().length() < Constants.MINIMUMLENGTHOFNAME) {
//            showDefaultAlert(act, act.getString(R.string.name_error_msg),null);
//        }
        else if (checkStringsContainsOnlySpecialChar(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_special_characters_not_allowed), act.getResources().getString(R.string.Ok), "", null);
        } else if (isNumeric(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_numbers_not_allowed), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }
        return false;
    }

    public static boolean isValidCardNo(Context act, String cardNo) {
        if (cardNo.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_card_no_blank), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }

        return false;
    }

    public static boolean isValidExpiryDate(Context act, String expiry) {
        if (!TextUtils.isEmpty(expiry) && expiry.length()<5) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_expiry_date), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }

        return false;
    }

    public static boolean isValidCVV(Context act, String cvv) {
        if (cvv.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_cvv_blank), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }

        return false;
    }

    public static boolean isValidNickName(Context act, String nick) {
        if (nick.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_nick_name_blank), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }

        return false;
    }

    public static boolean isValidUserName(Context act, String fName) {
        if (fName.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_user_name_blank), act.getResources().getString(R.string.Ok), "", null);
        }
//       else if (fName.trim().length() < Constants.MINIMUMLENGTHOFNAME) {
//            showDefaultAlert(act, act.getString(R.string.name_error_msg),null);
//        }
        else if (checkStringsContainsOnlySpecialChar(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_special_characters_not_allowed_user_name), act.getResources().getString(R.string.Ok), "", null);
        } else if (isNumeric(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_numbers_not_allowed_user_name), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }
        return false;
    }

    public static boolean isValidCompanyName(Context act, String fName) {
        if (fName.trim().length() <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_firm_name_blank), act.getResources().getString(R.string.Ok), "", null);
        }
//       else if (fName.trim().length() < Constants.MINIMUMLENGTHOFNAME) {
//            showDefaultAlert(act, act.getString(R.string.name_error_msg),null);
//        }
        else if (checkStringsContainsOnlySpecialChar(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_special_characters_not_allowed_in_firm), act.getResources().getString(R.string.Ok), "", null);
        } else if (isNumeric(fName)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.only_numbers_not_allowed_in_firm), act.getResources().getString(R.string.Ok), "", null);
        } else {
            return true;
        }
        return false;
    }
//
    public static boolean isValidDOB(Context act, String dob) {
        if (TextUtils.isEmpty(dob)) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_dob_blank), act.getResources().getString(R.string.Ok), "", null);
        } else
            return true;
//
        return false;
    }

    public static boolean isValidAnniversary(Context act, long dob) {
        if (dob <= 0) {
//            Toast.makeText(act.getApplicationContext(),act.getResources().getString(R.string.error_user_name_blank),Toast.LENGTH_SHORT).show();
//            showDefaultAlert(act, act.getResources().getString(R.string.error_user_name_blank), null);
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getResources().getString(R.string.error_anniversary_blank), act.getResources().getString(R.string.Ok), "", null);
        } else
            return true;
//
        return false;
    }
//
//
//
    public static boolean isValidEmail(Context act, String email) {
        boolean b = true;
        if (TextUtils.isEmpty(email)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_email_blank), act.getResources().getString(R.string.Ok), "", null);
        } else {
            b = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (!b) {
                retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.enter_valid_email), act.getResources().getString(R.string.Ok), "", null);
            }
        }
        return b;
    }
//
    public static boolean isValidAddress(Context act, String address) {
        boolean b = true;
        if (TextUtils.isEmpty(address)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_address_blank), act.getResources().getString(R.string.Ok), "", null);
        }
        return b;
    }
    public static boolean isValidCompleteAddress(Context act, String address) {
        boolean b = true;
        if (TextUtils.isEmpty(address)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_complete_address_blank), act.getResources().getString(R.string.Ok), "", null);
        }
        return b;
    }
    public static boolean isValidRoomFlatNo(Context act, String address) {
        boolean b = true;
        if (TextUtils.isEmpty(address)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_room_number_flat_no_blank), act.getResources().getString(R.string.Ok), "", null);
        }
        return b;
    }
    public static boolean isValidBulding(Context act, String address) {
        boolean b = true;
        if (TextUtils.isEmpty(address)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_bulding_name_blank), act.getResources().getString(R.string.Ok), "", null);
        }
        return b;
    }

    public static boolean isValidDestinationAddress(Context act, String refferal) {
        boolean b = true;
        if (TextUtils.isEmpty(refferal)) {
            b = false;
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_destination_address_blank), act.getResources().getString(R.string.Ok), "", null);
        }
        return b;
    }
//
//    //
    public static boolean isValidMobileNumber(Context act, String phone) {
        boolean b = true;
        if (TextUtils.isEmpty(phone) || phone.length() < 10 ) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_mobile_number_blank), act.getResources().getString(R.string.Ok), "", null);
            b = false;
        }
//        else if () {
//            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_mobile_number_wrong), act.getResources().getString(R.string.Ok), "", null);
//            b = false;
//        }
        return b;
    }


//    public static boolean isValidZipCode(Activity act, String phone) {
//        boolean b = true;
//        if (TextUtils.isEmpty(phone)) {
//            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_zip_code_blank), act.getResources().getString(R.string.Ok), "", null);
//            b = false;
//        } else if (!Pattern.matches("[0-9]{5,6}", phone)) {
//            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_zip_code_wrong), act.getResources().getString(R.string.Ok), "", null);
//            b = false;
//        }
//        return b;
//    }
//
//
//    //
    public static boolean isValidPassword(Context act, String pass) {
        boolean b = true;
        if (TextUtils.isEmpty(pass)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_password_blank), act.getResources().getString(R.string.Ok), "", null);
            b = false;
        }
//        else if (!Pattern.matches("[^\\s]{6,15}", pass)) {
//            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_password_pattern), act.getResources().getString(R.string.Ok), "", null);
//            b = false;
//        }
        return b;
    }
//
//    //
    public static boolean isPasswordMatch(Context act, String pass, String cPass) {
        boolean b = true;
        if (!cPass.equals(pass)) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), act.getString(R.string.error_password_match), act.getResources().getString(R.string.Ok), "", null);
            b = false;
        }
        return b;
    }

    public static boolean checkStringsContainsOnlySpecialChar(String inputString) {
        boolean found = false;
        try {
            String splChrs = "/^[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$/";
            found = inputString.matches("[" + splChrs + "]+");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return found;
    }

    public static boolean isNumeric(String str) {
        try {
            long d = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void progressDialog(Context mContext, String title) {
        if (!isProgressDialogShowing) {
            isProgressDialogShowing = true;

            apiCallingProgressDialog = new AppCompatDialog(mContext);
            apiCallingProgressDialog.setCancelable(false);
            apiCallingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));
            apiCallingProgressDialog.setContentView(R.layout.progress_alert);
            apiCallingProgressDialog.show();
        }
    }

    public static void dismissProgressDialog() {
        if (isProgressDialogShowing) {
            apiCallingProgressDialog.dismiss();
            isProgressDialogShowing = false;
//            apiCallingProgressDialog = null;
        }
    }

//    public static void makePayment(Context mContext, okhttp3.Callback callback1, String test_id, String rewards_redeem, String total_pay_payment, String payment_referance_no, String registration_id) {
//
//        JSONObject params = CmdFactory.createMakePaymentCmd(test_id, rewards_redeem, total_pay_payment, payment_referance_no, registration_id);
//        NetworkManager.requestForAPI(mContext, callback1, Constants.VAL_POST, AppUrls.STUDENT_MAKE_PAYMENT, params.toString(), true);
//    }
//
//    public static JSONObject getObjectFromResponse(Response response) {
//        jsonObject1 = null;
//
//        try {
//            String strResponse = response.body().string();
//            if (strResponse != null && !strResponse.isEmpty()) {
//
//                jsonObject1 = new JSONObject(strResponse);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return jsonObject1;
//    }

//    public static void manageFailure(final Activity activity, final IOException e, final View v, final View.OnClickListener retryClick) {
//
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Util.dismissProgressDialog();
//                if (v != null)
//                    v.setEnabled(true);
//                if (retryClick != null) {
//                    Util.retryAlertDialog(activity, activity.getResources().getString(R.string.app_name), activity.getResources().getString(R.string.Server_not_responding), activity.getResources().getString(R.string.No), activity.getResources().getString(R.string.Retry), retryClick);
//                }
//                else
//                    Util.showDefaultAlert(activity, "server not working. please try later.", null);
//            }
//        });
//
//
//    }


    public static boolean isValidResponse(JSONObject jsonObject) {
        boolean b = false;
        try {
            if (jsonObject != null && jsonObject.getBoolean(Constants.FLD_STATUS)) {
                b = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return b;
    }

    //parse the response coming from server using gson library.
    public static Object getJsonToClassObject(String jsonStr, Class<?> classs) {
        try {
            Gson gson = new GsonBuilder().create();
            return (Object) gson.fromJson(jsonStr, classs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void replaceFrg(FragmentActivity ctx, Fragment frag, boolean addToBackStack,
                                  int resId) {
        FragmentManager fm = ctx.getSupportFragmentManager();
        int addedFrgCount = fm.getBackStackEntryCount();
        FragmentTransaction ft = fm
                .beginTransaction();
        if (resId == Constants.DEFAULT_ID) {
            resId = R.id.fmContainer;
        }
        ft.replace(resId, frag, frag.getClass().getName());
        if (addToBackStack)
            ft.addToBackStack(frag.getClass().getName());
        ft.commitAllowingStateLoss();
    }


    public static Address getAddress(final Activity acti, double lat, double lon) {
        Geocoder geocoder = new Geocoder(acti, Locale.ENGLISH);

        Address address = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null) {
                address = addresses.get(0);
            } else {

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return address;
    }

    //to show a center toast.
    public static void showCenterToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    //will check whether device is connected to any internet connection or not.
//    public static boolean isDeviceOnline(Context context, boolean showPopUp, View.OnClickListener retryClick) {
//        boolean isNetAvailable = false;
//
//
//        try {
//            ConnectivityManager cm = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (cm.getActiveNetworkInfo() != null
//                    && cm.getActiveNetworkInfo().isAvailable()
//                    && cm.getActiveNetworkInfo().isConnected()) {
//                isNetAvailable = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            isNetAvailable = false;
//        }
//
////        try {
////            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
////            isNetAvailable= cm.getActiveNetworkInfo().isConnectedOrConnecting();
////        } catch (Exception e) {
////            isNetAvailable = false;
////            e.printStackTrace();
////        }
//        if (!isNetAvailable && showPopUp) {
//            Util.retryAlertDialog(context, context.getResources().getString(R.string.app_name), context.getResources().getString(R.string.msg_internet_connection), context.getResources().getString(R.string.No), context.getResources().getString(R.string.Retry), retryClick);
//        }
//        return isNetAvailable;
//    }

    public static String getImagePathFromResp(String imagePath, String name) {
        String imageUrl = imagePath + name;
        return imageUrl;
    }


//    public static void exportDB(Activity activity, String backupDBPath) {
//        File sd = new File(Environment.getExternalStorageDirectory()+"/rswater_backup/");
//        File data = Environment.getDataDirectory();
//        if(!sd.exists())
//            sd.mkdir();
//        FileChannel source = null;
//        FileChannel destination = null;
//        String currentDBPath = "/data/" + activity.getApplicationContext().getPackageName() + "/databases/" + DatabaseHelper.DATABASE_NAME;
//        File currentDB = new File(data, currentDBPath);
//        File backupDB = new File(sd, backupDBPath);
//        try {
//            source = new FileInputStream(currentDB).getChannel();
//            destination = new FileOutputStream(backupDB).getChannel();
//            destination.transferFrom(source, 0, source.size());
//            source.close();
//            destination.close();
//            Toast.makeText(activity, "DB Exported in your local storage!", Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static List<String> getWeekDayNames(Date startDate, Date endDate) {
        List<String> days = new ArrayList<String>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {

            days.add(startCal.getTime()+"");

            return Collections.unmodifiableList(days);
        }
// swap values
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {

            days.add(startCal.getTime()+"");

            startCal.add(Calendar.DAY_OF_MONTH, 1);

        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        return Collections.unmodifiableList(days);
    }

    public static String getFormetedDateTime(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("dd-MM-yy, hh:mm a");
        return df.format(date);
    }

    public static int getFormetedHour(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("HH");
        return Integer.parseInt(df.format(date));
    }

    public static int getFormetedMinute(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("mm");
        return Integer.parseInt(df.format(date));
    }

    public static int getFormetedDay(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("dd");
        return Integer.parseInt(df.format(date));
    }
    public static String getFormetedDate(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    public static String getFormetedDateMonth(long value){
        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("dd MMM, yyyy");
        return df.format(date);
    }

    public static String getFormetedTimeForEvent(long value){

        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(date);
    }

    public static String getDateTimeForOrders(long value){

        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
        return df.format(date);
    }

    public static String getDateFromTimeStamp(long value){

        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("dd");
        return df.format(date);
    }

    public static String getYearFromTimeStamp(long value){

        Date date = new Date(value);
        DateFormat df = new SimpleDateFormat("yyyy");
        return df.format(date);
    }

    public static String getDatefromCreatedAt(String value){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(value);
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getTimefromCreatedAt(String value){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(value);
//            return sdf.format(date);
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
            return sdf1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }


    public static void sendLocalBroadCast1(Context context, String intentType) {
        Intent intent = new Intent(intentType);
//        if (!TextUtils.isEmpty(orderId) && !TextUtils.isEmpty(noOfPeople)) {
//            intent.putExtra(Constants.FLD_ZOOP_ORDER_ID, orderId);
//            intent.putExtra(Constants.FLD_NO_OF_PEOPLE, noOfPeople);
//        }
        intent.putExtra("abc", "abc");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static AlertDialog consumeDialog(Context mContext) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_consume, null);
        dialogBuilder.setView(dialogView);
        retryCustomAlert = dialogBuilder.create();
        retryCustomAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        retryCustomAlert.show();
        return retryCustomAlert;
    }

    public static AlertDialog reserveEventDialog(Context mContext) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_reserve_event, null);
        dialogBuilder.setView(dialogView);
        retryCustomAlert = dialogBuilder.create();
        retryCustomAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        retryCustomAlert.show();
        return retryCustomAlert;
    }

    public static AlertDialog reserveTableDialog(Context mContext) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_reserve_table, null);
        dialogBuilder.setView(dialogView);
        retryCustomAlert = dialogBuilder.create();
        retryCustomAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        retryCustomAlert.show();
        return retryCustomAlert;
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    public static void shaveEventInCalendar(Context context, String title, long startTime, long entTime, String description){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                entTime);
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
        intent.putExtra(CalendarContract.Events.DESCRIPTION,description);
        ((Activity) context).startActivity(intent);
    }

    public static void vibrateOnClick(Context context) {
        try {

            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {

                if (vibrator.hasVibrator()) {
//                    if (vibrator.hasAmplitudeControl())
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
//                    else
//                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                vibrator.vibrate(200);
//            ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
            }
        } catch (Exception e){

        }
    }

    public static void checkAndOnTouchVibrationSetting(Context mContext){
        boolean isVibrateOnTouchEnabled = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0;

        if (!isVibrateOnTouchEnabled)
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
    }


    public static int getPixelDensity(Context mContext) {
        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics()
        );
        return px;
    }

    public static void showTaxInfoPopUp(Context mContext, ArrayList<TaxesBean> list) {
        Dialog alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TaxInfoLayoutBinding popBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.tax_info_layout, null, false);
        alert.setContentView(popBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);

        popBinding.rvTaxes.setLayoutManager(new LinearLayoutManager(mContext));
        popBinding.rvTaxes.setItemAnimator(new DefaultItemAnimator());

        TaxesAdapter taxAdapter = new TaxesAdapter(mContext, list);
        popBinding.rvTaxes.setAdapter(taxAdapter);
        taxAdapter.notifyDataSetChanged();


        popBinding.txtInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(true);
        alert.show();
    }

    public static void checkIsGPSEnabled(Context mContext, ResultCallback<LocationSettingsResult> resultCallback){
        GoogleApiClient mGoogleApiClient = null;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1 * 1000);
            locationRequest.setFastestInterval(1 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(resultCallback);
        }
    }

    public static void copyCode(Context mContext, String code){
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(code, code);
        clipboard.setPrimaryClip(clip);
        showCenterToast(mContext, mContext.getString(R.string.copy_to_clipboard));
    }

    public static void showOfferPopUp(Activity mContext, boolean showAddMore, String message, View.OnClickListener skipClick, View.OnClickListener addMoreClick){
        offerAlert = new Dialog(mContext);
        offerAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        offerAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        PopupDiscounOfferBinding discountBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_discoun_offer, null, false);
        offerAlert.setContentView(discountBinding.getRoot());


        Glide.with(mContext).asGif().load(R.raw.offers).into(discountBinding.imvOffer);

        int resID= mContext.getResources().getIdentifier("offers_tone", "raw", mContext.getPackageName());
        mediaPlayer = MediaPlayer.create(mContext,resID);
        mediaPlayer.start();

        discountBinding.txtDiscountMsg.setText(message);


        if (showAddMore) {
            discountBinding.btnSkip.setVisibility(View.VISIBLE);

//            discountBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    offerAlert.cancel();
//                    placeOrderProcess();
//                }
//            });

            discountBinding.btnSkip.setOnClickListener(skipClick);

            discountBinding.btnAddMore.setOnClickListener(addMoreClick);

//            discountBinding.btnAddMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    offerAlert.cancel();
//                    finish();
//                }
//            });
        } else {
            discountBinding.btnSkip.setVisibility(View.GONE);
            discountBinding.btnAddMore.setText(mContext.getString(R.string.Ok));

//            discountBinding.btnAddMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    offerAlert.cancel();
//                }
//            });
            discountBinding.btnAddMore.setOnClickListener(addMoreClick);
        }


        offerAlert.setCancelable(false);
        offerAlert.show();
    }

    public static void hideOfferPopUp(){
        offerAlert.cancel();
    }

    public static void SingleClickCleverTapEvent(String eventName){
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(eventName);
    }

    public static Date getDatefromDOB(long value){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = new Date(value);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isValidCommonMessage(Context act, String fName, String message) {
        if (fName.length() <= 0 ) {
            retryAlertDialog(act, act.getResources().getString(R.string.app_name), message, act.getResources().getString(R.string.Ok), "", null);
        } else
            return true;
//
        return false;
    }
}
