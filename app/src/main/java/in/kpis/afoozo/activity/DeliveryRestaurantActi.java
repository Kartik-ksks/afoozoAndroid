package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityDeliveryRestaurantBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.adapter.CuisineAdapter;
import in.kpis.afoozo.adapter.HomeAdapter;
import in.kpis.afoozo.bean.BannerListBean;
import in.kpis.afoozo.bean.CuisineBean;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.custome.HidingScrollListener;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

public class DeliveryRestaurantActi extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {


    private Context mContext;


    private ActivityDeliveryRestaurantBinding binding;

    private HomeAdapter mAdapter;

    private FusedLocationProviderClient mFusedLocationClient;

    private int LOCATION_REQUEST_CODE = 101;
    private int REQUEST_FOR_ADD = 102;
    private int ADDRESS_REQUEST_CODE = 103;


    private double liveLat;
    private double liveLong;

    //    private ArrayList<HistoryBean> liveList;
    private ArrayList<RestaurantBean> restaurantList;
    private ArrayList<RestaurantBean> cuRestaurantList;
    private ArrayList<CuisineBean> cuisineList;

    private CuisineAdapter cuisineAdapter;
    private HomeAdapter homeAdapter;
    private long cuisineId;
    private String cuisineName = "";
    private LocationCallback mLocationCallback;
    private boolean isFirstTime = true;
    private ArrayList<BannerListBean> bannerList;

    private BannerListBean bannerMainBean;
    private ArrayList<BannerListBean> shortBannerList = new ArrayList();
    private ArrayList<BannerListBean> bottomBannerList = new ArrayList();
    private SliderLayout sliderBottom;
    private SliderLayout sliderHome;
    private String fromWhich;
    private AddressBean addressBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_restaurant);

        mContext = DeliveryRestaurantActi.this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (getIntent().getExtras() != null) {
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            if (fromWhich.equals(getString(R.string.delivery))) {
                binding.llAddress.setVisibility(View.VISIBLE);
//                liveLat = getIntent().getDoubleExtra(Constants.LATITUDE, 0);
//                liveLong = getIntent().getDoubleExtra(Constants.LONGITUDE, 0);
//                addressBean = (AddressBean) getIntent().getSerializableExtra(Constants.ADDRESS_BEAN);
//                binding.txtAddress.setText(addressBean.getAddressLine1());
//                binding.txtLocality.setText(addressBean.getAddressLine2());
            }
        }
        initialize();
    }

    private void initialize() {
        sliderHome = (SliderLayout) findViewById(R.id.sliderHome);

        sliderBottom = (SliderLayout) findViewById(R.id.sliderBottom);

        binding.rvHome.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvHome.setItemAnimator(new DefaultItemAnimator());
        binding.rvHome.setNestedScrollingEnabled(false);

        binding.cuisineLayout.rvCuisine.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        binding.cuisineLayout.rvCuisine.setItemAnimator(new DefaultItemAnimator());
        binding.cuisineLayout.rvCuisine.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (CuisineBean cb : cuisineList)
                    cb.setSelected(false);
//                ((DashboardActi) getActivity()).isCousineSelected = true;
                cuisineList.get(position).setSelected(true);
                cuisineAdapter.notifyDataSetChanged();

//                cuisineId = cuisineList.get(position).getCuisineId();
                cuisineName = cuisineList.get(position).getCuisineTitle();
                binding.txtFilter.setText(cuisineName);
                cuisineFilter(cuisineName);
//                Utils.progressDialog(mContext, "");
//                callRestaurantApi();

            }
        }));

        binding.rvHome.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

//        binding.rvHome.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                goToMenuScreen(position);
//            }
//        }));

        binding.llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddressActi.class);
//                intent.putExtra(Constants.IS_FROM_HOME, false);
//                intent.putExtra(Constants.LATITUDE, liveLat);
//                intent.putExtra(Constants.LONGITUDE, liveLong);
                startActivityForResult(intent, ADDRESS_REQUEST_CODE);
            }
        });

        Utils.progressDialog(mContext, "");

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectCuisine();
            }
        });

        updateLocation();

