package in.kpis.afoozo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityLocationBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class LocationActi extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;

    private Context mContext;

    private MapView mapView;

    private ActivityLocationBinding binding;

//    private AutoCompleteTextView autoLocation;

    private GoogleMap googleMap;
    private LocationManager manager;
    private Criteria mCriteria;
    private String bestProvider;
    private Location mLocation;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;

    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

//    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    private boolean isFromRegister;
    private boolean isFromHome;
    private boolean doubleBackToExitPressedOnce;

    private double latitude;
    private double longitude;

    private String LOG_TAG = "LocationActivity";

    private String address;
    private String addressType;
    private String flatNo;
    private String buldingName;
    private String landmark;
    private String nickName;
    private String fromWhich;
    private Place place;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
//            = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                Log.e(LOG_TAG, "Place query did not complete. Error: " +
//                        places.getStatus().toString());
//                return;
//            }
//            // Selecting the first object buffer.
//            final Place place = places.get(0);
//            CharSequence attributions = places.getAttributions();
//
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location);

        mContext = LocationActi.this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        mapView = binding.mapView;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_key));
        }

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getExtras() != null){
//            isFromRegister = getIntent().getBooleanExtra(Constants.IS_FROM_REGISTER, false);
            isFromHome = getIntent().getBooleanExtra(Constants.IS_FROM_HOME, false);
        }
        configureCameraIdle();

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

        binding.toolbar.activityTitle.setText(getString(R.string.address));

        addressType = getString(R.string.home);

        if (isFromHome)
            binding.llAddressType.setVisibility(View.GONE);
        else
            binding.llAddressType.setVisibility(View.VISIBLE);


//        autoLocation = binding.autoLocation;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onBackPressed() {
        if (isFromRegister)
            backCountToExit();
        else
            finish();
    }

    private void backCountToExit() {

        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.Please_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.setOnCameraIdleListener(onCameraIdleListener);


        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }

        if (mContext != null) {

            manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            mCriteria = new Criteria();
            bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));

            mLocation = manager.getLastKnownLocation(bestProvider);
            if (mLocation != null) {
                Log.e("TAG", "GPS is on");
                final double currentLatitude = mLocation.getLatitude();
                final double currentLongitude = mLocation.getLongitude();
//                    LatLng loc1 = new LatLng(currentLatitude, currentLongitude);
//                    mMap.addMarker(new MarkerOptions().position(loc1).title("Your Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14.0f));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        }

        googleMap.setMyLocationEnabled(true);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        updateLocation();

    }

    private void getLiveLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

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
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                    }
                }
            }
        };
         LocationServices.getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    /**
     * Intent for Launch Search Address Activity
     * @param view
     */
    public void searchAddress(View view){
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

//        Intent intent = new Intent(mContext, SearchLocationActi.class);
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Set Address Type Home
     * @param view
     */
    public void setHomeType(View view){
        addressType = getString(R.string.home);
        binding.llHome.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_bg));
        binding.llWork.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.llOther.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));

        binding.txtHome.setTextColor(mContext.getResources().getColor(R.color.white));
        binding.txtWork.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        binding.txtOther.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        binding.imvHome.setVisibility(View.VISIBLE);
        binding.imvWork.setVisibility(View.GONE);
        binding.imvOther.setVisibility(View.GONE);

        binding.llNickName.setVisibility(View.GONE);
    }

    /**
     * Set Address Type Work
     * @param view
     */
    public void setWorkType(View view){
        addressType = getString(R.string.work);
        binding.llHome.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.llWork.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_bg));
        binding.llOther.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));

        binding.txtHome.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        binding.txtWork.setTextColor(mContext.getResources().getColor(R.color.white));
        binding.txtOther.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        binding.imvHome.setVisibility(View.GONE);
        binding.imvWork.setVisibility(View.VISIBLE);
        binding.imvOther.setVisibility(View.GONE);

        binding.llNickName.setVisibility(View.GONE);
    }

    /**
     * Set Address Type Other
     * @param view
     */

    public void setOtherType(View view){
        addressType = getString(R.string.other);
        binding.llHome.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.llWork.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_border));
        binding.llOther.setBackground(mContext.getResources().getDrawable(R.drawable.color_primary_rounded_bg));

        binding.txtHome.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        binding.txtWork.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        binding.txtOther.setTextColor(mContext.getResources().getColor(R.color.white));

        binding.imvHome.setVisibility(View.GONE);
        binding.imvWork.setVisibility(View.GONE);
        binding.imvOther.setVisibility(View.VISIBLE);

        binding.llNickName.setVisibility(View.VISIBLE);
    }

    /**
     * This metod is call on click of save button its check that all required fields are filled or not
     * @param view
     */

    public void saveProcess(View view){
        flatNo = binding.etCompleteAdd.getText().toString();
        buldingName = binding.etBuldingName.getText().toString();
        if (Utils.isValidAddress(mContext, address) && latitude > 0 && longitude > 0
                && Utils.isValidRoomFlatNo(mContext, flatNo)&& Utils.isValidBulding(mContext, buldingName)){
            if (isFromHome){
                goToPreviousScreen();
            } else {
                if (addressType.equals("Other")) {
                    nickName = binding.etNickName.getText().toString();
                    if (!TextUtils.isEmpty(nickName))
                        callSaveAddressApi();
                    else
                        Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.nick_name_blank_error), getString(R.string.Ok), "", null);
                } else
                    callSaveAddressApi();
            }
        }
    }

    /**
     * Using this method we get middle location on move map
     */

    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = googleMap.getCameraPosition().target;
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                Geocoder geocoder = new Geocoder(mContext);

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        if (!TextUtils.isEmpty(locality)) {
                            address = locality;
                            binding.txtAddress.setText(address);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * Api call for save address
     */
    private void callSaveAddressApi() {
        AddressBean bean = new AddressBean();
        bean.setAddressLine1(address);
        bean.setLatitude(latitude);
        bean.setLongitude(longitude);
        if (!TextUtils.isEmpty(flatNo))
            bean.setAddressLine2(flatNo +"  "+buldingName );
        if (addressType.equals("Other"))
            bean.setAddressType(nickName);
        else
            bean.setAddressType(addressType);

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Utils.showCenterToast(mContext, responseClass.getMessage());
                    if (responseClass.isSuccess()){
                        if (isFromRegister)
                            Utils.startActivity(mContext, Dashboard.class);
                        else if (isFromHome)
                            goToPreviousScreen();
                        else
                            finish();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_ADDRESS, json, "Loading...", true, AppUrls.REQUESTTAG_SAVEADDRESS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void goToPreviousScreen() {
        Intent intent = new Intent();
        intent.putExtra("flag", true);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        if (!TextUtils.isEmpty(flatNo))
            intent.putExtra(Constants.FLAT_NO, flatNo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    address = data.getStringExtra(Constants.ADDRESS);
                    latitude = data.getDoubleExtra(Constants.LATITUDE, 0);
                    longitude = data.getDoubleExtra(Constants.LONGITUDE, 0);
                    binding.txtAddress.setText(address);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
                }
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                address = place.getAddress();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                binding.txtAddress.setText(address);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Utils.showCenterToast(mContext, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
