package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.TaxesBean;
import com.kpis.afoozo.databinding.TaxRowLayoutBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TaxesAdapter extends RecyclerView.Adapter<TaxesAdapter.MyViewHolder> {

    private Context mContex;
    private ArrayList<TaxesBean> list;

    public TaxesAdapter(Context mContex, ArrayList<TaxesBean> list) {
        this.mContex = mContex;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaxRowLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tax_row_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaxesBean bean = list.get(position);

        holder.binding.txtTaxName.setText(bean.getKey());
        holder.binding.txtTaxAmount.setText(mContex.getResources().getString(R.string.Rs) + new DecimalFormat("0.00").format(Double.parseDouble(bean.getValue())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TaxRowLayoutBinding binding;

        public MyViewHolder(final TaxRowLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
