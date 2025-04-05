package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.PaymentTypeListRowBinding;

import java.util.ArrayList;

import in.kpis.afoozo.bean.SavedCardBean;

public class PaymentGetWayAdapter extends RecyclerView.Adapter<PaymentGetWayAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<String> savedCardList;

    public PaymentGetWayAdapter(Context mContext, ArrayList<String> savedCardList) {
        this.mContext = mContext;
        this.savedCardList = savedCardList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentTypeListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.payment_type_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String bean = savedCardList.get(position);
        if (bean.equals("Wallet")) {
            holder.binding.llPayment.setVisibility(View.GONE);
            holder.binding.txtPayUmoneySpice.setVisibility(View.GONE);
        } else if (bean.equals("PayUMoney") || bean.equals("CashFree")) {
            holder.binding.txtPayUmoneySpice.setVisibility(View.VISIBLE);
            holder.binding.txtPayemtType.setText(bean);
        } else {
            holder.binding.llPayment.setVisibility(View.VISIBLE);
            holder.binding.txtPayemtType.setText(bean);
            holder.binding.txtPayUmoneySpice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return savedCardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        PaymentTypeListRowBinding binding;

        public MyViewHolder(@NonNull PaymentTypeListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
