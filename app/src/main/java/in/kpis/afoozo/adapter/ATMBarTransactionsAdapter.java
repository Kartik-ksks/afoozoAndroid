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
import in.kpis.afoozo.bean.ATMBarTransactionBean;
import com.kpis.afoozo.databinding.AtmbarTransactionsRowBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ATMBarTransactionsAdapter extends RecyclerView.Adapter<ATMBarTransactionsAdapter.MyViewHolder> {

    private Context mContext;

    private ArrayList<ATMBarTransactionBean> list;

    public ATMBarTransactionsAdapter(Context mContext, ArrayList<ATMBarTransactionBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AtmbarTransactionsRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.atmbar_transactions_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ATMBarTransactionBean bean = list.get(position);

        try {
            if (!TextUtils.isEmpty(bean.getTransactionId())) {
                holder.binding.txtId.setText(bean.getTransactionId());
                holder.binding.txtId.setVisibility(View.VISIBLE);
            } else
                holder.binding.txtId.setVisibility(View.GONE);
            holder.binding.txtMessage.setText(bean.getMessage());
            holder.binding.txtType.setText(bean.getRfidTransactionType());
            holder.binding.txtAmount.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getAmount()));
            holder.binding.txtDate.setText(Utils.getFormetedDate(bean.getCreatedAtTimeStamp()));
            holder.binding.txtTime.setText(Utils.getFormetedTimeForEvent(bean.getCreatedAtTimeStamp()));
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AtmbarTransactionsRowBinding binding;

        public MyViewHolder(@NonNull AtmbarTransactionsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
