package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.BookNowActi;
import in.kpis.afoozo.activity.StealDealActi;
import in.kpis.afoozo.adapter.BookNowAdapter;
import in.kpis.afoozo.bean.BookNowBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import com.kpis.afoozo.databinding.FragmentBookNowBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookNowFrag extends Fragment {

    private View view;

    private Context mContext;

    private FragmentBookNowBinding binding;

    private BookNowAdapter mAdapter;
    private ArrayList<BookNowBean> categoryList;
    private String restaurantId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_now, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvBookNow.setLayoutManager(new GridLayoutManager(mContext, 2));
        binding.rvBookNow.setItemAnimator(new DefaultItemAnimator());

        binding.rvBookNow.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, BookNowActi.class);
                intent.putExtra("Category", categoryList.get(position).getTitle());
                intent.putExtra("CategoryId", categoryList.get(position).getRecordId());
                intent.putExtra("RestaurantId", restaurantId);
                startActivity(intent);
            }
        }));

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            restaurantId = ((StealDealActi) getActivity()).selectedRestaurant;
            callCategoryListApi();
        } catch (Exception e){

        }
    }

    public void refreshList(){
        restaurantId = ((StealDealActi) getActivity()).selectedRestaurant;
        callCategoryListApi();
    }

    private void setData() {

        mAdapter = new BookNowAdapter(mContext, categoryList, false);
        binding.rvBookNow.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    private void callCategoryListApi() {

        RestaurantRequestBean bean = new RestaurantRequestBean();
        bean.setRestaurantId(restaurantId);
        bean.setStart(0);
        bean.setLength(1000);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<BookNowBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<BookNowBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<BookNowBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        categoryList = responseClass1.getResponsePacket();

                        if (categoryList != null && categoryList.size() > 0) {
                            binding.rvBookNow.setVisibility(View.VISIBLE);
                            setData();
                        } else
                            binding.rvBookNow.setVisibility(View.GONE);
//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Log.e("StartEnd", "bookEnd");
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_STEAL_DEAL_CATEGORY_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_GETSTEALDEALCATEGORYLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
