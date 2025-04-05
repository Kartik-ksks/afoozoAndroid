package in.kpis.afoozo.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.MenuListBean;
import com.kpis.afoozo.databinding.HorizontalMainLayoutBinding;
import com.kpis.afoozo.databinding.MenuItemsListRowBinding;
import com.kpis.afoozo.databinding.SmallItemsListRowBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenuListBean> list;
    private int catPosition;
    private int subCatPosition;
    private String categoryName;
    private boolean isSubCategory;
    public AddRemoveClick callback;

    public MenuItemsAdapter(Context mContext, ArrayList<MenuListBean> list, int catPosition, AddRemoveClick callback) {
        this.mContext = mContext;
        this.list = list;
        this.catPosition = catPosition;
        this.callback = callback;
    }

    public MenuItemsAdapter(Context mContext, boolean isSubCategory, ArrayList<MenuListBean> list, int catPosition, int subCatPosition, AddRemoveClick callback) {
        this.mContext = mContext;
        this.list = list;
        this.isSubCategory = isSubCategory;
        this.catPosition = catPosition;
        this.subCatPosition = subCatPosition;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            MenuItemsListRowBinding itemsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.menu_items_list_row, parent, false);
            return new MyViewHolder(itemsBinding);
        } else if (viewType == 1) {
            SmallItemsListRowBinding smallBindig = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.small_items_list_row, parent, false);
            return new MyViewHolder(smallBindig);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        MenuListBean bean = list.get(position);
        try {
            if (bean.getImageSize().equalsIgnoreCase("BIG")){
                holder.itemsBinding.shimmerAddButton.startShimmer();
                if (!TextUtils.isEmpty(bean.getSubCategoryTitle()))
                    holder.itemsBinding.txtSubCat.setText(bean.getSubCategoryTitle());
                else
                    holder.itemsBinding.txtSubCat.setText("");

                holder.itemsBinding.txtTitle.setText(bean.getTitle());
                if(bean.getRating() == 00)
                    holder.itemsBinding.txtRating.setText("5.0");
                else
                    holder.itemsBinding.txtRating.setText(new DecimalFormat("0.0").format(bean.getRating()));
                holder.itemsBinding.txtDesc.setText(bean.getDescription());

                if (!TextUtils.isEmpty(bean.getSticker())){
                    holder.itemsBinding.txtOffer.setVisibility(View.VISIBLE);
                    holder.itemsBinding.txtOffer.setText(bean.getSticker());
                } else
                    holder.itemsBinding.txtOffer.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(bean.getItemImageUrlThumbnail())) {
                    holder.itemsBinding.imvImage.setVisibility(View.VISIBLE);
                    holder.itemsBinding.rlBigImage.setVisibility(View.VISIBLE);
                    Utils.setImage(mContext, holder.itemsBinding.imvImage, holder.itemsBinding.pbMenu, bean.getItemImageUrlThumbnail());
                } else
                    holder.itemsBinding.rlBigImage.setVisibility(View.GONE);

                holder.itemsBinding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getFinalPrice()));

                if (bean.getPrice() > 0 && bean.getPrice() != bean.getFinalPrice()) {
                    holder.itemsBinding.txtMrp.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()));
                    holder.itemsBinding.txtMrp.setPaintFlags(holder.itemsBinding.txtMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.itemsBinding.txtMrp.setVisibility(View.VISIBLE);
                } else
                    holder.itemsBinding.txtMrp.setVisibility(View.GONE);

                if (bean.getCustomization() != null && bean.getCustomization().size() > 0)
                    holder.itemsBinding.txtCustomizable.setVisibility(View.VISIBLE);
                else
                    holder.itemsBinding.txtCustomizable.setVisibility(View.GONE);



                if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
                    holder.itemsBinding.imvVegNonVeg.setImageResource(R.drawable.ic_veg);
                    holder.itemsBinding.imvVegNonVeg.setVisibility(View.VISIBLE);
                } else if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                    holder.itemsBinding.imvVegNonVeg.setImageResource(R.drawable.ic_non_veg_icon);
                    holder.itemsBinding.imvVegNonVeg.setVisibility(View.VISIBLE);
                } else{
                    holder.itemsBinding.imvVegNonVeg.setVisibility(View.GONE);
                }

                if (bean.getQuantity() > 0){
                    holder.itemsBinding.rlAdd.setVisibility(View.GONE);
                    holder.itemsBinding.llAddRemove.setVisibility(View.VISIBLE);
                    holder.itemsBinding.txtCount.setVisibility(View.VISIBLE);
                    holder.itemsBinding.pbCount.setVisibility(View.GONE);
                    holder.itemsBinding.txtCount.setText("" + bean.getQuantity());
                    holder.itemsBinding.imvEdit.setVisibility(View.VISIBLE);
                } else {
                    holder.itemsBinding.rlAdd.setVisibility(View.VISIBLE);
                    holder.itemsBinding.llAddRemove.setVisibility(View.GONE);
                    holder.itemsBinding.imvEdit.setVisibility(View.GONE);
                }

                holder.itemsBinding.viewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        holder.itemsBinding.rlAdd.setVisibility(View.GONE);
