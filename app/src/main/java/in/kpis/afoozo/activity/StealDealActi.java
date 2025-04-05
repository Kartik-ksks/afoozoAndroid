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
import com.kpis.afoozo.databinding.ActivityStealDealBinding;
import in.kpis.afoozo.fragment.BookNowFrag;
import in.kpis.afoozo.fragment.ConsumeFrag;
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

public class StealDealActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityStealDealBinding binding;
    private ArrayList<String> storList = new ArrayList<>();
    private ArrayList<RestaurantBean> list;
    private ViewPagerAdapter adapter;
    public String selectedRestaurant;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private boolean isFirstTime = true;

    private int REQUEST_ = 345;

    private String notificationTitle;
    private String notificationMessage;
    private boolean isFromNotification;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_steal_deal);

        if (getIntent().getExtras() != null){
            notificationTitle = getIntent().getStringExtra(Constants.TITLE);
            notificationMessage = getIntent().getStringExtra("message");
            imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
            isFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        }

        mContext = StealDealActi.this;

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

        binding.toolbar.activityTitle.setText(getString(R.string.steal_deal));

        binding.spStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!isFirstTime) {
                    selectedRestaurant = list.get(position).getRestaurantUuid();
                    refreshFragment();

                } else
                    isFirstTime = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!isFromNotification)
            enableGps();
        else
            Utils.showNotificationPopUp(mContext, notificationMessage, imageUrl, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.dismissNotificationAlert();
                    enableGps();
                }
            });

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
                            status.startResolutionForResult(StealDealActi.this, REQUEST_);

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

//                    setLocation();
                    callRestaurantListApi(location.getLatitude(), location.getLongitude());

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

//                    setLocation();
//                        Utils.dismissProgressDialog();
                        callRestaurantListApi(location.getLatitude(), location.getLongitude());
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
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

    private void refreshFragment() {
        int pos = binding.vpSDeal.getCurrentItem();
        Fragment activeFragment = adapter.getItem(pos);
        if(pos == 0)
            ((BookNowFrag)activeFragment).refreshList();
        if(pos == 1)
            ((ConsumeFrag)activeFragment).refreshList();
    }

    private void setData() {
        for (RestaurantBean rb: list)
            storList.add(rb.getTitle());

        selectedRestaurant = list.get(0).getRestaurantUuid();
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, storList);
        eventTypeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        binding.spStore.setAdapter(eventTypeAdapter);

        setupViewPager(binding.vpSDeal);
        binding.tabSDeal.setupWithViewPager(binding.vpSDeal);
        setupTabIcons();
    }

    /**
     * this Method is used to set custom layout for tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab_steal_deal, null);
        tabOne.setText(getString(R.string.book_now));
        binding.tabSDeal.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab_steal_deal, null);
        tabTwo.setText(getString(R.string.consume));
        binding.tabSDeal.getTabAt(1).setCustomView(tabTwo);

    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BookNowFrag(), getString(R.string.book_now));
        adapter.addFragment(new ConsumeFrag(), getString(R.string.consume));
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

    private void callRestaurantListApi(double latitude, double longitude) {
        RestaurantRequestBean bean = new RestaurantRequestBean();

        bean.setOrderType(Constants.STEAL_DEAL);
        bean.setLatitude(latitude);
        bean.setLongitude(longitude);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Log.e("StartEnd", "actiEnd");
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
                    Log.e("StartEnd", "actiEnd");
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESTAURANT_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_RESTAURANTLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
