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
import in.kpis.afoozo.bean.MenuListBean;
import com.kpis.afoozo.databinding.TopSellingListRowBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuListBean> list;

    public TopSellingAdapter(Context mContext, ArrayList<MenuListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TopSellingListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.top_selling_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuListBean bean = list.get(position);
        if (!TextUtils.isEmpty(bean.getItemImageUrlThumbnail())) {
            holder.binding.imvItem.setVisibility(View.VISIBLE);
            Utils.setImage(mContext, holder.binding.imvItem, holder.binding.pbTopPicks, bean.getItemImageUrlThumbnail());
        }else {
            holder.binding.pbTopPicks.setVisibility(View.GONE);
            holder.binding.imvItem.setVisibility(View.INVISIBLE);
        }
        holder.binding.txtTitle.setText(bean.getTitle());

        if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
            holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_veg);
            holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
            holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_non_veg_icon);
            holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
        } else{
            holder.binding.imvVegNonVeg.setVisibility(View.GONE);
        }

        holder.binding.txtRestaurant.setText(bean.getDescription());
        holder.binding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getFinalPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TopSellingListRowBinding binding;

        public MyViewHolder(final TopSellingListRowBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
