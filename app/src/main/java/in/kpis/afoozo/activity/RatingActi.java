package in.kpis.afoozo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.kpis.afoozo.R;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.adapter.RatingAdapter;
import in.kpis.afoozo.bean.OrderDetailsBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import in.kpis.afoozo.bean.RateDriverBean;
import com.kpis.afoozo.databinding.ActivityRatingBinding;
import com.kpis.afoozo.databinding.RetryAlertBinding;
import com.kpis.afoozo.databinding.TipPopupLayoutBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class RatingActi extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityRatingBinding binding;

    private BottomSheetBehavior sheetBehavior;

    private ArrayList<OrderDetailsItemBean> itemsList = new ArrayList<>();
    private RatingAdapter mAdapter;

    private String orderId;
    private String fromWhich;

    private double tipAmount;

    private int WALLET_REQUEST = 303;

    private String orderType;
    private String tipUrl;
    private boolean isTipDone;

    private Dialog retryAlert;
    private  boolean showTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating);

        mContext = RatingActi.this;

        if (getIntent().getExtras() != null){
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            orderType = getIntent().getStringExtra(Constants.ORDER_TYPE);
            isTipDone = getIntent().getBooleanExtra(Constants.IS_TIP_DONE, false);
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

        binding.toolbar.activityTitle.setText(getString(R.string.rating));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.llBottomLayout);

        binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvItems.setItemAnimator(new DefaultItemAnimator());

        binding.rbDriver.setOnRatingBarChangeListener(this);
        binding.rbRestaurant.setOnRatingBarChangeListener(this);

        setBottomSheet();

        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL))
            setDetailsData();
        else
            callOrderDetailApi(true);
    }

    @Override
    public void onBackPressed() {
        if (fromWhich.equals("History"))
            finish();
        else
            Utils.startActivityWithFinish(mContext, Dashboard.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WALLET_REQUEST && resultCode == RESULT_OK){
            callTipApi();
        } else {
            goToNextScreen();
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//        if (rating < 1.0f)
//            ratingBar.setRating(0);
    }

    private void setBottomSheet() {
        sheetBehavior.setHideable(false);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        binding.llBottomSheet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_down_arrow));
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        goToNextScreen();
//                        binding.llBottomSheet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_up_arrow));
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            binding.bottomSheet.txtTipAmount.setVisibility(View.VISIBLE);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            binding.bottomSheet.txtTipAmount.setVisibility(View.GONE);
        }
    }

    public void outerClick(View view){

    }

    public void add50Process(View view){
        binding.bottomSheet.txtTipAmount.setText("50");
    }
    public void add100Process(View view){
        binding.bottomSheet.txtTipAmount.setText("100");
    }
    public void add200Process(View view){
        binding.bottomSheet.txtTipAmount.setText("200");
    }
    public void add500Process(View view){
        binding.bottomSheet.txtTipAmount.setText("500");
    }

    public void addTipProcess(View view){
        String amount = binding.bottomSheet.txtTipAmount.getText().toString().trim();
        if (!TextUtils.isEmpty(amount) && Double.parseDouble(binding.bottomSheet.txtTipAmount.getText().toString()) > 0){
            tipAmount = Double.parseDouble(binding.bottomSheet.txtTipAmount.getText().toString());
            if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL))
                tipUrl = AppUrls.PAY_TIP_ON_STEAL_DEAL_ORDER;
            else
                tipUrl = AppUrls.PAY_TIP_ON_ORDER;
            callTipApi();
            toggleBottomSheet();
        } else
            Utils.showCenterToast(mContext, getString(R.string.please_enter_valid_tip_amount));
    }
    public void skipTipProcess(View view){
        toggleBottomSheet();
        goToNextScreen();
    }

    private void showTipLayout() {
        if (orderDetailsBean != null) {
            if (orderDetailsBean.getOrderType().equals(Constants.HOME_DELIVERY)) {
                binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_rider));
                binding.bottomSheet.txtTipMsg.setText(getString(R.string.should_you_wish_to_tip_the_server));
            } else {
                binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_server));
                binding.bottomSheet.txtTipMsg.setText(getString(R.string.should_you_wish_to_tip_the_server));
            }
        } else {
            binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_server));
            binding.bottomSheet.txtTipMsg.setText(getString(R.string.should_you_wish_to_tip_the_server));
        }

        binding.bottomSheet.txtTipAmount.setText("50");

        toggleBottomSheet();
    }

    private void setDetailsData() {
        if (orderDetailsBean != null) {
            if(orderDetailsBean.getOrderType().equals(Constants.HOME_DELIVERY)||
                    orderDetailsBean.getOrderType().equals(Constants.DINE_IN)){
                     showTip = true;
            }
            if (orderDetailsBean.getOrderType().equals(Constants.HOME_DELIVERY)) {
                binding.llDriver.setVisibility(View.VISIBLE);
            } else
                binding.llDriver.setVisibility(View.GONE);
            try {
                itemsList = orderDetailsBean.getItemList();
//                for (OrderDetailsItemBean ib : itemsList) {
//                    ib.setRatingUserToDish(1);
//                }
                mAdapter = new RatingAdapter(mContext, itemsList);
                binding.rvItems.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        } else {
            binding.llDriver.setVisibility(View.GONE);
            binding.llItems.setVisibility(View.GONE);
        }
    }


//    public void ratingProcess(View view){
//        Utils.startActivityWithFinish(mContext, Dashboard.class);
//    }

    OrderDetailsBean orderDetailsBean;
    private void callOrderDetailApi(boolean isShow){

        try {
            new NetworkManager(OrderDetailsBean.class, new NetworkManager.OnCallback<OrderDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        orderDetailsBean = (OrderDetailsBean) responseClass.getResponsePacket();
                        setDetailsData();

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

    int driverRating;
    int restRating;
    String suggestion;
    public void ratingProcess(View view){
        driverRating = (int) binding.rbDriver.getRating();
        restRating = (int) binding.rbRestaurant.getRating();
        suggestion = binding.etSuggestion.getText().toString();

        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL))
            callRatingApi(AppUrls.RATE_STEAL_DEAL_ORDER_BY_CUSTOMER);
        else
            callRatingApi(AppUrls.RATEORDERBYCUSTOMER);

    }

    private void goToNextScreen() {
//        if (!TextUtils.isEmpty(fromWhich) && fromWhich.equals("History")){
//            Intent intent = new Intent();
//            setResult(RESULT_OK, intent);
//            finish();
//        } else
            Utils.startActivityWithFinish(mContext, Dashboard.class);
    }

    private void callRatingApi(String url){

        RateDriverBean bean = new RateDriverBean();
        bean.setDriverRating(driverRating);
        bean.setRestaurantRating(restRating);
//        bean.setDriverAttitude(binding.chkAttitude.isChecked());
//        bean.setDriverDeliveryTime(binding.chkDeliveryTime.isChecked());
//        bean.setDriverFoodHandling(binding.chkFood.isChecked());

        if (binding.rbFoodPackingYes.isChecked())
            bean.setRestaurantFoodPacking(true);
        else if (binding.rbFoodPackingNo.isChecked())
            bean.setRestaurantFoodPacking(false);

        if (binding.rbPortionSizeYes.isChecked())
            bean.setRestaurantPortionSize(true);
        else if (binding.rbPortionSizeNo.isChecked())
            bean.setRestaurantPortionSize(false);

        if (binding.rbTasteYes.isChecked())
            bean.setRestaurantTaste(true);
        else if (binding.rbTasteNo.isChecked())
            bean.setRestaurantTaste(false);

        bean.setSuggestion(suggestion);

        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL)) {
            bean.setItemRating(5);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    customCleverDriverRatingEvent(bean);
                }
            }).start();

            ArrayList<RateDriverBean.ItemRatingListBean> ratingItemList = new ArrayList<>();
            for (OrderDetailsItemBean item : itemsList) {
                RateDriverBean.ItemRatingListBean beanItem = new RateDriverBean.ItemRatingListBean();
                beanItem.setOrderItemId((int) item.getOrderItemId());
                beanItem.setItemRating(item.getRatingUserToDish());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        customCleveItemRatingEvent(item);
                    }
                }).start();

                ratingItemList.add(beanItem);
            }

            bean.setItemRatingList(ratingItemList);
        }

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Utils.showCenterToast(mContext, responseClass.getMessage());
                    if (responseClass.isSuccess()){
                        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL)) {
                            if (isTipDone)
                                goToNextScreen();
                            else
                                if(showTip)
                                showTipLayout();
                                else
                                    goToNextScreen();
                        } else {
                            if (orderDetailsBean.getTipAmount() > 0)
                                goToNextScreen();
                            else {
                                if (showTip)
                                    showTipLayout();
                                else
                                    goToNextScreen();
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(responseClass.getMessage()) && responseClass.getMessage().equalsIgnoreCase("Order has been rated already."))
                            goToNextScreen();
                    }
                }
                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }

            ).callAPIJson(mContext, Constants.VAL_POST, url+orderId, json, "Loading...", true, AppUrls.REQUESTTAG_RATEORDERBYCUSTOMER );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callTipApi() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.TIP_AMOUNT, tipAmount);
            jsonObject.put(Constants.TIP_MESSAGE, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        goToNextScreen();
                    } else {
                        if (responseClass.getErrorCode() == 12){
                            showAddAmountPopUp();
                        } else
                            Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, tipUrl + orderId, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_PAYTIPONORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showAddAmountPopUp() {
        retryAlert = new Dialog(mContext);
        retryAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        retryAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = retryAlert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        RetryAlertBinding retryBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.retry_alert, null, false);
        retryAlert.setContentView(retryBinding.getRoot());

        retryBinding.txtRAMsg.setText(getString(R.string.your_balance_is_low));
        retryBinding.txtRAFirst.setText(getString(R.string.cancel));
        retryBinding.txtRASecond.setText(getString(R.string.Ok));

//        alert.setContentView(R.layout.buy_popup_layout);
        retryBinding.txtRAFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryAlert.dismiss();
                goToNextScreen();
            }
        });

        retryBinding.txtRASecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryAlert.dismiss();
                Intent intent = new Intent(mContext, WalletActi.class);
                intent.putExtra(Constants.PAYMENT_AMOUNT, tipAmount);
                intent.putExtra(Constants.IS_FROM_HOME, false);
                startActivityForResult(intent, WALLET_REQUEST);
            }
        });

        retryAlert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        retryAlert.setCancelable(false);
        retryAlert.show();
    }


