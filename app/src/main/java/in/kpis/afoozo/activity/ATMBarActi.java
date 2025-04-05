package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityAtmbarBinding;
import in.kpis.afoozo.fragment.ATMBarOrdersFrag;
import in.kpis.afoozo.fragment.ATMBarTransFrag;

import java.util.ArrayList;
import java.util.List;

public class ATMBarActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityAtmbarBinding binding;

    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_atmbar);

        mContext = ATMBarActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.prepaid_card));

        setupViewPager(binding.vpATMBar);
        binding.tabATMBar.setupWithViewPager(binding.vpATMBar);
        setupTabIcons();
    }

    /**
     * this Method is used to set custom layout for tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab_steal_deal, null);
        tabOne.setText(getString(R.string.orders));
        binding.tabATMBar.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(mContext).inflate(R.layout.custom_tab_steal_deal, null);
        tabTwo.setText(getString(R.string.transactions));
        binding.tabATMBar.getTabAt(1).setCustomView(tabTwo);

    }

    /**
     * this method is used set fragments in viewpager adapter
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ATMBarOrdersFrag(), getString(R.string.orders));
        adapter.addFragment(new ATMBarTransFrag(), getString(R.string.transactions));
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
}
