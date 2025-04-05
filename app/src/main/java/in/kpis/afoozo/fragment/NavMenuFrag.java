package in.kpis.afoozo.fragment;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kpis.afoozo.R;
import com.kpis.afoozo.databinding.FragmentNavMenuBinding;

import in.kpis.afoozo.activity.Dashboard;
import in.kpis.afoozo.adapter.NavMenuAdapter;
import in.kpis.afoozo.bean.UserBean;
import in.kpis.afoozo.util.SharedPref;
import in.kpis.afoozo.util.Utils;
import in.kpis.afoozo.util.recycler_view_utilities.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavMenuFrag extends Fragment implements View.OnClickListener {
    private Context mContex;
    private View view;
    private  FragmentNavMenuBinding binding;

    private NavMenuAdapter adapter;
    private FragmentDrawerListener drawerListener;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] navigationDrawerItems;
    private UserBean userBean;
    public void setDrawerListener(Dashboard listener) {
        this.drawerListener = listener;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContex = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_menu, container, false);
        view = binding.getRoot();
        initialize();
        return view;
    }

    private void initialize() {
        navigationDrawerItems = getResources().getStringArray(R.array.nav_drawer_items);
        binding.rvNavMenuList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NavMenuAdapter(getActivity(), navigationDrawerItems);
        binding.rvNavMenuList.setAdapter(adapter);
        binding.rvNavMenuList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(),
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, navigationDrawerItems[position]);
                mDrawerLayout.closeDrawer(containerView);
            }
        }));

        binding.relProfileSideBar.setOnClickListener(this);
        binding.btnLogout.setOnClickListener(this);
    }

    private void setData() {
        userBean = (UserBean) Utils.getJsonToClassObject(SharedPref.getUserModelJSON(getActivity()), UserBean.class);
        if (userBean != null) {
            binding.txtUsername.setText("" + userBean.getFullName());
            binding.txtMobile.setText("+91 " + userBean.getMobile());
            if (!TextUtils.isEmpty(userBean.getUserImage())) {
                Utils.setImage(getActivity(), binding.imvUserImage, userBean.getUserImage());
            } else if(userBean.getGender().equalsIgnoreCase("MALE")){
                Drawable res = getResources().getDrawable(R.drawable.profile_thumb);
                binding.imvUserImage.setImageDrawable(res);
            }else if(userBean.getGender().equalsIgnoreCase("FEMALE")) {
                Drawable res = getResources().getDrawable(R.drawable.girl);
                binding.imvUserImage.setImageDrawable(res);
            }
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
                setData();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.relProfileSideBar) {
            drawerListener.onDrawerItemSelected(view, mContex.getString(R.string.profile));
            mDrawerLayout.closeDrawer(containerView);
        } else if (vId == R.id.btnLogout){
            drawerListener.onDrawerItemSelected(view, mContex.getResources().getString(R.string.logout));
            mDrawerLayout.closeDrawer(containerView);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, String menuName);
    }

}
