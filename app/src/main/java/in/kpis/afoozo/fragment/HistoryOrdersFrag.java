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
import in.kpis.afoozo.activity.CartActi;
import in.kpis.afoozo.activity.NewOrderHistoryActi;
import in.kpis.afoozo.activity.OrderDetailsActi;
import in.kpis.afoozo.adapter.OrdersAdapter;
import in.kpis.afoozo.bean.OrdersBean;
import com.kpis.afoozo.databinding.FragmentHistoryOrdersBinding;
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
public class HistoryOrdersFrag extends Fragment implements OrderInterface, OrderHistoryInterface {

    private Context mContext;

    private View view;

    private FragmentHistoryOrdersBinding binding;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_orders, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvOrders.setItemAnimator(new DefaultItemAnimator());

        callGetOrderHistorysApi();
    }

    @Override
    public void reOrderClick(int position) {
        String orderType = list.get(position).getOrderType();
        if (orderType.equals(Constants.DINE_IN))
            orderType = mContext.getString(R.string.dine_in);
        else if (orderType.equals(Constants.HOME_DELIVERY))
            orderType = mContext.getString(R.string.delivery);
        else if (orderType.equals(Constants.TAKE_AWAY))
            orderType = mContext.getString(R.string.take_away);
//        callReOrderApi(list.get(position).getOrderReferenceId(), orderType);
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
        ((NewOrderHistoryActi) getActivity()).rateProcess(list.get(position).getOrderType(), list.get(position).getOrderReferenceId(), false, this);
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
        callGetOrderHistorysApi();
    }

    private void setData() {

        if (list != null && list.size() > 0) {
            binding.rvOrders.setVisibility(View.VISIBLE);
            mAdapter = new OrdersAdapter(mContext, false, false, list, this);
            binding.rvOrders.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            binding.rvOrders.setVisibility(View.GONE);
        }
    }

    private void callGetOrderHistorysApi() {

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETORDERLIST + "All" + "/History" +"/0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callReOrderApi(String orderId, String orderType) {

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        String generatedId = (String) responseClass.getResponsePacket();

                        Intent intent = new Intent(mContext, CartActi.class);
                        intent.putExtra(Constants.ORDER_ID, generatedId);
                        intent.putExtra(Constants.FROM_WHICH, orderType);
                        startActivity(intent);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.RE_ORDER + orderId, "", "Loading...", true, AppUrls.REQUESTTAG_REORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
