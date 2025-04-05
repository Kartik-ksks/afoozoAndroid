package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.CuisineListRowBinding;

import java.util.ArrayList;

import in.kpis.afoozo.bean.CuisineBean;
import in.kpis.afoozo.util.Utils;


public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CuisineBean> list;
    private boolean isFromHome;

    public CuisineAdapter(Context mContext, ArrayList<CuisineBean> list, boolean isFromHome){
        this.mContext = mContext;
        this.list = list;
        this.isFromHome = isFromHome;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        int width = parent.getMeasuredWidth() / 4;
//        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(width, width);
//        lp.setMargins(0, 0, 0, 0);
//        parent.setLayoutParams(lp);

        CuisineListRowBinding bindign = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cuisine_list_row, parent, false);

        return new MyViewHolder(bindign);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CuisineBean bean = list.get(position);

        try {
            Utils.setImage(mContext, holder.binding.imvCuisine, bean.getCuisineImageUrl());
            holder.binding.txtTitle.setText(bean.getCuisineTitle());
            if (isFromHome){
               if (bean.isSelected()) {
                   holder.binding.imvTrans.setVisibility(View.VISIBLE);
                   holder.binding.imvCuisine.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                   holder.binding.txtTitle.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
               } else {
                   holder.binding.imvTrans.setVisibility(View.GONE);
                   holder.binding.imvCuisine.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                   holder.binding.txtTitle.setTextColor(mContext.getResources().getColor(R.color.black));
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

        private CuisineListRowBinding binding;

        public MyViewHolder(final CuisineListRowBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
