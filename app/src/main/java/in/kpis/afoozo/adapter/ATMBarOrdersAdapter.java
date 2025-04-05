package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.ATMBarHistoryBean;
import com.kpis.afoozo.databinding.AtmbarOrdersRowBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ATMBarOrdersAdapter extends RecyclerView.Adapter<ATMBarOrdersAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ATMBarHistoryBean> list;

    public ATMBarOrdersAdapter(Context mContext, ArrayList<ATMBarHistoryBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AtmbarOrdersRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.atmbar_orders_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ATMBarHistoryBean bean = list.get(position);
        try {
            holder.binding.txtTitle.setText(bean.getRestaurantName());
            holder.binding.txtTotal.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getDrinkAmount()));

            holder.binding.txtItemName.setText(bean.getDrinkName() + " x " + new DecimalFormat("0").format(bean.getDrinkQuantity()) + " ml");

            holder.binding.txtTime.setText(Utils.getDateTimeForOrders(bean.getCreatedAtTimeStamp()));


        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AtmbarOrdersRowBinding binding;

        public MyViewHolder(@NonNull AtmbarOrdersRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
