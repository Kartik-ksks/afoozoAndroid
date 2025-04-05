package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.FragmentLiveBinding;

import in.kpis.afoozo.activity.OrderDetailsActi;
import in.kpis.afoozo.activity.TrackActi;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFrag extends Fragment {

    private Context mContext;

    private View view;

    private OrdersBean orderDetailBean;

    private FragmentLiveBinding binding;


    public static LiveFrag newInstance (OrdersBean orderDetailBean) {
        // Required empty public constructor
        LiveFrag fragment = new LiveFrag();
        Bundle args = new Bundle();
        args.putSerializable("LiveBean", orderDetailBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderDetailBean = (OrdersBean) getArguments().getSerializable("LiveBean");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live, container, false);
        view = binding.getRoot();

        initialize();

        return view;
}

    private void initialize() {
        binding.txtTitle.setText(orderDetailBean.getOrderReferenceId());

        binding.llLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderDetailBean.getOrderType().equals("HomeDelivery")) {
                    Intent intent = new Intent(mContext, TrackActi.class);
                    intent.putExtra(Constants.ORDER_ID, orderDetailBean.getOrderReferenceId());
                    startActivity(intent);
                } else if (!orderDetailBean.getOrderType().equals(Constants.STEAL_DEAL)){
                    Intent intent = new Intent(mContext, OrderDetailsActi.class);
                    intent.putExtra("IsFromTrack", false);
                    intent.putExtra("IsFromDetails", true);
                    intent.putExtra(Constants.ORDER_ID, orderDetailBean.getOrderReferenceId());
                    startActivity(intent);
                }
//                Intent intent = new Intent(mContext, TrackActi.class);
//                intent.putExtra(Constants.ORDER_ID, orderDetailBean.getRecordId());
//                startActivity(intent);
            }
        });
    }

}
