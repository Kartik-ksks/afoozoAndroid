package in.kpis.afoozo.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.FragmentCoinBinding;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.kpis.afoozo.adapter.WalletHistoryAdapter;
import in.kpis.afoozo.bean.WalletHistoryBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;

public class CoinFrag extends Fragment implements View.OnClickListener {

private View view;

private Context mContext;

private FragmentCoinBinding binding;
private boolean isDataSet;

private ArrayList<WalletHistoryBean> list;

@Override
public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        }

private int mYear;
private int mMonth;
private int mDay;
private Calendar finalToStart = Calendar.getInstance();
private Calendar finalToend = Calendar.getInstance();
private Calendar dateTimeCalender = Calendar.getInstance();
private long startLong;
private long endLong;

@Override
public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isDataSet)
        callGetHistoryListApi();
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coin, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvCoinHistory.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvCoinHistory.setItemAnimator(new DefaultItemAnimator());

        TodayDate();

        isDataSet = true;

        binding.txtStartDate.setOnClickListener(this);
        binding.txtEndDate.setOnClickListener(this);
        binding.btnSubmitDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtStartDate:
                selectDate(1);
                break;
            case R.id.txtEndDate:
                selectDate(2);
                break;
            case R.id.btnSubmitDate:

//                if (Utils.isValidStartDate(mContext, binding.txtStartDate.getText().toString()) && Utils.isValidEndDate(mContext, binding.txtendDate.getText().toString())) {
//                    binding.llCustomHistroy.setVisibility(View.GONE);
                callGetHistoryListApi();
//                }
                break;

        }
    }

    private void TodayDate() {
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, mDay);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.YEAR, mYear);
        binding.txtStartDate.setText(Utils.getFormetedDate(cal.getTimeInMillis()));
        binding.txtEndDate.setText(Utils.getFormetedDate(cal.getTimeInMillis()));

        startLong = cal.getTimeInMillis();
        endLong = cal.getTimeInMillis();
        callGetHistoryListApi();

    }


    public void selectDate(int whichDate) {
        // Get Current Date
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);

//        c.add(Calendar.YEAR, - 13);
//        long maxDate = c.getTime().getTime();
//        c.add(Calendar.YEAR, - 45);
//        long minDate = c.getTime().getTime();


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = String.format("%02d-%02d-%01d", dayOfMonth, monthOfYear + 1, year);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

//                        finalToSend.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        finalToSend.set(Calendar.MONTH, monthOfYear);
//                        finalToSend.set(Calendar.YEAR, year);

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

//                        if (mDay == cal.get(Calendar.DAY_OF_MONTH))
//                            binding.tvTime.setText("");

                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
//        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }


    private void setData() {
        binding.rvCoinHistory.setAdapter(new WalletHistoryAdapter(mContext, list));
    }

    private void callGetHistoryListApi() {
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<WalletHistoryBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<WalletHistoryBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<WalletHistoryBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0) {
                            binding.rvCoinHistory.setVisibility(View.VISIBLE);
                            setData();
                        }else {
                            binding.rvCoinHistory.setVisibility(View.GONE);
                        }
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_COIN_TRANSACTION_LIST_DATA + "0/-1" + "/" + startLong + "/" + endLong, "", "Loading...", true, AppUrls.REQUESTTAG_GETCOINTRANSACTIONLISTDATA);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}