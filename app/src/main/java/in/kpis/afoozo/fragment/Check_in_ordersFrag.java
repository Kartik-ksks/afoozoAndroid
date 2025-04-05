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
import com.kpis.afoozo.databinding.FragmentCheckInOrdersBinding;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import in.kpis.afoozo.adapter.CheckInAdapter;
import in.kpis.afoozo.bean.CheckListBean;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class Check_in_ordersFrag extends Fragment {

    private View view;

    private Context mContext;

    private FragmentCheckInOrdersBinding binding;
    private ArrayList<CheckListBean> checkList;
    private  CheckInAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_check_in_orders, container, false);

        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {

        binding.rvLive.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvLive.setItemAnimator(new DefaultItemAnimator());
        callGetLiveOrdersApi();
    }

    private void callGetLiveOrdersApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<CheckListBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<CheckListBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<CheckListBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        checkList = responseClass1.getResponsePacket();
//
                        if (checkList != null && checkList.size() > 0) {
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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_ROOM_BOOKING_LIST + "Live/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETROOMBOOKINGLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setData() {
         mAdapter = new CheckInAdapter(mContext, checkList);
         binding.rvLive.setAdapter(mAdapter);

    }
}