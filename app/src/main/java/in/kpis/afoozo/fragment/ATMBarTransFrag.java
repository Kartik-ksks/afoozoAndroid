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
import in.kpis.afoozo.adapter.ATMBarTransactionsAdapter;
import in.kpis.afoozo.bean.ATMBarTransactionBean;
import com.kpis.afoozo.databinding.FragmentAtmbarTransBinding;
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
public class ATMBarTransFrag extends Fragment {

    private Context mContext;

    private View view;

    private FragmentAtmbarTransBinding binding;

    private ArrayList<ATMBarTransactionBean> list;
    private ATMBarTransactionsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_atmbar_trans, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize(){

        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvTransactions.setItemAnimator(new DefaultItemAnimator());

        callGetATMBarTransactionsApi();
    }

    private void setData() {
        if (list != null && list.size() > 0){
            mAdapter = new ATMBarTransactionsAdapter(mContext, list);
            binding.rvTransactions.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void callGetATMBarTransactionsApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<ATMBarTransactionBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<ATMBarTransactionBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<ATMBarTransactionBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_RFID_TRANSACTION_LIST_DATA + "0/1000", "", "Loading...", true, AppUrls.REQUESTTAG_GETRFIDTRANSACTIONLISTDATA);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
