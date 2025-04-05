package in.kpis.afoozo.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.AddItemsNewActi;
import in.kpis.afoozo.adapter.MenuMainAdapter;
import in.kpis.afoozo.adapter.NewMenuAdapter;
import in.kpis.afoozo.bean.MenuBean;
import in.kpis.afoozo.bean.MenuListBean;
import com.kpis.afoozo.databinding.FragmentCategoryBinding;
import in.kpis.afoozo.interfaces.AddRemoveClick;
import in.kpis.afoozo.interfaces.CategoryAddRemove;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFrag extends Fragment implements AddRemoveClick, CategoryAddRemove {

    private View view;

    private FragmentCategoryBinding binding;

    private Context mContext;

    private int page;

    private MenuBean categoryBean;
//    private MenuItemsAdapter verticleAdapter;
//    private MenuItemsAdapter2 horizontalAdapter;
    private ArrayList<MenuListBean> itemList;
    private boolean isSameRestaurant;
    private MenuMainAdapter mAdapter;
    private NewMenuAdapter newAdapter;

    private int scrollPositon;
    private int oldPosition;
    private LinearLayoutManager layoutManager;

    // newInstance constructor for creating fragment with arguments
    public static CategoryFrag newInstance(int page, MenuBean categoryList) {
        CategoryFrag fragmentFirst = new CategoryFrag();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putSerializable("productList", categoryList);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);
        view = binding.getRoot();

        page = getArguments().getInt("someInt", 0);
//        categoryBean = (MenuBean) getArguments().getSerializable("productList");

        initialize();
        return view;
    }

    private void initialize() {
        isSameRestaurant = ((AddItemsNewActi) getActivity()) .isSameRestaurant;

        layoutManager = new LinearLayoutManager(mContext);
        binding.rvCategory.setLayoutManager(layoutManager);
        binding.rvCategory.setItemAnimator(new DefaultItemAnimator());
        binding.rvCategory.addOnScrollListener(onScrollListener);

        ArrayList<MenuBean> menuList = ((AddItemsNewActi) getActivity()).filterList;
        if (menuList != null && menuList.size() > page)
            categoryBean = menuList.get(page);
        else
            categoryBean = null;

        binding.cvSticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpendOrCollapsed(scrollPositon);
            }
        });

        setData();
    }

    private void setData() {

//        if (categoryBean != null) {
//            itemList = categoryBean.getMenuList();
//
//            binding.rvCategory.setVisibility(View.VISIBLE);
//
//            mAdapter = new MenuMainAdapter(mContext, categoryBean, page, this);
//            binding.rvCategory.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//
////            if (categoryBean.getCategoryViewType().equals("Vertical")) {
////                binding.rvCategory.setLayoutManager(new LinearLayoutManager(mContext));
////                binding.rvCategory.setItemAnimator(new DefaultItemAnimator());
////
////                verticleAdapter = new MenuItemsAdapter(mContext, itemList, page, this);
////                binding.rvCategory.setAdapter(verticleAdapter);
////                verticleAdapter.notifyDataSetChanged();
////            } else {
////                binding.rvCategory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
////                binding.rvCategory.setItemAnimator(new DefaultItemAnimator());
////
////                horizontalAdapter = new MenuItemsAdapter2(mContext, itemList, page, this);
////                binding.rvCategory.setAdapter(horizontalAdapter);
////                horizontalAdapter.notifyDataSetChanged();
////            }
//        } else
//            binding.rvCategory.setVisibility(View.GONE);

        if (categoryBean != null) {
            itemList = new ArrayList();
            if (categoryBean.getMenuList() != null && categoryBean.getMenuList().size() > 0) {
                for (MenuListBean mb : categoryBean.getMenuList())
                    itemList.add(mb);
            }

            if (categoryBean.getSubCategoryList() != null && categoryBean.getSubCategoryList().size() > 0){
                int position = 0;
                for (MenuBean mb: categoryBean.getSubCategoryList()){
                    MenuBean smb = new MenuBean();
                    smb.setSubCatPosition(position++);
                    smb.setCategoryUuid(mb.getCategoryUuid());
                    smb.setCategoryName(mb.getCategoryName());
                    smb.setExpended(mb.isExpended());
                    smb.setMenuList(mb.getMenuList());
                    itemList.add(new MenuListBean(smb));
                }
            }

            if (((AddItemsNewActi) getActivity()).showCatPosition == page && ((AddItemsNewActi) getActivity()).isFirst){
                if (((AddItemsNewActi) getActivity()).showItemIsSubCat){
                    newAdapter = new NewMenuAdapter(mContext, itemList, page, 0, false, this, true, ((AddItemsNewActi) getActivity()).showSubCatPosition, ((AddItemsNewActi) getActivity()).showItemPosition);
                    binding.rvCategory.setAdapter(newAdapter);
                    newAdapter.notifyDataSetChanged();
                    if (categoryBean.getMenuList() != null && categoryBean.getMenuList().size() > 0)
                        binding.rvCategory.scrollToPosition(categoryBean.getMenuList().size() + ((AddItemsNewActi) getActivity()).showSubCatPosition);
                    else
                        binding.rvCategory.scrollToPosition(((AddItemsNewActi) getActivity()).showSubCatPosition);

                    checkScrollPosition();

                } else {
                    setAdapter();
                    binding.rvCategory.scrollToPosition(((AddItemsNewActi) getActivity()).showItemPosition);
                }
                ((AddItemsNewActi) getActivity()).isFirst = false;
            } else
                setAdapter();
        } else
            binding.rvCategory.setVisibility(View.GONE);
    }

    private void setAdapter() {
        newAdapter = new NewMenuAdapter(mContext, itemList, page, 0, false, this);
        binding.rvCategory.setAdapter(newAdapter);
        newAdapter.notifyDataSetChanged();
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition() >= 0 ) {
                scrollPositon = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (oldPosition != scrollPositon)
                    checkScrollPosition();
            }
        }
    };

    /**
     * this method is check the scroll position and change the menu
     */
    private void checkScrollPosition() {
            oldPosition = scrollPositon;
            if (itemList.get(scrollPositon).getSubCategory() != null && itemList.get(scrollPositon).getSubCategory().isExpended()){
                binding.cvSticky.setVisibility(View.VISIBLE);
                binding.txtSubCatTitle.setText(itemList.get(scrollPositon).getSubCategory().getCategoryName());
            } else
                binding.cvSticky.setVisibility(View.GONE);
    }

    @Override
    public void addClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        ((AddItemsNewActi) getActivity()) .addClick(isSubCat, catPosition, subCatPosition, itemPosition, this);
    }

    @Override
    public void removeClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        ((AddItemsNewActi) getActivity()) .removeClick(isSubCat, catPosition, subCatPosition, itemPosition, this);
    }

    @Override
    public void editClick(boolean isSubCat, int catPosition, int subCatPosition, int itemPosition) {
        ((AddItemsNewActi) getActivity()) .editClick(isSubCat, catPosition, subCatPosition, itemPosition, null);
    }

    @Override
    public void stickyClick(int position) {
        setExpendOrCollapsed(position);
    }

    @Override
    public void scrollCallback() {
//        float y = newAdapter.rvSubCategorytoScroll.getChildAt(((AddItemsNewActi) getActivity()).showItemPosition).getY();
//        binding.rvCategory.scrollBy(0, (int) y);

        android.os.Handler handler1 = new android.os.Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    float y = newAdapter.rvSubCategorytoScroll.getY() + newAdapter.rvSubCategorytoScroll.getChildAt(((AddItemsNewActi) getActivity()).showItemPosition).getY() - 100;
                    binding.rvCategory.scrollBy(0, (int) y);
                } catch (Exception e){

                }
