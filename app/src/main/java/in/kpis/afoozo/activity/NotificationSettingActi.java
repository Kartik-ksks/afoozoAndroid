package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.NotiSettingAdapter;
import in.kpis.afoozo.bean.NotiSettingBean;
import in.kpis.afoozo.bean.SaveSettingBean;
import com.kpis.afoozo.databinding.ActivityNotificationSettingBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class NotificationSettingActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityNotificationSettingBinding binding;
    private ArrayList<NotiSettingBean> list = new ArrayList<>();
    private NotiSettingAdapter mAdapter;
    private ArrayList<NotiSettingBean> notificationList;
    private boolean isAllActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_setting);

        mContext = NotificationSettingActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.notification_setting));

        binding.rvNotificationSetting.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvNotificationSetting.setItemAnimator(new DefaultItemAnimator());

        binding.switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (notificationList != null && notificationList.size() >0){
                    for (NotiSettingBean nb: notificationList)
                        nb.setOnFlag(isChecked);
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            }
        });

        callGetRestaurantList();

    }

    private void setData() {
        for (NotiSettingBean nb: notificationList){
            if (!nb.isOnFlag()) {
                isAllActive = false;
                break;
            }
        }
        if (isAllActive)
            binding.switchAll.setChecked(true);
        else
            binding.switchAll.setChecked(false);

        mAdapter = new NotiSettingAdapter(mContext, notificationList);
        binding.rvNotificationSetting.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void callGetRestaurantList(){

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<NotiSettingBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<NotiSettingBean>>>(){}.getType();
                        ResponseClass<ArrayList<NotiSettingBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        notificationList = responseClass1.getResponsePacket();
                        if (notificationList != null && notificationList.size() > 0)
                        setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_NOTIFICATION_SETTING , "", "Loading...", true, AppUrls.REQUESTTAG_GETNOTIFICATIONSETTING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void callSateSettingList(View view){

        SaveSettingBean saveBean = new SaveSettingBean();
        saveBean.setNotificationList(notificationList);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
//                    if (responseClass.isSuccess()){
//
//                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
//                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.dismissProgressDialog();
                    Utils.showCenterToast(mContext, "Check your Internet Connection" );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_NOTIFICATION_SETTING , new Gson().toJson(saveBean), "Loading...", true, AppUrls.REQUESTTAG_SAVENOTIFICATIONSETTING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
