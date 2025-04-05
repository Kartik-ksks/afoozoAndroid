package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.RewardsAdapter;
import in.kpis.afoozo.bean.CouponListBean;
import in.kpis.afoozo.bean.RewardCouponBean;
import in.kpis.afoozo.bean.RewardsBean;
import com.kpis.afoozo.databinding.ActivityRewardsNewBinding;
import com.kpis.afoozo.databinding.RewardsNotificationLayoutBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class RewardsNewActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityRewardsNewBinding binding;
    private ArrayList<RewardsBean> rewardList;
    private ArrayList<CouponListBean> couponList;
    private ArrayList<RewardCouponBean> list = new ArrayList();
    private RewardsAdapter mAdapter;

    private int REWARD_REQUEST = 234;
    private int position;

    private String notificationTitle;
    private String notificationMessage;
    private boolean isFromNotification;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rewards_new);

        if (getIntent().getExtras() != null){
            notificationTitle = getIntent().getStringExtra(Constants.TITLE);
            notificationMessage = getIntent().getStringExtra("message");
            imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
            isFromNotification = getIntent().getBooleanExtra(Constants.IS_FROM_NOTIFICATION, false);
        }

        mContext = RewardsNewActi.this;
        initialize();
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification)
            Utils.startActivityWithFinish(mContext, Dashboard.class);
        else
            finish();
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
                onBackPressed();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.offers_rewards));

        binding.rvRewards.setLayoutManager(new GridLayoutManager(mContext, 2));
        binding.rvRewards.setItemAnimator(new DefaultItemAnimator());

        binding.rvRewards.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (list.get(position).getRewardsBean() != null) {
                    if (!list.get(position).getRewardsBean().isUsed()) {
                        goToFullScreen(position, view);
                    }
                } else
                    goToFullScreen(position, view);

            }
        }));

//        setData();
        if (!isFromNotification)
            callGetScratchCardApi(false);
        else
            Utils.showNotificationPopUp(mContext, notificationMessage, imageUrl, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.dismissNotificationAlert();
                    callGetScratchCardApi(false);
                }
            });

    }

    private void goToFullScreen(int position, View view) {
        this.position = position;
        Intent intent = new Intent(mContext, RewardFullActi.class);
        intent.putExtra("RewardsBean", list.get(position));
        // Get the transition name from the string

        ActivityOptionsCompat options =

                ActivityOptionsCompat.makeSceneTransitionAnimation(RewardsNewActi.this,
                        view,   // Starting view
                        "Reward"    // The String
                );

        ActivityCompat.startActivityForResult(RewardsNewActi.this, intent, REWARD_REQUEST, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REWARD_REQUEST && resultCode == RESULT_OK){
            RewardCouponBean rcBean = (RewardCouponBean) data.getSerializableExtra("RewardsBean");

            list.get(position).setRewardsBean(rcBean.getRewardsBean());
            list.get(position).setCouponListBean(rcBean.getCouponListBean());

            mAdapter.notifyDataSetChanged();
        }
    }

    private void setData() {
        if (list != null && list.size() > 0)
            list.clear();

        if (couponList != null && couponList.size() > 0){
            for (CouponListBean clb: couponList)
                list.add(new RewardCouponBean(clb, clb.getCreatedAtTimeStamp()));
        }

        if (list != null && list.size()>0) {
            Collections.sort(list, Collections.reverseOrder());

            if (rewardList != null && rewardList.size() > 0){
                for (RewardsBean rb: rewardList)
                    if (TextUtils.isEmpty(rb.getCouponCode()))
                        list.add(0,new RewardCouponBean(rb, rb.getCreatedAtTimeStamp()));
            }
            binding.rvRewards.setVisibility(View.VISIBLE);

            mAdapter = new RewardsAdapter(mContext, list);
            binding.rvRewards.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else {
            binding.rvRewards.setVisibility(View.GONE);
        }
    }

    private void callGetScratchCardApi(boolean showProgressBar) {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RewardsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<RewardsBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RewardsBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        rewardList = responseClass1.getResponsePacket();
                        if (showProgressBar)
                            setData();
                        else
                            callGetCouponApi();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_ALL_SCRATCH_CARD, "", "Loading...", showProgressBar, AppUrls.REQUESTTAG_GETALLSCRATCHCARD);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetCouponApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<CouponListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<CouponListBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<CouponListBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        couponList = responseClass1.getResponsePacket();
                        setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.COUPON_LIST + "ALL/ALL", "", "Loading...", true, AppUrls.REQUESTTAG_COUPONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
