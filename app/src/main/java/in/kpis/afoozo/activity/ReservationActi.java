package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.ReservationAdapter;
import in.kpis.afoozo.bean.CityBean;
import in.kpis.afoozo.bean.ReservationBean;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.RestaurantRequestBean;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.custome.RangeTimePickerDialog;
import com.kpis.afoozo.databinding.ActivityReservationBinding;
import com.kpis.afoozo.databinding.PopupReserveEventBinding;
import com.kpis.afoozo.databinding.PopupReserveTableBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReservationActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityReservationBinding binding;

    private ArrayList<RestaurantBean> reservationRestList = new ArrayList();
    private ArrayList<CityBean> cityList = new ArrayList<>();
    private ArrayList<String> cityStringList = new ArrayList<>();

    private ReservationAdapter mAdapter;
    private boolean isFromWallet;

    private ArrayList<String> occasionList;
    private RangeTimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);

        if (getIntent().getExtras() != null)
            isFromWallet = getIntent().getBooleanExtra(Constants.IS_FROM_WALLET, false);

        mContext = ReservationActi.this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
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

        if (isFromWallet)
            binding.toolbar.activityTitle.setText(getString(R.string.pay_at_store));
        else
            binding.toolbar.activityTitle.setText(getString(R.string.reservation));

        binding.rvReservation.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvReservation.setItemAnimator(new DefaultItemAnimator());
        binding.rvReservation.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isFromWallet){
                    Intent intent = new Intent(mContext, PayAtStoreActi.class);
                    intent.putExtra(Constants.RESTAURANT_NAME, reservationRestList.get(position).getTitle());
                    intent.putExtra(Constants.RESTAURANT_ID, reservationRestList.get(position).getRecordId());
                    startActivity(intent);
                } else {
                    if (occasionList != null && occasionList.size() > 0)
                        reserveTableDialog(mContext, reservationRestList.get(position).getRecordId());
                    else
                        callGetOccasionList(reservationRestList.get(position).getRecordId());
                }
            }
        }));
        binding.spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(liveLat != 0) {
                    if(position != 0) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        callGetRestaurantApi(cityList.get(position-1).getKey());
//                        if (selectedItem.equals("All Cities")) {
//                            // do your stuff
//                        }else{
//
//                        }
                    }else{
                        callGetRestaurantApi(0);
                    }
                }else {
                    callGetRestaurantApi(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getLiveLocation();
        callGetCityListApi();
        setReservationData();
    }

    private void setCityData() {

        if(cityList != null && cityList.size() > 0) {
            cityStringList.add("All Cities");
            for (CityBean bean : cityList) {
                cityStringList.add(bean.getValue());
            }

            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, cityStringList);
            cityAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
            binding.spCity.setAdapter(cityAdapter);
        }
    }

    private void setOccasionData() {

    }

    private void setReservationData() {
//        ReservationBean bean = new ReservationBean("Agashiye, Ahmedabad", "https://media.timeout.com/images/105239239/image.jpg");
//        reservationRestList.add(bean);
//        bean = new ReservationBean("Aish, Hyderabad", "https://www.athenaspahotel.com/media/cache/jadro_resize/rc/uJmoXtmd1563349268/jadroRoot/medias/_a1a8429.jpg");
//        reservationRestList.add(bean);
//        bean = new ReservationBean("Bukhara, Delhi", "https://www.omnihotels.com/-/media/images/hotels/bospar/restaurants/bospar-omni-parker-house-parkers-restaurant-1170.jpg");
//        reservationRestList.add(bean);
//        bean = new ReservationBean("Agashiye, Ahmedabad", "https://media.timeout.com/images/105239239/image.jpg");
//        reservationRestList.add(bean);
//        bean = new ReservationBean("Aish, Hyderabad", "https://www.athenaspahotel.com/media/cache/jadro_resize/rc/uJmoXtmd1563349268/jadroRoot/medias/_a1a8429.jpg");
//        reservationRestList.add(bean);
//        bean = new ReservationBean("Bukhara, Delhi", "https://www.omnihotels.com/-/media/images/hotels/bospar/restaurants/bospar-omni-parker-house-parkers-restaurant-1170.jpg");
//        reservationRestList.add(bean);

//        mAdapter = new ReservationAdapter(mContext, reservationList);
//        binding.rvReservation.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }



    private void setRestaurantData() {
        if (reservationRestList != null && reservationRestList.size() > 0){
            binding.rvReservation.setVisibility(View.VISIBLE);
            mAdapter = new ReservationAdapter(mContext, reservationRestList, true);
            binding.rvReservation.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }else{
            binding.rvReservation.setVisibility(View.GONE);
        }
    }


    private FusedLocationProviderClient mFusedLocationClient;

    private int LOCATION_REQUEST_CODE = 101;

    private double liveLat = 0;
    private double liveLong = 0;
    private void getLiveLocation() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    liveLat = location.getLatitude();
                    liveLong = location.getLongitude();

//                    setLocation();
                    callGetRestaurantApi(0);
                    Utils.dismissProgressDialog();

                } else {
                    getLiveLocation();
                }
            });
        }
    }

    private AlertDialog reserveAlert;
    private PopupReserveTableBinding popupReserveTableBinding;
    private UserBean userBean;

    private void reserveTableDialog(Context mContext, long restaurantId) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        popupReserveTableBinding = DataBindingUtil.inflate(inflater, R.layout.popup_reserve_table,null, false);
        dialogBuilder.setView(popupReserveTableBinding.getRoot());

        userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        popupReserveTableBinding.etName.setText(userBean.getFullName());
        popupReserveTableBinding.etMobile.setText(userBean.getMobile());

        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, occasionList);
        eventTypeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
        popupReserveTableBinding.spOccasion.setAdapter(eventTypeAdapter);

        popupReserveTableBinding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        popupReserveTableBinding.etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        reserveAlert = dialogBuilder.create();
        reserveAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reserveAlert.show();

        popupReserveTableBinding.btnReserveTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReserveApi(restaurantId);
            }
        });
    }

    private void callReserveApi(long restaurantId){
        if(TextUtils.isEmpty(popupReserveTableBinding.etName.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_name));
        }else if(TextUtils.isEmpty(popupReserveTableBinding.etMobile.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_mobile));
        }else if(TextUtils.isEmpty(popupReserveTableBinding.etDate.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_date));
        }else if(TextUtils.isEmpty(popupReserveTableBinding.etMobile.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_time));
        }if(TextUtils.isEmpty(popupReserveTableBinding.spOccasion.getSelectedItem().toString())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_occasion));
        }else if(TextUtils.isEmpty(popupReserveTableBinding.etNoOfPeople.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_noofpeople));
        }else{
            callPostReservationApi(restaurantId);
        }
    }


    private int mYear;
    private int mMonth;
    private int mDay;
    private int selectedHour;
    private int selectedMinute;
    private long reserveDateInLong;
    private long reserveTimeInLong;
    Calendar finalToSend = Calendar.getInstance();

    public void selectDate() {
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
        long minDate = c.getTime().getTime();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String date=String.format("%02d-%02d-%01d", dayOfMonth,monthOfYear+1,year);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);

                        finalToSend.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        finalToSend.set(Calendar.MONTH, monthOfYear);
                        finalToSend.set(Calendar.YEAR, year);



                        popupReserveTableBinding.etDate.setText(date);
                        reserveDateInLong = cal.getTimeInMillis();
                        Log.e("TIME in MILLIS", reserveDateInLong+"");
                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    private Calendar dateTimeCalender = Calendar.getInstance();

    private void selectTime(){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        timePickerDialog = new RangeTimePickerDialog(mContext, 2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                        selectedMinute = minute;
                        dateTimeCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTimeCalender.set(Calendar.MINUTE, minute);

                        finalToSend.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        finalToSend.set(Calendar.MINUTE, minute);

                        if (hourOfDay == 0) {
                            popupReserveTableBinding.etTime.setText(12 + ":" + minute + " AM");
                        } else if (hourOfDay == 12) {
                            popupReserveTableBinding.etTime.setText(12 + ":" + minute + " PM");
                        } else if (hourOfDay > 12) {
                            popupReserveTableBinding.etTime.setText(new DecimalFormat("00").format(hourOfDay -12) + ":" + minute + " PM");
                        } else {
                            popupReserveTableBinding.etTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + minute + " AM");
                        }

                        reserveTimeInLong = finalToSend.getTimeInMillis();
            }
        }, mHour, mMinute, false);

        if (finalToSend.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH))
            timePickerDialog.setMin(mHour, mMinute);
        timePickerDialog.show();
    }

