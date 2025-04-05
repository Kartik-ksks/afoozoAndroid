package in.kpis.afoozo.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityOrderHistoryBillToCompanyBinding;
import com.kpis.afoozo.databinding.ActivityOrderHistoryBinding;
import com.kpis.afoozo.databinding.TipPopupLayoutBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.kpis.afoozo.adapter.OrdersAdapter;
import in.kpis.afoozo.bean.BillToCompanyHistoryBean;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.interfaces.OrderInterface;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class OrderBillToCompanyHistoryActi extends AppCompatActivity implements OrderInterface, View.OnClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityOrderHistoryBillToCompanyBinding binding;

    private int RATE_REQUEST = 101;
    private int WALLET_REQUEST = 102;

    private double tipAmount;

    private String selectedType;
    private String orderId;
    private String tipMessage = "";

    private ArrayList<OrdersBean> list;
    private ArrayList<String> typeList = new ArrayList<>();
    private OrdersAdapter mAdapter;

    private Dialog tipPopUp;
    private BottomSheetBehavior sheetBehavior;
    private String tipUrl;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar finalToStart = Calendar.getInstance();
    private Calendar finalToend = Calendar.getInstance();
    private long startLong;
    private long endLong;
    private BillToCompanyHistoryBean billToCompanyBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_history_bill_to_company);

        if (getIntent().getExtras() != null) {
//            selectedType = getIntent().getStringExtra(Constants.ORDER_TYPE);
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        }
        selectedType = getString(R.string.billToCompany);

        mContext = OrderBillToCompanyHistoryActi.this;
        initialize();
    }

    private void initialize() {
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

        binding.toolbar.activityTitle.setText(getString(R.string.bill_to_company_orders));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.llBottomLayout);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvHistory.setItemAnimator(new DefaultItemAnimator());

        setTypeData();

        binding.spOrders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (typeList != null) {

//                    selectedType = typeList.get(position);
                    callGetOrderHistoryApi();

                } else {
                    callGetOrderHistoryApi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.txtStartDate.setOnClickListener(this);
        binding.txtEndDate.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
//        endLong = 0;
        endLong = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.HOUR_OF_DAY,00);
        cal.set(Calendar.MINUTE,00);

        startLong = cal.getTimeInMillis();
        binding.txtStartDate.setText(Utils.getFormetedDate(startLong));
        binding.txtEndDate.setText(Utils.getFormetedDate(endLong


        ));

        callGetOrderHistoryApi();
        setBottomSheet();

    }

    private void setBottomSheet() {
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        binding.llBottomSheet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_down_arrow));
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        binding.llBottomSheet.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_up_arrow));
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            binding.bottomSheet.txtTipAmount.setVisibility(View.VISIBLE);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            binding.bottomSheet.txtTipAmount.setVisibility(View.GONE);
        }
    }

    public void outerClick(View view) {

    }

    public void add50Process(View view) {
        binding.bottomSheet.txtTipAmount.setText("50");
    }

    public void add100Process(View view) {
        binding.bottomSheet.txtTipAmount.setText("100");
    }

    public void add200Process(View view) {
        binding.bottomSheet.txtTipAmount.setText("200");
    }

    public void add500Process(View view) {
        binding.bottomSheet.txtTipAmount.setText("500");
    }

    public void addTipProcess(View view) {
        String amount = binding.bottomSheet.txtTipAmount.getText().toString().trim();
        if (!TextUtils.isEmpty(amount) && Double.parseDouble(binding.bottomSheet.txtTipAmount.getText().toString()) > 0) {
            tipAmount = Double.parseDouble(binding.bottomSheet.txtTipAmount.getText().toString());
            callTipApi(tipUrl);
            toggleBottomSheet();
        } else
            Utils.showCenterToast(mContext, getString(R.string.please_enter_valid_tip_amount));
    }

    public void skipTipProcess(View view) {
        toggleBottomSheet();
    }

    private void setTypeData() {
        typeList.add(Constants.HOME_DELIVERY);
        typeList.add(Constants.TAKE_AWAY);
        typeList.add(Constants.DINE_IN);
        typeList.add(Constants.STEAL_DEAL);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, typeList);
        cityAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        binding.spOrders.setAdapter(cityAdapter);

        if (!TextUtils.isEmpty(selectedType)) {
            for (int i = 0; i < typeList.size(); i++) {
                if (typeList.get(i).equals(selectedType)) {
                    binding.spOrders.setSelection(i);
                    break;
                }
            }
        }