//                                layoutManager.scrollToPositionWithOffset(((AddItemsNewActi) getActivity()).showSubCatPosition, (int) y);
//                                newAdapter.lmSubCategorytoScroll.scrollToPositionWithOffset(((AddItemsNewActi) getActivity()).showItemPosition,0);
            }
        } , 2000);
    }

    private void setExpendOrCollapsed(int position) {
        if (itemList.get(position).getSubCategory().isExpended()){
            itemList.get(position).getSubCategory().setExpended(false);
//            ((AddItemsActi)mContext).expandCollapseSubCategory(categoryPosition, position, false);
            newAdapter.notifyItemChanged(position);
        } else {
            itemList.get(position).getSubCategory().setExpended(true);
//            ((AddItemsActi)mContext).expandCollapseSubCategory(categoryPosition, position, true);
            newAdapter.notifyItemChanged(position);
        }
        if (position == scrollPositon)
            checkScrollPosition();
        else
            layoutManager.scrollToPositionWithOffset(position, 0);
    }

    @Override
    public void categoryAddRemoveClick(boolean isSubCat, int position, int subCatPosition, int qty) {
        if (isSubCat)
            categoryBean.getSubCategoryList().get(subCatPosition).getMenuList().get(position).setQuantity(qty);
        else
            itemList.get(position).setQuantity(qty);

        newAdapter.notifyDataSetChanged();

//        if (categoryBean.getCategoryViewType().equals("Vertical"))
//            verticleAdapter.notifyDataSetChanged();
//        else
//            horizontalAdapter.notifyDataSetChanged();
    }
}
