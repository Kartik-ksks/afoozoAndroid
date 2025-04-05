package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuBean;
import com.kpis.afoozo.databinding.MenuLayoutRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuBean> list;
    private AddRemoveClick callback;

    public MenuAdapter(Context mContext, ArrayList<MenuBean> list, AddRemoveClick callback){
        this.mContext = mContext;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuLayoutRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.menu_layout_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuBean bean = list.get(position);

        try{
            holder.binding.txtTitle.setText(bean.getCategoryName());

            if (bean.getMenuList() != null && bean.getMenuList().size() > 0){
                if (bean.getCategoryViewType().equalsIgnoreCase("Vertical")) {
                    holder.binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.rvItems.setItemAnimator(new DefaultItemAnimator());

                    MenuItemsAdapter mAdapter = new MenuItemsAdapter(mContext, bean.getMenuList(), position, callback);
                    holder.binding.rvItems.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    holder.binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    holder.binding.rvItems.setItemAnimator(new DefaultItemAnimator());

                    MenuItemsAdapter2 mAdapter = new MenuItemsAdapter2(mContext, bean.getMenuList(), position, callback);
                    holder.binding.rvItems.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private  MenuLayoutRowBinding binding;

        public MyViewHolder(@NonNull final MenuLayoutRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
