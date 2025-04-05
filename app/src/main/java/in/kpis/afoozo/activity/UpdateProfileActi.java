package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kpis.afoozo.BuildConfig;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.bean.UploadFileBean;
import in.kpis.afoozo.bean.UserBean;
import com.kpis.afoozo.databinding.ActivityUpdateProfileBinding;
import com.kpis.afoozo.databinding.ProfilePhotoAlertBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class UpdateProfileActi extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityUpdateProfileBinding binding;

    private boolean isFromCamera;
    private boolean isFromRegister;

    private int mYear;
    private int mMonth;
    private int mDay;

    private int CAMERA = 1;
    private int GALLERY = 2;
    private int requestPermissionForPhoto = 101;

    private Uri fileUri;
    private Uri mImageUri;

    private long dobInLong;
    private long anniversaryInLong;

    private static String IMAGE_DIRECTORY_NAME ="Afoozo";
    private static String filePath;
    private String croppedFilePath;
    private String imageUrl;
    private String uploadedImageUrl;
    private String name;
    private String email;
    private String mobile;

    private UploadFileBean uploadFileBean;

    private AlertDialog cameraAlert;

    private UserBean userBean;
    private boolean isFacebookLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        if (getIntent().getExtras() != null)
            isFromRegister = getIntent().getBooleanExtra(Constants.IS_FROM_REGISTER, false);

        mContext = UpdateProfileActi.this;
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
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.update_profile));

        binding.etDob.setKeyListener(null);
        binding.etAnniversary.setKeyListener(null);

        if (isFromRegister)
            binding.llWhatsApp.setVisibility(View.VISIBLE);
        else
            binding.llWhatsApp.setVisibility(View.GONE);

        setData();
    }

    @Override
    public void onBackPressed() {
        if (!isFromRegister)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCancel:
                cameraAlert.dismiss();
                break;
            case R.id.txtFromGallery:
                cameraAlert.dismiss();
                isFromCamera = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAndRequestPermissions()) {
                        txtFromGallery();
                    }
                } else {
                    txtFromGallery();
                }
                break;
            case R.id.txtFromCamera:
                cameraAlert.dismiss();
                isFromCamera = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAndRequestPermissions()) {
                        txtFromCamera();
                    }
                } else {
                    txtFromCamera();
                }
                break;
        }
    }

    private void setData() {
        userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        if (userBean != null){
            binding.setUserData(userBean);
            dobInLong = userBean.getDateOfBirth();
            anniversaryInLong = userBean.getAnniversaryDate();
            uploadedImageUrl = userBean.getUserImage();

            if (!TextUtils.isEmpty(userBean.getUserImage()))
                Utils.setImage(mContext, binding.imvUser, userBean.getUserImage());
            else if(userBean.getGender().equals("MALE")){
                Drawable res = getResources().getDrawable(R.drawable.profile_thumb);
                binding.imvUser.setImageDrawable(res);
            }else if(userBean.getGender().equalsIgnoreCase("FEMALE")) {
                Drawable res = getResources().getDrawable(R.drawable.girl);
                binding.imvUser.setImageDrawable(res);
            }
        }
    }

    /**
     * This Method is for choose Date of birth
     * dobInLong =  hold value of date in long
     */
    public void setDob(final View v) {
        // Get Current Date
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);
//        c.add(Calendar.YEAR, - 13);
//        long maxDate = c.getTime().getTime();
//        c.add(Calendar.YEAR, - 45);
//        long minDate = c.getTime().getTime();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date=String.format("%02d-%02d-%01d", dayOfMonth,monthOfYear+1,year);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

                        if (v.getId() == R.id.etDob) {
                            binding.etDob.setText(date);
                            dobInLong = cal.getTimeInMillis();
                        } else {
                            binding.etAnniversary.setText(date);
                            anniversaryInLong = cal.getTimeInMillis();
                        }
                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
