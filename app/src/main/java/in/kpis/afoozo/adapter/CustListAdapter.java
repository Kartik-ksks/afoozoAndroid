package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.CustomizationOptionsBean;
import com.kpis.afoozo.databinding.CustomizationListRowBinding;
import in.kpis.afoozo.interfaces.CustomizationClick;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustListAdapter extends RecyclerView.Adapter<CustListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CustomizationOptionsBean> list;
    private int catPosition;
    private String type;
    private CustomizationClick callBack;

    public CustListAdapter(Context mContext, ArrayList<CustomizationOptionsBean> list, String type, int catPosition, CustomizationClick callBack){
        this.mContext = mContext;
        this.list = list;
        this.type = type;
        this.catPosition = catPosition;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public CustListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        CustomizationListRowBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.customization_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustListAdapter.MyViewHolder holder, final int position) {
        CustomizationOptionsBean bean = list.get(position);


        if (type.equals("SingleSelection")){
            holder.binding.rbCustomization.setVisibility(View.VISIBLE);

            if (bean.isAdded()){
                if (!holder.binding.rbCustomization.isChecked())
                    holder.binding.rbCustomization.setChecked(true);
            } else {
                if (holder.binding.rbCustomization.isChecked())
                    holder.binding.rbCustomization.setChecked(false);
            }

            holder.binding.txtTitle.setText(bean.getName() + "  ( +" + mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()) + " )");

            holder.binding.llCustomization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onRadioButtonClick(catPosition, position);
                }
            });
        } else {
            holder.binding.cbCustomization.setVisibility(View.VISIBLE);

            if (bean.isAdded()){
                if (!holder.binding.cbCustomization.isChecked())
                    holder.binding.cbCustomization.setChecked(true);
            } else {
                if (holder.binding.cbCustomization.isChecked())
                    holder.binding.cbCustomization.setChecked(false);
            }

            holder.binding.txtTitle.setText(bean.getName() + "  ( +" + mContext.getString(R.string.Rs) + new DecimalFormat("0.00").format(bean.getPrice()) + " )");

            holder.binding.llCustomization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                callBack.onCheckBoxClick(catPosition, position);
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CustomizationListRowBinding binding;

        public MyViewHolder(final CustomizationListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            this.binding = itemBinding;
        }
    }
}
