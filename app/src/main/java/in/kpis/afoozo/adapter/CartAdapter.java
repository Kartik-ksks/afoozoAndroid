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
import in.kpis.afoozo.bean.CustomizationBean;
import in.kpis.afoozo.bean.CustomizationOptionsBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import com.kpis.afoozo.databinding.CartListRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<OrderDetailsItemBean> list;
    private AddRemoveClick callBack;
//    private ArrayList<CustomItemsBean> customList;
    private String customeData;


    public CartAdapter(Context mContext, ArrayList<OrderDetailsItemBean> list, AddRemoveClick callBack){
        this.mContext = mContext;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CartListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.cart_list_row, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, final int position) {
        OrderDetailsItemBean bean = list.get(position);

        try {
            if (bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
                holder.binding.imvVegNon.setVisibility(View.VISIBLE);
                holder.binding.imvVegNon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_veg));
            } else if (bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                holder.binding.imvVegNon.setVisibility(View.VISIBLE);
                holder.binding.imvVegNon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_non_veg_icon));
            } else
                holder.binding.imvVegNon.setVisibility(View.GONE);

            holder.binding.txtItemCart.setText(bean.getTitle());

            if (bean.getCustomization() != null && bean.getCustomization().size()>0){
                customeData = "";
                for (CustomizationBean cb: bean.getCustomization()){
                    for (CustomizationOptionsBean cob: cb.getCustomizationOptions()){
                        if (TextUtils.isEmpty(customeData))
                            customeData = cob.getName();
                        else
                            customeData = customeData + " , " + cob.getName();
                    }
                }
                holder.binding.txtCustomization.setVisibility(View.VISIBLE);
                holder.binding.txtCustomization.setText(customeData);
            } else
                holder.binding.txtCustomization.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(bean.getSpecialInstruction()))
                holder.binding.txtInstruction.setText(bean.getSpecialInstruction());

            if (bean.getFinalPrice() > 0) {
                holder.binding.llAddRemove.setVisibility(View.VISIBLE);
                holder.binding.txtQty.setVisibility(View.GONE);
                holder.binding.txtQtyCart.setText("" + bean.getQuantity());
                holder.binding.txtAmountItem.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getItemTotal()));

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
            } else {
                holder.binding.llAddRemove.setVisibility(View.INVISIBLE);
                holder.binding.txtQty.setVisibility(View.VISIBLE);
                holder.binding.txtQty.setText("" + bean.getQuantity());
                holder.binding.txtAmountItem.setText(mContext.getString(R.string.free));
            }

            holder.binding.imvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.editClick(false, 0, 0, position);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CartListRowBinding binding;

        public MyViewHolder(final CartListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
