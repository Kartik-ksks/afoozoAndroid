package in.kpis.afoozo.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.NotificationListAdapter;
import in.kpis.afoozo.bean.NotificationListBean;
import com.kpis.afoozo.databinding.FragmentNotiTableBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotiTableFrag extends Fragment {

    private Context mContext;

    private View view;

    private FragmentNotiTableBinding binding;

    private ArrayList<NotificationListBean> list;
    private NotificationListAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_noti_table, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvTable.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvTable.setItemAnimator(new DefaultItemAnimator());

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_NOTIFICATION_LIST + "TableReservation", "", "Loading...", true, AppUrls.REQUESTTAG_GETNOTIFICATIONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        mAdapter = new NotificationListAdapter(mContext, list);
        binding.rvTable.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}
