package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.BankBean;
import com.kpis.afoozo.databinding.BankListRowBinding;
import in.kpis.afoozo.util.Utils;
import com.razorpay.Razorpay;

import java.util.ArrayList;


public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.MyViewHolder> {

    private Context mContext;
    private Razorpay razorpay;
    private ArrayList<BankBean> list;

    public BankListAdapter(Context mContext, ArrayList<BankBean> list, Razorpay razorpay){
        this.mContext = mContext;
        this.list = list;
        this.razorpay = razorpay;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BankListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bank_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setBankName(list.get(position).getBankName());
        Utils.setImage(mContext,holder.binding.imvBank, razorpay.getBankLogoUrl(list.get(position).getBankName()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private BankListRowBinding binding;

        public MyViewHolder(final BankListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
