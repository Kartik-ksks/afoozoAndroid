package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.adapter.AddressAdapter;
import in.kpis.afoozo.adapter.ReservationAdapter;
import in.kpis.afoozo.bean.BannerListBean;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import com.kpis.afoozo.databinding.ActivityRestaurantBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RestaurantActi extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityRestaurantBinding binding;

    private ReservationAdapter mAdapter;
    private String fromWhich;

    private ArrayList<RestaurantBean> restaurantList;

    private FusedLocationProviderClient mFusedLocationClient;

    private double liveLat = 0;
    private double liveLong = 0;
    private AddressBean addressBean;
    private ArrayList<AddressBean> list;

    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int REQUEST_ = 345;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private Location mLocation;
    private LocationCallback mLocationCallback;
    private int ADDRESS_REQUEST = 103;
    private SliderLayout sliderHome;
    private ArrayList<BannerListBean> shortBannerList = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        mContext = RestaurantActi.this;

        if (getIntent().getExtras() != null) {
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            if (fromWhich.equals(getString(R.string.delivery))){
                binding.llAddress.setVisibility(View.VISIBLE);
//                liveLat = getIntent().getDoubleExtra(Constants.LATITUDE, 0);
//                liveLong = getIntent().getDoubleExtra(Constants.LONGITUDE, 0);
                addressBean = (AddressBean) getIntent().getSerializableExtra(Constants.ADDRESS_BEAN);
                binding.txtAddress.setText(addressBean.getAddressLine1());
                binding.txtLocality.setText(addressBean.getAddressLine2());
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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

        binding.toolbar.activityTitle.setText(fromWhich);

        binding.rvRest.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvRest.setItemAnimator(new DefaultItemAnimator());

        binding.rvRest.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (fromWhich.equals(getString(R.string.locations)))
                    goToLocationScreen(position);
                else if (fromWhich.equals(getString(R.string.delivery)) || fromWhich.equals(getString(R.string.take_away)))
                    goToMenuScreen(position);
                 /*else if (fromWhich.equals(getString(R.string.reservation)))
                    Utils.reserveTableDialog(mContext);*/
            }
        }));

        if (fromWhich.equals(getString(R.string.delivery)) || fromWhich.equals(getString(R.string.dine_in)))
            callGetRestaurantApi();
        else {
            Utils.progressDialog(mContext, "");
            enableGps();
        }

        sliderHome = (SliderLayout) findViewById(R.id.sliderHome);

    }

    public void onAddAddress(View view) {
        goToLocationScreen();
    }

    private void goToLocationScreen() {
        Intent intent = new Intent(mContext, AddressActi.class);
        intent.putExtra(Constants.IS_FROM_CART, true);
        startActivityForResult(intent, ADDRESS_REQUEST);
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
                            status.startResolutionForResult(RestaurantActi.this, REQUEST_);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    default: {
                        Log.d("Location", "Default in");
//                            callGetVersionApi();
                        getLiveLocation();
                        break;
                    }

                }
            }
        });
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
                    callGetBannerApi(Constants.TAKE_AWAY);
                    callGetRestaurantApi();

                } else {
                    updateLocation();
                }
            });
        }
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
                        callGetRestaurantApi();
                    }
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_) {
            switch (resultCode) {
                case AppCompatActivity.RESULT_CANCELED: {
                    finish();
                    break;
                }
                default: {
//                    callGetVersionApi();
                    updateLocation();
                    break;
                }
            }
        }else if (requestCode == ADDRESS_REQUEST && resultCode == RESULT_OK) {
//            latitude = data.getDoubleExtra(Constants.LATITUDE, 0);
//            longitude = data.getDoubleExtra(Constants.LONGITUDE, 0);

            addressBean = (AddressBean) data.getSerializableExtra(Constants.ADDRESS_BEAN);
            binding.txtAddress.setText(addressBean.getAddressLine1());
            binding.txtLocality.setText(addressBean.getAddressLine2());
            callGetRestaurantApi();
//            addressBean.setAddressLine1(data.getStringExtra(Constants.ADDRESS));
//            if (!TextUtils.isEmpty(data.getStringExtra(Constants.FLAT_NO)))
//                addressBean.setAddressLine2(data.getStringExtra(Constants.FLAT_NO));
//            addressBean.setLatitude(latitude);
//            addressBean.setLongitude(longitude);
//
//            SharedPref.setAddressModelJSON(mContext, new Gson().toJson(addressBean));
        }
    }

    private void goToMenuScreen(int position) {
        customRestaurantViewedEvent(restaurantList.get(position).getRestaurantUuid(), restaurantList.get(position).getTitle());
        Intent intent = new Intent(mContext, AddItemsActi.class);
        intent.putExtra(Constants.RESTAURANT_ID, restaurantList.get(position).getRestaurantUuid());
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        intent.putExtra(Constants.RESTAURANT_NAME, restaurantList.get(position).getTitle());
        if (fromWhich.equals(getString(R.string.delivery)))
            intent.putExtra(Constants.ADDRESS_BEAN, addressBean);
        intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, restaurantList.get(position).getTakeAwayMinimumOrderAmount());
        intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, restaurantList.get(position).getDeliveryMinimumOrderAmount());
        intent.putExtra(Constants.IS_OPEN, restaurantList.get(position).isOpen());
        startActivity(intent);
    }

    private void goToLocationScreen(int position) {
        Intent intent = new Intent(mContext, RestaurantLocActi.class);
        intent.putExtra(Constants.RESTAURANT_DATA, restaurantList.get(position));
        startActivity(intent);
    }

    /**
     * This method set top slider
     */
    private void setTopSlider() {
        if (shortBannerList.size() > 0) {
//            Utils.setImage(mContext, binding.imvFixedImage, shortBannerList.get(0).getAdImageUrlLarge());
//        }
            for (int i = 0; i < shortBannerList.size(); i++) {
                String imageUrl = shortBannerList.get(i).getAdImageUrlLarge();

                DefaultSliderView textSliderView = new DefaultSliderView(mContext);
                // initialize a SliderLayout
                textSliderView
                        .description(null)
//                            .image(url_maps.get(name))
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString(Constants.SLIDER_ACTION, shortBannerList.get(i).getSliderAction());

                sliderHome.addSlider(textSliderView);
            }
        }
        sliderHome.setPresetTransformer(SliderLayout.Transformer.Default);
//        sliderBottom.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderHome.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderHome.setCustomAnimation(new DescriptionAnimation());
        sliderHome.setDuration(4000);
    }


    private void callGetBannerApi(final String type) {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<BannerListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<BannerListBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<BannerListBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

//                        if (type.equalsIgnoreCase(Constants.SHORTBANNER)) {
                            shortBannerList = responseClass1.getResponsePacket();
                            setTopSlider();
//                            callGetBannerApi(Constants.DELIVERY);
//                        } else {
////                            bottomBannerList = responseClass1.getResponsePacket();
//                            setSlider();
//                            setSlider2();
//                            Utils.dismissProgressDialog();

//                            callGetTopSellingItemsApi();
//                        }

                    } else {
                        Utils.dismissProgressDialog();
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_AD_BANNER_LIST + type + "?lat=" + liveLat + "&lng=" + liveLong, "", "Loading...", false, AppUrls.REQUESTTAG_GETADBANNERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }




    private void callGetRestaurantApi() {

        RestaurantRequestBean bean = new RestaurantRequestBean();
        if (fromWhich.equalsIgnoreCase(getString(R.string.take_away))) {
            bean.setOrderType(Constants.TAKE_AWAY);
            bean.setLatitude(liveLat);
            bean.setLongitude(liveLong);
        } else if (fromWhich.equalsIgnoreCase(getString(R.string.delivery))) {
            bean.setOrderType(Constants.HOME_DELIVERY);
            bean.setLatitude(addressBean.getLatitude());
            bean.setLongitude(addressBean.getLongitude());
        } else if (fromWhich.equalsIgnoreCase(getString(R.string.locations))) {
            bean.setOrderType(Constants.ALL);
            bean.setLatitude(liveLat);
            bean.setLongitude(liveLong);
        }
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
                        setRestaurantData();

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

    private void setRestaurantData() {
        if (restaurantList != null && restaurantList.size() > 0){
            if (fromWhich.equals(getString(R.string.locations)) || fromWhich.equals(getString(R.string.take_away)))
                mAdapter = new ReservationAdapter(mContext, restaurantList, true);
            else
                mAdapter = new ReservationAdapter(mContext, restaurantList, false);
            binding.rvRest.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            binding.imgCommingSoon.setVisibility(View.GONE);

        }else {
            binding.imgCommingSoon.setVisibility(View.VISIBLE);
            binding.rvRest.setVisibility(View.GONE);
            binding.llAddress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    public void customRestaurantViewedEvent(String restaurantId, String restaurantName){
        try {
            HashMap<String, Object> CategoryViewedAction = new HashMap<String, Object>();
            CategoryViewedAction.put("Restaurant Id", restaurantId);
            CategoryViewedAction.put("Restaurant Name", restaurantName);
            AppInitialization.getInstance().clevertapDefaultInstance.pushEvent(fromWhich + " Restaurant Viewed", CategoryViewedAction);
        } catch (Exception e){

        }
    }
}
