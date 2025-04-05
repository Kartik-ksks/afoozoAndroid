package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.OrdersAdapter;
import in.kpis.afoozo.bean.OrdersBean;
import com.kpis.afoozo.databinding.ActivityLiveOrderBinding;
import in.kpis.afoozo.fragment.LiveDealsFrag;
import in.kpis.afoozo.fragment.LiveOrdersFrag;
import in.kpis.afoozo.interfaces.OrderInterface;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LiveOrderActi extends AppCompatActivity implements OrderInterface {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityLiveOrderBinding binding;

    private String orderType;

    private ArrayList<OrdersBean> list;
    private ArrayList<String> typeList = new ArrayList<>();
    private OrdersAdapter mAdapter;
    private String orderId;
    private ViewPagerAdapter adapter;
    private Handler handler;
    private Timer pendingOrderTimer;
    private boolean isOrderThreadOn;
    private Runnable myRunnable;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.LOCAL_INTENT_STATUS_CHANGE)) {
//                if (orderId != null) {
//                    if (intent.getStringExtra(Constants.ORDER_ID).equalsIgnoreCase(orderId))
//                        orderId = intent.getStringExtra(Constants.ORDER_ID);
                    callGetLiveOrdersApi();
//                }
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_order);

        if (getIntent().getExtras() != null){
            orderType = getIntent().getStringExtra(Constants.ORDER_TYPE);
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        }

        mContext = LiveOrderActi.this;
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
              Utils.startActivityWithFinish(mContext,Dashboard.class);
            }
        });

//        setTypeData();

        binding.toolbar.activityTitle.setText(getString(R.string.live_orders));


        binding.rvLive.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvLive.setItemAnimator(new DefaultItemAnimator());

        callGetLiveOrdersApi();
//
//        binding.spOrders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(typeList != null) {
//
//                    selectedType = typeList.get(position);
//                    callGetLiveOrdersApi();
//
//                }else {
//                    callGetLiveOrdersApi();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
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
//        setupViewPager(binding.vpPager);
//        binding.tabs.setupWithViewPager(binding.vpPager);
//        setupTabIcons();
//
//        if (!TextUtils.isEmpty(orderType) && orderType.equals(Constants.STEAL_DEAL))
//            binding.vpPager.setCurrentItem(1);
    }

    @Override
    public void reOrderClick(int position) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        startThreadForOrder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.LOCAL_INTENT_STATUS_CHANGE));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        pendingOrderTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        Utils.startActivityWithFinish(mContext,Dashboard.class);
    }


    private void startThreadForOrder() {
        handler = new Handler();
        pendingOrderTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(myRunnable = new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            callGetLiveOrdersApi();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        pendingOrderTimer.schedule(doAsynchronousTask, 0, 30000);
        isOrderThreadOn = true;
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
        adapter.addFragment(new LiveOrdersFrag(), getString(R.string.orders));
        adapter.addFragment(new LiveDealsFrag(), getString(R.string.steal_deal));
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

    @Override
    public void layoutClick(int position) {
        if (list.get(position).getOrderType().equals("HomeDelivery")) {
            Intent intent = new Intent(mContext, TrackActi.class);
            intent.putExtra(Constants.ORDER_ID, list.get(position).getOrderReferenceId());
            startActivity(intent);
        } else if (!list.get(position).getOrderType().equals(Constants.STEAL_DEAL)){
            Intent intent = new Intent(mContext, OrderDetailsActi.class);
            intent.putExtra("IsFromTrack", false);
            intent.putExtra("IsFromDetails", true);
            intent.putExtra(Constants.ORDER_ID, list.get(position).getOrderReferenceId());
            startActivity(intent);
        }
    }

    @Override
    public void feedbackClick(int position) {

    }

    @Override
    public void tipRiderClick(int position) {

    }

    @Override
    public void tableClearClick(String orderId) {
        callClearTablesApi(orderId);


    }

    private void setTypeData() {
        typeList.add(Constants.HOME_DELIVERY);
        typeList.add(Constants.TAKE_AWAY);
        typeList.add(Constants.DINE_IN);
        typeList.add(Constants.STEAL_DEAL);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, typeList);
        cityAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        binding.spOrders.setAdapter(cityAdapter);

        if (!TextUtils.isEmpty(orderType)) {
            for (int i=0; i < typeList.size(); i++){
                if (typeList.get(i).equals(orderType)){
                    binding.spOrders.setSelection(i);
                    break;
                }
            }
        } else
            orderType = Constants.HOME_DELIVERY;
    }

    private void setData() {
        binding.rvLive.setVisibility(View.VISIBLE);

        mAdapter = new OrdersAdapter(mContext, true, false, list, this);
        binding.rvLive.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void callGetLiveOrdersApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<OrdersBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<OrdersBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<OrdersBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0) {
                            binding.txtNoOrder.setVisibility(View.GONE);
                            binding.rvLive.setVisibility(View.VISIBLE);
                            setData();
                        }else {
                            binding.txtNoOrder.setVisibility(View.VISIBLE);
                            binding.rvLive.setVisibility(View.GONE);
                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETORDERLIST + "All" + "/Live/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }




    private void callClearTablesApi(String orderId ) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Intent intent = new Intent(mContext, RatingActi.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Constants.ORDER_ID, orderId);

                        startActivity(intent);
                        finish();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.REQUEST_FOR_TABLE_CLEANING +orderId, "", "Loading...", true, AppUrls.REQUESTTAG_REQUESTFORTABLECLEANING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
