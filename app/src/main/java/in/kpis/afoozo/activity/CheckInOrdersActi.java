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
import com.kpis.afoozo.databinding.ActivityCheckInOrdersBinding;
import com.kpis.afoozo.databinding.ActivityWalletBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.kpis.afoozo.fragment.AddFrag;
import in.kpis.afoozo.fragment.Check_in_ordersFrag;
import in.kpis.afoozo.fragment.Check_out_Frag;
import in.kpis.afoozo.fragment.CoinFrag;
import in.kpis.afoozo.fragment.WalletHistoryFrag;

public class CheckInOrdersActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityCheckInOrdersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_orders);
      mContext = CheckInOrdersActi.this;

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

        binding.toolbar.activityTitle.setText(getString(R.string.check_in));
        setupViewPager(binding.vpCheckIn);
        binding.tabCheck.setupWithViewPager(binding.vpCheckIn);
    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Check_in_ordersFrag(), getString(R.string.check_in));
        adapter.addFragment(new Check_out_Frag(), getString(R.string.checkout));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("req",requestCode+"");

        int index = binding.vpCheckIn.getCurrentItem();
        WalletActi.ViewPagerAdapter adapter = ((WalletActi.ViewPagerAdapter)binding.vpCheckIn.getAdapter());
        final AddFrag tabFragment = (AddFrag) adapter.getFragment(index);
        tabFragment.onActivityResult(requestCode,resultCode,data);

    }
}