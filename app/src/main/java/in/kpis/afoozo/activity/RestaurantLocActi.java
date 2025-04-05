package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.RestaurantBean;
import com.kpis.afoozo.databinding.ActivityRestaurantLocBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import static android.Manifest.permission.CALL_PHONE;

public class RestaurantLocActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityRestaurantLocBinding binding;

    private GoogleMap googleMap;

    private RestaurantBean restaurantBean;

    private int REQUEST_ = 101;
    private final int REQUESTPERMISSIONCODE = 109;
    private Marker driverMarkar;
    private FusedLocationProviderClient mFusedLocationClient;
    private double liveLat;
    private double liveLng;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_loc);

        mContext = RestaurantLocActi.this;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (getIntent().getExtras() != null){
            restaurantBean = (RestaurantBean) getIntent().getSerializableExtra(Constants.RESTAURANT_DATA);
        }

        binding.mapViewRest.onCreate(savedInstanceState);

        binding.mapViewRest.onResume();

        try {
            MapsInitializer.initialize(mContext.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        binding.toolbar.activityTitle.setText(getString(R.string.locations));

        setMap();
    }

    private void setMap() {
        binding.mapViewRest.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                setData();

//                callOrderDetailApi(true);

            }
        });
    }

    private void setData() {
        if (restaurantBean != null){
            if (!TextUtils.isEmpty(restaurantBean.getTitle()))
                binding.txtTitle.setText(restaurantBean.getTitle());
            if (!TextUtils.isEmpty(restaurantBean.getRestaurantAddress()))
                binding.txtAddress.setText(restaurantBean.getRestaurantAddress());
            if (!TextUtils.isEmpty(restaurantBean.getEmails()))
                binding.txtEmail.setText(restaurantBean.getEmails());
            if (!TextUtils.isEmpty(restaurantBean.getMobileNumbers()))
                binding.txtPhone.setText(restaurantBean.getMobileNumbers());

            LatLng latLng = new LatLng(restaurantBean.getLatitude(), restaurantBean.getLongitude());

            driverMarkar = googleMap.addMarker(new MarkerOptions().position(latLng).title(restaurantBean.getTitle()).snippet(restaurantBean.getRestaurantAddress()).icon(Utils.bitmapDescriptorFromVector(mContext, R.drawable.ic_map_black)));

            googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(restaurantBean.getLatitude(),restaurantBean.getLongitude()) , 14.0f) );

        }
    }

    public void getLiveLocation(View view) {
        Utils.progressDialog(mContext, "");

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                Utils.dismissProgressDialog();
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLat = location.getLatitude();
                    liveLng = location.getLongitude();
                    goToGoogleMap();

                } else {
                    enableGps();
                }
            });
        }
    }

    private void enableGps() {
        mGoogleApiClient = null;
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
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(RestaurantLocActi.this, REQUEST_);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        default: {
                            Log.d("Location", "Default in");
//                            callGetVersionApi();
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.location_not_found_msg), getString(R.string.Ok), "", null);
                            break;
                        }

                    }
                }
            });
        }
    }

    /**
     * We get Response from another activity when we call startforresult intent
     * @param requestCode for which we request
     * location request response comes under this
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
                    break;
                }
            }
        }

    }

    public void callProcess(View view){
        TelephonyManager tm= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE){
            //No calling functionality
            Utils.showCenterToast(mContext, "No calling functionality available on your device");
        }
        else
        {
            //calling functionality
            checkPermission();
        }
    }

    private void goToGoogleMap(){
        if (liveLat > 0 && liveLng > 0) {
            String url = "http://maps.google.com/maps?saddr=" + liveLat + "," + liveLng + "&daddr=" + restaurantBean.getLatitude() + "," + restaurantBean.getLongitude();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intent);
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(mContext, CALL_PHONE);
            if (result != PackageManager.PERMISSION_GRANTED)
                requestPermission();
            else
                callAction();
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            callAction();
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{ CALL_PHONE}, REQUESTPERMISSIONCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTPERMISSIONCODE:
                if (grantResults.length > 0) {
                    boolean readSms = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readSms) {
                        callAction();
                    } else {
                        Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void callAction(){
        String number = "";
        if (restaurantBean.getMobileNumbers().contains(","))
            number = restaurantBean.getMobileNumbers().substring(0, restaurantBean.getMobileNumbers().indexOf(","));
        else
            number = restaurantBean.getMobileNumbers();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }
}
