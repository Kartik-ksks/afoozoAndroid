package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.DetailsAdapter;
import in.kpis.afoozo.bean.DriverLocationBean;
import in.kpis.afoozo.bean.OrderDetailsBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import com.kpis.afoozo.databinding.ActivityTrackBinding;
import in.kpis.afoozo.googleMap.DirectionsJSONParser;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.CALL_PHONE;

public class TrackActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityTrackBinding binding;
    private ArrayList<OrderDetailsItemBean> itemList = new ArrayList<>();
    private DetailsAdapter mAdapter;
    private boolean isFromCart;
    private String orderId;
    private OrderDetailsBean orderDetailsBean;

    private GoogleMap googleMap;
    private Handler handler;
    private Timer orderTimer = new Timer();

    private Marker driverMarkar;

    private boolean isOrderThreadOn;
    private boolean isMarkerSet;
    private boolean isDriverDataSet;
    private boolean isDataSet;
    private boolean isDriverMarkerSet;

    private int REQUESTPERMISSIONCODE = 222;

    private String origin;
    private String waypoints;

    private LatLng latlng1;
    private LatLng latlng2;
    private PolylineOptions lineOptions;
    private Polyline polyline;
    private boolean etaRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);

        if (getIntent().getExtras() != null){
            isFromCart = getIntent().getBooleanExtra(Constants.IS_FROM_CART, false);
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        }

        mContext = TrackActi.this;
        binding.mapViewOrder.onCreate(savedInstanceState);
        binding.mapViewOrder.onResume();
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
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.order_tracking));

        binding.rvTrackItems.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvTrackItems.setItemAnimator(new DefaultItemAnimator());

        binding.txtOrderDetails.setText(getString(R.string.order_details) + " " + orderId);

        binding.toolbar.toolbarCallResturant.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarCallResturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCallFunctionality("Resturant");
            }
        });
        binding.imvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCallFunctionality("Driver");
            }
        });

        setMap();
        callOrderDetailApi(true);
    }



    @Override
    public void onBackPressed() {
        if (isFromCart)
            Utils.startActivityWithFinish(mContext, Dashboard.class);
        else
            finish();
    }

    public void showInfoPopUp(View view) {
        if (orderDetailsBean != null && orderDetailsBean.getTaxJson() != null && orderDetailsBean.getTaxJson().size() > 0)
            Utils.showTaxInfoPopUp(mContext, orderDetailsBean.getTaxJson());
    }

    public void checkCallFunctionality(String from) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //No calling functionality
            Utils.showCenterToast(mContext, "No calling functionality");
        } else {
            //calling functionality
            checkPermission(from);
        }
    }

    public boolean checkPermission(String from) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(mContext, CALL_PHONE);
            if (result != PackageManager.PERMISSION_GRANTED)
                requestPermission();
            else
                callAction(from);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            callAction(from);
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{CALL_PHONE}, REQUESTPERMISSIONCODE);
    }

    @SuppressLint("MissingPermission")
    private void callAction(String from) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);

        if(from.equals("Resturant")){
            callIntent.setData(Uri.parse("tel:" + orderDetailsBean.getRestaurantContactNumber()));

        }else if(from.equals("Driver")) {

            callIntent.setData(Uri.parse("tel:" + orderDetailsBean.getDriverMobile()));
        }
        startActivity(callIntent);
    }

    private void setOrderData() {
//        OrderDetailsBeanItemBean bean = new OrderDetailsBeanItemBean("Special Thali");
//        itemList.add(bean);
        mAdapter = new DetailsAdapter(mContext, orderDetailsBean.getItemList());
        binding.rvTrackItems.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void goToDetailsScreen (View view){
        Intent intent = new Intent(mContext, OrderDetailsActi.class);
        intent.putExtra("IsFromTrack", true);
        intent.putExtra(Constants.ORDER_ID, orderId);
        startActivity(intent);
    }

    public void goToRatingScreen(View view){
//        Utils.startActivity(mContext, RatingActi.class);
    }

    private void callOrderDetailApi(boolean isShow){

        try {
            new NetworkManager(OrderDetailsBean.class, new NetworkManager.OnCallback<OrderDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        orderDetailsBean = (OrderDetailsBean) responseClass.getResponsePacket();

                        checkIsOrderAccepted();

//                        if(orderDetailsBean.getOrderStatus().equalsIgnoreCase("Preparing")) {
//                            binding.llDriverDetails.setVisibility(View.GONE);
//                        }else if(orderDetailsBean.getOrderStatus().equalsIgnoreCase("Dispatched") && orderDetailsBean.getDriverName() == null){
//                            binding.llDriverDetails.setVisibility(View.GONE);
//                        }else{
//                            binding.llDriverDetails.setVisibility(View.VISIBLE);
//                        }
                        if (!TextUtils.isEmpty(orderDetailsBean.getDriverName()))
                            binding.llDriverDetails.setVisibility(View.VISIBLE);
                        else
                            binding.llDriverDetails.setVisibility(View.GONE);

                        if (!isOrderThreadOn)
                            startOrderThread();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }

            ).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ORDER_DETAIL  + orderId, "", "Loading...", isShow, AppUrls.REQUESTTAG_ORDERDETAIL );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    DriverLocationBean driverLocationBean;
    private void callGetDriverLatLngApi(boolean isShow){

        try {
            new NetworkManager(DriverLocationBean.class, new NetworkManager.OnCallback<DriverLocationBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        driverLocationBean = (DriverLocationBean) responseClass.getResponsePacket();

                        checkIsOrderAccepted();

                        if (!isOrderThreadOn)
                            startOrderThread();

                    } else {
                        if(responseClass.getErrorCode() == 51){
                            callOrderDetailApi(false);
                        }else {
                            Utils.showCenterToast(mContext, responseClass.getMessage());
                        }
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }

            ).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETDRIVERLOCATION  + orderId, "", "Loading...", isShow, AppUrls.REQUESTTAG_GETDRIVERLOCATION );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    //SWAG DELIVERY

    @Override
    protected void onResume() {
        super.onResume();
         startOrderThread();
        callOrderDetailApi(false);
    }

    @Override
    protected void onPause() {
        if (isOrderThreadOn)
            orderTimer.cancel();
        super.onPause();
    }


    private void setMap() {
        binding.mapViewOrder.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.getUiSettings().setZoomControlsEnabled(true);
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

                callOrderDetailApi(true);

            }
        });
    }

    private void checkIsOrderAccepted() {

        if (orderDetailsBean.getOrderStatus().equalsIgnoreCase("OrderCancelled")) {
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.your_order_has_been_cancelled), "", getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.startActivityWithFinish(mContext, Dashboard.class);
                }
            });
        }

        setStatusLines(orderDetailsBean.getOrderStatus());
        if(!etaRefresh)
        showTImer();


        switch (orderDetailsBean.getOrderStatus()){
            case "Preparing":
//                binding.txtStatus.setText(getString(R.string.waiting_for_confirmation));
//                binding.txtLongStatus.setText("");
                break;
            case "Dispatched":
                callGetDriverLatLngApi(false);
//                binding.txtStatus.setText(getString(R.string.order_preparing));
//                binding.txtLongStatus.setText(getString(R.string.order_is_being_prepared));
                break;
            case "Delivered":
//                binding.txtStatus.setText(getString(R.string.order_accepted));
//                binding.txtLongStatus.setText(getString(R.string.order_is_being_prepared));
                break;
//            case "PickedUp":
////                binding.txtStatus.setText(getString(R.string.order_pickedup));
////                binding.txtLongStatus.setText(getString(R.string.order_is_on_the_way));
//                break;
//            case "OnTheWay":
////                binding.txtStatus.setText(getString(R.string.order_on_the_way));
////                binding.txtLongStatus.setText(getString(R.string.order_is_on_the_way));
//                break;
//            case "ReachedAtDestination":
////                binding.txtStatus.setText(R.string.reached_at_destination);
////                binding.txtLongStatus.setText(getString(R.string.driver_is_waiting_at_your_location));
//                break;
//            case "OrderDelivered":
////                binding.txtStatus.setText(R.string.order_delivered);
////                binding.txtLongStatus.setText("Thank you for using");
//                break;
        }

        if (!isDriverDataSet) {
            if (!TextUtils.isEmpty(orderDetailsBean.getDriverName())) {
                isDriverDataSet = true;
                binding.llDriver.setVisibility(View.VISIBLE);
                binding.txtDriverName.setText(orderDetailsBean.getDriverName());
                binding.txtRating.setRating(orderDetailsBean.getDriverRating());
                if (!TextUtils.isEmpty(orderDetailsBean.getDriverImageUrl()))
                    Utils.setImage(mContext, binding.imvDriverImage, orderDetailsBean.getDriverImageUrl());
            }
        }

        if (!isMarkerSet)
            showMarkars();

        if (!isDriverMarkerSet) {
            if (driverLocationBean != null && driverLocationBean.getLatitude() > 0) {
                isDriverMarkerSet = true;
                latlng1 = new LatLng(driverLocationBean.getLatitude(), driverLocationBean.getLongitude());
                showDriverMarkar(latlng1);
            }
        } else {
            latlng2 = new LatLng(driverLocationBean.getLatitude(), driverLocationBean.getLongitude());
            float toRotation = (float) Utils.bearingBetweenLocations(latlng1, latlng2);
            if (driverMarkar != null){
                animateMarker(driverMarkar, latlng2, false);
            }
            latlng1 = latlng2;
        }

        if (!isDataSet) {
            isDataSet = true;
            if (orderDetailsBean.getItemList() != null && orderDetailsBean.getItemList().size()>0){
                DetailsAdapter mAdapter = new DetailsAdapter(mContext, orderDetailsBean.getItemList());
                binding.rvTrackItems.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            binding.txtSubTotal.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getOrderSubTotal()));
            if(orderDetailsBean.getPaidByWallet()>0) {
                binding.llPaidByWallet.setVisibility(View.VISIBLE);
                binding.txWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPaidByWallet()));
            }
            if(orderDetailsBean.getLoyaltyAmount()>0) {
                binding.llLoyalty.setVisibility(View.VISIBLE);
                binding.txtCoinDiscount.setVisibility(View.VISIBLE);
                binding.txtCoinDiscount.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getLoyaltyAmount()));
            }else {
                binding.llLoyalty.setVisibility(View.GONE);
            }
            if(orderDetailsBean.getPaidOnDelivery()>0) {
                binding.llPaidOnDelivery.setVisibility(View.VISIBLE);
                binding.txPaidOnDelvery.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPaidOnDelivery()));
            }
            binding.txWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPaidByWallet()));
            if ((orderDetailsBean.getTaxAmount() - orderDetailsBean.getTaxDiscountAmount()) > 0 ) {
                binding.llTax.setVisibility(View.VISIBLE);
                binding.txtTax.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getTaxAmount() - orderDetailsBean.getTaxDiscountAmount()));
            } else
                binding.llTax.setVisibility(View.GONE);

            if (orderDetailsBean.getPackingCharges() > 0){
                binding.llPackingCharges.setVisibility(View.VISIBLE);
                binding.txtPacking.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPackingCharges()));
            } else
                binding.llPackingCharges.setVisibility(View.GONE);
            if (orderDetailsBean.getDeliveryFee() > 0) {
                binding.txtDeliveryCharge.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getDeliveryFee()));
                binding.llDeliveryCharges.setVisibility(View.VISIBLE);
            } else
                binding.llDeliveryCharges.setVisibility(View.GONE);

            if (orderDetailsBean.getCouponDiscountAmount() > 0) {
                binding.llCoupon.setVisibility(View.VISIBLE);
                binding.txtCouponDiscount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetailsBean.getCouponDiscountAmount()));
            } else
                binding.llCoupon.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(orderDetailsBean.getCouponCode())) {
                binding.llPromoCode.setVisibility(View.VISIBLE);
                binding.txtPromoCode.setText(orderDetailsBean.getCouponCode());
            } else
                binding.llPromoCode.setVisibility(View.GONE);
