package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CustomizationBean;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import com.kpis.afoozo.databinding.DetailsListRowBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<OrderDetailsItemBean> list;
    private ArrayList<CustomizationBean> customizationList;

    public DetailsAdapter(Context mContext, ArrayList<OrderDetailsItemBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        DetailsListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.details_list_row, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderDetailsItemBean bean = list.get(position);

        try {
            holder.binding.txtTitle.setText(bean.getTitle() + " x " + bean.getQuantity());
            holder.binding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getItemTotal()));

            if (bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
                holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
                holder.binding.imvVegNonVeg.setBackground(mContext.getResources().getDrawable(R.drawable.ic_veg));
            } else if (bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
                holder.binding.imvVegNonVeg.setBackground(mContext.getResources().getDrawable(R.drawable.ic_non_veg_icon));
            } else
                holder.binding.imvVegNonVeg.setVisibility(View.GONE);

            if (bean.getCustomization() != null && bean.getCustomization().size() > 0){
                String customeData = "";
                customizationList = bean.getCustomization();
                for (int i = 0; i<customizationList.size(); i++){
                    if (i ==0)
                        customeData = customizationList.get(i).getTitle();
                    else
                        customeData = customeData + ", " + customizationList.get(i).getTitle();
                }
                holder.binding.txtCustomization.setVisibility(View.VISIBLE);
                holder.binding.txtCustomization.setText(customeData);
            } else
                holder.binding.txtCustomization.setVisibility(View.GONE);
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public DetailsListRowBinding binding;

        public MyViewHolder(@NonNull final DetailsListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
