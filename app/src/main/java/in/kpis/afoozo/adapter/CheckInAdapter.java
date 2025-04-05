package in.kpis.afoozo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.CategoryListRowBinding;
import com.kpis.afoozo.databinding.CheckInListRowBinding;

import java.util.ArrayList;

import in.kpis.afoozo.bean.CheckListBean;
import in.kpis.afoozo.util.Utils;

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<CheckListBean> checkList;
    public CheckInAdapter(Context mContext, ArrayList<CheckListBean> checkList) {
        this.mContext = mContext;
        this.checkList = checkList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckInListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.check_in_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            CheckListBean bean = checkList.get(position);
            holder.binding.txtTitle.setText(bean.getRestaurantName());
            holder.binding.txtRoomType.setText(mContext.getString(R.string.room_type_)+ " "+bean.getRoomType());
            holder.binding.txtRoomNumber.setText(mContext.getString(R.string.room_number)+ " "+bean.getRoomNumber());
            holder.binding.txtStatus.setText(mContext.getString(R.string.status_)+ " "+bean.getBookingStatus());
            holder.binding.txtTotal.setText(mContext.getString(R.string.Rs)+ " "+bean.getBookingTotal());
            holder.binding.txtTotal.setText(mContext.getString(R.string.Rs)+ " "+bean.getBookingTotal());
            holder.binding.txtCheckInDate.setText(mContext.getString(R.string.check_in)+" : "+Utils.getDateTimeForOrders(bean.getCheckInDateTimeStamp()));
            holder.binding.txtCheckOutDate.setText(mContext.getString(R.string.checkout)+" : "+Utils.getDateTimeForOrders(bean.getCheckOutDateTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return checkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckInListRowBinding binding;
        public MyViewHolder(@NonNull CheckInListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
