package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.StealDealCartBean;
import com.kpis.afoozo.databinding.CartListRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StealDealCartAdapter extends RecyclerView.Adapter<StealDealCartAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<StealDealCartBean> list;
    private AddRemoveClick callBack;

    public StealDealCartAdapter(Context mContext, ArrayList<StealDealCartBean> list, AddRemoveClick callBack) {
        this.mContext = mContext;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cart_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StealDealCartBean bean = list.get(position);
        try {
            holder.binding.txtItemCart.setText(bean.getItemName());
            holder.binding.txtQtyCart.setText("" + bean.getStealDealItemQty());
            holder.binding.imvVegNon.setVisibility(View.GONE);
            holder.binding.txtAmountItem.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()));

            holder.binding.imvAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.addClick(false, 0, 0, position);
                }
            });

            holder.binding.imvRemoveCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.removeClick(false, 0, 0, position);
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

        private CartListRowBinding binding;

        public MyViewHolder(final CartListRowBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
