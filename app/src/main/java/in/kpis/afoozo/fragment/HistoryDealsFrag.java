package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
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
import in.kpis.afoozo.activity.NewOrderHistoryActi;
import in.kpis.afoozo.activity.OrderDetailsActi;
import in.kpis.afoozo.adapter.OrdersAdapter;
import in.kpis.afoozo.bean.OrdersBean;
import com.kpis.afoozo.databinding.FragmentHistoryDealsBinding;
import in.kpis.afoozo.interfaces.OrderHistoryInterface;
import in.kpis.afoozo.interfaces.OrderInterface;
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
public class HistoryDealsFrag extends Fragment implements OrderInterface, OrderHistoryInterface {

    private static final int RATE_REQUEST = 201;
    private Context mContext;

    private View view;

    private FragmentHistoryDealsBinding binding;

    private ArrayList<OrdersBean> list;
    private OrdersAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_deals, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize(){
        binding.rvDeals.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvDeals.setItemAnimator(new DefaultItemAnimator());

        callGetOrderHistoryApi();
    }

    @Override
    public void reOrderClick(int position) {

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
        ((NewOrderHistoryActi) getActivity()).rateProcess(list.get(position).getOrderType(), list.get(position).getOrderReferenceId(), (list.get(position).getTipAmount() > 0) ? true : false, this);
    }

    @Override
    public void tipRiderClick(int position) {
        ((NewOrderHistoryActi) getActivity()).showTipLayout(list.get(position).getOrderType(), list.get(position).getOrderReferenceId(), this);
    }

    @Override
    public void tableClearClick(String orderId ) {

    }

    @Override
    public void refreshList() {
        callGetOrderHistoryApi();
    }

    private void setData() {

        if (list != null && list.size() > 0) {
            binding.rvDeals.setVisibility(View.VISIBLE);
            mAdapter = new OrdersAdapter(mContext, false, true, list, this);
            binding.rvDeals.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            binding.rvDeals.setVisibility(View.GONE);
        }
    }

    private void callGetOrderHistoryApi() {

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETORDERLIST + Constants.STEAL_DEAL + "/History" +"/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
