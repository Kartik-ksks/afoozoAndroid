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
import com.kpis.afoozo.databinding.SubCategoryListRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;

import java.util.ArrayList;


public class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuBean> list;
    private MenuItemsAdapter mAdapter;
    private int categoryPosition;
    public AddRemoveClick callback;

    public SubCatAdapter(Context mContext, ArrayList<MenuBean> list, int categoryPosition, AddRemoveClick callback){
        this.mContext = mContext;
        this.list = list;
        this.categoryPosition = categoryPosition;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        SubCategoryListRowBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.sub_category_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final MenuBean bean = list.get(position);

        holder.binding.txtGroupTitle.setText(bean.getCategoryName());
//        holder.binding.txtGroupDesc.setText(bean.get());

        holder.binding.cvSubCatHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkClick(position, holder);
            }
        });


        setLayoutEpandedOrNot(holder, bean.isExpended());

        if (bean.getMenuList() != null && bean.getMenuList().size() > 0){
//            holder.binding.rvSubCategory.setVisibility(View.VISIBLE);

            mAdapter = new MenuItemsAdapter(mContext, true, bean.getMenuList(), categoryPosition, position, callback);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            holder.binding.rvSubCategory.setLayoutManager(mLayoutManager);
            holder.binding.rvSubCategory.setItemAnimator(new DefaultItemAnimator());
            holder.binding.rvSubCategory.setAdapter(mAdapter);
        }
    }

    private void setLayoutEpandedOrNot(MyViewHolder holder, boolean expended) {
        if (expended) {
            holder.binding.rvSubCategory.setVisibility(View.VISIBLE);
            holder.binding.imvGroupUpDown.setImageResource(R.drawable.ic_up_arrow);
        } else {
            holder.binding.rvSubCategory.setVisibility(View.GONE);
            holder.binding.imvGroupUpDown.setImageResource(R.drawable.ic_down_arrow);
        }
    }

    private void checkClick(int position, MyViewHolder holder) {
        if (list.get(position).isExpended()){
            list.get(position).setExpended(false);
//            ((AddItemsActi)mContext).expandCollapseSubCategory(categoryPosition, position, false);
            setLayoutEpandedOrNot(holder, false);
        } else {
            list.get(position).setExpended(true);
//            ((AddItemsActi)mContext).expandCollapseSubCategory(categoryPosition, position, true);
            setLayoutEpandedOrNot(holder, true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private SubCategoryListRowBinding binding;

        public MyViewHolder(final SubCategoryListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

}
