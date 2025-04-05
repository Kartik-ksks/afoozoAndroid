package in.kpis.afoozo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kpis.afoozo.R;
import in.kpis.afoozo.bean.EventsBean;
import in.kpis.afoozo.bean.ReservationBean;
import in.kpis.afoozo.bean.RestaurantBean;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.custome.RangeTimePickerDialog;
import com.kpis.afoozo.databinding.ActivityEventDetailsBinding;
import com.kpis.afoozo.databinding.PopupReserveEventBinding;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.CALL_PHONE;

public class EventDetailsActi extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityEventDetailsBinding binding;

    private String eventId;

    private EventsBean eventBean;

    private Dialog alert;

    private final int REQUESTPERMISSIONCODE = 109;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int selectedHour;
    private int selectedMinute;

    private long reserveDateInLong;

    private PopupReserveEventBinding reserveBinding;

    private Calendar dateTimeCalender = Calendar.getInstance();

    private UserBean userBean;
    private String fromWhich;
    private RangeTimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);

        if (getIntent().getExtras() != null){
            eventId = getIntent().getStringExtra("Event_Id");
            fromWhich = getIntent().getStringExtra(Constants.FROM_WHICH);
        }

        mContext = EventDetailsActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.event_details));

        if (fromWhich.equals(getString(R.string.upcoming)))
            binding.cvReserve.setVisibility(View.VISIBLE);
        else
            binding.cvReserve.setVisibility(View.GONE);

        callEventsDetailsApi();
    }

    private void setData() {
        try {
            if (!TextUtils.isEmpty(eventBean.getOccasionImageUrl()))
                Utils.setImage(mContext, binding.imvEvent, binding.pbEvent, eventBean.getOccasionImageUrl());

            dateTimeCalender.setTimeInMillis(eventBean.getStartDateTimeStamp());
            binding.txtEventName.setText(eventBean.getOccasionTitle());
            binding.txtVenue.setText(eventBean.getOccasionAddress());
            binding.txtStartDate.setText(Utils.getFormetedDate(eventBean.getStartDateTimeStamp()));
            binding.txtStartTime.setText(Utils.getFormetedTimeForEvent(eventBean.getStartDateTimeStamp()));
            binding.txtEndDate.setText(Utils.getFormetedDate(eventBean.getEndDateTimeStamp()));
            binding.txtEndTime.setText(Utils.getFormetedTimeForEvent(eventBean.getEndDateTimeStamp()));

            if (!TextUtils.isEmpty(eventBean.getDescription()))
                binding.txtDesc.setText(eventBean.getDescription());
        } catch (Exception e){

        }

    }

    public void shareProcess(View view){
       Utils.shareEvents(mContext, eventBean.getSharingContent());
    }

    /**
     * This method is used to save events in google calendar
     * @param view
     */
    public void saveProcess(View view){
        Utils.shaveEventInCalendar(mContext, eventBean.getOccasionTitle(), eventBean.getStartDateTimeStamp(),
                eventBean.getEndDateTimeStamp(), eventBean.getDescription());
    }

    public void goToMapScreen(View view){
        Intent intent = new Intent(mContext, MapActi.class);
        intent.putExtra(Constants.LATITUDE, eventBean.getLatitude());
        intent.putExtra(Constants.LONGITUDE, eventBean.getLongitude());
        intent.putExtra(Constants.ADDRESS, eventBean.getOccasionAddress());
        intent.putExtra(Constants.EVENT_NAME, eventBean.getOccasionTitle());
        startActivity(intent);
    }
    public void callProcess(View view){
        TelephonyManager tm= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE){
            //No calling functionality
            Utils.showCenterToast(mContext, "No calling functionality available on your device");
        }
        else
        {
            //calling functionality
            checkPermission();
        }
    }

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
//        long minDate = c.getTime().getTime();


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


                        reserveBinding.txtDate.setText(date);
                        reserveDateInLong = cal.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay);
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 568025136000L);
//        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
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
//                        if (hourOfDay == 0) {
//                            reserveBinding.txtTime.setText(12 + ":" + minute + " AM");
//                        } else if (hourOfDay == 12) {
//                            reserveBinding.txtTime.setText(12 + ":" + minute + " PM");
//                        } else if (hourOfDay > 12) {
//                            reserveBinding.txtTime.setText(new DecimalFormat("00").format(hourOfDay -12) + ":" + minute + " PM");
//                        } else {
//                            reserveBinding.txtTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + minute + " AM");
//                        }
//
//                    }
//                }, mHour, mMinute, false);
//        timePickerDialog.show();
//    }

    private void selectTime(){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        timePickerDialog = new RangeTimePickerDialog(mContext, 2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                dateTimeCalender.set(Calendar.HOUR, hourOfDay);
                dateTimeCalender.set(Calendar.MINUTE, minute);

                if (hourOfDay == 0) {
                    reserveBinding.txtTime.setText(12 + ":" + minute + " AM");
                } else if (hourOfDay == 12) {
                    reserveBinding.txtTime.setText(12 + ":" + minute + " PM");
                } else if (hourOfDay > 12) {
                    reserveBinding.txtTime.setText(new DecimalFormat("00").format(hourOfDay -12) + ":" + minute + " PM");
                } else {
                    reserveBinding.txtTime.setText(new DecimalFormat("00").format(hourOfDay) + ":" + minute + " AM");
                }
            }
        }, mHour, mMinute, false);

        int startHour = Utils.getFormetedHour(eventBean.getStartDateTimeStamp());
        int startMinutes = Utils.getFormetedMinute(eventBean.getStartDateTimeStamp());

        if (c.get(Calendar.DAY_OF_MONTH) == Utils.getFormetedDay(eventBean.getStartDateTimeStamp())) {
            if (startHour > mHour && (startHour == mHour && startMinutes > mMinute))
                timePickerDialog.setMin(startHour, startMinutes);
            else
                timePickerDialog.setMin(mHour, mMinute);
        } else {
            timePickerDialog.setMin(startHour, startMinutes);
        }

        timePickerDialog.setMax(Utils.getFormetedHour(eventBean.getEndDateTimeStamp()), Utils.getFormetedMinute(eventBean.getEndDateTimeStamp()));
        timePickerDialog.show();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(mContext, CALL_PHONE);
            if (result != PackageManager.PERMISSION_GRANTED)
                requestPermission();
            else
                callAction();
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            callAction();
            return true;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{ CALL_PHONE}, REQUESTPERMISSIONCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTPERMISSIONCODE:
                if (grantResults.length > 0) {
                    boolean readSms = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readSms) {
                        callAction();
                    } else {
                        Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void callAction(){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + eventBean.getContactNumber()));
        startActivity(callIntent);
    }

    public void showPopup(View view){
//        Utils.reserveEventDialog(mContext);

        alert = new Dialog(mContext);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window dialogWindow = alert.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(lp);

        reserveBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.popup_reserve_event, null, false);
        alert.setContentView(reserveBinding.getRoot());

