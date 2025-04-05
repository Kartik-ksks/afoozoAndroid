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
import in.kpis.afoozo.bean.BookNowBean;
import com.kpis.afoozo.databinding.BookNowListRowBinding;
import com.kpis.afoozo.databinding.EventsHeaderLayoutBinding;
import in.kpis.afoozo.util.Utils;

import java.util.ArrayList;

public class BookNowAdapter extends RecyclerView.Adapter<BookNowAdapter.MyViewHolder> {

    private boolean isConsume;
    private Context mContext;
    private ArrayList<BookNowBean> list;

    public BookNowAdapter(Context mContext, ArrayList<BookNowBean> list, boolean isConsume){
        this.mContext = mContext;
        this.list = list;
        this.isConsume = isConsume;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BookNowListRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.book_now_list_row, parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookNowBean bean = list.get(position);
        try {
            holder.itemBinding.txtTitle.setText(bean.getTitle());

            if (!TextUtils.isEmpty(bean.getCategoryIconUrl()))
                Utils.setImage(mContext, holder.itemBinding.imvItem, holder.itemBinding.pbBN, bean.getCategoryIconUrl());

            if (isConsume)
                holder.itemBinding.imvReward.setVisibility(View.VISIBLE);
            else
                holder.itemBinding.imvReward.setVisibility(View.GONE);

        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private EventsHeaderLayoutBinding headerBinding;
        private BookNowListRowBinding itemBinding;

        public MyViewHolder(@NonNull final EventsHeaderLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            headerBinding = itemBinding;
        }

        public MyViewHolder(@NonNull final BookNowListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
