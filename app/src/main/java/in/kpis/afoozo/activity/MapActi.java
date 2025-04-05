package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityMapBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class MapActi extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityMapBinding binding;
    private MapView mapView;

    private GoogleMap googleMap;

    final static int REQUEST_ = 199;

    private double eventLat;
    private double eventLng;
    private double liveLat;
    private double liveLong;

    private String eventName;
    private String address;

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;

    private boolean isForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);

        mContext = MapActi.this;

        eventLat = getIntent().getDoubleExtra(Constants.LATITUDE, 0);
        eventLng = getIntent().getDoubleExtra(Constants.LATITUDE, 0);
        eventName = getIntent().getStringExtra(Constants.EVENT_NAME);
        address = getIntent().getStringExtra(Constants.ADDRESS);

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
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.app_name));

        enableGps(false);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        this.googleMap.setOnCameraIdleListener(onCameraIdleListener);


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
            googleMap.addMarker(new MarkerOptions().position(new LatLng(eventLat, eventLng)).title(eventName).snippet(address)).showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(eventLat, eventLng), 14.0f));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }

    }

    public void checkLocation(View view){
        if (liveLat > 0 && liveLong > 0)
            goToGoogleMap();
        else enableGps(true);
    }

    private void enableGps(boolean goToMap) {
        mGoogleApiClient = null;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
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
                                if (goToMap)
                                    isForMap = true;
                                else
                                    isForMap = false;
                                status.startResolutionForResult(MapActi.this, REQUEST_);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        default: {
                            Log.d("Location", "Default in");
                            Utils.progressDialog(mContext, "");
                            getLiveLocation(goToMap);
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
                    Utils.progressDialog(mContext, "");
                    getLiveLocation(isForMap);
                    break;
                }
            }
        }

    }

    private void getLiveLocation(boolean goToMap) {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLat = location.getLatitude();
                    liveLong = location.getLongitude();

                    Utils.dismissProgressDialog();

                    if (goToMap)
                        goToGoogleMap();
                    else
                        isForMap = false;

                } else {
                    Utils.dismissProgressDialog();
                    Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.location_not_found_msg), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.progressDialog(mContext, "");
                            getLiveLocation(goToMap);
                        }
                    });
                }
            });
        }
    }

    private void goToGoogleMap() {
        String url = "http://maps.google.com/maps?saddr=" + liveLat + "," + liveLong + "&daddr=" + eventLat + "," + eventLng;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }
}
