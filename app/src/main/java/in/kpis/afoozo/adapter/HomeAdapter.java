package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.HomeListBeanBinding;
import com.kpis.afoozo.databinding.RestaurantListBeanBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

import in.kpis.afoozo.activity.DeliveryRestaurantActi;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.util.Utils;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private Context mContext;

    private boolean isFromHome;

    private ArrayList<RestaurantBean> list;

    public HomeAdapter(Context mContext, ArrayList<RestaurantBean> list, boolean isFromHome) {
        this.mContext = mContext;
        this.list = list;
        this.isFromHome = isFromHome;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isFromHome) {
            HomeListBeanBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.home_list_bean, viewGroup, false);
            return new MyViewHolder(binding);
        } else {
            RestaurantListBeanBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.restaurant_list_bean, viewGroup, false);
            return new MyViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RestaurantBean bean = list.get(position);

        try {
            Utils.setImage(mContext, holder.homeBinding.imvItems, holder.homeBinding.pbRest, bean.getRestaurantBannerUrl());
            holder.homeBinding.txtTitle.setText(bean.getTitle());
            for (int i=0; i<bean.getCuisineList().size();i++) {
                if(i == 0) {
                    holder.homeBinding.txtCuisine.append(bean.getCuisineList().get(i));
                }else {
                    holder.homeBinding.txtCuisine.append(", "+bean.getCuisineList().get(i));
                }
            }

//            holder.homeBinding.txtAddress.setText(bean.getRestaurantAddress());
            if(bean.getRating() == 00)
                holder.homeBinding.txtRating.setText("5.0");
            else
            holder.homeBinding.txtRating.setText(new DecimalFormat("0.0").format(bean.getRating()));
//            holder.homeBinding.txtDistance.setVisibility(View.VISIBLE);
            if(bean.isOpen()) {
                holder.homeBinding.txtRestClose.setText(mContext.getString(R.string.open) + " ");
            } else
                    holder.homeBinding.txtRestClose.setText(mContext.getString(R.string.closed));
                holder.homeBinding.txtRestClose.setTextColor(mContext.getResources().getColor(R.color.green));
//            }
//            else {
//                if (bean.isShowMessage())
//                    holder.homeBinding.txtRestClose.setText(mContext.getString(R.string.open_at) + " " + bean.getOpenTime());
//                else
//                    holder.homeBinding.txtRestClose.setText(mContext.getString(R.string.close));
//                holder.homeBinding.txtRestClose.setTextColor(mContext.getResources().getColor(R.color.red));
//            }

            holder.homeBinding.txtDeliveryTime.setText(Math.round(bean.getEstimatedTimeArrival())+" min.");
            holder.homeBinding.txtDistance.setText(new DecimalFormat("0.00").format(bean.getDistanceInMeter() / 1000 )+ " " + mContext.getString(R.string.kms_away));
            holder.homeBinding.llRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DeliveryRestaurantActi) mContext).goToMenuScreen(bean.getRestaurantUuid(), bean.getTitle(), bean.getDeliveryMinimumOrderAmount(), bean.isOpen());

                }
            });


        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
//        if (isFromHome) {
//            if (list.size() > 4)
//                return 4;
//            else
//                return list.size();
//        } else
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private HomeListBeanBinding homeBinding;
        private RestaurantListBeanBinding restBinding;

        public MyViewHolder(final HomeListBeanBinding itemBinding) {
            super(itemBinding.getRoot());

            homeBinding = itemBinding;
        }

        public MyViewHolder(final RestaurantListBeanBinding itemBinding) {
            super(itemBinding.getRoot());

            restBinding = itemBinding;
        }
    }

}
