package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RatingBar;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.OrderDetailsItemBean;
import com.kpis.afoozo.databinding.RatingListRowBinding;

import java.util.ArrayList;


public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<OrderDetailsItemBean> list;

    public RatingAdapter(Context mContext, ArrayList<OrderDetailsItemBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RatingListRowBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rating_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        OrderDetailsItemBean bean = list.get(position);

        holder.binding.txtItem.setText(bean.getTitle());
        holder.binding.rbRatingList.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (rating < 1.0f) {
//                    ratingBar.setRating(1);
//                    list.get(position).setRatingUserToDish(1);
//                } else
                    list.get(position).setRatingUserToDish((int)rating);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RatingListRowBinding binding;

        public MyViewHolder(final RatingListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