//        callCuisineApi();
    }

    private void cuisineFilter(String cuisineName) {
        cuRestaurantList = new ArrayList<>();
        for (int i = 0; i < restaurantList.size(); i++) {
            ArrayList<String> list = restaurantList.get(i).getCuisineList();
            for (String names : list) {
                if (names.equals(cuisineName)) {
                    cuRestaurantList.add(restaurantList.get(i));
                }
            }
        }
        setRestaurantData();
    }

    public void deselectCuisine() {
        for (CuisineBean cb : cuisineList)
            cb.setSelected(false);
        cuisineAdapter.notifyDataSetChanged();
        cuisineId = 0;
        cuisineName = "";
        Utils.progressDialog(mContext, "");
        callRestaurantApi();
    }

    public void goToMenuScreen(String restaurantId, String restaurantName, int minmumAmount, boolean open) {
        customRestaurantViewedEvent(restaurantId, restaurantName);
        Intent intent = new Intent(mContext, AddItemsActi.class);
        intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
        intent.putExtra(Constants.FROM_WHICH, fromWhich);
        intent.putExtra(Constants.RESTAURANT_NAME, restaurantName);
        if (fromWhich.equals(getString(R.string.delivery)))
            intent.putExtra(Constants.ADDRESS_BEAN, addressBean);
        intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, 0);
        intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, minmumAmount);
        intent.putExtra(Constants.IS_OPEN, open);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((DashboardActi) getActivity()).hideShowToolbar(false, false, "");

