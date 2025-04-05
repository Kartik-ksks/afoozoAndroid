package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

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
import android.widget.Toast;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.StealDealCartBean;
import in.kpis.afoozo.bean.StealDealDetailsBean;
import com.kpis.afoozo.databinding.ActivityReserveNowBinding;
import com.kpis.afoozo.databinding.PopupReserveDealBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReserveNowActi extends AppCompatActivity {

    private ActivityReserveNowBinding binding;
    private Toolbar toolbar;

    private Context mContext;

    private DatabaseHelper db;

    private StealDealDetailsBean detailsBean;

    private Dialog alert;
    private PopupReserveDealBinding reserveBinding;

    private int WALLET_REQUEST = 209;

    private long itemId;

    private boolean isFromDifferentRestaurant;

    private String category;
    private String restaurantId;

    private int qty;
    private int todayDate;
    private int totalItems;

    private double totalPrice;

    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reserve_now);

        if (getIntent().getExtras() != null){
            category = getIntent().getStringExtra("Category");
            itemId = getIntent().getLongExtra("Item_ID", 0);
            restaurantId = getIntent().getStringExtra("RestaurantId");
        }

        mContext = ReserveNowActi.this;
        db = new DatabaseHelper(mContext);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.shimmerAddButton.startShimmer();
        if (detailsBean != null)
            checkLocalCart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.shimmerAddButton.stopShimmer();
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

        binding.toolbar.activityTitle.setText(getString(R.string.reserve_now));

        date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd");
        todayDate = Integer.parseInt(dateFormat.format((date)));

        callItemDetailsApi();
    }

    public void addItemProcess(View view){

        Utils.vibrateOnClick(mContext);

        if (isFromDifferentRestaurant){
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.you_already_added_some_items), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteAllStealDealItems();
                    isFromDifferentRestaurant = false;
                    totalItems = 0;
                    totalPrice = 0;
                    addItemProcess(v);
                    Utils.dismissRetryAlert();
                }
            });
        } else {
            if (qty > 0)
                db.updateStealDealItemQty(itemId, ++qty);
            else {
                db.insertStealDealItem(restaurantId, itemId, detailsBean.getTitle(), ++qty, todayDate, detailsBean.getPrice());
                totalItems++;
            }

            totalPrice = totalPrice + detailsBean.getPrice();
        }

        setItemQty();
        setBottomPrice();
    }

    public void removeItemProcess(View view){
        Utils.vibrateOnClick(mContext);
        if (qty == 1) {
            db.deleteStealDealItem(itemId);
            qty--;
            totalItems--;
        } else {
            db.updateStealDealItemQty(itemId, --qty);
        }

        totalPrice = totalPrice - detailsBean.getPrice();

        setItemQty();
        setBottomPrice();
    }

    public void saveOrderProcess(View view){
        Utils.startActivity(mContext, StealDealCartActi.class);
    }

    private void setItemQty() {
        if (qty > 0){
            binding.rlAdd.setVisibility(View.GONE);
            binding.llAddRemove.setVisibility(View.VISIBLE);
            binding.txtCount.setVisibility(View.VISIBLE);
            binding.pbCount.setVisibility(View.GONE);
            binding.txtCount.setText("" + qty);
        } else {
            binding.rlAdd.setVisibility(View.VISIBLE);
            binding.llAddRemove.setVisibility(View.GONE);
        }
    }

    public void showReservePopUp(View view){
        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = alert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        reserveBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_reserve_deal, null, false);
        alert.setContentView(reserveBinding.getRoot());


        if (!TextUtils.isEmpty(detailsBean.getTitle()))
            reserveBinding.txtPopItemName.setText(detailsBean.getTitle());
        if (!TextUtils.isEmpty(category))
            reserveBinding.txtPopType.setText(category);

        reserveBinding.txtPopPrice.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(detailsBean.getPrice()));


        reserveBinding.btnPopReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reserveBinding.cbTerms.isChecked()) {
                    alert.dismiss();
                    callReserveItemApi();
                }else
                    Toast.makeText(mContext, getString(R.string.please_check_terms_and_conditions_first), Toast.LENGTH_SHORT).show();

            }
        });


        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(true);
        alert.show();
    }

    private void setData() {
        binding.txtItemName.setText(detailsBean.getTitle());

        if (!TextUtils.isEmpty(detailsBean.getImageUrl()))
            Utils.setImage(mContext, binding.imvItem, binding.pbItem, detailsBean.getImageUrl());

        if (!TextUtils.isEmpty(detailsBean.getBackgroundImageUrl())) {
            Utils.setImage(mContext, binding.imvBackground, detailsBean.getBackgroundImageUrl());
            binding.imvBackground.setVisibility(View.VISIBLE);
        } else
            binding.imvBackground.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(detailsBean.getShortDescription())) {
            binding.txtDetails.setText(detailsBean.getShortDescription());
            binding.txtDetails.setVisibility(View.VISIBLE);
        } else
            binding.txtDetails.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(detailsBean.getDetailedDescription())) {
            binding.wbStealDeal.loadData(detailsBean.getDetailedDescription(), "text/html; charset=utf-8", "UTF-8");
            binding.wbStealDeal.setVisibility(View.VISIBLE);
        } else
            binding.wbStealDeal.setVisibility(View.VISIBLE);

        checkLocalCart();

    }

    private void checkLocalCart() {
        ArrayList<StealDealCartBean> localCartList = new ArrayList<>();
        localCartList = db.getStealDealItems();

        qty = 0;
        totalItems = 0;
        totalPrice = 0;

        if (localCartList.size() > 0){
            if (!localCartList.get(0).getRestaurantId().equals(restaurantId)) {
                isFromDifferentRestaurant = true;

            } else {
                for (StealDealCartBean sdcb : localCartList) {
                    if (sdcb.getStealDealItemId() == itemId) {
                        qty = sdcb.getStealDealItemQty();
                        break;
                    }
                }
            }

            for (StealDealCartBean sdcb: localCartList)
                totalPrice = totalPrice + (sdcb.getStealDealItemQty() * sdcb.getPrice());

            totalItems = localCartList.size();

        }


        setItemQty();
        setBottomPrice();
    }

    private void setBottomPrice() {
        if (totalItems > 0){
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.llCart.llBottom.setVisibility(View.VISIBLE);

            if (totalItems == 1) {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.total));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalPrice);
            } else {
                binding.llCart.txtTotalItems.setText(totalItems + " " + getString(R.string.items));
                binding.llCart.txtCartTotal.setText(getString(R.string.Rs) + totalPrice);
            }
        } else
            binding.llCart.llBottom.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WALLET_REQUEST && resultCode == RESULT_OK){
            callReserveItemApi();
        }
    }

    private void callItemDetailsApi() {

        try {
            new NetworkManager(StealDealDetailsBean.class, new NetworkManager.OnCallback<StealDealDetailsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        detailsBean = (StealDealDetailsBean) responseClass.getResponsePacket();

                        if (detailsBean != null)
                            setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_STEAL_DEAL_ITEM_DETAIL + itemId, "", "Loading...", true, AppUrls.REQUESTTAG_GETSTEALDEALITEMDETAIL );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callReserveItemApi() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.RESTAURANT_ID, restaurantId);
            jsonObject.put(Constants.STEAL_DEAL_ITEM_ID, itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

                    } else {
                        if (responseClass.getErrorCode() == 12){
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.your_balance_is_low), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.dismissRetryAlert();
                                    Intent intent = new Intent(mContext, WalletActi.class);
                                    intent.putExtra(Constants.PAYMENT_AMOUNT, detailsBean.getPrice());
                                    intent.putExtra(Constants.IS_FROM_HOME, false);
                                    startActivityForResult(intent, WALLET_REQUEST);
                                }
                            });
                        } else
                            Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESERVE_STEAL_DEAL_ITEMS, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_RESERVESTEALDEALITEM );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
