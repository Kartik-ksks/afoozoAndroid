package in.kpis.afoozo.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.StealDealCartAdapter;
import in.kpis.afoozo.bean.ReserveRequestBean;
import in.kpis.afoozo.bean.StealDealCartBean;
import com.kpis.afoozo.databinding.ActivityStealDealCartBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.sqlite.DatabaseHelper;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StealDealCartActi extends AppCompatActivity implements AddRemoveClick {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityStealDealCartBinding binding;

    private DatabaseHelper db;

    private Dialog alert;
    private ViewDataBinding reserveBinding;

    ArrayList<StealDealCartBean> cartList = new ArrayList<>();
    private StealDealCartAdapter mAdapter;

    private static final int WALLET_REQUEST = 222;

    private double totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_steal_deal_cart);

        mContext = StealDealCartActi.this;
        db = new DatabaseHelper(mContext);
        initialize();
    }

    private void initialize(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.toolbar.activityTitle.setText(getString(R.string.checkout));

        binding.rvItems.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvItems.setItemAnimator(new DefaultItemAnimator());

        setData();
    }

    private void setData() {
        cartList = db.getStealDealItems();

        if (cartList != null && cartList.size() > 0){
            for (StealDealCartBean sdcb: cartList){
                totalAmount = totalAmount + (sdcb.getPrice() * sdcb.getStealDealItemQty());
            }
            binding.llData.setVisibility(View.VISIBLE);
            binding.imvNoData.setVisibility(View.GONE);

            mAdapter = new StealDealCartAdapter(mContext, cartList, this);
            binding.rvItems.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            binding.llData.setVisibility(View.GONE);
            binding.imvNoData.setVisibility(View.VISIBLE);
        }

        binding.txtTotal.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(totalAmount));
    }

    @Override
    public void addClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        int qty = cartList.get(itemPosition).getStealDealItemQty();
        db.updateStealDealItemQty(cartList.get(itemPosition).getStealDealItemId(), ++qty);
        cartList.get(itemPosition).setStealDealItemQty(qty);

        totalAmount = totalAmount + cartList.get(itemPosition).getPrice();
        binding.txtTotal.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(totalAmount));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        int qty = cartList.get(itemPosition).getStealDealItemQty();
        if (qty == 1) {
            db.deleteStealDealItem(cartList.get(itemPosition).getStealDealItemId());
            totalAmount = totalAmount - cartList.get(itemPosition).getPrice();
            cartList.remove(itemPosition);
        } else {
            db.updateStealDealItemQty(cartList.get(itemPosition).getStealDealItemId(), --qty);
            cartList.get(itemPosition).setStealDealItemQty(qty);
            totalAmount = totalAmount - cartList.get(itemPosition).getPrice();
        }

        if (cartList.size() == 0){
            binding.llData.setVisibility(View.GONE);
            binding.imvNoData.setVisibility(View.VISIBLE);
        }

        binding.txtTotal.setText(getString(R.string.Rs) + new DecimalFormat("0.00").format(totalAmount));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void editClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition) {

    }

    @Override
    public void stickyClick(int position) {

    }

    @Override
    public void scrollCallback() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WALLET_REQUEST && resultCode == RESULT_OK){
            callReserveItemApi();
        }
    }

    public void reserveProcess(View view){
        if (binding.cbTerms.isChecked()) {
            callReserveItemApi();
        }else
            Toast.makeText(mContext, getString(R.string.please_check_terms_and_conditions_first), Toast.LENGTH_SHORT).show();
    }

    private void callReserveItemApi() {

        ReserveRequestBean requestBean = new ReserveRequestBean();
        requestBean.setRestaurantId(cartList.get(0).getRestaurantId());
        requestBean.setStealDealItemIdQtyList(cartList);

        String json = new Gson().toJson(requestBean);

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                db.deleteAllStealDealItems();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });

                    } else {
                        if (responseClass.getErrorCode() == 12){
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.your_balance_is_low), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.dismissRetryAlert();
                                    Intent intent = new Intent(mContext, WalletActi.class);
                                    intent.putExtra(Constants.PAYMENT_AMOUNT, totalAmount);
                                    intent.putExtra(Constants.IS_FROM_HOME, false);
                                    startActivityForResult(intent, WALLET_REQUEST);
                                }
                            });
                        } else
                            Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESERVE_STEAL_DEAL_ITEMS, json, "Loading...", true, AppUrls.REQUESTTAG_RESERVESTEALDEALITEM );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