//    Rating Screen

    private void customCleverDriverRatingEvent(RateDriverBean driverBean){
        HashMap<String, Object> itemAddToCartAction = new HashMap<String, Object>();
        itemAddToCartAction.put("Driver Rating", driverBean.getDriverRating() > 0 ? driverBean.getDriverRating() : "N/A");
        itemAddToCartAction.put("Restaurant Rating", driverBean.getRestaurantRating() > 0 ? driverBean.getRestaurantRating() : "N/A");
        itemAddToCartAction.put("Order Type", orderDetailsBean.getOrderType());
        itemAddToCartAction.put("Order Id", orderId);
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Rate", itemAddToCartAction);
    }

    private void customCleveItemRatingEvent(OrderDetailsItemBean orderDetailsItemBean){
        HashMap<String, Object> itemAddToCartAction = new HashMap<String, Object>();
        itemAddToCartAction.put("Item Id", orderDetailsItemBean.getItemId());
        itemAddToCartAction.put("Item Name", orderDetailsItemBean.getTitle());
        itemAddToCartAction.put("Item Rating", orderDetailsItemBean.getRatingUserToDish() > 0 ? orderDetailsItemBean.getRatingUserToDish() : "N/A");
        AppInitialization.getInstance().clevertapDefaultInstance.pushEvent("Rate Item", itemAddToCartAction);
    }
}
