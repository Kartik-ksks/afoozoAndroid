package in.kpis.afoozo.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.activity.AddItemsActi;
import in.kpis.afoozo.activity.AddItemsNewActi;
import in.kpis.afoozo.activity.AddressActi;
import in.kpis.afoozo.activity.AddressBean;
import in.kpis.afoozo.activity.CheckInActi;
import in.kpis.afoozo.activity.ChecoutScanActi;
import in.kpis.afoozo.activity.Dashboard;
import in.kpis.afoozo.activity.DeliveryRestaurantActi;
import in.kpis.afoozo.activity.DineInActi;
import in.kpis.afoozo.activity.EventsActi;
import in.kpis.afoozo.activity.FeedbackActi;
import in.kpis.afoozo.activity.NotificationActi;
import in.kpis.afoozo.activity.NotificationPopUpActi;
import in.kpis.afoozo.activity.OffersActi;
import in.kpis.afoozo.activity.OrderBillToCompanyHistoryActi;
import in.kpis.afoozo.activity.OrderHistoryActi;
import in.kpis.afoozo.activity.RatingActi;
import in.kpis.afoozo.activity.ReservationActi;
import in.kpis.afoozo.activity.RestaurantActi;
import in.kpis.afoozo.activity.ScanQrActi;
import in.kpis.afoozo.activity.ScanRoomActivity;
import in.kpis.afoozo.activity.StealDealActi;
import in.kpis.afoozo.activity.WalletActi;
import in.kpis.afoozo.adapter.CategoryAdapter;
import in.kpis.afoozo.adapter.PopularAdapter;
import in.kpis.afoozo.adapter.TopSellingAdapter;
import in.kpis.afoozo.bean.BannerListBean;
import in.kpis.afoozo.bean.CategoryBean;
import in.kpis.afoozo.bean.LastOrderRateBean;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.bean.PopularBean;
import in.kpis.afoozo.bean.TopSellingBean;

import com.kpis.afoozo.databinding.FragmentHomeBinding;

