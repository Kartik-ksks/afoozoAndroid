package in.kpis.afoozo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.ActivityChooseTimeBinding;

import java.util.ArrayList;
import java.util.Calendar;

import in.kpis.afoozo.adapter.TimeAdapter;
import in.kpis.afoozo.bean.TimeListBean;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

public class ChooseTimeActi extends AppCompatActivity {

    private Toolbar toolbar;

    private Context mContext;

    private ActivityChooseTimeBinding binding;

    private int hour;
    private int minutes;
    private int openHour = 8;
    private int openMinute = 30;
    private int closeHour = 22;
    private int closeMinute = 30;
    private int convertedHour;

    private ArrayList<TimeListBean> todayList;
    private ArrayList<TimeListBean> tomorrowList;
    private TimeAdapter mAdapter;

    private long deliveryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_time);

        mContext = ChooseTimeActi.this;
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

        binding.toolbar.activityTitle.setText(getString(R.string.delivery_later));

        binding.rvTime.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvTime.setItemAnimator(new DefaultItemAnimator());
        binding.rvTime.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (TimeListBean tb: todayList){
                    tb.setSelected(false);
                }
                todayList.get(position).setSelected(true);
                mAdapter.notifyDataSetChanged();
            }
        }));

        createTimeList();
        setData();
    }

    private void createTimeList() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        todayList = new ArrayList<>();
        tomorrowList = new ArrayList<>();
        TimeListBean bean = new TimeListBean("Now", hour, minutes, hour, minutes, true);
//        todayList.add(bean);

        if (hour < openHour){
            hour = openHour;
            if (openMinute == 0){
                hour = openHour;
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                todayList.add(bean);
            } else if (openMinute > 31)
                hour = openHour+1;
            else {
                hour = openHour+1;
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                todayList.add(bean);
            }

            while (hour < closeHour){
                bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                todayList.add(bean);
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                todayList.add(bean);
            }
            if (todayList != null && todayList.size() > 0)
                todayList.get(0).setSelected(true);
        } else if (hour > closeHour){
            todayList = new ArrayList<>();
        } else {
            if (minutes < 15 )
                ++hour;
            else if (minutes > 45)
                hour = hour + 2;
            else {
                ++hour;
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                todayList.add(bean);
            }

            while (hour < closeHour){
                bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                todayList.add(bean);
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                todayList.add(bean);
            }

            if (hour == closeHour){
                if (closeMinute > 0 && closeMinute < 30 ){
                    bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                    todayList.add(bean);
                } else if (closeMinute >= 30 && closeMinute < 60){
                    bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                    todayList.add(bean);
                    bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                    todayList.add(bean);
                }
            }

            if (todayList != null && todayList.size() > 0)
                todayList.get(0).setSelected(true);
        }



        hour = openHour;

        if (openMinute == 0){
            hour = openHour;
            bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
            tomorrowList.add(bean);
        } else if (openMinute > 31)
            hour = openHour+1;
        else {
            hour = openHour+1;
            bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
            tomorrowList.add(bean);
        }

        while (hour < closeHour){
            bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
            tomorrowList.add(bean);
            bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
            tomorrowList.add(bean);
        }


        if (hour == closeHour){
            if (closeMinute > 0 && closeMinute < 30 ){
                bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                tomorrowList.add(bean);
            } else if (closeMinute >= 30 && closeMinute < 60){
                bean = new TimeListBean(getTimeString(hour, 00) , hour, 00, hour, 30, false);
                tomorrowList.add(bean);
                bean = new TimeListBean(getTimeString(hour, 30) , hour, 30, ++hour, 00, false);
                tomorrowList.add(bean);
            }
        }

        tomorrowList.get(0).setSelected(true);

    }

    private String getTimeString(int hour, int minutes){
        String timeString = "";
        if (hour < 12){
            if (minutes == 30)
                timeString = hour + ":30 AM - " + "12:00 PM";
            else
                timeString = hour + ":00 AM - " + hour + ":30 AM";
        } else if (hour == 12){
            if (minutes == 30)
                timeString = hour + ":30 PM - "  + "01:00 PM";
            else
                timeString = hour + ":00 PM - " + hour + ":30 PM";
        } else {
            convertedHour = hour-12;
            if (minutes == 30)
                timeString = convertedHour + ":30 PM - " + ++convertedHour  + ":00 PM";
            else
                timeString = convertedHour + ":00 PM - " + convertedHour + ":30 PM";
        }
        return timeString;
    }

    private void setData() {
        mAdapter = new TimeAdapter(mContext, todayList);
        binding.rvTime.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void setScheduleTime(View view) {
        Calendar calendar = Calendar.getInstance();
//        if (isTodayList) {
//        if (todayList.get(0).isSelected())
//            deliveryTime = Calendar.getInstance().getTimeInMillis();
//        else {
            for (TimeListBean tb : todayList) {
                if (tb.isSelected()) {
                    calendar.set(Calendar.HOUR_OF_DAY, tb.getEndHour());
                    calendar.set(Calendar.MINUTE, tb.getEndMinute());
                    break;
                }
            }
//        } else {
//            for (TimeListBean tb: tomorrowList) {
//                if (tb.isSelected()) {
//                    int day = calendar.get(Calendar.DAY_OF_MONTH);
//                    calendar.set(Calendar.DAY_OF_MONTH, day+1);
//                    calendar.set(Calendar.HOUR_OF_DAY, tb.getEndHour());
//                    calendar.set(Calendar.MINUTE, tb.getEndMinute());
//                    break;
//                }
//            }
//        }

            deliveryTime = calendar.getTimeInMillis();
//        }

        Intent intent = new Intent();
        intent.putExtra(Constants.DELIVERY_TIME, deliveryTime);
        setResult(RESULT_OK, intent);
        finish();
    }
}
