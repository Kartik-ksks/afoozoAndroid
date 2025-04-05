package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuListBean;
import com.kpis.afoozo.databinding.YouMayLikeLayoutBinding;

import java.util.ArrayList;


public class YouMayLikeAdaper extends RecyclerView.Adapter<YouMayLikeAdaper.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuListBean> list;

    public YouMayLikeAdaper(Context mContext, ArrayList<MenuListBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        YouMayLikeLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.you_may_like_layout, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuListBean bean = list.get(position);

        try {
//            if (!TextUtils.isEmpty(bean.getImageUrl()))
//                Utils.setImage(mContext, holder.binding.imvItem, holder.binding.pbLike, bean.getImageUrl());
//            holder.binding.txtItem.setText(bean.getTitle());
//            holder.binding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(255.36));

//            if (bean.getProductType().equals("VEG"))
//                holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_veg);
//            else
//                holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_non_veg_icon);

//            holder.binding.imvAdd.setTag(position);
//            holder.binding.imvAdd.setOnClickListener(clickListener);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private YouMayLikeLayoutBinding binding;

        public MyViewHolder(final YouMayLikeLayoutBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