import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.service.DataObject;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFrag extends Fragment implements BaseSliderView.OnSliderClickListener {

    private SliderLayout sliderHome;
    private SliderLayout sliderBottom;

    private PagerIndicator custom_indicator_home;
    private PagerIndicator custom_indicator_bottom;

    private View view;

    private Context mContext;

    private int REQUEST_ = 345;
    private int ADDRESS_REQUEST = 103;

    private FragmentHomeBinding binding;
    private ArrayList<BannerListBean> topBannerList = new ArrayList();
    private ArrayList<BannerListBean> bottomBannerList = new ArrayList();
    private ArrayList<CategoryBean> categoryList = new ArrayList();
    private ArrayList<PopularBean> popularList = new ArrayList();

    private CategoryAdapter categoryAdapter;
    private PopularAdapter popularAdapter;

    private FusedLocationProviderClient mFusedLocationClient;

    private AddressBean addressBean;
    private double liveLat;
    private double liveLong;
    private LocationCallback mLocationCallback;
    private TopSellingBean topSellingBean;
    private ArrayList<AddressBean> list;
    private ArrayList<OrdersBean> liveList;
    private LivePagerAdapter adapterViewPager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        view = binding.getRoot();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        initialize();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((Dashboard) getActivity()).setTitle("Afoozo");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void initialize() {
        sliderHome = (SliderLayout) view.findViewById(R.id.sliderHome);
        sliderBottom = (SliderLayout) view.findViewById(R.id.sliderBottom);

        custom_indicator_home = (PagerIndicator) view.findViewById(R.id.custom_indicator_home);
        custom_indicator_bottom = (PagerIndicator) view.findViewById(R.id.custom_indicator_bottom);

//        binding.rvCategory.setLayoutManager(new GridLayoutManager(mContext, 3));
//        binding.rvCategory.setItemAnimator(new DefaultItemAnimator());
////        binding.rvCategory.addItemDecoration(new GridDividerDecoration(mContext));
//        binding.rvCategory.setNestedScrollingEnabled(false);

        binding.rvPopular.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        binding.rvPopular.setItemAnimator(new DefaultItemAnimator());
        binding.rvPopular.setNestedScrollingEnabled(false);

//        binding.rvDelivery.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
//        binding.rvDelivery.setItemAnimator(new DefaultItemAnimator());
//        binding.rvDelivery.setNestedScrollingEnabled(false);
//        binding.rvDelivery.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(mContext, AddressActi.class);
//                intent.putExtra(Constants.IS_FROM_CART, true);
//                intent.putExtra(Constants.FROM_WHICH, getString(R.string.delivery));
//                intent.putExtra(Constants.TITLE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getTitle());
//                intent.putExtra(Constants.IS_OPEN, topSellingBean.getHomeDeliveryTopSellingItem().get(position).isRestaurantOpen());
//                intent.putExtra(Constants.RESTAURANT_ID, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getRestaurantUuid());
//                intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getTakeAwayMinimumOrderAmount());
//                intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, topSellingBean.getHomeDeliveryTopSellingItem().get(position).getDeliveryMinimumOrderAmount());
//                startActivityForResult(intent, ADDRESS_REQUEST);
//            }
//        }));
//
//        binding.rvDineIn.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
//        binding.rvDineIn.setItemAnimator(new DefaultItemAnimator());
//        binding.rvDineIn.setNestedScrollingEnabled(false);
//        binding.rvDineIn.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(mContext, ScanQrActi.class);
//                intent.putExtra(Constants.IS_FROM_CART, true);
//                intent.putExtra(Constants.FROM_WHICH, getString(R.string.dine_in));
//                intent.putExtra(Constants.TITLE, topSellingBean.getDineInTopSellingItem().get(position).getTitle());
//                intent.putExtra(Constants.RESTAURANT_ID, topSellingBean.getDineInTopSellingItem().get(position).getRestaurantUuid());
//                startActivityForResult(intent, ADDRESS_REQUEST);
//            }
//        }));
//
//
//        binding.rvPickup.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
//        binding.rvPickup.setItemAnimator(new DefaultItemAnimator());
//        binding.rvPickup.setNestedScrollingEnabled(false);
//        binding.rvPickup.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                goToAddItemsScreen(getString(R.string.take_away), topSellingBean.getTakeAwayTopSellingItem().get(position).getRestaurantUuid(), topSellingBean.getTakeAwayTopSellingItem().get(position).isRestaurantOpen(), topSellingBean.getTakeAwayTopSellingItem().get(position).getTitle(), topSellingBean.getTakeAwayTopSellingItem().get(position).getTakeAwayMinimumOrderAmount(), topSellingBean.getTakeAwayTopSellingItem().get(position).getDeliveryMinimumOrderAmount());
//            }
//        }));

        if (categoryList != null && categoryList.size() > 0)
            categoryList.clear();
        if (popularList != null && popularList.size() > 0)
            popularList.clear();

        binding.rvCategory.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Utils.SingleClickCleverTapEvent("Dashboard DineIn Clicked");
                        Intent intent = new Intent(mContext, DineInActi.class);
                        intent.putExtra(Constants.FROM_WHICH, getString(R.string.dine_in));
                        startActivity(intent);
//                        Utils.startActivity(mContext, DineInActi.class);
                        break;
                    case 1:
                        Utils.SingleClickCleverTapEvent("Dashboard HomeDelivery Clicked");
                        goToDeliveryRestaurantScreen(getString(R.string.delivery));
//                        callGetAddressApi();
//                        Utils.startActivity(mContext, RestaurantActi.class);
//                        goToLocationScreen();
                        break;
                    case 2:
                        Utils.SingleClickCleverTapEvent("Dashboard TakeAway Clicked");
                        goToRestaurantScreen(mContext.getString(R.string.take_away));
                        break;
//                    case 3:
//                        Utils.startActivity(mContext, StealDealActi.class);
//                        break;
//                    case 3:
//                        Utils.startActivity(mContext, ReservationActi.class);
//                        break;
//                    case 4:
//                        Utils.startActivity(mContext, EventsActi.class);
//                        break;
                }
            }
        }));
        binding.tabDots.setupWithViewPager(binding.pager, true);

//        binding.llcheckIn.setOnClickListener(v -> Utils.startActivity(mContext, ChecoutScanActi.class));
        binding.llCafe.setOnClickListener(v -> {
            Utils.SingleClickCleverTapEvent("Scan to order Clicked");
            Intent intent = new Intent(mContext, DineInActi.class);
            intent.putExtra(Constants.FROM_WHICH, getString(R.string.cafe));
            startActivity(intent);
        });

//        makeCategoryList();
//        makePopularList();

        Utils.progressDialog(mContext, "");
        enableGps();

        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_dine_in:
                    Intent dineInIntent = new Intent(getActivity(), DineInActi.class);
                    dineInIntent.putExtra(Constants.FROM_WHICH, getString(R.string.dine_in));
                    startActivity(dineInIntent);
                    return true;
                case R.id.nav_delivery:
                    goToDeliveryRestaurantScreen(getString(R.string.delivery));
