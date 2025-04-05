package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.DetailsAdapter;
import in.kpis.afoozo.bean.OrderDetailsBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import com.kpis.afoozo.databinding.ActivityOrderDetialsBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.CALL_PHONE;

public class OrderDetailsActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityOrderDetialsBinding binding;
    private int REQUESTPERMISSIONCODE = 222;

    private ArrayList<OrderDetailsItemBean> itemList = new ArrayList<>();
    private DetailsAdapter mAdapter;
    private OrderDetailsBean orderDetailsBean;
    private boolean isFromTrack;
    private boolean isFromDetails;
    private String orderId;
    private boolean isOrderThreadOn;
    private Handler handler;
    private Timer orderTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detials);

        if (getIntent().getExtras() != null){
            isFromTrack = getIntent().getBooleanExtra("IsFromTrack", false);
            isFromDetails = getIntent().getBooleanExtra("IsFromDetails", false);
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        }

        mContext = OrderDetailsActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.order_details));

        binding.rvDetails.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvDetails.setItemAnimator(new DefaultItemAnimator());

        binding.toolbar.toolbarCallResturant.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarCallResturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCallFunctionality();
            }
        });

        callOrderDetailApi();
    }

    @Override
    protected void onResume() {
        super.onResume();

            startOrderThread();
    }

    @Override
    protected void onPause() {
        if (isOrderThreadOn)
            orderTimer.cancel();
        super.onPause();
    }


    public void showInfoPopUp(View view) {
        if (orderDetailsBean != null && orderDetailsBean.getTaxJson() != null && orderDetailsBean.getTaxJson().size() > 0)
            Utils.showTaxInfoPopUp(mContext, orderDetailsBean.getTaxJson());
    }

    private void setData() {
        binding.txtRestaurantName.setText(orderDetailsBean.getRestaurantName());
        binding.txtOrderId.setText(orderId);
        binding.txtStatus.setText(orderDetailsBean.getOrderStatus());
        binding.txtDateTime.setText(Utils.getDateTimeForOrders(orderDetailsBean.getOrderDateTime()));

        if(orderDetailsBean.getPaidByWallet()>0) {
            binding.llPaidByWallet.setVisibility(View.VISIBLE);
            binding.txWalletAmount.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPaidByWallet()));
        }
        if(orderDetailsBean.getPaidOnDelivery()>0) {
            binding.llPaidOnDelivery.setVisibility(View.VISIBLE);
            binding.txPaidOnDelvery.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPaidOnDelivery()));
        }

        if(orderDetailsBean.getLoyaltyAmount()>0) {
            binding.llLoyalty.setVisibility(View.VISIBLE);
            binding.txtCoinDiscount.setVisibility(View.VISIBLE);
            binding.txtCoinDiscount.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getLoyaltyAmount()));
        }else {
            binding.llLoyalty.setVisibility(View.GONE);
        }

        if (orderDetailsBean.getOrderType().equals(Constants.HOME_DELIVERY)) {
            binding.cvAddress.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(orderDetailsBean.getRestaurantName()))
                binding.txtRestaurant.setText(orderDetailsBean.getRestaurantName());
            binding.txtRestAddress.setText(orderDetailsBean.getPickUpCompleteAddress());
            binding.txtDeliveryAddress.setText(orderDetailsBean.getDeliveryCompleteAddress());
        } else
            binding.cvAddress.setVisibility(View.GONE);

        if(orderDetailsBean.getOrderType().equals(Constants.TAKE_AWAY)
                || orderDetailsBean.getOrderType().equals(Constants.CAFE)){
//            if (!isOrderThreadOn)
//                startOrderThread();

            if(isFromDetails){
                showTImer();
                callGetQRCodeApi();
                binding.llQrCode.setVisibility(View.VISIBLE);
                if( orderDetailsBean.getOrderType().equals(Constants.CAFE))
                    binding.txtETA.setVisibility(View.GONE);
            }
            if(orderDetailsBean.getOrderStatus().equals("Dispatched")){
                binding.txtStatus.setText(getString(R.string.order_ready));
            }else if(orderDetailsBean.getOrderStatus().equals("Delivered")){
                binding.txtStatus.setText(getString(R.string.pickedup));
                binding.toolbar.toolbarCallResturant.setVisibility(View.GONE);
            }else{
                binding.txtStatus.setText(orderDetailsBean.getOrderStatus());
            }
        }else if(orderDetailsBean.getOrderType().equals(Constants.DINE_IN)) {
            binding.CvDineInStatus.setVisibility(View.VISIBLE);
            if (!isOrderThreadOn)
                startOrderThread();
            setStatusLines(orderDetailsBean.getOrderStatus());
        }else if(orderDetailsBean.getOrderType().equals(Constants.CAFE)){
//            if (!isOrderThreadOn)
//                startOrderThread();
        }


        binding.txtRestBill.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetailsBean.getOrderSubTotal()));

        if ((orderDetailsBean.getTaxAmount() - orderDetailsBean.getTaxDiscountAmount()) > 0 ) {
            binding.llTax.setVisibility(View.VISIBLE);
            binding.txtTax.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getTaxAmount() - orderDetailsBean.getTaxDiscountAmount()));
        } else
            binding.llTax.setVisibility(View.GONE);

        if (orderDetailsBean.getPackingCharges() > 0){
            binding.llPacking.setVisibility(View.VISIBLE);
            binding.txtPacking.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getPackingCharges()));
        } else
            binding.llPacking.setVisibility(View.GONE);
        if (orderDetailsBean.getDeliveryFee() > 0) {
            binding.txtDeliveryCharge.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getDeliveryFee()));
            binding.DeliveryCharge.setVisibility(View.VISIBLE);
        } else
            binding.DeliveryCharge.setVisibility(View.GONE);

        if (orderDetailsBean.getCouponDiscountAmount() > 0) {
            binding.llPromoDiscount.setVisibility(View.VISIBLE);
            binding.txtPromoDiscount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetailsBean.getCouponDiscountAmount()));
        } else
            binding.llPromoDiscount.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(orderDetailsBean.getCouponCode())) {
            binding.llPromoCode.setVisibility(View.VISIBLE);
            binding.txtPromoCode.setText(orderDetailsBean.getCouponCode());
        } else
            binding.llPromoCode.setVisibility(View.GONE);


