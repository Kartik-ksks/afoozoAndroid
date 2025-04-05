package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuTabBean;
import com.kpis.afoozo.databinding.MenuTabListRowBinding;

import java.util.ArrayList;

public class MenuTabAdapter extends RecyclerView.Adapter<MenuTabAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuTabBean> list;

    public MenuTabAdapter(Context mContext, ArrayList<MenuTabBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuTabListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.menu_tab_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuTabBean bean = list.get(position);
        holder.binding.txtTitle.setText(bean.getTitle());
        if (bean.isSelected()){
            holder.binding.underLine.setVisibility(View.VISIBLE);
            holder.binding.txtTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.binding.underLine.setVisibility(View.GONE);
            holder.binding.txtTitle.setTextColor(mContext.getResources().getColor(R.color.textPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private MenuTabListRowBinding binding;

        public MyViewHolder(@NonNull final MenuTabListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
