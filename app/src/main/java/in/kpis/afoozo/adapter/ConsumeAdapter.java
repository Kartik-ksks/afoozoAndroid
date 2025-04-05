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
import in.kpis.afoozo.bean.ConsumeBean;
import com.kpis.afoozo.databinding.ConsumeListRowBinding;
import in.kpis.afoozo.util.Utils;

import java.util.ArrayList;

public class ConsumeAdapter extends RecyclerView.Adapter<ConsumeAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ConsumeBean> list;

    public ConsumeAdapter(Context mContext, ArrayList<ConsumeBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConsumeListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.consume_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ConsumeBean bean = list.get(position);
        try{
            holder.binding.txtTitle.setText(bean.getStealDealItemTitle());
            holder.binding.txtValidTill.setText(mContext.getString(R.string.valid_till) + " " + Utils.getFormetedDateTime(bean.getConsumeExpiryDate()));

            if (!TextUtils.isEmpty(bean.getStealDealItemImage())) {
                holder.binding.imvItem.setVisibility(View.VISIBLE);
                Utils.setImage(mContext, holder.binding.imvItem, holder.binding.pbItem, bean.getStealDealItemImage());
            } else {
                holder.binding.imvItem.setVisibility(View.GONE);
                holder.binding.pbItem.setVisibility(View.GONE);
            }

            if (bean.getConsumeAfter() > 0){
                holder.binding.txtConsumeAfter.setVisibility(View.VISIBLE);
                holder.binding.txtConsumeAfter.setText(mContext.getString(R.string.consume_after) + " \n" + Utils.getFormetedDateTime(bean.getConsumeAfter()));
            } else
                holder.binding.txtConsumeAfter.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(bean.getBackgroundImageUrl())) {
                holder.binding.imvBackground.setVisibility(View.VISIBLE);
                Utils.setImage(mContext, holder.binding.imvBackground, bean.getBackgroundImageUrl());
            } else
                holder.binding.imvBackground.setVisibility(View.GONE);


            if (bean.isGifted())
                holder.binding.imvGift.setVisibility(View.VISIBLE);
            else
                holder.binding.imvGift.setVisibility(View.GONE);

            holder.binding.txtQty.setText(bean.getRemainingQuantity() + " " + bean.getConsumableUnitPostfix());
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ConsumeListRowBinding binding;

        public MyViewHolder(final ConsumeListRowBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
