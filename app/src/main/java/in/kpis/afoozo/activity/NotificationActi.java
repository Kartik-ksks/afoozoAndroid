package in.kpis.afoozo.activity;

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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;

import in.kpis.afoozo.adapter.NotificationListAdapter;
import in.kpis.afoozo.bean.MenuListBean;
import in.kpis.afoozo.bean.NotificationBean;
import com.kpis.afoozo.databinding.ActivityNotificationBinding;

import in.kpis.afoozo.bean.NotificationListBean;
import in.kpis.afoozo.fragment.NotiEventFrag;
import in.kpis.afoozo.fragment.NotiTableFrag;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityNotificationBinding binding;
    private ArrayList<NotificationListBean> list;
    private NotificationListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        mContext = NotificationActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.notification));


        binding.rvNotification.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvNotification.setItemAnimator(new DefaultItemAnimator());

        callNotificationApi();
    }

    private void callNotificationApi() {
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<NotificationListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<NotificationListBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<NotificationListBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0)
                            setData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_NOTIFICATION_LIST + "All", "", "Loading...", true, AppUrls.REQUESTTAG_GETNOTIFICATIONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        mAdapter = new NotificationListAdapter(mContext, list);
        binding.rvNotification.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


}
