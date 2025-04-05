package in.kpis.afoozo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kpis.afoozo.R;

import in.kpis.afoozo.activity.LiveOrderActi;
import in.kpis.afoozo.bean.OrdersBean;
import com.kpis.afoozo.databinding.OrderListRowBinding;
import in.kpis.afoozo.interfaces.OrderInterface;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private boolean isLive;
    private boolean isStealDeal;
    private Context mContext;
    private ArrayList<OrdersBean> list;

    public OrderInterface callBack;

    public OrdersAdapter(Context mContext, boolean isLive, boolean isStealDeal, ArrayList<OrdersBean> list, OrderInterface callBack){
        this.mContext = mContext;
        this.isLive = isLive;
        this.isStealDeal = isStealDeal;
        this.list = list;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.order_list_row, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        OrdersBean bean = list.get(position);
        try {
            holder.binding.txtTitle.setText(bean.getRestaurantName());

            if (isStealDeal){
                holder.binding.imvType.setVisibility(View.GONE);
                holder.binding.txtTotal.setText(bean.getBuyingQty() + bean.getConsumableUnitPostFix());
                holder.binding.txtItemName.setText(bean.getItemName());
            } else {
                holder.binding.imvType.setVisibility(View.VISIBLE);
                holder.binding.txtTotal.setText(mContext.getString(R.string.Rs) + bean.getOrderTotal());
                holder.binding.txtItemName.setText(bean.getOrderItemText());

                if (bean.getOrderType().equalsIgnoreCase(Constants.TAKE_AWAY))
                    holder.binding.imvType.setImageDrawable(mContext.getDrawable(R.drawable.ic_pickup));
                else if (bean.getOrderType().equalsIgnoreCase(Constants.HOME_DELIVERY))
                    holder.binding.imvType.setImageDrawable(mContext.getDrawable(R.drawable.ic_delivery));
                else if (bean.getOrderType().equalsIgnoreCase(Constants.DINE_IN)) {
                    holder.binding.imvType.setImageDrawable(mContext.getDrawable(R.drawable.ic_dine_in));
                }
            }
            holder.binding.txtTime.setText(Utils.getDateTimeForOrders(bean.getOrderDateTime()));

            if (!TextUtils.isEmpty(bean.getDeliveryAddress())){
                holder.binding.txtAddress.setText(bean.getDeliveryAddress());
                holder.binding.txtAddress.setVisibility(View.VISIBLE);
            } else
                holder.binding.txtAddress.setVisibility(View.GONE);


            if (isLive) {
                if (bean.getOrderType().equalsIgnoreCase(Constants.DINE_IN) && !bean.getOrderStatus().equals("Ordered") && !bean.getOrderStatus().equals("Preparing")) {
                    holder.binding.btnClearTable.setVisibility(View.VISIBLE);
                }
                holder.binding.btnReOrder.setVisibility(View.GONE);
                holder.binding.btnFeedback.setVisibility(View.GONE);
                holder.binding.btnDriverTip.setVisibility(View.GONE);
            } else {
                if (bean.getOrderType().equals(Constants.STEAL_DEAL))
                    holder.binding.btnReOrder.setVisibility(View.GONE);
                else
                    holder.binding.btnReOrder.setVisibility(View.VISIBLE);

                if (bean.getOrderType().equals(Constants.HOME_DELIVERY))
                    holder.binding.btnDriverTip.setText(mContext.getString(R.string.tip_rider));
                else if  (bean.getOrderType().equals(Constants.DINE_IN))
                    holder.binding.btnDriverTip.setText(mContext.getString(R.string.tip_server));
                else if (bean.getOrderType().equals(Constants.TAKE_AWAY) || bean.getOrderType().equals(Constants.CAFE)){
                    holder.binding.btnDriverTip.setVisibility(View.GONE);
                }

                if (!bean.isDriverRated()) {
                    holder.binding.btnFeedback.setVisibility(View.VISIBLE);
                    holder.binding.btnFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callBack.feedbackClick(position);
                        }
                    });
                } else
                    holder.binding.btnFeedback.setVisibility(View.GONE);
                holder.binding.btnReOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.reOrderClick(position);
                    }
                });

                if (bean.getTipAmount() > 0)
                    holder.binding.btnDriverTip.setVisibility(View.GONE);
                else {
                       if (bean.getOrderType().equals(Constants.TAKE_AWAY) || bean.getOrderType().equals(Constants.CAFE)){
                        holder.binding.btnDriverTip.setVisibility(View.GONE);
                    }else
                    holder.binding.btnDriverTip.setVisibility(View.VISIBLE);
                    holder.binding.btnDriverTip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callBack.tipRiderClick(position);
                        }
                    });
                }


            }

            holder.binding.llHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.layoutClick(position);
                }
            });



        } catch (Exception e){

        }

        holder.binding.btnClearTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.tableClearClick(bean.getOrderReferenceId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private OrderListRowBinding binding;

        public MyViewHolder(@NonNull final OrderListRowBinding itemBinding) {
            super(itemBinding.getRoot());

            binding = itemBinding;
        }
    }
}