//            selectedType = Constants.HOME_DELIVERY;
    }

    private void setData() {

        if (list != null && list.size() > 0) {
            binding.txtTotalOrders.setText(getString(R.string.total_orders)+"\n"+list.size());
            binding.txtTotalBtc.setText(getString(R.string.total_btc)+"\n"+(new DecimalFormat("0.00").format(billToCompanyBean.getAmount()-billToCompanyBean.getRemainingamount())) +"/"+new DecimalFormat("0.00").format(billToCompanyBean.getAmount()));
            binding.rvHistory.setVisibility(View.VISIBLE);
            if (selectedType.equals(Constants.STEAL_DEAL))
                mAdapter = new OrdersAdapter(mContext, false, true, list, this);
            else
                mAdapter = new OrdersAdapter(mContext, false, false, list, this);
            binding.rvHistory.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            binding.rvHistory.setVisibility(View.GONE);
        }
    }

    @Override
    public void reOrderClick(int position) {
        String orderType = list.get(position).getOrderType();
        if (orderType.equals(Constants.DINE_IN))
            orderType = mContext.getString(R.string.dine_in);
        else if (orderType.equals(Constants.HOME_DELIVERY))
            orderType = mContext.getString(R.string.delivery);
        else if (orderType.equals(Constants.TAKE_AWAY))
            orderType = mContext.getString(R.string.take_away);
        callReOrderApi(list.get(position).getOrderReferenceId(), orderType);
    }

    @Override
    public void layoutClick(int position) {
        if (!list.get(position).getOrderType().equals(Constants.STEAL_DEAL)) {
            Intent intent = new Intent(mContext, OrderDetailsActi.class);
            intent.putExtra("IsFromTrack", false);
            intent.putExtra(Constants.ORDER_ID, list.get(position).getOrderReferenceId());
            startActivity(intent);
        }
    }

    @Override
    public void feedbackClick(int position) {
        Intent intent = new Intent(mContext, RatingActi.class);
        intent.putExtra(Constants.ORDER_ID, list.get(position).getOrderReferenceId());
        intent.putExtra(Constants.FROM_WHICH, "History");
        startActivityForResult(intent, RATE_REQUEST);
    }

    @Override
    public void tipRiderClick(int position) {
        showTipLayout(position);
    }

    @Override
    public void tableClearClick(String orderId) {

    }

    private void showTipLayout(int position) {
        if (list.get(position).getOrderType() != null && list.get(position).getOrderType().equals(Constants.HOME_DELIVERY)) {
            binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_rider));
            binding.bottomSheet.txtTipMsg.setText(getString(R.string.help__rider_and_his_family_by_adding_a_tip));
            tipUrl = AppUrls.PAY_TIP_ON_ORDER;
        } else {
            binding.bottomSheet.txtTipTitle.setText(getString(R.string.tip_server));
            binding.bottomSheet.txtTipMsg.setText(getString(R.string.help__server_and_his_family_by_adding_a_tip));

            if (list.get(position).getOrderType().equals(Constants.STEAL_DEAL))
                tipUrl = AppUrls.PAY_TIP_ON_STEAL_DEAL_ORDER;
            else
                tipUrl = AppUrls.PAY_TIP_ON_ORDER;
        }

        binding.bottomSheet.txtTipAmount.setText("50");

        orderId = list.get(position).getOrderReferenceId();

        toggleBottomSheet();
    }

    private void showTipPopUp(int position) {
        tipPopUp = new Dialog(mContext);
        tipPopUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tipPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = tipPopUp.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        TipPopupLayoutBinding tipBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.tip_popup_layout, null, false);
        tipPopUp.setContentView(tipBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);
        tipBinding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tipBinding.etAmount.getText().toString()) && Integer.parseInt(tipBinding.etAmount.getText().toString()) > 0) {
                    tipPopUp.dismiss();
                    orderId = list.get(position).getOrderReferenceId();
                    tipAmount = Double.parseDouble(tipBinding.etAmount.getText().toString());
                    tipMessage = tipBinding.etTipMsg.getText().toString();
//                    callTipApi();
                }
            }
        });

        tipPopUp.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        tipPopUp.setCancelable(true);
        tipPopUp.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RATE_REQUEST && resultCode == RESULT_OK) {
            callGetOrderHistoryApi();
        } else if (requestCode == WALLET_REQUEST && resultCode == RESULT_OK) {
            callTipApi(tipUrl);
        }
    }

    private void callGetOrderHistoryApi() {
//        JsonObject json = new JsonObject();
//        try {
//            json.addProperty(Constants.START_DATE,startLong);
//            json.addProperty(Constants.END_DATE,endLong);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            new NetworkManager(BillToCompanyHistoryBean.class, new NetworkManager.OnCallback<BillToCompanyHistoryBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
//                        Type objType = new TypeToken<ResponseClass<ArrayList<OrdersBean>>>() {
//                        }.getType();
//                        ResponseClass<ArrayList<OrdersBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        billToCompanyBean = (BillToCompanyHistoryBean) responseClass.getResponsePacket();

                        list = billToCompanyBean.getList();
                        setData();
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_ORDER_LIST_FOR_BILL_TO_COMPANY+"/"+startLong+"/"+endLong,"", "Loading...", true, AppUrls.REQUESTTAG_GETORDERLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callReOrderApi(String orderId, String orderType) {
        try {
             new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        String generatedId = (String) responseClass.getResponsePacket();

                        Intent intent = new Intent(mContext, CartActi.class);
                        intent.putExtra(Constants.ORDER_ID, generatedId);
                        intent.putExtra(Constants.FROM_WHICH, orderType);
                        startActivity(intent);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.RE_ORDER + orderId, "", "Loading...", true, AppUrls.REQUESTTAG_REORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callTipApi(String url) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.TIP_AMOUNT, tipAmount);
            jsonObject.put(Constants.TIP_MESSAGE, tipMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    } else {
                        if (responseClass.getErrorCode() == 12) {
                            Utils.retryAlertDialog(mContext, getString(R.string.app_name), getString(R.string.your_balance_is_low), getString(R.string.cancel), getString(R.string.Ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.dismissRetryAlert();
                                    Intent intent = new Intent(mContext, WalletActi.class);
                                    intent.putExtra(Constants.PAYMENT_AMOUNT, tipAmount);
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
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, url + orderId, jsonObject.toString(), "Loading...", true, AppUrls.REQUESTTAG_PAYTIPONORDER);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtStartDate:
                selectDate(1);
                break;
            case R.id.txtEndDate:
                selectDate(2);
                break;
        }

    }

    public void selectDate(int whichDate) {
        // Get Current Date
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = String.format("%02d-%02d-%01d", dayOfMonth, monthOfYear + 1, year);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

                        if (whichDate == 1) {
                            finalToStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            finalToStart.set(Calendar.MONTH, monthOfYear);
                            finalToStart.set(Calendar.YEAR, year);
                            startLong = cal.getTimeInMillis();
                            binding.txtStartDate.setText(date);
                        } else {
                            finalToend.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            finalToend.set(Calendar.MONTH, monthOfYear);
                            finalToend.set(Calendar.YEAR, year);
                            endLong = cal.getTimeInMillis();
                            binding.txtEndDate.setText(date);
                        }

                        if (startLong>0 && endLong >0){
                            callGetOrderHistoryApi();
                        }

//                        if (mDay == cal.get(Calendar.DAY_OF_MONTH))
//                            binding.tvTime.setText("");

                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
//        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }
}
