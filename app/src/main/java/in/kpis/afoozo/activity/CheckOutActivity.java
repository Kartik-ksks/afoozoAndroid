package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityCheckOutBinding;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.kpis.afoozo.adapter.RoomSpinnerAdapter;
import in.kpis.afoozo.bean.OrdersBean;
import in.kpis.afoozo.bean.RoomAvailabilityBean;
import in.kpis.afoozo.bean.SaveRoomBookingBean;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

public class CheckOutActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {
    String[] car = {"SingleOccupancy", "DoubleOccupancy"};


    private long dateInLong;
    private int mYear;
    private int mMonth;
    private int mDay;
    private long startDateLong;
    private long endDateLong;
    private int whichDate;
    private Toolbar toolbar;
    private Context mContext;

    private ActivityCheckOutBinding binding;

    private String roomType = "SingleOccupancy";
    public long roomId;
    public long restaurantId;

    public long checkInDate;
    public long checkOutDate;
    ArrayList<RoomAvailabilityBean> roomAvailabilityList;
    private boolean isAvailible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);
        mContext = CheckOutActivity.this;
        Spinner spin = (Spinner) findViewById(R.id.spinnerMultiSelect);
        spin.setOnItemSelectedListener(this);
        mContext = CheckOutActivity.this;
        if (getIntent() != null) {
            restaurantId = getIntent().getLongExtra(Constants.SCAN_QR_BEAN, 0);

        }
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, car);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.activityTitle.setText(getString(R.string.check_in));

        binding.llDatePickerOpen.setOnClickListener(this);
        binding.llDatePickerClose.setOnClickListener(this);
        binding.onProcess.setOnClickListener(this);
//        callRoomAvailabilityApi();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDatePickerOpen:
                whichDate = 1;
                selectDate();
                break;
            case R.id.llDatePickerClose:
                whichDate = 2;
                selectDate();
                break;
            case R.id.onProcess:
                SaveRoom();
                break;
        }
    }

    private void SaveRoom() {
        if (Utils.isValidCommonMessage(mContext, binding.txtStartDate.getText().toString(), mContext.getString(R.string.please_select_check_in_date))
                && Utils.isValidCommonMessage(mContext, binding.txtEndDate.getText().toString(), mContext.getString(R.string.please_select_check_out_date))) {
            if (isAvailible) {
                callSaveRoomBookingApi();
            } else {
                Utils.isValidCommonMessage(mContext, "", mContext.getString(R.string.no_room_avalible));
            }
        }
    }

    public void selectDate() {
//        binding.txtTodayRequest.setBackgroundResource(R.drawable.round_gray_border_bg);
//        binding.llStartDate.setBackgroundResource(R.drawable.round_white_bg);

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
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            startDateLong = cal.getTimeInMillis();
                            binding.txtStartDate.setText(date);
                        } else if (whichDate == 2) {
                            cal.set(Calendar.HOUR_OF_DAY, 23);
                            cal.set(Calendar.MINUTE, 59);
                            cal.set(Calendar.SECOND, 59);
                            endDateLong = cal.getTimeInMillis();
                            binding.txtEndDate.setText(date);
//                            callSalesReportListApi();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {

                }
            }
        });
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
        if (startDateLong > 0 && whichDate == 2)
            datePickerDialog.getDatePicker().setMinDate(startDateLong);
        datePickerDialog.show();
//        setSalesReportData();
    }

    public void openCalendar() {
        // Get Current Date
        Date today = new Date();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.setTime(today);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date = String.format("%02d-%02d-%01d", dayOfMonth, monthOfYear + 1, year);
//
//                        binding.txtHistoryDate.setText(date);
//                        binding.txtDateShow.setText(date);
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

                        dateInLong = cal.getTimeInMillis();
//                            callApi();

                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
//        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            roomType = "SingleOccupancy";
        } else if (position == 1) {
            roomType = "DoubleOccupancy";
        }
        callRoomAvailabilityApi();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    ////////////////////////////////////Driver lst


    private void setAvailibleRoomList() {
//        if (roomAvailabilityList != null && roomAvailabilityList.size() > 0)
//            roomAvailabilityList.clear();

        RoomSpinnerAdapter customAdapter = new RoomSpinnerAdapter(mContext, roomAvailabilityList);
        binding.spinnerRoomList.setAdapter(customAdapter);

        binding.spinnerRoomList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                if (roomAvailabilityList.get(position).isSecondaryValue()) {
                roomId = roomAvailabilityList.get(position).getRecordId();
//                } else
//                    key = null;

//                Toast.makeText(parent.getContext(),
//                        "OnItemSelectedListener : " + boyList.get(position).getValue(),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void callRoomAvailabilityApi() {
        RoomAvailabilityBean bean = new RoomAvailabilityBean();
        bean.setRestaurantRecordId(restaurantId);
        bean.setRoomType(roomType);
        String jsonObject = new Gson().toJson(bean);

        NetworkManager networkManager;
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RoomAvailabilityBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Type objType = new TypeToken<ResponseClass<ArrayList<RoomAvailabilityBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RoomAvailabilityBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        roomAvailabilityList = responseClass1.getResponsePacket();

                        if (roomAvailabilityList != null && roomAvailabilityList.size() > 0) {
                            isAvailible = true;
                            setAvailibleRoomList();
                        } else
                            isAvailible = false;

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.GET_ROOM_AVAILABILITY, jsonObject, "Loading...", true, AppUrls.REQUESTTAG_GETROOMAVAILABILITY);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callSaveRoomBookingApi() {

        SaveRoomBookingBean bean = new SaveRoomBookingBean();
        bean.setRestaurantId(restaurantId);
        bean.setRoomType(roomType);
        bean.setRoomId(roomId);
        bean.setCheckInDateTimeStamp(startDateLong);
        bean.setCheckOutDateTimeStamp(endDateLong);

        String jsonObject = new Gson().toJson(bean);

        NetworkManager networkManager;
        try {
            new NetworkManager(String.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()) {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                        String orderId = (String) responseClass.getResponsePacket();

                        Intent intent = new Intent(mContext, CartActi.class);
                        intent.putExtra(Constants.ORDER_ID, orderId);
                        intent.putExtra(Constants.RESTAURANT_ID, restaurantId);
                        intent.putExtra(Constants.FROM_WHICH, Constants.ROOM_BOOKING);
                        startActivity(intent);
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found));
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVE_ROOM_BOOKING, jsonObject, "Loading...", true, AppUrls.REQUESTTAG_SAVEROOMBOOKING);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