//        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    public void selectPhotoVideoAlert(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        ProfilePhotoAlertBinding photoAlertBinding = DataBindingUtil.inflate(inflater, R.layout.profile_photo_alert, null, false);
        View dialogView = photoAlertBinding.getRoot();
        dialogBuilder.setView(dialogView);

        photoAlertBinding.txtFromGallery.setOnClickListener(this);
        photoAlertBinding.txtFromCamera.setOnClickListener(this);
        photoAlertBinding.txtCancel.setOnClickListener(this);

        cameraAlert = dialogBuilder.create();
        cameraAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Setting dialogview
        Window window = cameraAlert.getWindow();
        window.setGravity(Gravity.CENTER);
        cameraAlert.show();
    }

    private boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        int writeExternalPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (readExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

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
            if (isFromCamera) {
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            }

            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (isFromCamera) {
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        txtFromCamera();

                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

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
                                                break;
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(mContext, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        txtFromGallery();

                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        showDialogOK("Storage Permission required for this app",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
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
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void txtFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fileUri = getOutputMediaFileUri2(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
            startActivityForResult(intent, CAMERA);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(mContext, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        Uri photoUri = Uri.fromFile(getOutputMediaFile());
        mImageUri = photoUri;
        return photoUri;
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        filePath = mediaFile.getAbsolutePath();
        return mediaFile;
    }

    public Uri getOutputMediaFileUri2(int type) {
//        return Uri.fromFile(getOutputMediaFile(type));

        Uri photoUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ContentResolver resolver = mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + timeStamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + IMAGE_DIRECTORY_NAME );

            photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            File newFile = getOutputMediaFile();
            Log.e("MyPath", BuildConfig.APPLICATION_ID);
            photoUri = FileProvider.getUriForFile(mContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    newFile);
        }
        mImageUri = photoUri;
        return photoUri;
    }

    private void txtFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select"), GALLERY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            beginCrop(mImageUri);
//            filePath1 = filePath;
//            setImage(filePath1);

        } else if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
//            filePath2 = getRealPathFromURI(selectedImageUri);
//            setImage(filePath2);
            beginCrop(selectedImageUri);

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
//        Uri destination = Uri.fromFile(new File(mContext.getCacheDir(), "cropped.jpg"));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        Uri destination = Uri.fromFile(new File(mContext.getCacheDir(), "IMG_"+ timeStamp + ".jpg"));

        croppedFilePath = destination.getPath();
//        Crop.of(source, destination).asSquare().start(UpdateProfileActi.this);
        Crop.of(source, destination).start(UpdateProfileActi.this);
    }

    public void handleCrop(int resultCode, Intent result) {

    if (resultCode == RESULT_OK) {

            Utils.setImage(mContext, binding.imvUser, croppedFilePath);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Utils.showCenterToast(mContext, Crop.getError(result).getMessage());
        }
    }
    public void updateProcess(View view){
        name = binding.etName.getText().toString();
        email = binding.etEmail.getText().toString();
        mobile = binding.etMobile.getText().toString();

        if (Utils.isValidName(mContext, name) &&
                Utils.isValidEmail(mContext, email) &&
                Utils.isValidMobileNumber(mContext, mobile) &&
                Utils.isValidDOB(mContext, binding.etDob.getText().toString())) {
            if (!TextUtils.isEmpty(croppedFilePath))
                callUploadImageApi();
            else {
                updateProfile();
            }
        }
    }

    private void updateProfile() {
        UserBean bean = new UserBean();
        bean.setFullName(name);
        bean.setEmail(email);
        bean.setMobile(mobile);
        bean.setDateOfBirth(dobInLong);
        bean.setAnniversaryDate(anniversaryInLong);
        bean.setUserImage(uploadedImageUrl);
        bean.setGender(binding.rbMale.isChecked() ? "MALE" : "FEMALE");

        if(!SharedPref.isProfileUpdate(mContext)){
            bean.setCreateCustomerOnPaymentGateway(true);
        }else{
            bean.setCreateCustomerOnPaymentGateway(false);
        }

        String json = new Gson().toJson(bean);
        callUpdateProfileApi(json);
    }

    private void callUploadImageApi() {

        new NetworkManager(UploadFileBean.class, new NetworkManager.OnCallback<UploadFileBean>() {
            @Override
            public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                if (responseClass.isSuccess() && responseClass.getResponsePacket() != null){
                    uploadFileBean = (UploadFileBean) responseClass.getResponsePacket();
                    uploadedImageUrl = uploadFileBean.getFileUrl();
                    updateProfile();
                } else {
                    Utils.showCenterToast(mContext, responseClass.getMessage());
                }
            }

            @Override
            public void onFailure(boolean success, String response, int which) {
                Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
            }
        }).uploadFileToServer(mContext, AppUrls.UPLOAD_FILE, croppedFilePath, true, AppUrls.REQUESTTAG_UPLOADFILE);

    }

    private void callUpdateProfileApi(String json) {

        try {
            new NetworkManager(UserBean.class, new NetworkManager.OnCallback<UserBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        UserBean user = (UserBean) responseClass.getResponsePacket();
                        userBean.setRazorPayCustomerId(user.getRazorPayCustomerId());



//                        if (isFromRegister){
//                            ZohoSalesIQ.Visitor.setName(userBean.getFullName());
//                            ZohoSalesIQ.Visitor.setContactNumber(userBean.getMobile());
//                        }

                        updateData();

                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.UPDATE_PROFILE, json, "Loading...." ,true, AppUrls.REQUESTTAG_UPDATEPROFILE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void updateData() {
        userBean.setFullName(name);
        userBean.setMobile(mobile);
        userBean.setEmail(email);
        userBean.setGender(binding.rbMale.isChecked() ? "MALE" : "FEMALE");
        userBean.setUserImage(uploadedImageUrl);
        userBean.setDateOfBirth(dobInLong);
        if (anniversaryInLong > 0)
            userBean.setAnniversaryDate(anniversaryInLong);

        String json = new Gson().toJson(userBean);
        SharedPref.setUserModelJSON(mContext, json);

        sendDataToCleverTap(userBean);

        if (isFromRegister) {
            SharedPref.setIsProfileUpdate(mContext, true);
            Utils.startActivityWithFinish(mContext, Dashboard.class);
        }else {
            finish();
        }
    }
     /*  Claver tab
     * */

    private void sendDataToCleverTap(UserBean user){
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", user.getFullName()); // String
        profileUpdate.put("Identity", user.getUuid()); // String or number
//        profileUpdate.put("LoginFrom", facebookData != null ? "Facebook" :app "Mobile"); // String or number
//        if (facebookData != null && facebookData.getFacebookUserId() != null)
//            profileUpdate.put("Facebook_Id", user.getFacebookUserId()); // Email address of the user
        profileUpdate.put("user_email", user.getEmail()); // Email address of the user
        profileUpdate.put("Email", user.getEmail()); // Email address of the user
        profileUpdate.put("user_mobile", user.getMobile()); // Phone (with the country code, starting with +)
        profileUpdate.put("Phone", "+91"+user.getMobile()); // Phone (with the country code, starting with +)
        profileUpdate.put("Gender", user.getGender().equalsIgnoreCase("Male")?"M":"F"); // Can be either M or F
// profileUpdate.put("Married", user.getAnniversaryDate() != 0 ? "Y" : "N");
//        if (user.getAnniversaryDate() != null && Utils.getDatefromDOB(user.getAnniversaryDate()) != null)
//            profileUpdate.put("Anniversary", Utils.getDatefromDOB(user.getAnniversaryDate())); // Can be either Y or N
        if (user.getDateOfBirth() != 0 && Utils.getDatefromDOB(user.getDateOfBirth()) != null)
            profileUpdate.put("DOB", Utils.getDatefromDOB(user.getDateOfBirth())); // Date of Birth. Set the Date object to the appropriate value first
// profileUpdate.put("Age", 28); // Not required if DOB is set
        profileUpdate.put("Tz", "Asia/Kolkata"); //an abbreviation such as "PST", a full name such as "America/Los_Angeles",
//or a custom ID such as "GMT-8:00"
        profileUpdate.put("Photo", user.getUserImage()); // URL to the Image

        if (isFromRegister)
            profileUpdate.put("MSG-whatsapp", binding.cbWhatsApp.isChecked());

// optional fields. controls whether the user will be sent email, push etc.
// profileUpdate.put("MSG-email", true); // Disable email notifications
// profileUpdate.put("MSG-push", true); // Enable push notifications
// profileUpdate.put("MSG-sms", true); // Disable SMS notifications
// profileUpdate.put("MSG-dndPhone", false); // Opt out phone number from SMS notifications

        AppInitialization.getInstance().clevertapDefaultInstance.pushProfile(profileUpdate);
        Utils.SingleClickCleverTapEvent("Profile Updated");
    }





}
