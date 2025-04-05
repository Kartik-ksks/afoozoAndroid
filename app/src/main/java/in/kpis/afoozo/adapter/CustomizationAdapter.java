package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CustomizationBean;
import com.kpis.afoozo.databinding.CustomizationLayoutBinding;
import in.kpis.afoozo.interfaces.CustomizationClick;

import java.util.ArrayList;

public class CustomizationAdapter extends RecyclerView.Adapter<CustomizationAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<CustomizationBean> list;
    private RecyclerView.LayoutManager mLayoutManager;
    private CustomizationClick callBack;

    private CustListAdapter mAdapter;

    public CustomizationAdapter(Context mContext, ArrayList<CustomizationBean> list, CustomizationClick callBack){
        this.mContext = mContext;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public CustomizationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        CustomizationLayoutBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.customization_layout, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomizationAdapter.MyViewHolder holder, int position) {
        CustomizationBean bean = list.get(position);

        holder.binding.txtTitle.setText(bean.getTitle());


        if (bean.getCustomizationOptions() != null && bean.getCustomizationOptions().size()> 0){
            mLayoutManager = new LinearLayoutManager(mContext);
            holder.binding.rvCusListRow.setLayoutManager(mLayoutManager);
            holder.binding.rvCusListRow.setItemAnimator(new DefaultItemAnimator());

            mAdapter = new CustListAdapter(mContext, bean.getCustomizationOptions(), bean.getCustomizationType(), position, callBack);

            holder.binding.rvCusListRow.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomizationLayoutBinding binding;

        public MyViewHolder(final CustomizationLayoutBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
