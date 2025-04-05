package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.PopularBean;
import com.kpis.afoozo.databinding.PopularListRowBinding;
import in.kpis.afoozo.util.Utils;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<PopularBean> list;

    public PopularAdapter(Context mContext, ArrayList<PopularBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.popular_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PopularBean bean = list.get(position);
        try {
            Utils.setImage(mContext, holder.binding.imvCat, holder.binding.pbPopular, bean.getImage());
            holder.binding.txtName.setText(bean.getTitle());
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private PopularListRowBinding binding;

        public MyViewHolder(final PopularListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
