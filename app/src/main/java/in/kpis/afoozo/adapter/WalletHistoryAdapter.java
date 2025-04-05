package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.WalletHistoryBean;
import com.kpis.afoozo.databinding.WalletHistoryLayoutBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<WalletHistoryBean> list;

    public WalletHistoryAdapter(Context mContext, ArrayList<WalletHistoryBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WalletHistoryLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.wallet_history_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WalletHistoryBean bean = list.get(position);

        try {
            holder.binding.txtId.setText(bean.getTransactionReferenceId());
            holder.binding.txtMessage.setText(bean.getRemark());
            holder.binding.txtType.setText(bean.getTransactionType());
            holder.binding.txtAmount.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getTransactionAmount()));
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

        private WalletHistoryLayoutBinding binding;

        public MyViewHolder(final WalletHistoryLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
