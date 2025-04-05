package in.kpis.afoozo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.EventsBean;
import com.kpis.afoozo.databinding.EventsHeaderLayoutBinding;
import com.kpis.afoozo.databinding.EventsItemsListRowBinding;
import in.kpis.afoozo.interfaces.EventListClick;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.StickHeaderItemDecoration;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> implements StickHeaderItemDecoration.StickyHeaderInterface {
    private Context mContext;
    private boolean isFuture;
    private ArrayList<EventsBean> list;
    private EventListClick callBack;

    public EventsAdapter(Context mContext, boolean isFuture, ArrayList<EventsBean> list, EventListClick callBack){
        this.mContext = mContext;
        this.isFuture = isFuture;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0){
            EventsHeaderLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.events_header_layout, parent, false);
            return new MyViewHolder(binding);
        } else {
            EventsItemsListRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.events_items_list_row, parent, false);
            return new MyViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EventsBean bean = list.get(position);
        try {
            if (bean.isHeading())
                holder.headingBinding.txtHeading.setText(bean.getHeading());
            else {
                holder.itemBinding.txtTitle.setText(bean.getOccasionTitle());

                if (!TextUtils.isEmpty(bean.getOccasionImageUrl()))
                    Utils.setImage(mContext, holder.itemBinding.imvEvent, holder.itemBinding.pbEvents, bean.getOccasionImageUrl());

                if (!TextUtils.isEmpty(bean.getOccasionAddress()))
                    holder.itemBinding.txtAddress.setText(bean.getOccasionAddress());

                holder.itemBinding.txtDate.setText(Utils.getDateFromTimeStamp(bean.getStartDateTimeStamp()));
                holder.itemBinding.txtYear.setText(Utils.getYearFromTimeStamp(bean.getStartDateTimeStamp()));
                holder.itemBinding.txtMonth.setText(bean.getMonth());

                holder.itemBinding.txtStartTime.setText(mContext.getString(R.string.start_time) + " " + Utils.getFormetedTimeForEvent(bean.getStartDateTimeStamp()));

                holder.itemBinding.cvEvents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.eventListClick(position);
                    }
                });

                if (isFuture) {
                    holder.itemBinding.imvAddEvent.setVisibility(View.VISIBLE);
                    holder.itemBinding.imvAddEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callBack.addEventClick(position);
                        }
                    });
                } else
                    holder.itemBinding.imvAddEvent.setVisibility(View.GONE);

            }
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).isHeading())? 0 : 1;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.events_header_layout;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        TextView tv = header.findViewById(R.id.txtHeading);
        if(list.size() > 0)
            tv.setText(list.get(headerPosition).getHeading());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        if(list.size() > 0) {
            if (list.get(itemPosition).isHeading())
                return true;
            else
                return false;
        }
        return false;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private EventsHeaderLayoutBinding headingBinding;
        private EventsItemsListRowBinding itemBinding;

        public MyViewHolder(final EventsItemsListRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public MyViewHolder(final EventsHeaderLayoutBinding headingBinding) {
            super(headingBinding.getRoot());
            this.headingBinding = headingBinding;
        }
    }
}
