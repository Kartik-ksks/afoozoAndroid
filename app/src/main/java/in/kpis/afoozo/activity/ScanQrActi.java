package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.google.gson.Gson;
import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.ConsumeBean;
import in.kpis.afoozo.bean.ScanQrBean;
import com.kpis.afoozo.databinding.ActivityScanQrBinding;
import com.kpis.afoozo.databinding.PopupGiftBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanQrActi extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 303;
    private Context mContext;

    private ActivityScanQrBinding binding;

    private CodeScanner mCodeScanner;

    private boolean isPermission;
    private boolean isFlashOn;

    private int requestPermissionForPhoto = 101;
    private int quantity;

    private String fromWhich;
    private ConsumeBean consumeBean;

    private Dialog alert;
    private PopupGiftBinding consumeBinding;
    private String restaurntId;
    private CameraManager mCameraManager;
    private String mCameraId;
    private Date date;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_qr);
        mContext = ScanQrActi.this;

        if (getIntent().getExtras() != null){
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);

            if (fromWhich.equals(getString(R.string.consume))){
                quantity = getIntent().getIntExtra(Constants.QUANTITY, 0);
                consumeBean = (ConsumeBean) getIntent().getSerializableExtra("consume_bean");
            } else
                restaurntId = getIntent().getStringExtra(Constants.RESTAURANT_ID);


            if(getIntent().getStringExtra(Constants.TITLE) != null)
                title = getIntent().getStringExtra(Constants.TITLE);
        }
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initialize() {
        isPermission = checkAndRequestPermissions();
        date = new Date();
        if (isPermission)
            showScaneScreen();
    }

    private void showScaneScreen() {
        Intent i = new Intent(ScanQrActi.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }

    private void showConsumePopUp(String scannedCode) {
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        consumeBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_gift, null, false);
        alert.setContentView(consumeBinding.getRoot());

        consumeBinding.txtPGItem.setText(consumeBean.getStealDealItemTitle());
        consumeBinding.txtPGStore.setText(consumeBean.getStealDealItemTitle());
        consumeBinding.txtPGQty.setText(quantity + " " + consumeBean.getConsumableUnitPostfix());


        consumeBinding.btnPGConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                callConsumeApi(scannedCode);
            }
        });

        alert.setCancelable(true);
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK)
        {
            if(data==null)
                showScaneScreen();
            //Getting the passed result
            String scanResult = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
//            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
            String scannedCode = scanResult.substring((scanResult.indexOf('?')+1), scanResult.length());
            if (scannedCode != null) {
                if (fromWhich.equals(getString(R.string.dine_in)))
                    callScanQrCodeApi(scannedCode);
                else if(fromWhich.equals(getString(R.string.cafe)))
                    callScanCafeQrCodeApi(scannedCode);
                else
                    showConsumePopUp(scannedCode);
            }

        } else
            finish();
    }

    private boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
//        int flashPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.FLASHLIGHT);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
//        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CAMERA);
//        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestPermissionForPhoto);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionForPhoto) {

            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                // Check for both permissions
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    isPermission = true;
                    showScaneScreen();

                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    showDialogOK("Camera and Storage Permission required for this app",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkAndRequestPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish();
                                            break;
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void callScanQrCodeApi(String code) {
        try {
            new NetworkManager(ScanQrBean.class, new NetworkManager.OnCallback<ScanQrBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        ScanQrBean scanBean = (ScanQrBean) responseClass.getResponsePacket();
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("dd");
                            scanBean.setDate(Integer.parseInt(dateFormat.format((date))));
                        } catch (Exception e){

                        }
                        if (scanBean != null && scanBean.getRestaurantUuid() != null) {
                            SharedPref.setScanBeanJson(mContext, new Gson().toJson(scanBean));
                            if (!TextUtils.isEmpty(restaurntId)){
                                Intent intent = new Intent(mContext, AddItemsNewActi.class);
                                intent.putExtra(Constants.SCAN_QR_BEAN, scanBean);
                                intent.putExtra(Constants.FROM_WHICH, fromWhich);
                                intent.putExtra(Constants.TITLE, title);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.SCAN_QR_BEAN, scanBean);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        showScaneScreen();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.SCAN_QR_CODE + code, "", "Loading...", true, AppUrls.REQUESTTAG_SCANQRCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callScanCafeQrCodeApi(String code) {
        Log.e("QrCode","####"+code);
        try {
            new NetworkManager(ScanQrBean.class, new NetworkManager.OnCallback<ScanQrBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        ScanQrBean scanBean = (ScanQrBean) responseClass.getResponsePacket();
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("dd");
                            scanBean.setDate(Integer.parseInt(dateFormat.format((date))));
                        } catch (Exception e){

                        }
                        if (scanBean != null && scanBean.getRestaurantUuid() != null ) {
                            if(fromWhich.equals(getString(R.string.cafe)))
                            SharedPref.setScanCafeBeanJson(mContext, new Gson().toJson(scanBean));

                            if (!TextUtils.isEmpty(restaurntId)){
                                Intent intent = new Intent(mContext, AddItemsNewActi.class);
                                intent.putExtra(Constants.SCAN_QR_BEAN, scanBean);
                                intent.putExtra(Constants.FROM_WHICH, fromWhich);
                                intent.putExtra(Constants.TITLE, title);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(Constants.SCAN_QR_BEAN, scanBean);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        showScaneScreen();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.SCAN_CAFE_QR_CODE + code, "", "Loading...", true, AppUrls.REQUESTTAG_SCANCAFEQRCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callConsumeApi(String code) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.QR_CODE, code);
            jsonObject.put(Constants.STEAL_DEAL_ITEM_RESERVATION_ID, consumeBean.getStealDealItemReservationId());
            jsonObject.put(Constants.QUANTITY, quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(ScanQrBean.class, new NetworkManager.OnCallback<ScanQrBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        showScaneScreen();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.STEAL_DEAL_ORDER, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_STEALDEALORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