//                        holder.itemsBinding.llAddRemove.setVisibility(View.VISIBLE);
//                        holder.itemsBinding.txtCount.setVisibility(View.GONE);
//                        holder.itemsBinding.pbCount.setVisibility(View.VISIBLE);
                        Utils.vibrateOnClick(mContext);
                        callback.addClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.itemsBinding.imbAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.addClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.itemsBinding.imbRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.removeClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.itemsBinding.imvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.editClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

            } else if (bean.getImageSize().equalsIgnoreCase("SMALL")){
                holder.smallBindig.shimmerAddButton.startShimmer();
                if (!TextUtils.isEmpty(bean.getSubCategoryTitle()))
                    holder.smallBindig.txtSubCat.setText(bean.getSubCategoryTitle());
                else
                    holder.smallBindig.txtSubCat.setText("");

                holder.smallBindig.txtTitle.setText(bean.getTitle());
                if(bean.getRating() == 0)
                    holder.smallBindig.txtRating.setText("5.0");
                else
                    holder.smallBindig.txtRating.setText(new DecimalFormat("0.0").format(bean.getRating()));

                holder.smallBindig.txtDesc.setText(bean.getDescription());

                if (!TextUtils.isEmpty(bean.getItemImageUrlThumbnail())) {
                    holder.smallBindig.imvImage.setVisibility(View.VISIBLE);
                    holder.smallBindig.rLSmallImage.setVisibility(View.VISIBLE);
                    Utils.setImage(mContext, holder.smallBindig.imvImage, holder.smallBindig.pbMenu, bean.getItemImageUrlThumbnail());
                } else
                    holder.smallBindig.rLSmallImage.setVisibility(View.GONE);

//                if (!TextUtils.isEmpty(bean.getVegNonVeg()))
//                    holder.smallBindig.imvVegNonVeg.setImageResource(bean.getVegNonVeg().equalsIgnoreCase("Veg")? R.drawable.ic_veg : R.drawable.ic_non_veg_icon);
//                else
//                    holder.smallBindig.imvVegNonVeg.setImageResource(R.drawable.ic_veg);

                if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("Veg")) {
                    holder.smallBindig.imvVegNonVeg.setImageResource(R.drawable.ic_veg);
                    holder.smallBindig.imvVegNonVeg.setVisibility(View.VISIBLE);
                } else if (!TextUtils.isEmpty(bean.getVegNonVeg()) && bean.getVegNonVeg().equalsIgnoreCase("NonVeg")) {
                    holder.smallBindig.imvVegNonVeg.setImageResource(R.drawable.ic_non_veg_icon);
                    holder.smallBindig.imvVegNonVeg.setVisibility(View.VISIBLE);
                } else{
                    holder.smallBindig.imvVegNonVeg.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(bean.getSticker())){
                    holder.smallBindig.txtSticker.setVisibility(View.VISIBLE);
                    holder.smallBindig.txtSticker.setText(bean.getSticker());
                } else
                    holder.smallBindig.txtSticker.setVisibility(View.GONE);

                holder.smallBindig.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getFinalPrice()));

                if (bean.getPrice() > 0 && bean.getPrice() != bean.getFinalPrice()) {
                    holder.smallBindig.txtMrp.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()));
                    holder.smallBindig.txtMrp.setPaintFlags(holder.smallBindig.txtMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.smallBindig.txtMrp.setVisibility(View.VISIBLE);
                } else {
                    holder.smallBindig.txtMrp.setVisibility(View.GONE);
                }

                if (bean.getCustomization() != null && bean.getCustomization().size() > 0)
                    holder.smallBindig.txtCustomizable.setVisibility(View.VISIBLE);
                else
                    holder.smallBindig.txtCustomizable.setVisibility(View.GONE);

                if (bean.getQuantity() > 0){
                    holder.smallBindig.rlAdd.setVisibility(View.GONE);
                    holder.smallBindig.llAddRemove.setVisibility(View.VISIBLE);
                    holder.smallBindig.txtCount.setVisibility(View.VISIBLE);
                    holder.smallBindig.pbCount.setVisibility(View.GONE);
                    holder.smallBindig.txtCount.setText("" + bean.getQuantity());
                    holder.smallBindig.imvEdit.setVisibility(View.VISIBLE);
                } else {
                    holder.smallBindig.rlAdd.setVisibility(View.VISIBLE);
                    holder.smallBindig.llAddRemove.setVisibility(View.GONE);
                    holder.smallBindig.imvEdit.setVisibility(View.GONE);
                }

                holder.smallBindig.viewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        holder.smallBindig.rlAdd.setVisibility(View.GONE);
//                        holder.smallBindig.llAddRemove.setVisibility(View.VISIBLE);
//                        holder.smallBindig.txtCount.setVisibility(View.GONE);
//                        holder.smallBindig.pbCount.setVisibility(View.VISIBLE);
                        Utils.vibrateOnClick(mContext);
                        callback.addClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.smallBindig.imbAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.addClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.smallBindig.imbRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.removeClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });

                holder.smallBindig.imvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.vibrateOnClick(mContext);
                        callback.editClick(isSubCategory, catPosition, subCatPosition, position);
                    }
                });
            }
        } catch (Exception e){
            Log.i("IOException", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getImageSize().equalsIgnoreCase("SMALL") ? 1 : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private MenuItemsListRowBinding itemsBinding;
        private SmallItemsListRowBinding smallBindig;

        public MyViewHolder(@NonNull final MenuItemsListRowBinding itemsBinding ) {
            super(itemsBinding.getRoot());
            this.itemsBinding = itemsBinding;
        }

        public MyViewHolder(@NonNull final SmallItemsListRowBinding smallBindig ) {
            super(smallBindig.getRoot());
            this.smallBindig = smallBindig;
        }
    }
}
