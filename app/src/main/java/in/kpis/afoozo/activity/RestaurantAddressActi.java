package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityRestaurantAddressBinding;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.util.Arrays;
import java.util.List;

public class RestaurantAddressActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityRestaurantAddressBinding binding;
    private int AUTOCOMPLETE_REQUEST_CODE = 101;

    private String address;

    private double latitude;
    private double longitude;

    private Place place;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_address);
        mContext = RestaurantAddressActi.this;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_key));
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
                finish();
            }
        });

        binding.toolbar.activityTitle.setVisibility(View.GONE);
        binding.toolbar.txtAfoozo.setVisibility(View.VISIBLE);


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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    setLocation();
                    goToRestaurantScreen();

                } else {
                    Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.location_not_found_msg), getString(R.string.Ok), "", null);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                address = place.getAddress();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                binding.txtAddress.setText(address);

                goToRestaurantScreen();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Utils.showCenterToast(mContext, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void goToRestaurantScreen() {
        Intent intent = new Intent(mContext, RestaurantActi.class);
        intent.putExtra(Constants.FROM_WHICH, getString(R.string.delivery));
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        startActivity(intent);
    }
}
