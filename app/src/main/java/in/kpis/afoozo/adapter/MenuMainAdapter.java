package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuBean;
import com.kpis.afoozo.databinding.MenuMainLayoutBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;

public class MenuMainAdapter extends RecyclerView.Adapter<MenuMainAdapter.MyViewHolder> {

    private Context mContext;
    private MenuBean menuBean;
    private int catPosition;
    private AddRemoveClick callback;

    public MenuMainAdapter(Context mContext, MenuBean menuBean, int catPosition, AddRemoveClick callback) {
        this.mContext = mContext;
        this.menuBean = menuBean;
        this.catPosition = catPosition;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuMainLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.menu_main_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (menuBean != null){
            if (menuBean.getMenuList() != null && menuBean.getMenuList().size() > 0){
                holder.binding.rvItems.setVisibility(View.VISIBLE);
                if (menuBean.getCategoryViewType().equals("Vertical")) {
                    holder.binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.binding.rvItems.setItemAnimator(new DefaultItemAnimator());

                    MenuItemsAdapter mAdapter = new MenuItemsAdapter(mContext, menuBean.getMenuList(), catPosition, callback);
                    holder.binding.rvItems.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    holder.binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
                    holder.binding.rvItems.setItemAnimator(new DefaultItemAnimator());

                    MenuItemsAdapter2 horizontalAdapter = new MenuItemsAdapter2(mContext, menuBean.getMenuList(), catPosition, callback);
                    holder.binding.rvItems.setAdapter(horizontalAdapter);
                    horizontalAdapter.notifyDataSetChanged();
                }
            } else
                holder.binding.rvItems.setVisibility(View.GONE);

            if (menuBean.getSubCategoryList() != null && menuBean.getSubCategoryList().size() > 0){
                holder.binding.rvSubCategory.setVisibility(View.VISIBLE);

                SubCatAdapter subCatAdapter = new SubCatAdapter(mContext, menuBean.getSubCategoryList(), catPosition, callback);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                holder.binding.rvSubCategory.setLayoutManager(mLayoutManager);
                holder.binding.rvSubCategory.setItemAnimator(new DefaultItemAnimator());
                holder.binding.rvSubCategory.setAdapter(subCatAdapter);
            } else
                holder.binding.rvSubCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private MenuMainLayoutBinding binding;

        public MyViewHolder(final MenuMainLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
