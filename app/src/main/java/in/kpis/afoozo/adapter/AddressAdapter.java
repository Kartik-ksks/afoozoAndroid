package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.AddressBean;
import com.kpis.afoozo.databinding.AddressListRowBinding;
import in.kpis.afoozo.interfaces.AddressListClick;

import java.util.ArrayList;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<AddressBean> list;
    private AddressListClick callBack;

    public AddressAdapter(Context mContext, ArrayList<AddressBean> list, AddressListClick callBack){
        this.mContext = mContext;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddressListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.address_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddressBean bean = list.get(position);

        try {
            holder.binding.txtType.setText(bean.getAddressType());
            holder.binding.txtAddress.setText(bean.getAddressLine1());

            holder.binding.imvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onClickDelete(position);
                }
            });
            holder.binding.llAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onClickLayout(position);
                }
            });
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AddressListRowBinding binding;

        public MyViewHolder(final AddressListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }
}
