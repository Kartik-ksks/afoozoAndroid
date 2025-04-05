package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.SavedCardBean;
import com.kpis.afoozo.databinding.SavedCardListRowBinding;

import java.util.ArrayList;


public class SavedCardAdapter extends RecyclerView.Adapter<SavedCardAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<SavedCardBean> list;

    public SavedCardAdapter(Context mContext, ArrayList<SavedCardBean> list){
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SavedCardListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.saved_card_list_row, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SavedCardBean bean = list.get(position);

        try {
            if(bean.getNetwork() != null) {
                if(bean.getNetwork().equalsIgnoreCase("visa")){
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_visa);
                }else if(bean.getNetwork().equalsIgnoreCase("mastercard")){
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_master_card);
                }else if(bean.getNetwork().equalsIgnoreCase("diners")){
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_diners_card);
                }else if(bean.getNetwork().equalsIgnoreCase("amex")){
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_amex);
                }else if(bean.getNetwork().equalsIgnoreCase("jcb")){
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_jcb);
                }else{
                    holder.binding.imvCardNetwork.setImageResource(R.drawable.ic_card);

                }
            }
            holder.binding.txtCardNumber.setText("xxxx xxxx xxxx "+bean.getLast4());
            holder.binding.txtCardNetwork.setText(bean.getNetwork());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private SavedCardListRowBinding binding;

        public MyViewHolder(final SavedCardListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }
}
