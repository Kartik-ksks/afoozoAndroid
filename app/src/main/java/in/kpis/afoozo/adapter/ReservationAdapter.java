package in.kpis.afoozo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.RestaurantBean;
import com.kpis.afoozo.databinding.ReservationListRowBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<RestaurantBean> list;
    private boolean showClose;

    public ReservationAdapter(Context mContext, ArrayList<RestaurantBean> list, boolean showClose){
        this.mContext = mContext;
        this.list = list;
        this.showClose = showClose;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReservationListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.reservation_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RestaurantBean bean = list.get(position);
        try {
            holder.binding.txtTitle.setText(bean.getTitle());
            double distance = (double) bean.getDistanceInMeter()/1000;
            holder.binding.txtDistance.setText(new DecimalFormat("0.00").format(distance) + " Kms away");
            if (!TextUtils.isEmpty(bean.getRestaurantBannerUrl())) {
                holder.binding.imvReservation.setVisibility(View.VISIBLE);
                Utils.setImage(mContext, holder.binding.imvReservation, bean.getRestaurantBannerUrl());
            } else
                holder.binding.imvReservation.setVisibility(View.GONE);

            if (!showClose) {
                if (bean.isOpen())
                    holder.binding.imvClosed.setVisibility(View.GONE);
                else
                    holder.binding.imvClosed.setVisibility(View.VISIBLE);
            } else
                holder.binding.imvClosed.setVisibility(View.GONE);

        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ReservationListRowBinding binding;

        public MyViewHolder(@NonNull final ReservationListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
