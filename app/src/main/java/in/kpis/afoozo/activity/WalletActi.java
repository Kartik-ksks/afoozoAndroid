package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityWalletBinding;

import in.kpis.afoozo.AppInitialization;
import in.kpis.afoozo.bean.WalletBean;
import in.kpis.afoozo.fragment.AddFrag;
import in.kpis.afoozo.fragment.CoinFrag;
import in.kpis.afoozo.fragment.ConsumeFrag;
import in.kpis.afoozo.fragment.SpendFrag;
import in.kpis.afoozo.fragment.WalletHistoryFrag;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WalletActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityWalletBinding binding;
    private double totalBalance;
    private boolean isFromHome;
    private long payableAmount;
    public double remainingAmount;
    WalletBean    walletBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);

        if (getIntent().getExtras() != null){
            isFromHome = getIntent().getBooleanExtra(Constants.IS_FROM_HOME, false);
            if (!isFromHome)
                payableAmount = (long) getIntent().getDoubleExtra(Constants.PAYMENT_AMOUNT, 0);
        }

        mContext = WalletActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.wallet_money));

        binding.txtAmount.setText(getString(R.string.Rs) + " 0.00");

    }

    private void setData() {
        if (!isFromHome) {
            remainingAmount = payableAmount - totalBalance;
            if (payableAmount <= totalBalance){
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        setupViewPager(binding.vpWallet);
        binding.tabWallet.setupWithViewPager(binding.vpWallet);

    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddFrag(), getString(R.string.add_rs));
//        adapter.addFragment(new SpendFrag(), getString(R.string.spend_rs));
        adapter.addFragment(new WalletHistoryFrag(), getString(R.string.history_rs));
        adapter.addFragment(new CoinFrag(),  getString(R.string.coin_rs));
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

        HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<>();
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }
        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        callGetBalanceApi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("req",requestCode+"");

        int index = binding.vpWallet.getCurrentItem();
        ViewPagerAdapter adapter = ((ViewPagerAdapter)binding.vpWallet.getAdapter());
        final AddFrag tabFragment = (AddFrag) adapter.getFragment(index);
        tabFragment.onActivityResult(requestCode,resultCode,data);

    }

    public void callGetBalanceApi(){

        try {
            new NetworkManager(WalletBean.class, new NetworkManager.OnCallback<WalletBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {

                        if (responseClass.isSuccess()) {
                                walletBean = (WalletBean) responseClass.getResponsePacket();
                            binding.txtAmount.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(walletBean.getWalletBalance()));
                            binding.txtCoin.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(walletBean.getCoinBalance()));
                        }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            customCleverTapEvent(  walletBean.getWalletBalance());
                        }
                    }).start();
                    setData();
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_COIN_AND_WALLET_BALANCE, "", "Loading...", true, AppUrls.REQUESTTAG_GETCOINANDWALLETBALANCE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void customCleverTapEvent( double amount){
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Wallet Balance", amount);
        AppInitialization.getInstance().clevertapDefaultInstance.pushProfile(profileUpdate);
    }


}
