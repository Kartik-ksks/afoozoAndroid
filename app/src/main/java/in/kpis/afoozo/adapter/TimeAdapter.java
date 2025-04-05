package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.TimeListRowBinding;

import java.util.ArrayList;

import in.kpis.afoozo.bean.TimeListBean;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<TimeListBean> list;

    public TimeAdapter(Context mContext, ArrayList<TimeListBean> list){
        this.mContext = mContext;
        this.list = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimeListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.time_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeListBean bean = list.get(position);

        try {
            holder.binding.txtTime.setText(bean.getTitle());
            if (bean.isSelected())
                holder.binding.cbTime.setChecked(true);
            else
                holder.binding.cbTime.setChecked(false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TimeListRowBinding binding;

        public MyViewHolder(final TimeListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
