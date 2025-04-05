package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.RewardFullActi;
import in.kpis.afoozo.adapter.RewardsAdapter;
import in.kpis.afoozo.bean.RewardsBean;
import com.kpis.afoozo.databinding.FragmentRewardsBinding;
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
public class RewardsFrag extends Fragment {

    private View view;

    private Context mContext;

    private FragmentRewardsBinding binding;

    private ArrayList<RewardsBean> list = new ArrayList();
    private RewardsAdapter mAdapter;
    private int REWARD_REQUEST = 345;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rewards, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvRewards.setLayoutManager(new GridLayoutManager(mContext, 2));
        binding.rvRewards.setItemAnimator(new DefaultItemAnimator());

//        setData();
        callGetScratchCardApi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REWARD_REQUEST && resultCode == getActivity().RESULT_OK){
            callGetScratchCardApi();
        }
    }

    private void setData() {

        if (list != null && list.size()>0) {
            binding.rvRewards.setVisibility(View.VISIBLE);

//            mAdapter = new RewardsAdapter(mContext, list);
//            binding.rvRewards.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();

            binding.rvRewards.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(mContext, RewardFullActi.class);
                    intent.putExtra("RewardsBean", list.get(position));
                    // Get the transition name from the string

                    ActivityOptionsCompat options =

                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                    view,   // Starting view
                                    "Reward"    // The String
                            );

                    ActivityCompat.startActivityForResult(getActivity(), intent, REWARD_REQUEST, options.toBundle());
                }
            }));
        } else {
            binding.rvRewards.setVisibility(View.GONE);
        }
    }

    private void callGetScratchCardApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RewardsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<RewardsBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RewardsBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_ALL_SCRATCH_CARD, "", "Loading...", true, AppUrls.REQUESTTAG_GETALLSCRATCHCARD);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