//        alert.setContentView(R.layout.buy_popup_layout);

        userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(mContext), UserBean.class);

        if (userBean.getFullName() != null && !TextUtils.isEmpty(userBean.getFullName()))
            reserveBinding.etName.setText(userBean.getFullName());
        if (!TextUtils.isEmpty(userBean.getMobile()))
            reserveBinding.etMobile.setText(userBean.getMobile());

//        reserveBinding.txtDate.setText(Utils.getFormetedDate(eventBean.getStartDateTimeStamp()));
        reserveBinding.txtTime.setText(Utils.getFormetedTimeForEvent(eventBean.getStartDateTimeStamp()));

        reserveBinding.etNoOfPeople.setText("");

        reserveBinding.txtTime.setOnClickListener(this);
        reserveBinding.txtDate.setOnClickListener(this);

        reserveBinding.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.showCenterToast(mContext, getString(R.string.curriently_we_are_working_on_it));
//                alert.cancel();
                reserveProcess(eventBean.getRestaurantId());
            }
        });


        alert.getWindow().getAttributes().windowAnimations = R.style.Animation_Dialog;
        alert.setCancelable(true);
        alert.show();

    }

    private void reserveProcess(long restaurantId){
        if(TextUtils.isEmpty(reserveBinding.etName.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_name));
        }else if(TextUtils.isEmpty(reserveBinding.etMobile.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_mobile));
        }
//        else if(TextUtils.isEmpty(reserveBinding.txtDate.getText().toString().trim())){
//            Utils.showCenterToast(mContext, getString(R.string.please_enter_date));
//        }
        else if(TextUtils.isEmpty(reserveBinding.txtTime.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_time));
        }else if(TextUtils.isEmpty(reserveBinding.etNoOfPeople.getText().toString().trim())){
            Utils.showCenterToast(mContext, getString(R.string.please_enter_noofpeople));
        }else{
            callPostReservationApi(restaurantId);
        }
    }

    private void callEventsDetailsApi() {
        try {
            new NetworkManager(EventsBean.class, new NetworkManager.OnCallback<EventsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        eventBean = (EventsBean) responseClass.getResponsePacket();
                        setData();
//
                    } else {
                        Utils.showCenterToast(mContext, responseClass.getMessage());
                    }
                }

                @Override
                public void onFailure(boolean success, String response, int which) {
                    Utils.showCenterToast(mContext, getString(R.string.internet_connection_not_found) );
                }
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_EVENT_DETAIL + eventId, "", "Loading...", true, AppUrls.REQUESTTAG_GETEVENTDETAIL);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void callPostReservationApi(long restaurantId) {

        ReservationBean bean = new ReservationBean();
        bean.setRestaurantId(restaurantId);
        bean.setEventId(eventBean.getRecordId());
        bean.setReservedByName(reserveBinding.etName.getText().toString().trim());
        bean.setReservedByMobile(reserveBinding.etMobile.getText().toString().trim());
        bean.setReservedByEmail(userBean.getEmail()!= null ? userBean.getEmail():"");
        bean.setReservationDateTime(dateTimeCalender.getTimeInMillis());
        bean.setOccasion(eventBean.getOccasionTitle());
        bean.setNumberOfPeople(Integer.parseInt(reserveBinding.etNoOfPeople.getText().toString().trim()));
        bean.setSpecialInstruction("");

        String json = new Gson().toJson(bean);

        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<RestaurantBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        alert.dismiss();
                        Utils.showSuccessPopUp(mContext, responseClass.getMessage(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.dismissSuccessAlert();
                                finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtDate:
                selectDate();
                break;
            case R.id.txtTime:
                selectTime();
                break;
        }
    }
}