//        if (isFirstTime)
//            callLiveOrdersApi(false);
//        else
//            callLiveOrdersApi(true);
    }

    @Override
    public void onStop() {
        if (mFusedLocationClient != null && mLocationCallback != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        super.onStop();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    liveLat = data.getDoubleExtra(Constants.LATITUDE, 0);
                    liveLong = data.getDoubleExtra(Constants.LONGITUDE, 0);
                    setLocation();
                }
            }
        } else if (requestCode == ADDRESS_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                addressBean = (AddressBean) data.getSerializableExtra(Constants.ADDRESS_BEAN);

                if (data != null) {
                    liveLat = addressBean.getLatitude();
                    liveLong = addressBean.getLongitude();
                    setLocation();
                }
            }

        }
    }

    private void showViews() {
        binding.llHeader.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private void hideViews() {
        binding.llHeader.animate().translationY(-binding.llHeader.getHeight()).setInterpolator(new AccelerateInterpolator(2));

//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
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

                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        liveLat = location.getLatitude();
                        liveLong = location.getLongitude();

                        callGetNearestAddressApi();
//                        setLocation();
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    }
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void setLocation() {
        Address address = Utils.getAddress(DeliveryRestaurantActi.this, liveLat, liveLong);

        if (address != null) {

            if (address != null && address.getAddressLine(0) != null) {

                String addressString = "";
                if (!TextUtils.isEmpty(address.getAddressLine(0))) {
                    addressString = address.getAddressLine(0);

                    if (addressString.contains(address.getFeatureName()))
                        addressString = addressString.replace(address.getFeatureName() + ", ", "");
                } else {
                    if (!TextUtils.isEmpty(address.getSubLocality()))
                        addressString = addressString + address.getSubLocality() + ", ";
                    if (!TextUtils.isEmpty(address.getSubAdminArea()))
                        addressString = addressString + address.getSubAdminArea() + ", ";
                    if (!TextUtils.isEmpty(address.getAdminArea()))
                        addressString = addressString + address.getAdminArea();
                }

                binding.txtAddress.setText(addressString);

                AddressBean addressBean = new AddressBean();
                addressBean.setAddressLine1(addressString);
                addressBean.setLatitude(liveLat);
                addressBean.setLatitude(liveLong);

                String addressJson = new Gson().toJson(addressBean);

                SharedPref.setAddressModelJSON(mContext, addressJson);

//                        if (!TextUtils.isEmpty(address.getFeatureName())){
//                            binding.txtLocality.setText(address.getFeatureName());
//                            binding.txtLocality.setVisibility(View.VISIBLE);
//                        } else
//                            binding.txtLocality.setVisibility(View.VISIBLE);

                callRestaurantApi();

                callCuisineApi();
            }
        }
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

    /**
     * This Method set bottom Slider
     */
    private void setSlider() {

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

//                add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString(Constants.SLIDER_ACTION, bottomBannerList.get(i).getSliderAction());

                sliderBottom.addSlider(textSliderView);
            }
        }
        sliderBottom.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderBottom.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderBottom.setCustomAnimation(new DescriptionAnimation());
        sliderBottom.setDuration(4000);
    }


    private void setCuisineData() {
        cuisineAdapter = new CuisineAdapter(mContext, cuisineList, true);
        binding.cuisineLayout.rvCuisine.setAdapter(cuisineAdapter);
        cuisineAdapter.notifyDataSetChanged();
    }

    private void setRestaurantData() {
        if (!TextUtils.isEmpty(cuisineName)) {
            binding.txtFilter.setText(cuisineName);
            binding.txtFilter.setVisibility(View.VISIBLE);
            binding.btnClear.setVisibility(View.VISIBLE);
            binding.llBanner.setVisibility(View.GONE);
//            binding.llClearFilter.setVisibility(View.VISIBLE);
            homeAdapter = new HomeAdapter(mContext, cuRestaurantList, true);
            binding.rvHome.setAdapter(homeAdapter);
            homeAdapter.notifyDataSetChanged();
        } else {
            binding.txtFilter.setVisibility(View.GONE);
            binding.btnClear.setVisibility(View.GONE);
            binding.llBanner.setVisibility(View.VISIBLE);
//            binding.llClearFilter.setVisibility(View.GONE);
            homeAdapter = new HomeAdapter(mContext, restaurantList, true);
            binding.rvHome.setAdapter(homeAdapter);
            homeAdapter.notifyDataSetChanged();
        }


        Utils.dismissProgressDialog();
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

                        if (type.equalsIgnoreCase(Constants.SHORTBANNER)) {
                            shortBannerList = responseClass1.getResponsePacket();
                            setTopSlider();
                            callGetBannerApi(Constants.DELIVERY);
                        } else {
                            bottomBannerList = responseClass1.getResponsePacket();
                            setSlider();
//                            setSlider2();
                            Utils.dismissProgressDialog();

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

    private void callCuisineApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<CuisineBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<CuisineBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<CuisineBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        cuisineList = responseClass1.getResponsePacket();

                        setCuisineData();
                        callGetBannerApi(Constants.SHORTBANNER);

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.CUISINE_LIST, "", "Loading...", false, AppUrls.REQUESTTAG_CUISINELIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callRestaurantApi() {
        RestaurantRequestBean bean = new RestaurantRequestBean();
        bean.setOrderType(Constants.HOME_DELIVERY);
        bean.setLatitude(liveLat);
        bean.setLongitude(liveLong);
        bean.setStart(0);
        bean.setCityId(0);
        bean.setLength(1000);
        bean.setSearchKey("");
        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<RestaurantBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RestaurantBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        restaurantList = responseClass1.getResponsePacket();

                        if (restaurantList != null && restaurantList.size() > 0) {
                            setRestaurantData();
                            binding.rvHome.setVisibility(View.VISIBLE);
                            binding.txtNotServing.setVisibility(View.GONE);
                        } else {
                            binding.rvHome.setVisibility(View.GONE);
                            binding.txtNotServing.setVisibility(View.VISIBLE);
                            binding.txtFilter.setVisibility(View.GONE);
                            binding.btnClear.setVisibility(View.GONE);
                            binding.llBanner.setVisibility(View.VISIBLE);

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
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESTAURANT_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_RESTAURANTLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*  This Api use for Get Nearest  Address User
     * */
    private void callGetNearestAddressApi() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.LATITUDE, liveLat);
            jsonObject.put(Constants.LONGITUDE, liveLong);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(AddressBean.class, new NetworkManager.OnCallback<AddressBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        addressBean = (AddressBean) responseClass.getResponsePacket();
                        liveLat = addressBean.getLatitude();
                        liveLong = addressBean.getLongitude();

                        if (addressBean != null && addressBean.getRecordId() != 0)
//                            AppInitialization.addressId = addressBean.getRecordId();

                            setLocation();

                    } else {
// Utils.showCenterToast(mContext, responseClass.getMessage());
                        setLocation();
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
// Utils.showCenterToast(mContext, getString(R.string.internet_conanection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_NEAREST_ADDRESS+"?lat="+liveLat +"&lng="+liveLong,"", "Loading...", false, AppUrls.REQUESTTAG_GETNEARESTADDRESS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        int pos = slider.getBundle().getInt("Position");
//        callGetRestaurantDetailsApi(bannerList.get(pos).getRestaurantId(), bannerList.get(pos).getCategoryId());
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
