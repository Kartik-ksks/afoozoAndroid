package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.NotificationListBean;
import com.kpis.afoozo.databinding.NotificationListLayoutBinding;
import in.kpis.afoozo.util.Utils;

import java.util.ArrayList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NotificationListBean> list;

    public NotificationListAdapter(Context mContext, ArrayList<NotificationListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NotificationListLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.notification_list_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotificationListBean bean = list.get(position);
        try{
            holder.binding.txtTitle.setText(bean.getNotificationTitle());
            holder.binding.txtDesc.setText(bean.getNotificationMessage());
            holder.binding.txtTime.setText(Utils.getFormetedTimeForEvent(bean.getCreatedAt()));
            holder.binding.txtDate.setText(Utils.getFormetedDateMonth(bean.getCreatedAt()));
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private NotificationListLayoutBinding binding;

        public MyViewHolder(final NotificationListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
