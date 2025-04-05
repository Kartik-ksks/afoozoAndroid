package in.kpis.afoozo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kpis.afoozo.R;

import java.util.ArrayList;

import in.kpis.afoozo.bean.RoomAvailabilityBean;

public class RoomSpinnerAdapter extends BaseAdapter {
    Context mContext;
    int flags[];
    LayoutInflater inflter;
    private ArrayList<RoomAvailabilityBean> roomList;

    public RoomSpinnerAdapter(Context mContext, ArrayList<RoomAvailabilityBean> roomList) {
        this.mContext = mContext;
        this.roomList = roomList;
        inflter = (LayoutInflater.from(mContext));

    }

    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_room_spinner_item, null);
        TextView roomNumber = (TextView) view.findViewById(R.id.txtRoomNumber);
        TextView txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        LinearLayout llDriver = view.findViewById(R.id.llDriver);
//        status.setText(roomList.get(i).isSecondaryValue()+"");
        roomNumber.setText(mContext.getString(R.string.room_number) + roomList.get(i).getRoomNumber());
        txtPrice.setText(mContext.getString(R.string.Rs) + roomList.get(i).getRoomPrice());
//        if(roomList.get(i).isSecondaryValue()){
//            status.setText(" ("+mContext.getText(R.string.active)+")");
//        }else {
//            status.setText(" ("+mContext.getText(R.string.inActive)+")");
//        }
//        if(roomList.get(i).getValue().equals("Select Driver")){
////            names.setTextSize(18);
//            names.setTypeface(null, Typeface.BOLD);
//            status.setVisibility(View.GONE);
//        }else {
//            status.setVisibility(View.VISIBLE);
//        }


        return view;
    }
}
