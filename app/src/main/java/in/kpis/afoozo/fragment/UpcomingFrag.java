package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.EventDetailsActi;
import in.kpis.afoozo.activity.EventsActi;
import in.kpis.afoozo.adapter.EventsAdapter;
import in.kpis.afoozo.bean.EventsBean;
import com.kpis.afoozo.databinding.FragmentUpcomingBinding;
import in.kpis.afoozo.interfaces.EventListClick;
import in.kpis.afoozo.okhttpServcice.AppUrls;
import in.kpis.afoozo.okhttpServcice.NetworkManager;
import in.kpis.afoozo.okhttpServcice.response.ResponseClass;
import in.kpis.afoozo.util.Constants;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.StickHeaderItemDecoration;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFrag extends Fragment implements EventListClick {

    private View view;

    private Context mContext;

    private FragmentUpcomingBinding binding;
    private EventsAdapter mAdapter;
    private ArrayList<EventsBean> list = new ArrayList();
    private ArrayList<EventsBean> eventList = new ArrayList();

    private boolean isDataSet;
    private String selectedRest = "All";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isDataSet)
            refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedRest = ((EventsActi) getActivity()).selectedRestaurant;
    }

    private void initialize() {
        binding.rvUpcoming.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvUpcoming.setItemAnimator(new DefaultItemAnimator());

        callEventsListApi();
        isDataSet = true;
    }

    public void refreshList(){
        try {
            selectedRest = ((EventsActi) getActivity()).selectedRestaurant;
            callEventsListApi();
        } catch (Exception e){

        }
    }

    @Override
    public void eventListClick(int position) {
        Intent intent = new Intent(mContext, EventDetailsActi.class);
        intent.putExtra("Event_Id", eventList.get(position).getUuid());
        intent.putExtra(Constants.FROM_WHICH, getString(R.string.upcoming));
        startActivity(intent);
    }

    @Override
    public void addEventClick(int position) {
        Utils.shaveEventInCalendar(mContext, eventList.get(position).getOccasionTitle(), eventList.get(position).getStartDateTimeStamp(),
                eventList.get(position).getEndDateTimeStamp(), eventList.get(position).getDescription());
    }


    private void setData() {
        String month = "";
        if (eventList != null && eventList.size() > 0)
            eventList.clear();
        for (EventsBean eb: list){
            if (eb.getMonth().equalsIgnoreCase(month))
                eventList.add(eb);
            else {
                EventsBean eventsBean = new EventsBean(true, eb.getMonth());
                eventList.add(eventsBean);
                eventList.add(eb);
                month = eb.getMonth();
            }
        }
        mAdapter = new EventsAdapter(mContext, true, eventList, this);
        binding.rvUpcoming.setAdapter(mAdapter);
        binding.rvUpcoming.addItemDecoration(new StickHeaderItemDecoration(mAdapter));
        mAdapter.notifyDataSetChanged();
    }

    private void callEventsListApi() {
        try {
            new NetworkManager(ArrayList.class, new NetworkManager.OnCallback<EventsBean>() {
                @Override
                public void onSuccess(boolean success, ResponseClass responseClass, int which) {
                    if (responseClass.isSuccess()){
                        Type objType = new TypeToken<ResponseClass<ArrayList<EventsBean>>>() {
                        }.getType();
                        ResponseClass<ArrayList<EventsBean>> responseClass1 = new Gson().fromJson(responseClass.getCompletePacket(), objType);

                        list = responseClass1.getResponsePacket();

                        if (list != null && list.size() > 0)
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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_UPCOMING_LIST + selectedRest +"/0/1000?search=", "", "Loading...", true, AppUrls.REQUESTTAG_GETUPCOMINGLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
