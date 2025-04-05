package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.NotiSettingBean;
import com.kpis.afoozo.databinding.SettingListRowBinding;

import java.util.ArrayList;

public class NotiSettingAdapter extends RecyclerView.Adapter<NotiSettingAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NotiSettingBean> list;

    public NotiSettingAdapter(Context mContext, ArrayList<NotiSettingBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SettingListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.setting_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotiSettingBean bean = list.get(position);
        try {
            holder.binding.txtTitle.setText(bean.getRestaurantName());
            if (bean.isOnFlag())
                holder.binding.switchSetting.setChecked(true);
            else
                holder.binding.switchSetting.setChecked(false);

            holder.binding.switchSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setOnFlag(isChecked);
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

        private SettingListRowBinding binding;

        public MyViewHolder(@NonNull SettingListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
