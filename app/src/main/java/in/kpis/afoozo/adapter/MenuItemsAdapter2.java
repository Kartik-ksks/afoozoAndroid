package in.kpis.afoozo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuListBean;
import com.kpis.afoozo.databinding.HorizontalItemsListRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuItemsAdapter2 extends RecyclerView.Adapter<MenuItemsAdapter2.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuListBean> list;
    private AddRemoveClick callBack;
    private int catPosition;

    public MenuItemsAdapter2(Context mContext, ArrayList<MenuListBean> list, int catPosition, AddRemoveClick callBack) {
        this.mContext = mContext;
        this.list = list;
        this.catPosition = catPosition;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_items_list_row, parent, false);

        int px = Utils.getPixelDensity(mContext);

        int width = parent.getMeasuredWidth() / 2;
        CardView.LayoutParams lp = new CardView.LayoutParams(width - px*2, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp.setMargins(px, px, px, px);
        itemView.setLayoutParams(lp);

//        HorizontalItemsListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.horizontal_items_list_row, parent, false);
        HorizontalItemsListRowBinding binding = DataBindingUtil.bind(itemView);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        MenuListBean bean = list.get(position);

        try{
            holder.binding.shimmerAddButton.startShimmer();
            holder.binding.txtTitle.setText(bean.getTitle());
            if (!TextUtils.isEmpty(bean.getItemImageUrlThumbnail())) {
                holder.binding.imvItem.setVisibility(View.VISIBLE);
                Utils.setImage(mContext, holder.binding.imvItem, holder.binding.pbMenu, bean.getItemImageUrlThumbnail());
            } else
                holder.binding.imvItem.setVisibility(View.GONE);

//            if (!TextUtils.isEmpty(bean.getVegNonVeg()))
//                holder.binding.imvVegNonVeg.setImageResource(bean.getVegNonVeg().equalsIgnoreCase("Veg")? R.drawable.ic_veg : R.drawable.ic_non_veg_icon);
//            else
//                holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_veg);

            if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
                holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_veg);
                holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                holder.binding.imvVegNonVeg.setImageResource(R.drawable.ic_non_veg_icon);
                holder.binding.imvVegNonVeg.setVisibility(View.VISIBLE);
            } else{
                holder.binding.imvVegNonVeg.setVisibility(View.GONE);
            }

            holder.binding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()));

            if (bean.getCustomization() != null && bean.getCustomization().size() > 0)
                holder.binding.txtCustomizable.setVisibility(View.VISIBLE);
            else
                holder.binding.txtCustomizable.setVisibility(View.GONE);

            if (bean.getQuantity() > 0){
                holder.binding.rlAdd.setVisibility(View.GONE);
                holder.binding.llAddRemove.setVisibility(View.VISIBLE);
                holder.binding.txtCount.setVisibility(View.VISIBLE);
                holder.binding.pbCount.setVisibility(View.GONE);
                holder.binding.txtCount.setText("" + bean.getQuantity());
            } else {
                holder.binding.rlAdd.setVisibility(View.VISIBLE);
                holder.binding.llAddRemove.setVisibility(View.GONE);
            }

            holder.binding.rlAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.binding.rlAdd.setVisibility(View.GONE);
//                    holder.binding.llAddRemove.setVisibility(View.VISIBLE);
//                    holder.binding.txtCount.setVisibility(View.GONE);
//                    holder.binding.pbCount.setVisibility(View.VISIBLE);
                    Utils.vibrateOnClick(mContext);
                    callBack.addClick(false, catPosition, 0, position);
                }
            });

            holder.binding.imbAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.vibrateOnClick(mContext);
                    callBack.addClick(false, catPosition, 0, position);
                }
            });

            holder.binding.imbRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.vibrateOnClick(mContext);
                    callBack.removeClick(false, catPosition, 0, position);
                }
            });

        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private HorizontalItemsListRowBinding binding;

        public MyViewHolder(@NonNull HorizontalItemsListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
