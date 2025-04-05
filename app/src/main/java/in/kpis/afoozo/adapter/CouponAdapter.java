package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CouponListBean;
import com.kpis.afoozo.databinding.CouponListRowBinding;
import in.kpis.afoozo.interfaces.CouponInterface;

import java.util.ArrayList;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CouponListBean> list;
    private boolean isApply;
    private boolean isFromOffers;

    private CouponInterface callback;

    public CouponAdapter(Context mContext, boolean isFromOffers, ArrayList<CouponListBean> list, CouponInterface callback){
        this.mContext = mContext;
        this.isFromOffers = isFromOffers;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        CouponListRowBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.coupon_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final CouponListBean bean = list.get(position);

        try {

            holder.binding.txtCoupon.setText(bean.getCouponCode());

            if (bean.getDiscountType().equals("FixedPercentage"))
                holder.binding.txtOffer.setText(bean.getDiscountValue() + "% OFF upto " + mContext.getString(R.string.Rs) + bean.getMaxDiscountAmount());
            else
                holder.binding.txtOffer.setText(mContext.getString(R.string.Rs) + bean.getDiscountValue() + " OFF");

            holder.binding.txtDesc.setText(bean.getDescription());

            if (isFromOffers) {
                holder.binding.btnCopy.setVisibility(View.VISIBLE);
                holder.binding.btnCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onCopy(position);
                    }
                });
            } else {
                holder.binding.txtApply.setVisibility(View.VISIBLE);
                holder.binding.txtApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onApply(position);
                    }
                });
            }

            holder.binding.txtViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onViewDetials(position);
                }
            });
        } catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CouponListRowBinding binding;

        public MyViewHolder(final CouponListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

}
