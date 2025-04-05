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
import in.kpis.afoozo.adapter.CouponAdapter;
import in.kpis.afoozo.bean.CouponListBean;
import com.kpis.afoozo.databinding.FragmentOffersBinding;
import in.kpis.afoozo.interfaces.CouponInterface;
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
public class OffersFrag extends Fragment implements CouponInterface {

    private View view;

    private Context mContext;

    private FragmentOffersBinding binding;
    private ArrayList<CouponListBean> list = new ArrayList();
    private CouponAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offers, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvOffers.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvOffers.setItemAnimator(new DefaultItemAnimator());

        callGetCouponApi();
    }

    private void setData() {
        mAdapter = new CouponAdapter(mContext, true, list, this);
        binding.rvOffers.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.COUPON_LIST + "All", "", "Loading...", true, AppUrls.REQUESTTAG_COUPONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApply(int position) {

    }

    @Override
    public void onCopy(int position) {
        Utils.copyCode(mContext, list.get(position).getCouponCode());
    }

    @Override
    public void onViewDetials(int position) {

    }
}
