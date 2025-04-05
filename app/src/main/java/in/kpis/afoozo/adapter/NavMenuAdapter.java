package in.kpis.afoozo.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.NavMenuItemBinding;


/**
 * Created by Nss Solutions on 17-11-2016.
 */

public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.DrawerViewHolder> {

    private String[] drawerMenuList;
    private Context mContext;

    public NavMenuAdapter(Context mContext, String[] drawerMenuList) {
        this.mContext = mContext;
        this.drawerMenuList = drawerMenuList;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NavMenuItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.nav_menu_item, parent, false);
        return new DrawerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        String menuNmae = drawerMenuList[position];
        holder.binding.txtNavItem.setText(menuNmae);
        if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.home))) {
            holder.binding.topBorder.setVisibility(View.VISIBLE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_home);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.reservation))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_reservation);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.events))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_calendar);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.notification))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_notification);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.help_and_support))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_help_and_support);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.check_in))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_check_in);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.wallet_money))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_wallet);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.order_history))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_order_history);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.bill_to_company_orders))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_order_history);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.live_orders))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_live_orders);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.prepaid_card))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_prepaid_card);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.offers_rewards))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_offers);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.locations))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_location);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.feedback))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_feedback);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.about_app))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_about_app);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.terms_conditions))) {
            holder.binding.topBorder.setVisibility(View.GONE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_terms);
        } else if (menuNmae.equalsIgnoreCase(mContext.getResources().getString(R.string.live_chat))) {
            holder.binding.topBorder.setVisibility(View.VISIBLE);
            holder.binding.imvNavItemIcon.setImageResource(R.drawable.ic_help_and_support);
        }

    }

    @Override
    public int getItemCount() {
        return drawerMenuList.length;
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {

        private NavMenuItemBinding binding;

        public DrawerViewHolder(final NavMenuItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}