//    private void selectTime() {
//        final Calendar c = Calendar.getInstance();
//        int mHour = c.get(Calendar.HOUR_OF_DAY);
//        int mMinute = c.get(Calendar.MINUTE);
//
//        long minDate = c.getTime().getTime();
//
//        // Launch Time Picker Dialog
//        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, 2,
//                new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay,
//                                          int minute) {
//                        selectedHour = hourOfDay;
//                        selectedMinute = minute;
//                        dateTimeCalender.set(Calendar.HOUR, hourOfDay);
//                        dateTimeCalender.set(Calendar.MINUTE, minute);
//
//                        finalToSend.set(Calendar.HOUR, hourOfDay);
//                        finalToSend.set(Calendar.MINUTE, minute);
//
//                        if (hourOfDay == 0) {
//                            popupReserveTableBinding.etTime.setText(12 + ":" + minute + " AM");
//                        } else if (hourOfDay == 12) {
//                            popupReserveTableBinding.etTime.setText(12 + ":" + minute + " PM");
//                        } else if (hourOfDay > 12) {
//                            popupReserveTableBinding.etTime.setText(new DecimalFormat("00").format(hourOfDay -12) + ":" + minute + " PM");
//                        } else {
//                            popupReserveTableBinding.etTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + minute + " AM");
//                        }
//
//                        reserveTimeInLong = finalToSend.getTimeInMillis();
//                        Log.e("TIME in MILLIS", reserveTimeInLong+"");
//
//                    }
//                }, mHour, mMinute, false);
//        timePickerDialog.show();
//    }

    /////////////////////////////////  API Calling Section /////////////////////////////////////////

    private void callGetRestaurantApi(int cityId) {

        RestaurantRequestBean bean = new RestaurantRequestBean();
        bean.setLatitude(liveLat);
        bean.setLongitude(liveLong);
        bean.setCityId(cityId);
        bean.setOrderType("All");
        bean.setStart(0);
        bean.setLength(-1);
        bean.setSearchKey("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<RestaurantBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<RestaurantBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        reservationRestList.clear();
                        reservationRestList = responseClass1.getResponsePacket();
                        setRestaurantData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.RESTAURANT_LIST, json, "Loading...", true, AppUrls.REQUESTTAG_RESTAURANTLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetOccasionList(long recordId) {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<String>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<String>>>() {
                        }.getType();
                        ResponseClass<ArrayList<String>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);
                        occasionList = responseClass1.getResponsePacket();
                        reserveTableDialog(mContext, recordId);

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.OCCASION_LIST, "", "Loading...", true, AppUrls.REQUESTTAG_OCCASIONLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callGetCityListApi() {

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<CityBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<CityBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<CityBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        cityList = responseClass1.getResponsePacket();
                        setCityData();

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GETALLCITYLIST, null, "Loading...", true, AppUrls.REQUESTTAG_GETALLCITYLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callPostReservationApi(long restaurantId) {

        ReservationBean bean = new ReservationBean();
        bean.setRestaurantId(restaurantId);
        bean.setEventId(0);
        bean.setReservedByName(popupReserveTableBinding.etName.getText().toString().trim());
        bean.setReservedByMobile(popupReserveTableBinding.etMobile.getText().toString().trim());
        bean.setReservedByEmail(userBean.getEmail()!= null ? userBean.getEmail():"");
        bean.setReservationDateTime(finalToSend.getTimeInMillis());
        bean.setOccasion(popupReserveTableBinding.spOccasion.getSelectedItem().toString());
        bean.setNumberOfPeople(Integer.parseInt(popupReserveTableBinding.etNoOfPeople.getText().toString().trim()));
        bean.setSpecialInstruction(popupReserveTableBinding.etSpecialInstruction.getText().toString().trim());

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){

                        reserveAlert.dismiss();
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                Utils.startActivityWithFinish(mContext, Dashboard.class);
                            }
                        });

                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_POST, AppUrls.SAVERESERVATION, json, "Loading...", true, AppUrls.REQUESTTAG_SAVERESERVATION);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