//        if (!TextUtils.isEmpty(orderDetailsBean.getPa())) {
//            binding.llPromoCode.setVisibility(View.VISIBLE);
//            binding.txtPromoCode.setText(orderDetailsBean.getCouponCode());
//        } else
//            binding.llPromoCode.setVisibility(View.GONE);
//
//        if (!TextUtils.isEmpty(orderDetailsBean.getCouponCode())) {
//            binding.llPromoCode.setVisibility(View.VISIBLE);
//            binding.txtPromoCode.setText(orderDetailsBean.getCouponCode());
//        } else
//            binding.llPromoCode.setVisibility(View.GONE);
//            binding.txtRewardDiscount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(orderDetailsBean.getCoinDiscount()));
        binding.txtTotal.setText(getString(R.string.Rs) + new DecimalFormat("#.00").format(orderDetailsBean.getOrderTotal()));

        mAdapter = new DetailsAdapter(mContext, orderDetailsBean.getItemList());
        binding.rvDetails.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void checkCallFunctionality() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //No calling functionality
            Utils.showCenterToast(mContext, "No calling functionality");
        } else {
            //calling functionality
            checkPermission();
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
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{CALL_PHONE}, REQUESTPERMISSIONCODE);
    }

    @SuppressLint("MissingPermission")
    private void callAction() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + orderDetailsBean.getRestaurantContactNumber()));
        startActivity(callIntent);
    }


    private void setStatusLines(String status){
        switch (status) {
            case "Ordered":
                binding.viewLeft.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.viewRight.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
                binding.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
//                binding.btnCallDriver.setVisibility(View.GONE);
                break;
            case "Preparing":
                binding.viewLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.viewRight.setBackgroundColor(getResources().getColor(R.color.gray));
                binding.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_circle_bg));
//                bindiCallDriver.setVisibility(View.VISIBLE);
                break;
            case "Dispatched":
                binding.viewLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.viewRight.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
//                binding.btnCallDriver.setVisibility(View.VISIBLE);
//                goToRatingScreen();
                break;

            case "Delivered":
                binding.viewLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.viewRight.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                binding.viewConfirmed.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewOnTheWay.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
                binding.viewDelivery.setBackground(getResources().getDrawable(R.drawable.grey_black_circle_bg));
//                binding.btnCallDriver.setVisibility(View.VISIBLE);
//                goToRatingScreen();
                break;
        }
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
                            callOrderDetailApi();
//                            if(orderDetailsBean.getOrderStatus().equalsIgnoreCase("Preparing")) {
//                            } else if(orderDetailsBean.getOrderStatus().equalsIgnoreCase("Dispatched") && orderDetailsBean.getDriverName() == null){
//                                callOrderDetailApi();
//                            }else{
//                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        orderTimer.schedule(doAsynchronousTask, 60000, 60000);
    }


    long minutes;
    int seconds;
    private void showTImer(){
        if(orderDetailsBean.getOrderStatus().equals("Ordered")){
            binding.txtETA.setVisibility(View.GONE);
        }else {
            binding.txtETA.setVisibility(View.VISIBLE);
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

        Log.i("======= Hours"," :: "+hours+"--"+min);


//        orderDetailsBean.getAcceptRejectAtTime();

//        Toast.makeText(mContext,   orderDetailsBean.getAcceptRejectAtTime()+"", Toast.LENGTH_SHORT).show();

        orderDetailsBean.getOrderStatus();
       long totalMinutes = orderDetailsBean.getOrderReadyIn();
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
                    showMin = "0"+String.valueOf(minutes);
                else
                    showMin = String.valueOf(minutes);
                binding.txtETA.setText("ETA - "+showMin+":"+showSec+" min");
                seconds -= 1;

                if(seconds == 0)
                {
                    binding.txtETA.setText("ETA - "+showMin+":"+showSec+" min");

                    seconds=60;
                    minutes=minutes-1;
                }
            }

            public void onFinish() {
                binding.txtETA.setText("Your Order Almost Ready");
            }

        }.start();
    }

    public void goToRatingScreen(View view){
        Utils.startActivity(mContext, RatingActi.class);
    }

    private void callOrderDetailApi(){

        try {
            new NetworkManager(OrderDetailsBean.class, new NetworkManager.OnCallback<OrderDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        orderDetailsBean = (OrderDetailsBean) responseClass.getResponsePacket();

                        if (orderDetailsBean != null)
                            setData();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }

            ).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ORDER_DETAIL  + orderId, "", "Loading...", true, AppUrls.REQUESTTAG_ORDERDETAIL );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /////////////////////Get Qr Code

    private void callGetQRCodeApi(){
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        String s= (String)responseClass.getResponsePacket();
                        byte[] imageAsBytes = Base64.decode(s, Base64.DEFAULT);
                        binding.imvQr.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }

            ).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_QR_IMAGE_BY_TEXT  + orderId, "", "Loading...", true, AppUrls.REQUESTTAG_GETQRIMAGEBYTEXT );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