//            binding.txtRewardDiscount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetailsBean.getCoinDiscount()));
            binding.txtGrandTotal.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getOrderTotal()));
        }

//        if (!TextUtils.isEmpty(orderDetailsBean.getEta()))
//            binding.txtETA.setText(orderDetailsBean.getEta() + " min");

        showRoute();

    }

    private void setStatusLines(String status){
        switch (status){
            case "Preparing":
                binding.llTop.viewLeft.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.llTop.viewRight.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.llTop.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.llTop.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
                binding.llTop.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
//                binding.btnCallDriver.setVisibility(View.GONE);
                break;
            case "Dispatched":
                binding.llTop.viewLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.llTop.viewRight.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.llTop.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.llTop.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.llTop.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
//                binding.btnCallDriver.setVisibility(View.VISIBLE);
                break;
            case "Delivered":
                binding.llTop.viewLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.llTop.viewRight.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.llTop.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.llTop.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.llTop.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
//                binding.btnCallDriver.setVisibility(View.VISIBLE);
                goToRatingScreen();
                break;
        }
    }

    long minutes;
    int seconds;
    private void showTImer(){
        if(orderDetailsBean.getOrderStatus().equals("Ordered")){
            binding.txtETA.setVisibility(View.GONE);
        }else {
            binding.txtETA.setVisibility(View.VISIBLE);
            etaRefresh = true;
        }
        int days;
        int hours;
        int min;

        long time = System.currentTimeMillis();
        long difference = time -  orderDetailsBean.getAcceptRejectAtTime();
        days = (int) (difference / (1000 * 60 * 60 * 24));
        hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        min = (min < 0 ? -min : min);

        Log.i("Hours"," :: "+hours+"--"+min);


        orderDetailsBean.getOrderStatus();
        long totalMinutes = orderDetailsBean.getEstimatedTimeArrival();
        minutes = totalMinutes- min;
        seconds = 60;
        new CountDownTimer(minutes*1000*60, 1000) {

            public void onTick(long millisUntilFinished) {
                String showSec;
                String showMin;

                if(seconds < 10)
                    showSec = "0"+String.valueOf(seconds);
                else
                    showSec = String.valueOf(seconds);

                if(minutes < 10)
                    showMin = "0"+ String.valueOf(minutes);
                else
                    showMin = String.valueOf(minutes);
                binding.txtETA.setText("ETA - "+showMin+" min");
                seconds -= 1;

                if(seconds == 0)
                {
                    binding.txtETA.setText("ETA - "+showMin+" min");
                    Log.d("real time",showMin);

                    seconds=60;
                    minutes=minutes-1;
                }
            }

            public void onFinish() {
                binding.txtETA.setText("Your Order Almost Ready");
            }

        }.start();
    }

    private void goToRatingScreen() {
        orderTimer.cancel();
        Intent intent = new Intent(mContext, RatingActi.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.FROM_WHICH, "Track");
        startActivity(intent);
        finish();
    }


    private void showRoute() {
        String url = getMapsApiDirectionsUrl();
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private void showMarkars(){

        isMarkerSet = true;

        ArrayList<LatLng> latLongList = new ArrayList<>();

        LatLng pickUP = new LatLng(orderDetailsBean.getPickUpLatitude(), orderDetailsBean.getPickUpLongitude());
//        LatLng pickUP = new LatLng(orderDetailsBean.getgOutletLat(), orderDetailsBean.getgOutletLong());
        googleMap.addMarker(new MarkerOptions().position(pickUP).title("PickUp Address").snippet(orderDetailsBean.getPickUpCompleteAddress()).icon(Utils.bitmapDescriptorFromVector(mContext, R.drawable.ic_pickup_marker)));
        latLongList.add(pickUP);


        LatLng destination = new LatLng(orderDetailsBean.getDeliveryLatitude(), orderDetailsBean.getDeliveryLongitude());
        googleMap.addMarker(new MarkerOptions().position(destination).title("Destination").snippet("").icon(Utils.bitmapDescriptorFromVector(mContext, R.drawable.ic_drop)));
        latLongList.add(destination);

        if(driverLocationBean != null && driverLocationBean.getLatitude() != 0) {
            LatLng driver = new LatLng(driverLocationBean.getLatitude(), driverLocationBean.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(driver).title("Driver").snippet("").icon(Utils.bitmapDescriptorFromVector(mContext, R.drawable.ic_motorbike)));
            latLongList.add(driver);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < latLongList.size(); i++) {
            builder.include(latLongList.get(i));
        }
        LatLngBounds bounds = builder.build();

        int padding = 30; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);
    }

    private void startOrderThread() {
        isOrderThreadOn = true;
        handler = new Handler();
        orderTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            if(orderDetailsBean.getOrderType().equalsIgnoreCase(Constants.CAFE) || orderDetailsBean.getOrderType().equalsIgnoreCase(Constants.CAFE)){
                                callOrderDetailApi(false);
                            }else {
                                if (orderDetailsBean.getOrderStatus().equalsIgnoreCase("Preparing")) {
                                    callOrderDetailApi(false);
                                } else if (orderDetailsBean.getOrderStatus().equalsIgnoreCase("Dispatched") && orderDetailsBean.getDriverName() == null) {
                                    callOrderDetailApi(false);
                                } else {
                                    callGetDriverLatLngApi(false);
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        orderTimer.schedule(doAsynchronousTask, 60000, 60000);
    }

    private void showDriverMarkar(LatLng latlng) {

        driverMarkar = googleMap.addMarker(new MarkerOptions().position(latlng).title("Marker Title").snippet("Marker Description").icon(Utils.bitmapDescriptorFromVector(mContext, R.drawable.ic_motorbike)));
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 10000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    private String getMapsApiDirectionsUrl() {

        waypoints ="";

        if (orderDetailsBean.getOrderStatus().equals("Preparing"))
            origin = "origin=" + orderDetailsBean.getPickUpLatitude() + "," + orderDetailsBean.getPickUpLongitude() +"&";
        else if (orderDetailsBean.getOrderStatus().equals("Dispatched") && orderDetailsBean.getDriverName() == null)
            origin = "origin=" + orderDetailsBean.getPickUpLatitude() + "," + orderDetailsBean.getPickUpLongitude() +"&";
        else {
            if(driverLocationBean != null && driverLocationBean.getLatitude() != 0)
                origin = "origin=" + driverLocationBean.getLatitude() + "," + driverLocationBean.getLongitude() + "&";
        }

        origin = origin + "destination="
                + orderDetailsBean.getDeliveryLatitude() + ","
                + orderDetailsBean.getDeliveryLongitude()
                + "&waypoints=via:";


        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + "mode=driving&"
                + "transit_routing_preference=less_driving&"
                + origin  + params + "&key=" + getString(R.string.google_key);
        return url;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            Utils.dismissProgressDialog();

            if (result != null) {
                if (polyline != null)
                    polyline.remove();
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap point = path.get(j);

                        double lat = Double.parseDouble((String) point.get("lat"));
                        double lng = Double.parseDouble((String) point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(getResources().getColor(R.color.colorPrimary));
                    lineOptions.geodesic(true);

                }
                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null)
                    polyline = googleMap.addPolyline(lineOptions);
            }

        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
