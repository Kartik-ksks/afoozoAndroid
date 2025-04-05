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
import com.kpis.afoozo.databinding.FragmentPastBinding;
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
public class PastFrag extends Fragment implements EventListClick {

    private View view;

    private Context mContext;

    private FragmentPastBinding binding;

    private EventsAdapter mAdapter;
    private ArrayList<EventsBean> list = new ArrayList();
    private ArrayList<EventsBean> eventList = new ArrayList<>();

    private String selectedRest = "All";

    private boolean isDataSet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isDataSet)
                refreshList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_past, container, false);
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
        binding.rvPast.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvPast.setItemAnimator(new DefaultItemAnimator());

        callEventsListApi();
        isDataSet = true;
    }

    public void refreshList(){
        selectedRest = ((EventsActi) getActivity()).selectedRestaurant;
        callEventsListApi();
    }

    @Override
    public void eventListClick(int position) {
        Intent intent = new Intent(mContext, EventDetailsActi.class);
        intent.putExtra("Event_Id", eventList.get(position).getUuid());
        intent.putExtra(Constants.FROM_WHICH, getString(R.string.past));
        startActivity(intent);
    }

    @Override
    public void addEventClick(int position) {
        Utils.shaveEventInCalendar(mContext, list.get(position).getOccasionTitle(), list.get(position).getStartDateTimeStamp(),
                list.get(position).getEndDateTimeStamp(), list.get(position).getDescription());
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

        mAdapter = new EventsAdapter(mContext, false, eventList, this);
        binding.rvPast.setAdapter(mAdapter);
        binding.rvPast.addItemDecoration(new StickHeaderItemDecoration(mAdapter));
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
            }).callAPIJson(mContext, Constants.VAL_GET, AppUrls.GET_PAST_LIST + selectedRest + "/0/1000?search=", "", "Loading...", true, AppUrls.REQUESTTAG_GETPASTLIST);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