//                    deliveryIntent.putExtra(Constants.IS_FROM_CART, true);
//                    deliveryIntent.putExtra(Constants.FROM_WHICH, getString(R.string.delivery));
//                    startActivity(deliveryIntent);
                    return true;
//                case R.id.nav_take_away:
//                    Intent takeAwayIntent = new Intent(getActivity(), RestaurantActi.class);
//                    takeAwayIntent.putExtra(Constants.FROM_WHICH, getString(R.string.take_away));
//                    startActivity(takeAwayIntent);
//                    return true;
                case R.id.nav_check_in:
                    Intent checkInIntent = new Intent(getActivity(), ChecoutScanActi.class);
                    startActivity(checkInIntent);
                    return true;
            }
            return false;
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
                            status.startResolutionForResult(getActivity(), REQUEST_);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    default: {
                        Log.d("Location", "Default in");
//                            callGetVersionApi();
                        updateLocation();
                        break;
                    }

                }
            }
        });
    }

    private void goToAddItemsScreen(String fromWhich, String restId, boolean isOpen, String title, int takeawayMinOrderValue, int deliveryMinOrderValue) {
//        Intent intent = new Intent(mContext, AddItemsNewActi.class);
        Intent intent = new Intent(mContext, HomeFrag.class);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        intent.putExtra(Constants.IS_OPEN, isOpen);
        intent.putExtra(Constants.RESTAURANT_ID, restId);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, takeawayMinOrderValue);
        intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, deliveryMinOrderValue);
        startActivity(intent);
    }

    private void getLiveLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLat = location.getLatitude();
                    liveLong = location.getLongitude();

