package in.kpis.afoozo.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kpis.afoozo.R;
import in.kpis.afoozo.adapter.NotificationAdapter;
import in.kpis.afoozo.bean.NotificationBean;
import com.kpis.afoozo.databinding.FragmentNotificationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFrag extends Fragment {

    private Context mContext;

    private View view;

    private FragmentNotificationBinding binding;

    private int page;
    private int position;

    private String imageUrl;

    private NotificationBean notifiBean;

    // newInstance constructor for creating fragment with arguments
    public static NotificationFrag newInstance(int page, String imageUrl, NotificationBean notiBean) {
        NotificationFrag fragmentFirst = new NotificationFrag();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", imageUrl);
        args.putSerializable("productList", notiBean);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        imageUrl = getArguments().getString("someTitle");
        notifiBean = (NotificationBean) getArguments().getSerializable("productList");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.rvNotification.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvNotification.setItemAnimator(new DefaultItemAnimator());

        setData();
    }

    private void setData() {
        NotificationAdapter mAdapter = new NotificationAdapter(mContext, notifiBean.getList());
        binding.rvNotification.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}
