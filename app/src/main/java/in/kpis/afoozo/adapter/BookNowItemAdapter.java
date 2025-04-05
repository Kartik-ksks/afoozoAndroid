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
import in.kpis.afoozo.bean.BookNowItemsBean;
import com.kpis.afoozo.databinding.BookNowItemRowBinding;
import in.kpis.afoozo.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookNowItemAdapter extends RecyclerView.Adapter<BookNowItemAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<BookNowItemsBean> list;

    public BookNowItemAdapter(Context mContext, ArrayList<BookNowItemsBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookNowItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.book_now_item_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookNowItemsBean bean = list.get(position);
        try {
            holder.binding.txtTitle.setText(bean.getTitle());
            if (!TextUtils.isEmpty(bean.getSticker())) {
                holder.binding.txtOffer.setVisibility(View.VISIBLE);
                holder.binding.txtOffer.setText(bean.getSticker());
            } else
                holder.binding.txtOffer.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(bean.getImageUrl())) {
                Utils.setImage(mContext, holder.binding.imvItem, holder.binding.pbBN, bean.getImageUrl());
                holder.binding.imvItem.setVisibility(View.VISIBLE);
            }else {
                holder.binding.imvItem.setVisibility(View.INVISIBLE);
                holder.binding.pbBN.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(bean.getBackgroundImageUrl())) {
                Utils.setImage(mContext, holder.binding.imvBackground, holder.binding.pbBN, bean.getBackgroundImageUrl());
                holder.binding.imvBackground.setVisibility(View.VISIBLE);
            } else
                holder.binding.imvBackground.setVisibility(View.GONE);

            holder.binding.txtPrice.setText(mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()));
//            holder.binding.txtMrp.setText(mContext.getString(R.string.Rs) + " 200");
//            holder.binding.txtMrp.setPaintFlags(holder.binding.txtMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private BookNowItemRowBinding binding;

        public MyViewHolder(@NonNull final BookNowItemRowBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}