//                    setLocation();
                    AppInitialization.getInstance().clevertapDefaultInstance.setLocation(location);
                    callGetBannerApi(Constants.DASHBOARD_TOP);

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
//                        Utils.dismissProgressDialog();
                        callGetBannerApi(Constants.DASHBOARD_TOP);
                        AppInitialization.getInstance().clevertapDefaultInstance.setLocation(location);
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                }
            }
        };
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
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void goToLocationScreen() {
        Intent intent = new Intent(mContext, AddressActi.class);
        intent.putExtra(Constants.IS_FROM_CART, true);
        startActivityForResult(intent, ADDRESS_REQUEST);
    }

    private void goToRestaurantScreen(String from) {
        Intent intent = new Intent(mContext, RestaurantActi.class);
        intent.putExtra(Constants.FROM_WHICH, from);
        if (from.equals(getString(R.string.delivery))) {
            intent.putExtra(Constants.ADDRESS_BEAN, addressBean);
        }
        startActivity(intent);
    }

    private void goToDeliveryRestaurantScreen(String from) {
        Intent intent = new Intent(mContext, DeliveryRestaurantActi.class);
        intent.putExtra(Constants.FROM_WHICH, from);
        if (from.equals(getString(R.string.delivery))) {
            intent.putExtra(Constants.ADDRESS_BEAN, addressBean);
        }
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_REQUEST && resultCode == getActivity().RESULT_OK) {
//            latitude = data.getDoubleExtra(Constants.LATITUDE, 0);
//            longitude = data.getDoubleExtra(Constants.LONGITUDE, 0);

            addressBean = (AddressBean) data.getSerializableExtra(Constants.ADDRESS_BEAN);
//            addressBean.setAddressLine1(data.getStringExtra(Constants.ADDRESS));
//            if (!TextUtils.isEmpty(data.getStringExtra(Constants.FLAT_NO)))
//                addressBean.setAddressLine2(data.getStringExtra(Constants.FLAT_NO));
//            addressBean.setLatitude(latitude);
//            addressBean.setLongitude(longitude);
//
//            SharedPref.setAddressModelJSON(mContext, new Gson().toJson(addressBean));

            goToDeliveryRestaurantScreen(getString(R.string.delivery));
        } else if (requestCode == REQUEST_) {
            switch (resultCode) {
                case AppCompatActivity.RESULT_CANCELED: {
                    getActivity().finish();
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

    private void makePopularList() {
        PopularBean popularBean = new PopularBean("Burger", "https://www.seriouseats.com/recipes/images/2015/07/20150728-homemade-whopper-food-lab-35-1500x1125.jpg");
        popularList.add(popularBean);
        popularBean = new PopularBean("Dessert", "https://www.bbcgoodfood.com/sites/default/files/recipe-collections/collection-image/2013/05/rocky-road-cheesecake-pudding.jpg");
        popularList.add(popularBean);
        popularBean = new PopularBean("Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?ixlib=rb-1.2.1&w=1000&q=80");
        popularList.add(popularBean);
        popularBean = new PopularBean("North Indian", "https://static.eazydiner.com/resized/750X436/eazytrendz%2F2137%2Ftrend20181213060017.jpg");
        popularList.add(popularBean);
        popularBean = new PopularBean("Chat", "https://previews.123rf.com/images/indianfoodimages/indianfoodimages1906/indianfoodimages190600310/124641641-group-of-bombay-chat-food-includes-golgappa-panipuri-bhel-puri-sev-poori-dahipuri-ragda-pattice-raj-.jpg");
        popularList.add(popularBean);
        popularBean = new PopularBean("Burger", "https://www.seriouseats.com/recipes/images/2015/07/20150728-homemade-whopper-food-lab-35-1500x1125.jpg");
        popularList.add(popularBean);

        popularAdapter = new PopularAdapter(mContext, popularList);
        binding.rvPopular.setAdapter(popularAdapter);
        popularAdapter.notifyDataSetChanged();
    }


    /**
     * This method is used to make category list
     */
    private void makeCategoryList() {
        CategoryBean categoryBean = new CategoryBean(mContext.getResources().getString(R.string.dine_in), R.drawable.ic_dine_in);
        categoryList.add(categoryBean);
        categoryBean = new CategoryBean(mContext.getResources().getString(R.string.delivery), R.drawable.ic_delivery);
        categoryList.add(categoryBean);
        categoryBean = new CategoryBean(mContext.getResources().getString(R.string.take_away), R.drawable.ic_pickup);
        categoryList.add(categoryBean);
//        categoryBean = new CategoryBean(mContext.getResources().getString(R.string.steal_deal), R.drawable.ic_steal_deal);
//        categoryList.add(categoryBean);
//        categoryBean = new CategoryBean(mContext.getResources().getString(R.string.reservation), R.drawable.ic_reservation_2);
//        categoryList.add(categoryBean);
//        categoryBean = new CategoryBean(mContext.getResources().getString(R.string.events), R.drawable.ic_events_text);
//        categoryList.add(categoryBean);

        categoryAdapter = new CategoryAdapter(mContext, categoryList);
        binding.rvCategory.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

    }

    /**
     * This method set bottom slider
     */
    private void setSlider2() {
        if (bottomBannerList.size() >= 0) {
            for (int i = 0; i < bottomBannerList.size(); i++) {
                String imageUrl = bottomBannerList.get(i).getAdImageUrlLarge();

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
                        .putString(Constants.SLIDER_ACTION, bottomBannerList.get(i).getSliderAction());

                sliderBottom.addSlider(textSliderView);
            }
        }
        sliderBottom.setPresetTransformer(SliderLayout.Transformer.Default);
//        sliderBottom.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderBottom.setCustomIndicator(custom_indicator_bottom);
        sliderBottom.setCustomAnimation(new DescriptionAnimation());
        sliderBottom.setDuration(4000);
    }

    /**
     * This Method set Slider
     */
    private void setSlider() {

        if (topBannerList.size() >= 0) {
            for (int i = 0; i < topBannerList.size(); i++) {
                String imageUrl = topBannerList.get(i).getAdImageUrlLarge();

                DefaultSliderView textSliderView = new DefaultSliderView(mContext);
                // initialize a SliderLayout
                textSliderView
                        .description(null)
//                            .image(url_maps.get(name))
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

//                add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString(Constants.SLIDER_ACTION, topBannerList.get(i).getSliderAction());

                sliderHome.addSlider(textSliderView);
            }
        }
        sliderHome.setPresetTransformer(SliderLayout.Transformer.Default);
//        sliderHome.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderHome.setCustomIndicator(custom_indicator_home);
        sliderHome.setCustomAnimation(new DescriptionAnimation());
        sliderHome.setDuration(4000);
    }

    private void setTopSellingData() {
        if (topSellingBean != null) {
            if (topSellingBean.getDineInTopSellingItem() != null && topSellingBean.getDineInTopSellingItem().size() > 0) {
                TopSellingAdapter dineInAdapter = new TopSellingAdapter(mContext, topSellingBean.getDineInTopSellingItem());
//                binding.rvDineIn.setAdapter(dineInAdapter);
                dineInAdapter.notifyDataSetChanged();
//                binding.llDineIn.setVisibility(View.VISIBLE);
//                binding.txtTopDineIn.setText(topSellingBean.getDineInTitle());
            }

            if (topSellingBean.getHomeDeliveryTopSellingItem() != null && topSellingBean.getHomeDeliveryTopSellingItem().size() > 0) {
                TopSellingAdapter deliveryAdapter = new TopSellingAdapter(mContext, topSellingBean.getHomeDeliveryTopSellingItem());
//                binding.rvDelivery.setAdapter(deliveryAdapter);
                deliveryAdapter.notifyDataSetChanged();
//                binding.llDelivery.setVisibility(View.VISIBLE);
//                binding.txtTopDelivery.setText(topSellingBean.getHomeDeliveryTitle());
            }

            if (topSellingBean.getTakeAwayTopSellingItem() != null && topSellingBean.getTakeAwayTopSellingItem().size() > 0) {
                TopSellingAdapter pickupAdapter = new TopSellingAdapter(mContext, topSellingBean.getTakeAwayTopSellingItem());
//                binding.rvPickup.setAdapter(pickupAdapter);
                pickupAdapter.notifyDataSetChanged();
//                binding.llPickup
//                .setVisibility(View.VISIBLE);
//                binding.txtTopPickup.setText(topSellingBean.getTakeAwayTitle());
            }
        }
        try {

            if (getActivity() != null) {
                if (((Dashboard) getActivity()).isFromNotification)
                    checkNotificationData();
                else if (((Dashboard) getActivity()).isFromSplash)
                    callGetLastOrderRatedApi();

                ((Dashboard) getActivity()).isHomeLoad = true;
            }
        } catch (Exception e) {

        }
    }

    private void checkNotificationData() {
        Utils.dismissProgressDialog();
        DataObject notificationBean = (DataObject) Utils.getJsonToClassObject(SharedPref.getNotificationJSON(mContext), DataObject.class);
        if (notificationBean != null) {
            Intent intent = new Intent(mContext, NotificationPopUpActi.class);
            intent.putExtra(Constants.ORDER_TYPE, notificationBean.getType());
            intent.putExtra(Constants.MESSAGE, notificationBean.getMessage());
            intent.putExtra(Constants.IMAGE_URL, notificationBean.getImage());
            startActivity(intent);
        }

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

                        if (type.equalsIgnoreCase(Constants.DASHBOARD_TOP)) {
                            topBannerList = responseClass1.getResponsePacket();
                            setSlider();
                            callGetBannerApi(Constants.DASHBOARD_BOTTOM);
                        } else {
                            bottomBannerList = responseClass1.getResponsePacket();
                            setSlider2();
                            Utils.dismissProgressDialog();
                            callGetLiveOrdersApi();
//                            callGetTopSellingItemsApi();
                        }

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

    private void callGetTopSellingItemsApi() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.LATITUDE, liveLat);
            jsonObject.put(Constants.LONGITUDE, liveLong);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(TopSellingBean.class, new NetworkManager.OnCallback<TopSellingBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        topSellingBean = (TopSellingBean) responseClass.getResponsePacket();
                        setTopSellingData();
                    } else {
                        if (((Dashboard) getActivity()).isFromNotification)
                            checkNotificationData();
                        else if (((Dashboard) getActivity()).isFromSplash)
                            callGetLastOrderRatedApi();
                        ((Dashboard) getActivity()).isHomeLoad = true;
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_TOP_SELLING_ITEM_LIST, jsonObject.toString(), "Loading...", false, AppUrls.REQUESTTAG_GETTOPSELLINGITEMLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetLastOrderRatedApi() {

        try {
            new NetworkManager(LastOrderRateBean.class, new NetworkManager.OnCallback<LastOrderRateBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        LastOrderRateBean lastOrderBean = (LastOrderRateBean) responseClass.getResponsePacket();
                        Intent intent = new Intent(mContext, RatingActi.class);
                        intent.putExtra(Constants.ORDER_ID, lastOrderBean.getOrderId());
                        intent.putExtra(Constants.FROM_WHICH, "Home");
                        intent.putExtra(Constants.ORDER_TYPE, lastOrderBean.getOrderType());
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_LAST_ORDER_WHICH_WAS_NOT_RATED, "", "Loading...", false, AppUrls.REQUESTTAG_GETLASTORDERWHICHWASNOTRATED);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (!TextUtils.isEmpty(slider.getBundle().getString(Constants.SLIDER_ACTION)))
            switch (slider.getBundle().getString(Constants.SLIDER_ACTION)) {
                case "NotificationList":
                    customCleverTapEvent(slider.getDescription(), "NotificationList");
                    Utils.startActivity(mContext, NotificationActi.class);
                    break;
                case "OrderList":
                    customCleverTapEvent(slider.getDescription(), "OrderList");
                    Utils.startActivity(mContext, OrderHistoryActi.class);
                    break;
                case "BillToCompany":
                    customCleverTapEvent(slider.getDescription(), "OrderList");
                    Utils.startActivity(mContext, OrderBillToCompanyHistoryActi.class);
                    break;
                case "OfferList":
                    customCleverTapEvent(slider.getDescription(), "OfferList");
                    Utils.startActivity(mContext, OffersActi.class);
                    break;
                case "StealDeal":
                    Utils.startActivity(mContext, StealDealActi.class);
                    break;
                case "HomeDelivery":
                    customCleverTapEvent(slider.getDescription(), "HomeDelivery");

                    goToDeliveryRestaurantScreen(getString(R.string.delivery));
                    break;
                case "Cafe":
                    customCleverTapEvent(slider.getDescription(), "Cafe");
                    Intent cafeIntent = new Intent(mContext, DineInActi.class);
                    cafeIntent.putExtra(Constants.FROM_WHICH, getString(R.string.cafe));
                    startActivity(cafeIntent);
                    break;
                case "DineIn":
                    customCleverTapEvent(slider.getDescription(), "DineIn");
                    Intent intent = new Intent(mContext, DineInActi.class);
                    intent.putExtra(Constants.FROM_WHICH, getString(R.string.dine_in));
                    startActivity(intent);
                    break;
                case "TakeAway":
                    customCleverTapEvent(slider.getDescription(), "TakeAway");
                    goToRestaurantScreen(mContext.getString(R.string.take_away));
                    break;
                case "FeedBack":
                    customCleverTapEvent(slider.getDescription(), "FeedBack");
                    Utils.startActivity(mContext, FeedbackActi.class);
                    break;
                case "Wallet":
                    customCleverTapEvent(slider.getDescription(), "Wallet");
                    Utils.startActivity(mContext, WalletActi.class);
                    break;
                case "Reservation":
                    customCleverTapEvent(slider.getDescription(), "Reservation");

                    Utils.startActivity(mContext, ReservationActi.class);
                    break;
                case "EventList":
                    customCleverTapEvent(slider.getDescription(), "EventList");

                    Utils.startActivity(mContext, EventsActi.class);
                    break;
            }

    }

    private void callGetAddressApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<AddressBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<AddressBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<AddressBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0) {
                            setData();

//                            binding.rvAddress.setVisibility(View.VISIBLE);
                        } else
                            goToLocationScreen();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection");
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ADDRESS_LIST, "", "Loading...", true, AppUrls.REQUESTTAG_ADDRESSLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setData() {

        if (list != null && list.size() > 0) {
            addressBean = list.get(0);
            goToDeliveryRestaurantScreen(getString(R.string.delivery));
//            mAdapter = new AddressAdapter(mContext, list, this);
//            binding.rvAddress.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();

//            binding.txtAddress.setText(addressBean.getAddressLine1());
        }
    }

    /* this api use for the buttom live order show */

    private void callGetLiveOrdersApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<OrdersBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<OrdersBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<OrdersBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        liveList = responseClass1.getResponsePacket();

                        if (liveList != null && liveList.size() > 0)
                            setLiveData();
                        else
                            binding.rlLiveOrders.setVisibility(View.GONE);
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETORDERLIST + "All" + "/Live/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setLiveData() {
        binding.rlLiveOrders.setVisibility(View.VISIBLE);
        adapterViewPager = new LivePagerAdapter(getActivity().getSupportFragmentManager());
        binding.pager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();
    }

    private class LivePagerAdapter extends FragmentStatePagerAdapter {

        public LivePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return LiveFrag.newInstance(liveList.get(position));

        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return liveList.size();
        }
    }

    private void customCleverTapEvent(String whichBanner, String navigationAction){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> bannerTappedAction = new HashMap<String, Object>();
                bannerTappedAction.put("Banner Navigated To", navigationAction);
                if(whichBanner!= null &&whichBanner.equalsIgnoreCase("TOP"))
                    AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Top Banner Clicked", bannerTappedAction);
                else
                    AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Bottom Banner Clicked", bannerTappedAction);
            }
        }).start();


    }

}
