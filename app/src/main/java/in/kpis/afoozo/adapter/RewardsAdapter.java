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
import in.kpis.afoozo.bean.RewardCouponBean;
import com.kpis.afoozo.databinding.RewardsListRowBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<RewardCouponBean> list;

    public RewardsAdapter(Context mContext, ArrayList<RewardCouponBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RewardsListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rewards_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        RewardCouponBean bean = list.get(position);
        try {
            if (bean.getRewardsBean() != null) {
                holder.binding.txtValidity.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(bean.getRewardsBean().getCouponCode())) {
                    holder.binding.imvScratch.setVisibility(View.GONE);
                    holder.binding.txtAmount.setText((mContext.getString(R.string.you_get_rs_off_on_your_next_order)).replace("_", new DecimalFormat("0").format(bean.getRewardsBean().getCouponDiscountAmount())));
                    holder.binding.txtCouponCode.setText(bean.getRewardsBean().getCouponCode());

                    if (bean.getRewardsBean().isUsed())
                        holder.binding.transView.setVisibility(View.VISIBLE);
                    else
                        holder.binding.transView.setVisibility(View.GONE);

                } else
                    holder.binding.imvScratch.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imvScratch.setVisibility(View.GONE);
                holder.binding.txtValidity.setVisibility(View.VISIBLE);
                if (bean.getCouponListBean().getDiscountType().equalsIgnoreCase("FixedPercentage"))
                    holder.binding.txtAmount.setText((mContext.getString(R.string.you_get_off_on_your_next_order)).replace("_", (new DecimalFormat("0").format(bean.getCouponListBean().getDiscountValue()) + "%")));
                else
                    holder.binding.txtAmount.setText((mContext.getString(R.string.you_get_rs_off_on_your_next_order)).replace("_", new DecimalFormat("0").format(bean.getCouponListBean().getMaxDiscountAmount())));
                holder.binding.txtCouponCode.setText(bean.getCouponListBean().getCouponCode());
                holder.binding.txtValidity.setText(mContext.getString(R.string.valid_till) + " " + bean.getCouponListBean().getEndDate());
            }

        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RewardsListRowBinding binding;

        public MyViewHolder(@NonNull RewardsListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
