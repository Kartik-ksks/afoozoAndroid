package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.google.gson.Gson;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityScanQrBinding;
import com.kpis.afoozo.databinding.ActivityScanRoomBinding;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.kpis.afoozo.bean.ScanQrBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class ScanRoomActivity extends AppCompatActivity {

    private Context mContext;

    private ActivityScanRoomBinding binding;

    private CodeScanner mCodeScanner;
    private String restaurntId;
    private Date date;
    private String title = "";
    private boolean isPermission;
    private boolean isFlashOn;
    private static final int REQUEST_CODE_QR_SCAN = 303;
    private int requestPermissionForPhoto = 101;
    private int quantity;

    private String fromWhich;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_room);
        mContext = ScanRoomActivity.this;

        initialize();
    }

    private void showScaneScreen() {
        Intent i = new Intent(ScanRoomActivity.this, QrCodeActivity.class);
        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
    }


    private void initialize() {
        isPermission = checkAndRequestPermissions();

        date = new Date();

        if (isPermission)
            showScaneScreen();
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
                    callScanRoomApi(scannedCode);
            }

        } else
            finish();
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

    private void callScanRoomApi(String code) {
        try {
            new NetworkManager(ScanQrBean.class, new NetworkManager.OnCallback<ScanQrBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        ScanQrBean scanBean = (ScanQrBean) responseClass.getResponsePacket();
                        if (scanBean != null) {
                            if (scanBean.getRestaurantRecordId()!= 0 ){
                                Intent intent = new Intent(mContext, CheckOutActivity.class);

                                intent.putExtra(Constants.SCAN_QR_BEAN, scanBean.getRestaurantRecordId());
                                startActivity(intent);
                                 finish();
                            }else {
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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.SCAN_QR_CODEFOR_ROOM_BOOKING + code, "", "Loading...", true, AppUrls.REQUESTTAG_SCANQRCODEFORROOMBOOKING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}