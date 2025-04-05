package in.kpis.afoozo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityNewOrderHistoryBinding;

import in.kpis.afoozo.adapter.OrdersAdapter;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.fragment.HistoryDealsFrag;
import in.kpis.afoozo.fragment.HistoryOrdersFrag;
import in.kpis.afoozo.interfaces.OrderHistoryInterface;
import in.kpis.afoozo.interfaces.OrderInterface;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewOrderHistoryActi extends AppCompatActivity implements OrderInterface {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityNewOrderHistoryBinding binding;
    private ViewPagerAdapter adapter;

    private int RATE_REQUEST = 101;
    private int WALLET_REQUEST = 102;

    private double tipAmount;

    private OrderHistoryInterface callback;

    private String orderId;
    private String tipMessage = "";
    private BottomSheetBehavior sheetBehavior;
    private String tipUrl;
    private String orderType;
    private ArrayList<OrdersBean> list;
    private OrdersAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_order_history);

        if (getIntent().getExtras() != null){
            orderType = getIntent().getStringExtra(Constants.ORDER_TYPE);
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        }

        mContext = NewOrderHistoryActi.this;
        initialize();
    }

    private void initialize(){
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
        binding.toolbar.activityTitle.setText(getString(R.string.order_history));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.llBottomLayout);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvHistory.setItemAnimator(new DefaultItemAnimator());

        callGetOrderHistorysApi();

//        binding.vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        setBottomSheet();
//
//        setupViewPager(binding.vpPager);
//        binding.tabs.setupWithViewPager(binding.vpPager);
//        setupTabIcons();
//
//        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL))
//            binding.vpPager.setCurrentItem(1);
    }

    /**
     * this Method is used to set custom layout for tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.orders));
        binding.tabs.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.steal_deal));
        binding.tabs.getTabAt(1).setCustomView(tabTwo);


    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HistoryOrdersFrag(), getString(R.string.orders));
        adapter.addFragment(new HistoryDealsFrag(), getString(R.string.steal_deal));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void reOrderClick(int position) {
        String orderType = list.get(position).getOrderType();
        if (orderType.equals(Constants.DINE_IN))
            orderType = mContext.getString(R.string.dine_in);
        else if (orderType.equals(Constants.HOME_DELIVERY))
            orderType = mContext.getString(R.string.delivery);
        else if (orderType.equals(Constants.TAKE_AWAY))
            orderType = mContext.getString(R.string.take_away);
        callReOrderApi(list.get(position).getOrderReferenceId(), orderType);
    }

    @Override
    public void layoutClick(int position) {
        if (!list.get(position).getOrderType().equals(Constants.STEAL_DEAL)) {
            Intent intent = new Intent(mContext, OrderDetailsActi.class);
            intent.putExtra("IsFromTrack", false);
            intent.putExtra(Constants.ORDER_ID, list.get(position).getOrderReferenceId());
            startActivity(intent);
        }
    }

    @Override
    public void feedbackClick(int position) {
        rateProcess(list.get(position).getOrderType(), list.get(position).getOrderReferenceId(), false, null);
    }

    @Override
    public void tipRiderClick(int position) {
        showTipLayout(list.get(position).getOrderType(), list.get(position).getOrderReferenceId(), null);
    }

    @Override
    public void tableClearClick(String orderId ) {

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

    private void setBottomSheet() {
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
            binding.bottomSheet.txtTipAmount.setVisibility(View.VISIBLE);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bottomSheet.txtTipAmount.setVisibility(View.GONE);
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
            callTipApi(tipUrl);
            toggleBottomSheet();
        } else
            Utils.showCenterToast(mContext, getString(R.string.please_enter_valid_tip_amount));
    }
    public void skipTipProcess(View view){
        toggleBottomSheet();
    }


    public void showTipLayout(String orderType, String orderId, OrderHistoryInterface callback) {
        this.callback = callback;
        this.orderId = orderId;

        if (orderType != null && orderType.equals(Constants.HOME_DELIVERY)) {
            binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_rider));
            binding.bottomSheet.txtTipMsg.setText(getString(R.string.help__rider_and_his_family_by_adding_a_tip));
            tipUrl = AppUrls.PAY_TIP_ON_ORDER;
        } else {
            binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_server));
            binding.bottomSheet.txtTipMsg.setText(getString(R.string.help__server_and_his_family_by_adding_a_tip));

            if (orderType.equals(Constants.STEAL_DEAL))
                tipUrl = AppUrls.PAY_TIP_ON_STEAL_DEAL_ORDER;
            else
                tipUrl = AppUrls.PAY_TIP_ON_ORDER;
        }

        binding.bottomSheet.txtTipAmount.setText("5");

        toggleBottomSheet();
    }

    private void setData() {

        if (list != null && list.size() > 0) {
            binding.rvHistory.setVisibility(View.VISIBLE);
            mAdapter = new OrdersAdapter(mContext, false, false, list, this);
            binding.rvHistory.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            binding.txtNoOrder.setVisibility(View.GONE);

        } else {
            binding.rvHistory.setVisibility(View.GONE);
            binding.txtNoOrder.setVisibility(View.VISIBLE);

        }
    }

    public void rateProcess(String orderType, String orderId, boolean isTipDone, OrderHistoryInterface callback){
        this.callback = callback;

        Intent intent = new Intent(mContext, RatingActi.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.ORDER_TYPE, orderType);
        intent.putExtra(Constants.IS_TIP_DONE, isTipDone);
        intent.putExtra(Constants.FROM_WHICH, "History");
        startActivityForResult(intent, RATE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RATE_REQUEST && resultCode == RESULT_OK){
//            callback.refreshList();
            callGetOrderHistorysApi();
        } else if (requestCode == WALLET_REQUEST && resultCode == RESULT_OK){
            callTipApi(tipUrl);
        }
    }

    private void callTipApi(String url) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.TIP_AMOUNT, tipAmount);
            jsonObject.put(Constants.TIP_MESSAGE, tipMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                        callback.refreshList();
                        callGetOrderHistorysApi();
                    } else {
                        if (responseClass.getErrorCode() == 12){
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.your_balance_is_low), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.dismissRetryAlert();
                                    Intent intent = new Intent(mContext, WalletActi.class);
                                    intent.putExtra(Constants.PAYMENT_AMOUNT, tipAmount);
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
            }).callAPIJson(mContext, Constants.VAL_POST, url + orderId, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_PAYTIPONORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetOrderHistorysApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<OrdersBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<OrdersBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<OrdersBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        list = responseClass1.getResponsePacket();
                        setData();

                    } else {

                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETORDERLIST + "All" + "/History" +"/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callReOrderApi(String orderId, String orderType) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        String generatedId = (String) responseClass.getResponsePacket();

                        Intent intent = new Intent(mContext, CartActi.class);
                        intent.putExtra(Constants.ORDER_ID, generatedId);
                        intent.putExtra(Constants.FROM_WHICH, orderType);
                        startActivity(intent);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.RE_ORDER + orderId, "", "Loading...", true, AppUrls.REQUESTTAG_REORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
