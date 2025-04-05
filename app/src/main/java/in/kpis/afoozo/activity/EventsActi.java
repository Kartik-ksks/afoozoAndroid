package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import com.kpis.afoozo.databinding.ActivityEventsBinding;
import in.kpis.afoozo.fragment.PastFrag;
import in.kpis.afoozo.fragment.UpcomingFrag;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EventsActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityEventsBinding binding;

    private ArrayList<String> eventTypeList = new ArrayList<>();

    private ArrayList<RestaurantBean> list;

    private ViewPagerAdapter adapter;

    public String selectedRestaurant = "All";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private double liveLat;
    private double liveLong;
    private int REQUEST_ = 345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_events);

        mContext = EventsActi.this;
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

        binding.spEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    selectedRestaurant = String.valueOf(list.get(position-1).getRecordId());
                    refreshFragment();
//                        if (selectedItem.equals("All Cities")) {
//                            // do your stuff
//                        }else{
//
//                        }
                }else{
                    selectedRestaurant = "All";
                    refreshFragment();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.events));

//        setupTabIcons();

        enableGps();
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
                            status.startResolutionForResult(EventsActi.this, REQUEST_);

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

    @Override
    protected void onPause() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        super.onPause();
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

    private void refreshFragment() {
        int pos = binding.vpEvents.getCurrentItem();
        Fragment activeFragment = adapter.getItem(pos);
        if(pos == 0)
            ((UpcomingFrag)activeFragment).refreshList();
        if(pos == 1)
            ((PastFrag)activeFragment).refreshList();
    }

    private void setData() {
        eventTypeList.add("All Locations");
        for (RestaurantBean rb: list)
            eventTypeList.add(rb.getTitle());

        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, eventTypeList);
        eventTypeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        binding.spEvents.setAdapter(eventTypeAdapter);

        setupViewPager(binding.vpEvents);
        binding.tabEvents.setupWithViewPager(binding.vpEvents);
    }

    /**
     * this Method is used to set custom layout for tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.upcoming));
        binding.tabEvents.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.past));
        binding.tabEvents.getTabAt(1).setCustomView(tabTwo);

    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UpcomingFrag(), getString(R.string.upcoming));
        adapter.addFragment(new PastFrag(), getString(R.string.past));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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

                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0)
                            setData();
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
}
