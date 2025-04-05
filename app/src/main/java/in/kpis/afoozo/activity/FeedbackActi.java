package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.bean.SaveFeedbackBean;
import in.kpis.afoozo.bean.UserBean;
import com.kpis.afoozo.databinding.ActivityFeedbackBinding;
import com.kpis.afoozo.databinding.ActivityReserveNowBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FeedbackActi extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {

    private ActivityFeedbackBinding binding;
    private Toolbar toolbar;

    private Context mContext;

    private ArrayList<RestaurantBean> restaurantList = new ArrayList<>();

    private double liveLat;
    private double liveLong;

    private String name;
    private String mobile;

    private UserBean userBean;

    private ArrayList<String> restStringList = new ArrayList<>();

    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);

        mContext = FeedbackActi.this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
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
                finish();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.feedback));

        userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        binding.rbMusic.setOnRatingBarChangeListener(this);
        binding.rbQualityFood.setOnRatingBarChangeListener(this);
        binding.rbServices.setOnRatingBarChangeListener(this);
        binding.rbClean.setOnRatingBarChangeListener(this);
        binding.rbBehaviour.setOnRatingBarChangeListener(this);

        setData();
        updateLocation();
    }

    private void updateLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    getLiveLocation();
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        liveLat = location.getLatitude();
                        liveLong = location.getLongitude();

//                    setLocation();
                        Utils.dismissProgressDialog();
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                        callRestaurantListApi();
                    }
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void getLiveLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLat = location.getLatitude();
                    liveLong = location.getLongitude();

//                    setLocation();
                    Utils.dismissProgressDialog();
                    callRestaurantListApi();

                } else {
                    updateLocation();
                }
            });
        }
    }

    private void setData() {
        if (userBean != null){
            binding.etName.setText(userBean.getFullName());
            binding.etMobile.setText(userBean.getMobile());
        }
    }


    private void setRadioGroup(ArrayList<RestaurantBean> list){

        for (int i = 0; i < list.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(list.get(i).getTitle());
            radioButton.setId(i);
            binding.rgPayment.addView(radioButton);
        }
        binding.rgPayment.check(0);

        //set listener to radio button group
        binding.rgPayment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = binding.rgPayment.getCheckedRadioButtonId();
                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);
//                paymentType = radioBtn.getText()+"";
                Toast.makeText(FeedbackActi.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//        if (rating < 1){
//            ratingBar.setRating(1);
//        } else
            ratingBar.setRating(rating);
    }

    public void feedbackProcess(View view){
        name = binding.etName.getText().toString();
        mobile = binding.etMobile.getText().toString();

        if (Utils.isValidName(mContext, name) && Utils.isValidMobileNumber(mContext, mobile)){
//            if (binding.rbQualityFood.getRating() <= 0 || binding.rbServices.getRating() <= 0 || binding.rbClean.getRating() <= 0 || binding.rbBehaviour.getRating() <= 0 || binding.rbMusic.getRating() <= 0){
//                Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.please_rate_all_the_fields_first), "", getString(R.string.Ok), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Utils.dismissRetryAlert();
//                    }
//                });
//            } else
                callSaveFeedbackpi();
        }
    }

    private void setRestaurntData() {
        for (RestaurantBean bean : restaurantList) {
            restStringList.add(bean.getTitle());
        }

        ArrayAdapter<String> restAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, restStringList);
        restAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        binding.spRestaurant.setAdapter(restAdapter);
    }

    public void clearProcess(View view){
        binding.etName.setText("");
        binding.etMobile.setText("");
        binding.rbQualityFood.setRating(0);
        binding.rbServices.setRating(0);
        binding.rbClean.setRating(0);
        binding.rbBehaviour.setRating(0);
        binding.rbMusic.setRating(0);
        binding.etSuggestion.setText("");
        binding.etComment.setText("");

        binding.spRestaurant.setSelection(0);

//        binding.rgPayment.check(0);
    }

    private void callRestaurantListApi() {
        RestaurantRequestBean bean = new RestaurantRequestBean();

        bean.setOrderType("All");
        bean.setLatitude(liveLat);
        bean.setLongitude(liveLong);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<RestaurantBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RestaurantBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        restaurantList = responseClass1.getResponsePacket();

                        if (restaurantList != null && restaurantList.size() > 0)
                            setRestaurntData();
//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESTAURANT_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_RESTAURANTLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callSaveFeedbackpi() {
        SaveFeedbackBean bean = new SaveFeedbackBean();
        bean.setCustomerName(name);
        bean.setCustomerMobile(mobile);
//        bean.setRestaurantId("" + restaurantList.get(binding.rgPayment.getCheckedRadioButtonId()).getRecordId());
        bean.setRestaurantId("" + restaurantList.get(binding.spRestaurant.getSelectedItemPosition()).getRecordId());
        bean.setQualityOfFood("" + (int) binding.rbQualityFood.getRating());
        bean.setServiceQuality("" + (int) binding.rbServices.getRating());
        bean.setCleaning("" + (int) binding.rbClean.getRating());
        bean.setStaffBehavior("" + (int) binding.rbBehaviour.getRating());
        bean.setMusic("" + (int) binding.rbMusic.getRating());
        bean.setBestPartOfRestaurant(binding.etComment.getText().toString());
        bean.setSuggestion(binding.etSuggestion.getText().toString());
        float rating = (binding.rbQualityFood.getRating() + binding.rbServices.getRating() + binding.rbClean.getRating() + binding.rbMusic.getRating() + binding.rbBehaviour.getRating())/5;
        new Thread(new Runnable() {
            @Override
            public void run() {
                customCleverTapEvent(bean, rating);
            }
        }).start();

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                finish();
                            }
                        });
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_FEEDBACK, json, "Loading...", true, AppUrls.REQUESTTAG_SAVEFEEDBACK);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void customCleverTapEvent(SaveFeedbackBean bean, float rating){

        HashMap<String, Object> feedbackAction = new HashMap<String, Object>();
        feedbackAction.put("Quality of Food", bean.getQualityOfFood());
        feedbackAction.put("Music", bean.getMusic());
        feedbackAction.put("Services", bean.getServiceQuality());
        feedbackAction.put("Cleaniness of dining premises", bean.getCleaning());
        feedbackAction.put("Behaviour of Staff", bean.getStaffBehavior());
        feedbackAction.put("Location Id", bean.getRestaurantId());
//        feedbackAction.put("Location Name", bean.getRestaurantName());
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Feedback", feedbackAction);

    }

}
