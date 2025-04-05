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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.AddressAdapter;
import com.kpis.afoozo.databinding.ActivityAddressBinding;
import in.kpis.afoozo.interfaces.AddressListClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.DividerItemDecorationGray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddressActi extends AppCompatActivity implements View.OnClickListener, AddressListClick {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityAddressBinding binding;

    private int LOCATION_REQUEST_CODE = 1001;

    private String address;
    private double latitude;
    private double longitude;

    private AddressAdapter mAdapter;
    private ArrayList<AddressBean> list;

    private boolean isFromCart;

    private int addPosition;

    private String fromWhich;
    private String restaurantId;
    private String title;
    private boolean isFromNotification;
    private boolean isOpen;
    private int takeawayMinOrderValue;
    private int deliveryMinOrderValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address);

        if (getIntent().getExtras() != null) {
            isFromCart = getIntent().getBooleanExtra(Constants.IS_FROM_CART, false);
            isFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
            isOpen = getIntent().getBooleanExtra(Constants.IS_OPEN, false);
            restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID);
            title = getIntent().getStringExtra(Constants.TITLE);
            takeawayMinOrderValue = getIntent().getIntExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, 0);
            deliveryMinOrderValue = getIntent().getIntExtra(Constants.DELIVERY_MIN_ORDER_VALUE, 0);
        }

        mContext = AddressActi.this;
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callGetAddressApi();
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

        binding.toolbar.activityTitle.setText(getString(R.string.address));

        binding.rvAddress.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvAddress.setItemAnimator(new DefaultItemAnimator());
        binding.rvAddress.addItemDecoration(new DividerItemDecorationGray(mContext));

    }

    private void setData() {

        if (list != null && list.size() > 0) {
            mAdapter = new AddressAdapter(mContext, list, this);
            binding.rvAddress.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtAddAddress:
                goToLocationScreen();
                break;
        }
    }

    @Override
    public void onClickLayout(int position) {
        if (isFromCart) {
            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.would_you_like_to_choose_this_address_as_delivery_address), getString(R.string.cancel), getString(R.string.Yes), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.dismissRetryAlert();
                    goToPreviousScreen(position);
                }
            });
        }else {
            goToPreviousScreen(position);
        }
    }

    @Override
    public void onClickDelete(int position) {
        Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.would_you_like_to_delete_this_address), getString(R.string.cancel), getString(R.string.Yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dismissRetryAlert();
                callDeleteAddressApi(list.get(position).getRecordId());
            }
        });
    }

    private void goToLocationScreen() {
        Intent intent = new Intent(mContext, LocationActi.class);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        startActivityForResult(intent, LOCATION_REQUEST_CODE);
    }

    private void goToPreviousScreen(int position){
        if (isFromNotification){
            Intent intent = new Intent(mContext, RestaurantActi.class);
            intent.putExtra(Constants.FROM_WHICH, fromWhich);
            intent.putExtra(Constants.ADDRESS_BEAN, list.get(position));
            startActivity(intent);
        } else {
            if (!TextUtils.isEmpty(fromWhich) && !TextUtils.isEmpty(restaurantId)) {
//                Intent intent = new Intent(mContext, AddItemsNewActi.class);
                Intent intent = new Intent(mContext, AddItemsActi.class);
                intent.putExtra(Constants.FROM_WHICH, fromWhich);
                intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
                intent.putExtra(Constants.IS_OPEN, isOpen);
                intent.putExtra(Constants.TITLE, title);
                intent.putExtra(Constants.ADDRESS_BEAN, list.get(position));
                intent.putExtra(Constants.TAKEAWAY_MIN_ORDER_VALUE, takeawayMinOrderValue);
                intent.putExtra(Constants.DELIVERY_MIN_ORDER_VALUE, deliveryMinOrderValue);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent();
                intent.putExtra("flag", true);
                intent.putExtra(Constants.ADDRESS_BEAN, list.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void callGetAddressApi(){

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<AddressBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<AddressBean>>>(){}.getType();
                        ResponseClass<ArrayList<AddressBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() >0) {
                            binding.rvAddress.setVisibility(View.VISIBLE);
                            setData();
                        } else
                            binding.rvAddress.setVisibility(View.GONE);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.ADDRESS_LIST, "", "Loading...", true, AppUrls.REQUESTTAG_ADDRESSLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callDeleteAddressApi(long addressId){

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    Utils.showCenterToast(mContext, responseClass.getMessage());
                    if (responseClass.isSuccess()){

                        callGetAddressApi();

                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_DELETE, AppUrls.DELETE_ADDRESS + addressId, "", "Loading...", true, AppUrls.REQUESTTAG_DELETEADDRESS);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
