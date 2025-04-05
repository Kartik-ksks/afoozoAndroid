package in.kpis.afoozo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.CouponAdapter;
import in.kpis.afoozo.bean.CouponBean;
import in.kpis.afoozo.bean.CouponListBean;
import com.kpis.afoozo.databinding.ActivityCouponBinding;
import in.kpis.afoozo.interfaces.CouponInterface;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.DividerItemDecorationGray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CouponActi extends AppCompatActivity implements CouponInterface {

    private Toolbar toolbar;

    private Context mContext;
    private ActivityCouponBinding binding;

    private CouponAdapter couponAdapter;
    private ArrayList<CouponListBean> couponList = new ArrayList<>();

    private double totalAmount;

    private String venderId;
    private String venderType;
    private String orderId;
    private String couponCode;
    private String fromWhich;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coupon);

        if (getIntent().getExtras() != null) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
        }

        mContext = CouponActi.this;
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        binding.rvCoupons.setLayoutManager(mLayoutManager1);
        binding.rvCoupons.setItemAnimator(new DefaultItemAnimator());
        binding.rvCoupons.addItemDecoration(new DividerItemDecorationGray(mContext));

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.apply_coupon));

        callGetCouponApi();
    }

    @Override
    public void onApply(int position) {
        if(fromWhich.equalsIgnoreCase(Constants.ROOM_BOOKING)){
            callCouponCodeApi(AppUrls.APPLY_COUPON_FOR_BOOKING, couponList.get(position).getCouponCode());

        }else
        callCouponCodeApi(AppUrls.APPLY_COUPON_CODE, couponList.get(position).getCouponCode());
    }

    @Override
    public void onCopy(int position) {

    }

    @Override
    public void onViewDetials(int position) {

    }

    public void checkCouponCode(View view){
        if (!TextUtils.isEmpty(binding.eTextCoupon.getText())) {
            couponCode = binding.eTextCoupon.getText().toString();
            if(fromWhich.equalsIgnoreCase(Constants.ROOM_BOOKING)){
                callCouponCodeApi(AppUrls.APPLY_COUPON_FOR_BOOKING, couponCode);

            }else
            callCouponCodeApi(AppUrls.APPLY_COUPON_CODE, couponCode);
        } else
            Utils.showCenterToast(mContext, getString(R.string.coupon_code_error));
    }

    private void setCouponCodeList() {
        if (couponList != null && couponList.size() > 0){
            binding.llCouponList.setVisibility(View.VISIBLE);
            binding.txtNoCouponCode.setVisibility(View.GONE);

            couponAdapter = new CouponAdapter(mContext, false, couponList, this);
            binding.rvCoupons.setAdapter(couponAdapter);
            couponAdapter.notifyDataSetChanged();
        } else {
            binding.llCouponList.setVisibility(View.GONE);
            binding.txtNoCouponCode.setVisibility(View.VISIBLE);
        }
    }

    private void callGetCouponApi() {
        String type = "";

        if (fromWhich.equals(getString(R.string.dine_in)))
            type = Constants.DINE_IN;
        else if (fromWhich.equals(getString(R.string.cafe)))
            type = Constants.CAFE;
        else if (fromWhich.equals(getString(R.string.take_away)))
            type = Constants.TAKE_AWAY;
        else if (fromWhich.equals(Constants.ROOM_BOOKING))
            type = Constants.ROOM_BOOKING;
        else
            type = "Delivery";

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<CouponListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<CouponListBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<CouponListBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        couponList = responseClass1.getResponsePacket();
                        setCouponCodeList();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.COUPON_LIST + type + "/" + restaurantId, "", "Loading...", true, AppUrls.REQUESTTAG_COUPONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callCouponCodeApi(String url, String couponCode){
        this.couponCode = couponCode;
        CouponListBean bean = new CouponListBean();
        bean.setOrderReferenceId(orderId);
        bean.setCouponCode(couponCode);

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        try {
            new NetworkManager(CouponBean.class, new NetworkManager.OnCallback<CouponBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        try {
                            SharedPref.setCouponCode(mContext, couponCode);
                            goToPreviousScreen();
                        } catch (Exception e){

                        }
//                        Utils.showCenterToast(mContext, getString(R.string.coupon_applied));
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, url, json, "Loading...", true, AppUrls.REQUESTTAG_APPLYCOUPONCODE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void goToPreviousScreen() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
